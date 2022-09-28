package com.itbatia.app.rest;

import com.itbatia.app.dto.*;
import com.itbatia.app.dto.response.UsersResponse;
import com.itbatia.app.model.User;
import com.itbatia.app.service.UserService;
import com.itbatia.app.util.Utility;
import com.itbatia.app.util.validators.UserValidator;
import com.itbatia.app.util.exceptions.UserNotUpdatedException;
import com.itbatia.app.util.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.itbatia.app.util.exceptions.ErrorsUtil.returnErrorsToClient;

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class UserRestControllerV1 {

    private final UserService userService;
    private final UserMapper userMapper;
    private final ModelMapper modelMapper;
    private final UserValidator userValidator;
    private final Utility utility;

    @GetMapping
    public UsersResponse getAll() {
        List<UserDTO> users = userService.getAllUsers().stream().map(userMapper::convertToUserDTO).toList();
        return new UsersResponse(users);
    }

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable("id") Long id) throws UsernameNotFoundException {
        return userMapper.convertToUserDTO(userService.getById(id));
    }

    //Админ обновляет пользователей
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") long id, @RequestBody @Valid UserToUpdateDTO userDTO,
                                    BindingResult bindingResult) {
        User userToUpdate = convertToUser(userDTO);

        if (bindingResult.hasErrors()) {
            throw new UserNotUpdatedException(returnErrorsToClient(bindingResult));
        }

        userService.update(id, userToUpdate);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    //Пользователь сам обновляет свои данные
    @PatchMapping("/update/yourself")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> updateYourself(@RequestBody @Valid UserToUpdateYourselfDTO userDTO,
                                            BindingResult bindingResult) {

        User userToUpdate = convertToUser(userDTO);
        userToUpdate.setId(utility.getUserFromContext().getId());

        userValidator.validate(userToUpdate, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new UserNotUpdatedException(returnErrorsToClient(bindingResult));
        }

        userService.updateYourself(userToUpdate);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        userService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private <T> User convertToUser(T t) {
        return modelMapper.map(t, User.class);
    }
}
