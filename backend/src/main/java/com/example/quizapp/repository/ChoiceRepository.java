package com.example.quizapp.repository;

import com.example.quizapp.entity.Choice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChoiceRepository extends JpaRepository<Choice, Long> {
    
    List<Choice> findByQuizId(Long quizId);
    
    List<Choice> findByQuizIdAndIsCorrectTrue(Long quizId);
}
