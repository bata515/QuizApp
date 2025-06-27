package com.example.quizapp.application.users.service;

import com.example.quizapp.application.users.bodymodel.CreateUserBodyModel;
import com.example.quizapp.domain.users.domainobject.User;
import com.example.quizapp.domain.users.irepositoryinterface.IUserRepositoryInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CreateUserService {

    @Autowired
    private IUserRepositoryInterface userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(CreateUserBodyModel createUserBodyModel) {
        // メールが既に存在するかを確認
        if (userRepository.findByMail(createUserBodyModel.getMail()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }
        User user = new User(
                UUID.randomUUID(),
                createUserBodyModel.getFamilyName(),
                createUserBodyModel.getFamilyNameRuby(),
                createUserBodyModel.getFirstName(),
                createUserBodyModel.getFirstNameRuby(),
                createUserBodyModel.getMail(),
                // パスワードを暗号化して設定
                passwordEncoder.encode(createUserBodyModel.getPassword()),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        return userRepository.createUser(user);
    }
}
