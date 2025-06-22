package com.example.quizapp.repository;

import com.example.quizapp.entity.Quiz;
import com.example.quizapp.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    
    List<Quiz> findByCategory(Category category);
    
    List<Quiz> findByCategoryId(Long categoryId);
    
    @Query(value = "SELECT * FROM quizzes WHERE category_id = :categoryId ORDER BY RANDOM()", nativeQuery = true)
    List<Quiz> findByCategoryIdOrderByRandom(@Param("categoryId") Long categoryId);
    
    long countByCategory(Category category);
    
    long countByCategoryId(Long categoryId);
}
