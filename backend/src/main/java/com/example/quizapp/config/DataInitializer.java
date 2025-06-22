package com.example.quizapp.config;

import com.example.quizapp.entity.Admin;
import com.example.quizapp.entity.Category;
import com.example.quizapp.service.AdminService;
import com.example.quizapp.service.CategoryService;
import com.example.quizapp.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private QuizService quizService;
    
    @Override
    public void run(String... args) throws Exception {
        // 管理者アカウントの初期化
        if (!adminService.existsByUsername("admin")) {
            adminService.createAdmin("admin", "password");
            System.out.println("初期管理者アカウントを作成しました: admin / password");
        }
        
        // サンプルカテゴリーの作成
        if (categoryService.getAllCategories().isEmpty()) {
            Category javaCategory = categoryService.createCategory("Java", "Java言語に関するクイズ");
            Category springCategory = categoryService.createCategory("Spring Boot", "Spring Bootフレームワークに関するクイズ");
            Category generalCategory = categoryService.createCategory("一般常識", "一般的な知識に関するクイズ");
            
            // サンプルクイズの作成
            createSampleQuizzes(javaCategory, springCategory, generalCategory);
            
            System.out.println("サンプルデータを作成しました");
        }
    }
    
    private void createSampleQuizzes(Category javaCategory, Category springCategory, Category generalCategory) {
        try {
            // Javaクイズ
            quizService.createQuiz(
                "Javaでクラスを継承するために使用するキーワードは何ですか？",
                "Javaでは「extends」キーワードを使用してクラスを継承します。",
                javaCategory.getId(),
                Arrays.asList("extends", "implements", "inherit", "super"),
                Arrays.asList(true, false, false, false)
            );
            
            quizService.createQuiz(
                "Javaの基本データ型として正しいものを全て選択してください。",
                "int、boolean、doubleはJavaの基本データ型です。Stringは参照型です。",
                javaCategory.getId(),
                Arrays.asList("int", "String", "boolean", "double"),
                Arrays.asList(true, false, true, true)
            );
            
            // Spring Bootクイズ
            quizService.createQuiz(
                "Spring Bootアプリケーションのメインクラスに付けるアノテーションは何ですか？",
                "@SpringBootApplicationアノテーションは、@Configuration、@EnableAutoConfiguration、@ComponentScanを含む複合アノテーションです。",
                springCategory.getId(),
                Arrays.asList("@SpringBootApplication", "@Application", "@SpringBoot", "@Main"),
                Arrays.asList(true, false, false, false)
            );
            
            quizService.createQuiz(
                "Spring BootでRESTコントローラーを作成する際に使用するアノテーションを全て選択してください。",
                "@RestControllerと@Controllerの両方がRESTコントローラーの作成に使用できます。@RestControllerは@Controller + @ResponseBodyの組み合わせです。",
                springCategory.getId(),
                Arrays.asList("@RestController", "@Controller", "@Service", "@Repository"),
                Arrays.asList(true, true, false, false)
            );
            
            // 一般常識クイズ
            quizService.createQuiz(
                "日本の首都はどこですか？",
                "日本の首都は東京です。",
                generalCategory.getId(),
                Arrays.asList("東京", "大阪", "京都", "名古屋"),
                Arrays.asList(true, false, false, false)
            );
            
            quizService.createQuiz(
                "以下の中で惑星として正しいものを全て選択してください。",
                "火星と金星は太陽系の惑星です。月は地球の衛星、太陽は恒星です。",
                generalCategory.getId(),
                Arrays.asList("火星", "月", "金星", "太陽"),
                Arrays.asList(true, false, true, false)
            );
            
        } catch (Exception e) {
            System.err.println("サンプルクイズの作成中にエラーが発生しました: " + e.getMessage());
        }
    }
}
