package com.example.quizapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswerRequestDto {
    private List<Long> selectedChoiceIds;
}
