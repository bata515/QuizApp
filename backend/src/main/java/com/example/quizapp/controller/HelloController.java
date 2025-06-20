package com.example.quizapp.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController  // JSONや文字列を返すAPI用のコントローラ
public class HelloController {

    @GetMapping("/")
    public String hello() {
        return "Hello World";
    }
}