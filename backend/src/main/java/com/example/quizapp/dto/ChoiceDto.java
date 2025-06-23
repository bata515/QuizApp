package com.example.quizapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChoiceDto {
    private Long id;
    private String text;
    private Boolean isCorrect; // 管理者用のレスポンスでのみ使用
}
