package com.example.quizapp.service;

import com.example.quizapp.dto.*;
import com.example.quizapp.entity.*;
import com.example.quizapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * クイズ関連のビジネスロジックを処理するサービスクラス
 * プレイヤー向けのクイズ機能と管理者向けのクイズ管理機能を提供
 */
@Service
@RequiredArgsConstructor
@Transactional
public class QuizService {

    private final QuizRepository quizRepository;
    private final ChoiceRepository choiceRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final CategoryRepository categoryRepository;

    /**
     * カテゴリー内の未回答クイズをランダムに1問取得（プレイヤー用）
     * @param categoryId カテゴリーID
     * @param sessionId プレイヤーのセッションID
     * @return Optional<QuizDto> クイズ情報（正解情報は含まない）
     */
    @Transactional(readOnly = true)
    public Optional<QuizDto> getRandomQuizByCategory(Long categoryId, String sessionId) {
        // 未回答のクイズを取得
        List<Quiz> unansweredQuizzes = quizRepository.findUnansweredQuizzesByCategory(categoryId, sessionId);
        
        if (unansweredQuizzes.isEmpty()) {
            return Optional.empty();
        }
        
        // ランダムに1問選択
        Collections.shuffle(unansweredQuizzes);
        Quiz quiz = unansweredQuizzes.get(0);
        
        return Optional.of(convertToDtoForPlayer(quiz));
    }

