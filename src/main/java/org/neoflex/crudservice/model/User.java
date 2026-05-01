package org.neoflex.crudservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("Идентификатор пользователя")
    @Column(name = "user_id")
    private Long userId;

    @Comment("Фамилия пользователя")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Comment("Имя пользователя")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Comment("Логин пользователя")
    @Column(name = "login", nullable = false)
    private String login;

    @Comment("Дата рождения пользователя")
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;
}
