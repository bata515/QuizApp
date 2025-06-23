package com.example.quizapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * メインページ用のコントローラー
 * 静的ファイルの配信を担当
 */
@Controller
public class HomeController {

    /**
     * メインページを表示
     * @return String テンプレート名
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 管理者ページを表示
     * @return String テンプレート名
     */
    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }
}