    /**
     * クイズの回答を処理（プレイヤー用）
     * @param quizId クイズID
     * @param sessionId プレイヤーのセッションID
     * @param answerRequest 回答内容
     * @return QuizAnswerResponseDto 回答結果
     */
    public QuizAnswerResponseDto answerQuiz(Long quizId, String sessionId, QuizAnswerRequestDto answerRequest) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("クイズが見つかりません"));

        // 既に回答済みかチェック
        if (quizAttemptRepository.existsBySessionIdAndQuizId(sessionId, quizId)) {
            throw new RuntimeException("このクイズは既に回答済みです");
        }

        // 正解の選択肢IDを取得
        List<Long> correctChoiceIds = choiceRepository.findByQuizIdAndIsCorrectTrue(quizId)
                .stream()
                .map(Choice::getId)
                .collect(Collectors.toList());

        // 回答が正解かどうかを判定（選択した選択肢が正解の選択肢と完全一致する必要がある）
        List<Long> selectedChoiceIds = answerRequest.getSelectedChoiceIds();
        Collections.sort(selectedChoiceIds);
        Collections.sort(correctChoiceIds);
        boolean isCorrect = selectedChoiceIds.equals(correctChoiceIds);

        // 回答結果を保存
        QuizAttempt attempt = new QuizAttempt(sessionId, quiz, selectedChoiceIds, isCorrect);
        quizAttemptRepository.save(attempt);

        // レスポンスを作成
        return new QuizAnswerResponseDto(
                isCorrect,
                quiz.getExplanation(),
                correctChoiceIds,
                selectedChoiceIds
        );
    }

    /**
     * カテゴリー内のクイズ結果を取得（プレイヤー用）
     * @param categoryId カテゴリーID
     * @param sessionId プレイヤーのセッションID
     * @return Optional<QuizResultDto> クイズ結果
     */
    @Transactional(readOnly = true)
    public Optional<QuizResultDto> getQuizResult(Long categoryId, String sessionId) {
        Category category = categoryRepository.findById(categoryId)
                .orElse(null);
        
        if (category == null) {
            return Optional.empty();
        }

        // カテゴリー内の総問題数
        int totalQuestions = quizRepository.findByCategoryId(categoryId).size();
        
        // 正解数を取得
        Long correctAnswers = quizAttemptRepository.countCorrectAnswersBySessionIdAndCategoryId(sessionId, categoryId);
        
        // 回答済み問題数
        List<QuizAttempt> attempts = quizAttemptRepository.findBySessionIdAndCategoryId(sessionId, categoryId);
        
        // 全問題を回答していない場合は結果を返さない
        if (attempts.size() < totalQuestions) {
            return Optional.empty();
        }

        double scorePercentage = totalQuestions > 0 ? (double) correctAnswers / totalQuestions * 100 : 0;

        return Optional.of(new QuizResultDto(
                categoryId,
                category.getName(),
                totalQuestions,
                correctAnswers.intValue(),
                scorePercentage
        ));
    }

    /**
     * 全てのクイズを取得（管理者用）
     * @return List<QuizDto> クイズのリスト
     */
    @Transactional(readOnly = true)
    public List<QuizDto> getAllQuizzes() {
        return quizRepository.findAll().stream()
                .map(this::convertToDtoForAdmin)
                .collect(Collectors.toList());
    }

    /**
     * IDでクイズを取得（管理者用）
     * @param id クイズID
     * @return Optional<QuizDto> クイズ情報
     */
    @Transactional(readOnly = true)
    public Optional<QuizDto> getQuizById(Long id) {
        return quizRepository.findById(id)
                .map(this::convertToDtoForAdmin);
    }

    /**
     * 新しいクイズを作成（管理者用）
     * @param quizDto クイズ情報
     * @return QuizDto 作成されたクイズ
     */
    public QuizDto createQuiz(QuizDto quizDto) {
        Category category = categoryRepository.findById(quizDto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("カテゴリーが見つかりません"));

        Quiz quiz = new Quiz(quizDto.getQuestion(), quizDto.getExplanation(), category);
        Quiz savedQuiz = quizRepository.save(quiz);

        // 選択肢を保存
        List<Choice> choices = quizDto.getChoices().stream()
                .map(choiceDto -> new Choice(savedQuiz, choiceDto.getText(), choiceDto.getIsCorrect()))
                .collect(Collectors.toList());
        choiceRepository.saveAll(choices);

        return convertToDtoForAdmin(savedQuiz);
    }

    /**
     * クイズを更新（管理者用）
     * @param id クイズID
     * @param quizDto 更新するクイズ情報
     * @return Optional<QuizDto> 更新されたクイズ
     */
    public Optional<QuizDto> updateQuiz(Long id, QuizDto quizDto) {
        return quizRepository.findById(id)
                .map(quiz -> {
                    Category category = categoryRepository.findById(quizDto.getCategoryId())
                            .orElseThrow(() -> new RuntimeException("カテゴリーが見つかりません"));

                    quiz.setQuestion(quizDto.getQuestion());
                    quiz.setExplanation(quizDto.getExplanation());
                    quiz.setCategory(category);
                    Quiz savedQuiz = quizRepository.save(quiz);

                    // 既存の選択肢を削除して新しい選択肢を保存
                    choiceRepository.deleteAll(quiz.getChoices());
                    List<Choice> choices = quizDto.getChoices().stream()
                            .map(choiceDto -> new Choice(savedQuiz, choiceDto.getText(), choiceDto.getIsCorrect()))
                            .collect(Collectors.toList());
                    choiceRepository.saveAll(choices);

                    return convertToDtoForAdmin(savedQuiz);
                });
    }

    /**
     * クイズを削除（管理者用）
     * @param id クイズID
     * @return boolean 削除成功の場合true
     */
    public boolean deleteQuiz(Long id) {
        if (quizRepository.existsById(id)) {
            quizRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * QuizエンティティをQuizDtoに変換（プレイヤー用：正解情報を含まない）
     * @param quiz Quizエンティティ
     * @return QuizDto 変換されたDTO
     */
    private QuizDto convertToDtoForPlayer(Quiz quiz) {
        List<ChoiceDto> choiceDtos = quiz.getChoices().stream()
                .map(choice -> new ChoiceDto(choice.getId(), choice.getText(), null)) // 正解情報は含まない
                .collect(Collectors.toList());

        return new QuizDto(
                quiz.getId(),
                quiz.getQuestion(),
                null, // 解説は回答後に提供
                quiz.getCategory().getId(),
                quiz.getCategory().getName(),
                choiceDtos
        );
    }

    /**
     * QuizエンティティをQuizDtoに変換（管理者用：正解情報を含む）
     * @param quiz Quizエンティティ
     * @return QuizDto 変換されたDTO
     */
    private QuizDto convertToDtoForAdmin(Quiz quiz) {
        List<ChoiceDto> choiceDtos = quiz.getChoices().stream()
                .map(choice -> new ChoiceDto(choice.getId(), choice.getText(), choice.getIsCorrect()))
                .collect(Collectors.toList());

        return new QuizDto(
                quiz.getId(),
                quiz.getQuestion(),
                quiz.getExplanation(),
                quiz.getCategory().getId(),
                quiz.getCategory().getName(),
                choiceDtos
        );
    }
}
