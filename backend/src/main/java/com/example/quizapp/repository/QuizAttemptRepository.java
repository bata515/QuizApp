package com.example.quizapp.repository;

import com.example.quizapp.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    
    List<QuizAttempt> findBySessionId(String sessionId);
    
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.sessionId = :sessionId AND qa.quiz.category.id = :categoryId")
    List<QuizAttempt> findBySessionIdAndCategoryId(@Param("sessionId") String sessionId, 
                                                   @Param("categoryId") Long categoryId);
    
    @Query("SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.sessionId = :sessionId AND qa.quiz.category.id = :categoryId AND qa.isCorrect = true")
    Long countCorrectAnswersBySessionIdAndCategoryId(@Param("sessionId") String sessionId, 
                                                     @Param("categoryId") Long categoryId);
    
    boolean existsBySessionIdAndQuizId(String sessionId, Long quizId);
}
