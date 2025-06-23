package com.example.quizapp.controller;

import com.example.quizapp.dto.*;
import com.example.quizapp.service.AdminService;
import com.example.quizapp.service.CategoryService;
import com.example.quizapp.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 管理者向けのAPIコントローラー
 * JWT認証が必要な管理機能を提供
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final CategoryService categoryService;
    private final QuizService quizService;

    /**
     * 管理者ログイン
     * @param loginRequest ログイン情報
     * @return AdminLoginResponseDto ログイン結果（JWTトークンを含む）
     */
    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponseDto> login(@RequestBody AdminLoginRequestDto loginRequest) {
        return adminService.login(loginRequest)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());

    }

    // ===== カテゴリー管理 =====

    /**
     * 全てのカテゴリーを取得（管理者用）
     * @return List<CategoryDto> カテゴリーのリスト
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * カテゴリーを作成
     * @param categoryDto カテゴリー情報
     * @return CategoryDto 作成されたカテゴリー
     */
    @PostMapping("/categories")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto categoryDto) {
        CategoryDto createdCategory = categoryService.createCategory(categoryDto);
        return ResponseEntity.ok(createdCategory);
    }

    /**
     * カテゴリーを更新
     * @param id カテゴリーID
     * @param categoryDto 更新するカテゴリー情報
     * @return CategoryDto 更新されたカテゴリー
     */
    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id, 
                                                     @RequestBody CategoryDto categoryDto) {
        Optional<CategoryDto> updatedCategory = categoryService.updateCategory(id, categoryDto);
        
        if (updatedCategory.isPresent()) {
            return ResponseEntity.ok(updatedCategory.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * カテゴリーを削除
     * @param id カテゴリーID
     * @return ResponseEntity<Void>
     */
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        boolean deleted = categoryService.deleteCategory(id);
        
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ===== クイズ管理 =====

    /**
     * 全てのクイズを取得（管理者用）
     * @return List<QuizDto> クイズのリスト
     */
    @GetMapping("/quizzes")
    public ResponseEntity<List<QuizDto>> getAllQuizzes() {
        List<QuizDto> quizzes = quizService.getAllQuizzes();
        return ResponseEntity.ok(quizzes);
    }

    /**
     * IDでクイズを取得（管理者用）
     * @param id クイズID
     * @return QuizDto クイズ情報
     */
    @GetMapping("/quizzes/{id}")
    public ResponseEntity<QuizDto> getQuizById(@PathVariable Long id) {
        Optional<QuizDto> quiz = quizService.getQuizById(id);
        
        if (quiz.isPresent()) {
            return ResponseEntity.ok(quiz.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * クイズを作成
     * @param quizDto クイズ情報
     * @return QuizDto 作成されたクイズ
     */
    @PostMapping("/quizzes")
    public ResponseEntity<QuizDto> createQuiz(@RequestBody QuizDto quizDto) {
        try {
            QuizDto createdQuiz = quizService.createQuiz(quizDto);
            return ResponseEntity.ok(createdQuiz);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * クイズを更新
     * @param id クイズID
     * @param quizDto 更新するクイズ情報
     * @return QuizDto 更新されたクイズ
     */
    @PutMapping("/quizzes/{id}")
    public ResponseEntity<QuizDto> updateQuiz(@PathVariable Long id, 
                                             @RequestBody QuizDto quizDto) {
        try {
            Optional<QuizDto> updatedQuiz = quizService.updateQuiz(id, quizDto);
            
            if (updatedQuiz.isPresent()) {
                return ResponseEntity.ok(updatedQuiz.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * クイズを削除
     * @param id クイズID
     * @return ResponseEntity<Void>
     */
    @DeleteMapping("/quizzes/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        boolean deleted = quizService.deleteQuiz(id);
        
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
