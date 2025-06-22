package com.example.quizapp.service;

import com.example.quizapp.entity.Quiz;
import com.example.quizapp.entity.QuizOption;
import com.example.quizapp.entity.Category;
import com.example.quizapp.repository.QuizRepository;
import com.example.quizapp.repository.QuizOptionRepository;
import com.example.quizapp.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {
    
    @Autowired
    private QuizRepository quizRepository;
    
    @Autowired
    private QuizOptionRepository quizOptionRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }
    
    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findById(id);
    }
    
    public List<Quiz> getQuizzesByCategory(Long categoryId) {
        return quizRepository.findByCategoryId(categoryId);
    }
    
    public List<Quiz> getRandomQuizzesByCategory(Long categoryId) {
        return quizRepository.findByCategoryIdOrderByRandom(categoryId);
    }
    
    public long countQuizzesByCategory(Long categoryId) {
        return quizRepository.countByCategoryId(categoryId);
    }
    
    @Transactional
    public Quiz createQuiz(String question, String explanation, Long categoryId, 
                          List<String> optionTexts, List<Boolean> correctAnswers) {
        
        // カテゴリーの存在確認
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("カテゴリーが見つかりません: " + categoryId));
        
        // 選択肢は4つ必要
        if (optionTexts.size() != 4 || correctAnswers.size() != 4) {
            throw new IllegalArgumentException("選択肢は4つ必要です");
        }
        
        // 少なくとも1つの正解が必要
        if (!correctAnswers.contains(true)) {
            throw new IllegalArgumentException("少なくとも1つの正解が必要です");
        }
        
        // クイズを作成
        Quiz quiz = new Quiz(question, explanation, category);
        quiz = quizRepository.save(quiz);
        
        // 選択肢を作成
        for (int i = 0; i < 4; i++) {
            QuizOption option = new QuizOption(
                optionTexts.get(i), 
                correctAnswers.get(i), 
                i + 1, 
                quiz
            );
            quizOptionRepository.save(option);
        }
        
        return quiz;
    }
    
    @Transactional
    public Quiz updateQuiz(Long id, String question, String explanation, Long categoryId,
                          List<String> optionTexts, List<Boolean> correctAnswers) {
        
        Quiz quiz = quizRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("クイズが見つかりません: " + id));
        
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("カテゴリーが見つかりません: " + categoryId));
        
        // 選択肢は4つ必要
        if (optionTexts.size() != 4 || correctAnswers.size() != 4) {
            throw new IllegalArgumentException("選択肢は4つ必要です");
        }
        
        // 少なくとも1つの正解が必要
        if (!correctAnswers.contains(true)) {
            throw new IllegalArgumentException("少なくとも1つの正解が必要です");
        }
        
        // クイズを更新
        quiz.setQuestion(question);
        quiz.setExplanation(explanation);
        quiz.setCategory(category);
        quiz = quizRepository.save(quiz);
        
        // 既存の選択肢を削除
        quizOptionRepository.deleteByQuiz(quiz);
        
        // 新しい選択肢を作成
        for (int i = 0; i < 4; i++) {
            QuizOption option = new QuizOption(
                optionTexts.get(i), 
                correctAnswers.get(i), 
                i + 1, 
                quiz
            );
            quizOptionRepository.save(option);
        }
        
        return quiz;
    }
    
    @Transactional
    public void deleteQuiz(Long id) {
        if (!quizRepository.existsById(id)) {
            throw new IllegalArgumentException("クイズが見つかりません: " + id);
        }
        
        // 選択肢も一緒に削除される（cascade設定により）
        quizRepository.deleteById(id);
    }
    
    public List<QuizOption> getQuizOptions(Long quizId) {
        return quizOptionRepository.findByQuizIdOrderByOptionOrder(quizId);
    }
    
    public List<QuizOption> getCorrectOptions(Long quizId) {
        return quizOptionRepository.findByQuizIdAndIsCorrectTrue(quizId);
    }
    
    public boolean checkAnswer(Long quizId, List<Long> selectedOptionIds) {
        List<QuizOption> correctOptions = getCorrectOptions(quizId);
        List<Long> correctOptionIds = correctOptions.stream()
            .map(QuizOption::getId)
            .toList();
        
        // 選択された選択肢と正解の選択肢が完全に一致するかチェック
        return selectedOptionIds.size() == correctOptionIds.size() && 
               selectedOptionIds.containsAll(correctOptionIds);
    }
}
