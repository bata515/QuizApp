package com.example.quizapp.application.users.controller;

import com.example.quizapp.application.users.bodymodel.CreateUserBodyModel;
import com.example.quizapp.application.users.service.CreateUserService;
import com.example.quizapp.application.users.service.ReadUserService;
import com.example.quizapp.application.users.viewmodel.UserViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    ReadUserService readUserService;
    @Autowired
    CreateUserService createUserService;

    @GetMapping("/read/{id}")
    public UserViewModel findUserById(@PathVariable("id") UUID id) {
        return this.readUserService.readUserById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody CreateUserBodyModel createUserBodyModel) {
        try {
            createUserService.createUser(createUserBodyModel);
            return ResponseEntity.ok("User registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
