package com.example.quizapp.controller;

import com.example.quizapp.dto.*;
import com.example.quizapp.service.CategoryService;
import com.example.quizapp.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * プレイヤー向けのクイズAPIコントローラー
 * ログイン不要でクイズに挑戦できる機能を提供
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class QuizController {

    private final CategoryService categoryService;
    private final QuizService quizService;

    /**
     * 全てのカテゴリーを取得
     * @return List<CategoryDto> カテゴリーのリスト
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * カテゴリー内のクイズをランダムに1問取得
     * @param categoryId カテゴリーID
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return QuizDto クイズ情報
     */
    @GetMapping("/quizzes")
    public ResponseEntity<QuizDto> getRandomQuiz(@RequestParam Long category,
                                                HttpServletRequest request,
                                                HttpServletResponse response) {
        // セッションIDを取得または生成
        String sessionId = getOrCreateSessionId(request, response);
        
        Optional<QuizDto> quiz = quizService.getRandomQuizByCategory(category, sessionId);
        
        if (quiz.isPresent()) {
            return ResponseEntity.ok(quiz.get());
        } else {
            // 未回答のクイズがない場合（全問回答済み）
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * クイズの回答を送信
     * @param quizId クイズID
     * @param answerRequest 回答内容
     * @param request HttpServletRequest
     * @return QuizAnswerResponseDto 回答結果
     */
    @PostMapping("/quizzes/{quizId}/answer")
    public ResponseEntity<QuizAnswerResponseDto> answerQuiz(@PathVariable Long quizId,
                                                           @RequestBody QuizAnswerRequestDto answerRequest,
                                                           HttpServletRequest request) {
        // セッションIDを取得
        String sessionId = getSessionIdFromRequest(request);
        if (sessionId == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            QuizAnswerResponseDto response = quizService.answerQuiz(quizId, sessionId, answerRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * カテゴリー内のクイズ結果を取得
     * @param categoryId カテゴリーID
     * @param request HttpServletRequest
     * @return QuizResultDto クイズ結果
     */
    @GetMapping("/result")
    public ResponseEntity<QuizResultDto> getQuizResult(@RequestParam Long category,
                                                      HttpServletRequest request) {
        // セッションIDを取得
        String sessionId = getSessionIdFromRequest(request);
        if (sessionId == null) {
            return ResponseEntity.badRequest().build();
        }

        Optional<QuizResultDto> result = quizService.getQuizResult(category, sessionId);
        
        if (result.isPresent()) {
            return ResponseEntity.ok(result.get());
        } else {
            // まだ全問回答していない場合
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * リクエストからセッションIDを取得、存在しない場合は新規作成
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return String セッションID
     */
    private String getOrCreateSessionId(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = getSessionIdFromRequest(request);
        
        if (sessionId == null) {
            // 新しいセッションIDを生成
            sessionId = UUID.randomUUID().toString();
            
            // Cookieに保存
            Cookie cookie = new Cookie("sessionId", sessionId);
            cookie.setMaxAge(60 * 60 * 24 * 7); // 7日間有効
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        
        return sessionId;
    }

    /**
     * リクエストからセッションIDを取得
     * @param request HttpServletRequest
     * @return String セッションID（存在しない場合はnull）
     */
    private String getSessionIdFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("sessionId".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
