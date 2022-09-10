package com.itbatia.app.util.validators;

import com.itbatia.app.model.User;
import com.itbatia.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

@Component
public class UserValidator implements Validator {

    private final UserService userService;

    @Autowired
    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;

        // Для регистрации нового пользователя
        if (user.getId() == null && userService.findByUsername(user.getUsername()).isPresent()) {
            errors.rejectValue("username", "", "Человек с таким логином уже зарегистрирован!");
            return;
        }
        // Для изменения данных существующего пользователя
        if (!(user.getId() == null) && userService.findByUsername(user.getUsername()).isPresent()) {
            if (!Objects.equals(
                    userService.findByUsername(user.getUsername()).get().getId(),
                    user.getId())) {
                errors.rejectValue("username", "", "Человек с таким логином уже зарегистрирован!");
            }
        }
    }
}
