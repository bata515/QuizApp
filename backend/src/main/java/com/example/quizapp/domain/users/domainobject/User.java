package com.example.quizapp.domain.users.domainobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
public class User {
    private final UUID id;
    private final String familyName;
    private final String familyNameRuby;
    private final String firstName;
    private final String firstNameRuby;

    private final String mail;

    private final String password;

    private final LocalDateTime created;

    private final LocalDateTime modified;


    public User(UUID id, String familyName, String familyNameRuby, String firstName, String firstNameRuby, String mail, String password, LocalDateTime created, LocalDateTime modified) {
        this.id = id;
        this.familyName = familyName;
        this.familyNameRuby = familyNameRuby;
        this.firstName = firstName;
        this.firstNameRuby = firstNameRuby;
        this.mail = mail;
        this.password = password;
        this.created = created;
        this.modified = modified;
    }

    public static User createUser(UUID id, String familyName, String familyNameRuby, String firstName, String firstNameRuby,
                                  String mail, String password, LocalDateTime created, LocalDateTime modified) {
        return new User(
                id, familyName, familyNameRuby, firstName, firstNameRuby, mail, password, created, modified
        );
    }
}
