package com.example.quizapp.application.users.bodymodel;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CreateUserBodyModel {
    private String familyName;
    private String familyNameRuby;
    private String firstName;
    private String firstNameRuby;
    private String mail;
    private String password;
}
