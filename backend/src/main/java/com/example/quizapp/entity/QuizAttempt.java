package com.example.quizapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Entity
@Table(name = "quiz_attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @ElementCollection
    @CollectionTable(name = "attempt_selected_choices", joinColumns = @JoinColumn(name = "attempt_id"))
    @Column(name = "choice_id")
    private List<Long> selectedChoiceIds;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    public QuizAttempt(String sessionId, Quiz quiz, List<Long> selectedChoiceIds, Boolean isCorrect) {
        this.sessionId = sessionId;
        this.quiz = quiz;
        this.selectedChoiceIds = selectedChoiceIds;
        this.isCorrect = isCorrect;
    }
}
