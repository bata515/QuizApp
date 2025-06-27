package com.example.quizapp.infrastructer.users.dbmodel;

import com.example.quizapp.domain.users.domainobject.User;
import jakarta.persistence.*;
import lombok.*;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class UserDbModel implements Serializable {
    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "family_name")
    private String familyName;

    @Column(name = "family_name_ruby")
    private String familyNameRuby;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "first_name_ruby")
    private String firstNameRuby;

    @Column(name = "mail")
    private String mail;

    @Column(name = "password")
    private String password;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "modified")
    private LocalDateTime modified;

    public User adaptToUser() {
        return new User(
                this.id,
                this.familyName,
                this.familyNameRuby,
                this.firstName,
                this.firstNameRuby,
                mail, password, created, modified);
    }

    public static UserDbModel adaptToUserDbModel(User user) {
        return new UserDbModel(user.getId(), user.getFamilyName(), user.getFamilyNameRuby(), user.getFirstName(),
                user.getFirstNameRuby(), user.getMail(), user.getPassword(), user.getCreated(), user.getModified());
    }
}
