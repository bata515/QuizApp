package com.example.quizapp.repository;

import com.example.quizapp.entity.QuizOption;
import com.example.quizapp.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizOptionRepository extends JpaRepository<QuizOption, Long> {
    
    List<QuizOption> findByQuizOrderByOptionOrder(Quiz quiz);
    
    List<QuizOption> findByQuizIdOrderByOptionOrder(Long quizId);
    
    List<QuizOption> findByQuizAndIsCorrectTrue(Quiz quiz);
    
    List<QuizOption> findByQuizIdAndIsCorrectTrue(Long quizId);
    
    void deleteByQuiz(Quiz quiz);
    
    void deleteByQuizId(Long quizId);
}
