package com.example.quizapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerResponseDto {
    private boolean isCorrect;
    private String explanation;
    private List<Long> correctChoiceIds;
    private List<Long> selectedChoiceIds;
}
