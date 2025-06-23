package com.example.quizapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultDto {
    private Long categoryId;
    private String categoryName;
    private int totalQuestions;
    private int correctAnswers;
    private double scorePercentage;
}
