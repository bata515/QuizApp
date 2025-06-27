package com.example.quizapp.application.users.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";  // login.htmlを表示
    }

    @GetMapping("/register")
    public String showRegistrationPage() {
        return "/register.html";
    }
}
