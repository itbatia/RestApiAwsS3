package com.itbatia.app.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class UserToUpdateYourselfDTO {

    @NotEmpty(message = "Введите логин")
    @Size(min = 3, max = 50, message = "Логин должен быть от 3 до 50 символов длиной")
    @NotBlank(message = "Логин не может состоять только из пробелов")
    private String username;

    @NotEmpty(message = "Введите пароль")
    @Size(min = 4, message = "Длинна пароля не должна быть ниже 4 символов")
    @NotBlank(message = "Пароль не может состоять только из пробелов")
    private String password;

    @NotEmpty(message = "Введите имя")
    @Size(min = 2, max = 30, message = "Имя должно быть от 2 до 30 символов длиной")
    @NotBlank(message = "Имя не может состоять только из пробелов")
    private String firstName;

    @NotEmpty(message = "Введите фамилию")
    @Size(min = 2, max = 30, message = "Фамилия должна быть от 2 до 30 символов длиной")
    @NotBlank(message = "Фамилия не может состоять только из пробелов")
    private String lastName;
}
