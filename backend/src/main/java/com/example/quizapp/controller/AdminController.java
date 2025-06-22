package com.example.quizapp.controller;

import com.example.quizapp.entity.Category;
import com.example.quizapp.entity.Quiz;
import com.example.quizapp.service.CategoryService;
import com.example.quizapp.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private QuizService quizService;
    
    // ログイン画面
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout,
                       Model model) {
        if (error != null) {
            model.addAttribute("error", "ユーザー名またはパスワードが間違っています");
        }
        if (logout != null) {
            model.addAttribute("message", "ログアウトしました");
        }
        return "admin/login";
    }
    
    // ダッシュボード
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        List<Quiz> quizzes = quizService.getAllQuizzes();
        
        model.addAttribute("categoryCount", categories.size());
        model.addAttribute("quizCount", quizzes.size());
        model.addAttribute("categories", categories);
        
        return "admin/dashboard";
    }
    
    // カテゴリー管理
    @GetMapping("/categories")
    public String manageCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "admin/categories";
    }
    
    // カテゴリー作成フォーム
    @GetMapping("/categories/new")
    public String newCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/category-form";
    }
    
    // カテゴリー編集フォーム
    @GetMapping("/categories/{id}/edit")
    public String editCategoryForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("カテゴリーが見つかりません"));
            model.addAttribute("category", category);
            return "admin/category-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/categories";
        }
    }
    
    // カテゴリー保存
    @PostMapping("/categories")
    public String saveCategory(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        try {
            if (category.getId() == null) {
                categoryService.createCategory(category.getName(), category.getDescription());
                redirectAttributes.addFlashAttribute("success", "カテゴリーを作成しました");
            } else {
                categoryService.updateCategory(category.getId(), category.getName(), category.getDescription());
                redirectAttributes.addFlashAttribute("success", "カテゴリーを更新しました");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }
    
    // カテゴリー削除
    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("success", "カテゴリーを削除しました");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }
    
    // クイズ管理
    @GetMapping("/quizzes")
    public String manageQuizzes(@RequestParam(required = false) Long categoryId, Model model) {
        List<Category> categories = categoryService.getAllCategories();
        List<Quiz> quizzes;
        
        if (categoryId != null) {
            quizzes = quizService.getQuizzesByCategory(categoryId);
            model.addAttribute("selectedCategoryId", categoryId);
        } else {
            quizzes = quizService.getAllQuizzes();
        }
        
        model.addAttribute("categories", categories);
        model.addAttribute("quizzes", quizzes);
        return "admin/quizzes";
    }
    
    // クイズ作成フォーム
    @GetMapping("/quizzes/new")
    public String newQuizForm(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("quiz", new Quiz());
        return "admin/quiz-form";
    }
    
    // クイズ編集フォーム
    @GetMapping("/quizzes/{id}/edit")
    public String editQuizForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Quiz quiz = quizService.getQuizById(id)
                .orElseThrow(() -> new IllegalArgumentException("クイズが見つかりません"));
            List<Category> categories = categoryService.getAllCategories();
            
            model.addAttribute("quiz", quiz);
            model.addAttribute("categories", categories);
            model.addAttribute("options", quizService.getQuizOptions(id));
            
            return "admin/quiz-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/quizzes";
        }
    }
    
    // クイズ保存
    @PostMapping("/quizzes")
    public String saveQuiz(@RequestParam String question,
                          @RequestParam String explanation,
                          @RequestParam Long categoryId,
                          @RequestParam String option1,
                          @RequestParam String option2,
                          @RequestParam String option3,
                          @RequestParam String option4,
                          @RequestParam(required = false) List<String> correctAnswers,
                          @RequestParam(required = false) Long quizId,
                          RedirectAttributes redirectAttributes) {
        try {
            List<String> optionTexts = Arrays.asList(option1, option2, option3, option4);
            List<Boolean> correctFlags = Arrays.asList(
                correctAnswers != null && correctAnswers.contains("1"),
                correctAnswers != null && correctAnswers.contains("2"),
                correctAnswers != null && correctAnswers.contains("3"),
                correctAnswers != null && correctAnswers.contains("4")
            );
            
            if (quizId == null) {
                quizService.createQuiz(question, explanation, categoryId, optionTexts, correctFlags);
                redirectAttributes.addFlashAttribute("success", "クイズを作成しました");
            } else {
                quizService.updateQuiz(quizId, question, explanation, categoryId, optionTexts, correctFlags);
                redirectAttributes.addFlashAttribute("success", "クイズを更新しました");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/quizzes";
    }
    
    // クイズ削除
    @PostMapping("/quizzes/{id}/delete")
    public String deleteQuiz(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            quizService.deleteQuiz(id);
            redirectAttributes.addFlashAttribute("success", "クイズを削除しました");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/quizzes";
    }
}
