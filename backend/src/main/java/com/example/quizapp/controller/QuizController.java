package com.example.quizapp.controller;

import com.example.quizapp.entity.Category;
import com.example.quizapp.entity.Quiz;
import com.example.quizapp.entity.QuizOption;
import com.example.quizapp.service.CategoryService;
import com.example.quizapp.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/quiz")
public class QuizController {
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private QuizService quizService;
    
    // カテゴリー選択画面
    @GetMapping("/categories")
    public String showCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "quiz/categories";
    }
    
    // クイズ開始
    @PostMapping("/start")
    public String startQuiz(@RequestParam Long categoryId, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            Category category = categoryService.getCategoryById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("カテゴリーが見つかりません"));
            
            List<Quiz> quizzes = quizService.getRandomQuizzesByCategory(categoryId);
            if (quizzes.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "このカテゴリーにはクイズがありません");
                return "redirect:/quiz/categories";
            }
            
            // セッションにクイズ情報を保存
            session.setAttribute("categoryId", categoryId);
            session.setAttribute("categoryName", category.getName());
            session.setAttribute("quizzes", quizzes);
            session.setAttribute("currentQuizIndex", 0);
            session.setAttribute("correctAnswers", 0);
            session.setAttribute("userAnswers", new ArrayList<Boolean>());
            
            return "redirect:/quiz/question";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "クイズの開始に失敗しました: " + e.getMessage());
            return "redirect:/quiz/categories";
        }
    }
    
    // 問題表示
    @GetMapping("/question")
    public String showQuestion(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        @SuppressWarnings("unchecked")
        List<Quiz> quizzes = (List<Quiz>) session.getAttribute("quizzes");
        Integer currentIndex = (Integer) session.getAttribute("currentQuizIndex");
        
        if (quizzes == null || currentIndex == null) {
            redirectAttributes.addFlashAttribute("error", "セッションが無効です。最初からやり直してください。");
            return "redirect:/quiz/categories";
        }
        
        if (currentIndex >= quizzes.size()) {
            return "redirect:/quiz/result";
        }
        
        Quiz currentQuiz = quizzes.get(currentIndex);
        List<QuizOption> options = quizService.getQuizOptions(currentQuiz.getId());
        
        model.addAttribute("quiz", currentQuiz);
        model.addAttribute("options", options);
        model.addAttribute("currentIndex", currentIndex + 1);
        model.addAttribute("totalQuestions", quizzes.size());
        model.addAttribute("categoryName", session.getAttribute("categoryName"));
        
        return "quiz/question";
    }
    
    // 回答処理
    @PostMapping("/answer")
    public String submitAnswer(@RequestParam(required = false) List<Long> selectedOptions,
                             HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        List<Quiz> quizzes = (List<Quiz>) session.getAttribute("quizzes");
        Integer currentIndex = (Integer) session.getAttribute("currentQuizIndex");
        Integer correctAnswers = (Integer) session.getAttribute("correctAnswers");
        @SuppressWarnings("unchecked")
        List<Boolean> userAnswers = (List<Boolean>) session.getAttribute("userAnswers");
        
        if (quizzes == null || currentIndex == null || correctAnswers == null || userAnswers == null) {
            return "redirect:/quiz/categories";
        }
        
        Quiz currentQuiz = quizzes.get(currentIndex);
        List<QuizOption> options = quizService.getQuizOptions(currentQuiz.getId());
        List<QuizOption> correctOptions = quizService.getCorrectOptions(currentQuiz.getId());
        
        // 選択肢がない場合は空のリストとして扱う
        if (selectedOptions == null) {
            selectedOptions = new ArrayList<>();
        }
        
        // 正解チェック
        boolean isCorrect = quizService.checkAnswer(currentQuiz.getId(), selectedOptions);
        
        if (isCorrect) {
            session.setAttribute("correctAnswers", correctAnswers + 1);
        }
        userAnswers.add(isCorrect);
        session.setAttribute("userAnswers", userAnswers);
        
        // 結果表示用のデータを準備
        model.addAttribute("quiz", currentQuiz);
        model.addAttribute("options", options);
        model.addAttribute("correctOptions", correctOptions);
        model.addAttribute("selectedOptions", selectedOptions);
        model.addAttribute("isCorrect", isCorrect);
        model.addAttribute("currentIndex", currentIndex + 1);
        model.addAttribute("totalQuestions", quizzes.size());
        model.addAttribute("categoryName", session.getAttribute("categoryName"));
        
        // 次の問題のインデックスを更新
        session.setAttribute("currentQuizIndex", currentIndex + 1);
        
        return "quiz/answer";
    }
    
    // 最終結果表示
    @GetMapping("/result")
    public String showResult(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        @SuppressWarnings("unchecked")
        List<Quiz> quizzes = (List<Quiz>) session.getAttribute("quizzes");
        Integer correctAnswers = (Integer) session.getAttribute("correctAnswers");
        @SuppressWarnings("unchecked")
        List<Boolean> userAnswers = (List<Boolean>) session.getAttribute("userAnswers");
        
        if (quizzes == null || correctAnswers == null || userAnswers == null) {
            redirectAttributes.addFlashAttribute("error", "セッションが無効です。");
            return "redirect:/quiz/categories";
        }
        
        int totalQuestions = quizzes.size();
        double percentage = totalQuestions > 0 ? (double) correctAnswers / totalQuestions * 100 : 0;
        
        model.addAttribute("correctAnswers", correctAnswers);
        model.addAttribute("totalQuestions", totalQuestions);
        model.addAttribute("percentage", Math.round(percentage * 10.0) / 10.0);
        model.addAttribute("categoryName", session.getAttribute("categoryName"));
        
        // セッションをクリア
        session.removeAttribute("categoryId");
        session.removeAttribute("categoryName");
        session.removeAttribute("quizzes");
        session.removeAttribute("currentQuizIndex");
        session.removeAttribute("correctAnswers");
        session.removeAttribute("userAnswers");
        
        return "quiz/result";
    }
}
