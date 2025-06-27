package com.example.quizapp.application.users.viewmodel;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@ToString
@EqualsAndHashCode
@Getter
public class UserViewModel {
    private final UUID id;
    private final String familyName;
    private final String firstName;
    private final String mail;

    public UserViewModel(UUID id, String familyName, String firstName, String mail) {
        this.id = id;
        this.familyName = familyName;
        this.firstName = firstName;
        this.mail = mail;
    }

    public static UserViewModel adaptToUserVewModel(UUID id,
                                                    String familyName, String firstName, String mail) {
        return new UserViewModel(
                id, familyName, firstName, mail
        );
    }
}
