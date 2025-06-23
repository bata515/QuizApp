package com.example.quizapp.config;

import com.example.quizapp.entity.AdminUser;
import com.example.quizapp.entity.Category;
import com.example.quizapp.entity.Choice;
import com.example.quizapp.entity.Quiz;
import com.example.quizapp.repository.AdminUserRepository;
import com.example.quizapp.repository.CategoryRepository;
import com.example.quizapp.repository.ChoiceRepository;
import com.example.quizapp.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * アプリケーション起動時に初期データを投入するクラス
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AdminUserRepository adminUserRepository;
    private final CategoryRepository categoryRepository;
    private final QuizRepository quizRepository;
    private final ChoiceRepository choiceRepository;
    
    // 循環依存を避けるため、PasswordEncoderを直接インスタンス化
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) throws Exception {
        System.out.println("DataInitializer開始");
        
        // 管理者ユーザーが存在しない場合のみ初期データを投入
        long adminCount = adminUserRepository.count();
        System.out.println("管理者ユーザー数: " + adminCount);
        if (adminCount == 0) {
            initializeAdminUser();
        }

        // カテゴリーが存在しない場合のみ初期データを投入
        long categoryCount = categoryRepository.count();
        System.out.println("カテゴリー数: " + categoryCount);
        
        if (categoryCount == 0) {
            System.out.println("初期データを投入します");
            initializeCategories();
            initializeQuizzes();
        } else {
            System.out.println("カテゴリーが既に存在するため、初期データの投入をスキップします");
        }
        
        System.out.println("DataInitializer完了");
    }
    /**
     * 既存のクイズデータをクリア
     */
    private void clearExistingData() {
        System.out.println("既存データをクリア中...");
        // 外部キー制約を考慮して正しい順序で削除
        choiceRepository.deleteAll();
        quizRepository.deleteAll();
        categoryRepository.deleteAll();
        System.out.println("既存データをクリアしました");
    }

    /**
     * 管理者ユーザーの初期データを投入
     */
    private void initializeAdminUser() {
        AdminUser admin = new AdminUser();
        admin.setUsername("admin");
        admin.setPasswordHash(passwordEncoder.encode("password"));
        adminUserRepository.save(admin);
        
        System.out.println("管理者ユーザーを作成しました: username=admin, password=password");
    }

    /**
     * カテゴリーの初期データを投入
     */
    private void initializeCategories() {
        Category javaCategory = new Category("Java");
        Category springCategory = new Category("Spring Boot");
        Category databaseCategory = new Category("データベース");
        
        categoryRepository.saveAll(Arrays.asList(javaCategory, springCategory, databaseCategory));
        
        System.out.println("カテゴリーの初期データを投入しました");
    }

    /**
     * クイズの初期データを投入
     */
    private void initializeQuizzes() {
        Category javaCategory = categoryRepository.findAll().get(0);
        Category springCategory = categoryRepository.findAll().get(1);
        Category databaseCategory = categoryRepository.findAll().get(2);

        // Javaクイズ
        Quiz javaQuiz1 = new Quiz();
        javaQuiz1.setQuestion("Javaでクラスを継承するために使用するキーワードはどれですか？");
        javaQuiz1.setExplanation("Javaでクラスを継承するには「extends」キーワードを使用します。");
        javaQuiz1.setCategory(javaCategory);
        quizRepository.save(javaQuiz1);

        choiceRepository.saveAll(Arrays.asList(
            new Choice(javaQuiz1, "implements", false),
            new Choice(javaQuiz1, "extends", true),
            new Choice(javaQuiz1, "inherits", false),
            new Choice(javaQuiz1, "super", false)
        ));

        Quiz javaQuiz2 = new Quiz();
        javaQuiz2.setQuestion("Javaの基本データ型はどれですか？（複数選択）");
        javaQuiz2.setExplanation("int、boolean、doubleは基本データ型です。Stringは参照型です。");
        javaQuiz2.setCategory(javaCategory);
        quizRepository.save(javaQuiz2);

        choiceRepository.saveAll(Arrays.asList(
            new Choice(javaQuiz2, "int", true),
            new Choice(javaQuiz2, "String", false),
            new Choice(javaQuiz2, "boolean", true),
            new Choice(javaQuiz2, "double", true)
        ));

        // Spring Bootクイズ
        Quiz springQuiz1 = new Quiz();
        springQuiz1.setQuestion("Spring BootでREST APIを作成する際に使用するアノテーションはどれですか？");
        springQuiz1.setExplanation("@RestControllerアノテーションを使用してREST APIコントローラーを作成します。");
        springQuiz1.setCategory(springCategory);
        quizRepository.save(springQuiz1);

        choiceRepository.saveAll(Arrays.asList(
            new Choice(springQuiz1, "@Controller", false),
            new Choice(springQuiz1, "@RestController", true),
            new Choice(springQuiz1, "@Service", false),
            new Choice(springQuiz1, "@Repository", false)
        ));

        // データベースクイズ
        Quiz dbQuiz1 = new Quiz();
        dbQuiz1.setQuestion("SQLでデータを取得するために使用するコマンドはどれですか？");
        dbQuiz1.setExplanation("SELECTコマンドを使用してデータベースからデータを取得します。");
        dbQuiz1.setCategory(databaseCategory);
        quizRepository.save(dbQuiz1);

        choiceRepository.saveAll(Arrays.asList(
            new Choice(dbQuiz1, "INSERT", false),
            new Choice(dbQuiz1, "UPDATE", false),
            new Choice(dbQuiz1, "SELECT", true),
            new Choice(dbQuiz1, "DELETE", false)
        ));

        System.out.println("クイズの初期データを投入しました");
    }
}
