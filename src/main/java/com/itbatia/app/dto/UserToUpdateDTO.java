package com.itbatia.app.dto;

import com.itbatia.app.model.Role;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class UserToUpdateDTO {

    @NotEmpty(message = "Введите имя")
    @Size(min = 2, max = 30, message = "Имя должно быть от 2 до 30 символов длиной")
    @NotBlank(message = "Имя не может состоять только из пробелов")
    private String firstName;

    @NotEmpty(message = "Введите фамилию")
    @Size(min = 2, max = 30, message = "Фамилия должна быть от 2 до 30 символов длиной")
    @NotBlank(message = "Фамилия не может состоять только из пробелов")
    private String lastName;

    private Role role;
}