package com.example.quizapp.repository;

import com.example.quizapp.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    
    List<Quiz> findByCategoryId(Long categoryId);
    
    // PostgreSQLのRANDOM()関数を使用するためnativeQueryを使用
    // JPQLではRANDOM()がサポートされていないため
    @Query(value = "SELECT * FROM quizzes WHERE category_id = :categoryId ORDER BY RANDOM()", nativeQuery = true)
    List<Quiz> findByCategoryIdRandomOrder(@Param("categoryId") Long categoryId);
    
    @Query("SELECT q FROM Quiz q WHERE q.category.id = :categoryId AND q.id NOT IN " +
           "(SELECT qa.quiz.id FROM QuizAttempt qa WHERE qa.sessionId = :sessionId)")
    List<Quiz> findUnansweredQuizzesByCategory(@Param("categoryId") Long categoryId, 
                                               @Param("sessionId") String sessionId);
}
