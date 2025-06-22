package com.example.quizapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "quiz_options")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizOption {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String optionText;
    
    @Column(nullable = false)
    private Boolean isCorrect = false;
    
    @Column(nullable = false)
    private Integer optionOrder; // 選択肢の順序（1, 2, 3, 4）
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;
    
    // カスタムコンストラクタ（IDを除く）
    public QuizOption(String optionText, Boolean isCorrect, Integer optionOrder, Quiz quiz) {
        this.optionText = optionText;
        this.isCorrect = isCorrect;
        this.optionOrder = optionOrder;
        this.quiz = quiz;
    }
}
