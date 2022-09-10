package com.itbatia.app.rest;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.itbatia.app.dto.UserDTO;
import com.itbatia.app.dto.UserToUpdateDTO;
import com.itbatia.app.dto.UserToUpdateYourselfDTO;
import com.itbatia.app.dto.UsersResponse;
import com.itbatia.app.model.User;
import com.itbatia.app.service.UserService;
import com.itbatia.app.util.validators.UserValidator;
import com.itbatia.app.util.exceptions.UserNotUpdatedException;
import com.itbatia.app.util.mappers.UserMapper;
import com.itbatia.app.util.exceptions.ErrorResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.itbatia.app.util.Utility.getUserFromContext;
import static com.itbatia.app.util.exceptions.ErrorsUtil.returnErrorsToClient;

@RestController
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UserRestControllerV1 {

    private final UserService userService;
    private final UserMapper userMapper;
    private final ModelMapper modelMapper;
    private final UserValidator userValidator;

    @Autowired
    public UserRestControllerV1(UserService userService, UserMapper userMapper,
                                ModelMapper modelMapper, UserValidator userValidator) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.modelMapper = modelMapper;
        this.userValidator = userValidator;
    }

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
        userToUpdate.setId(getUserFromContext().getId());

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

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(UsernameNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(UserNotUpdatedException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(AmazonS3Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private <T> User convertToUser(T t) {
        return modelMapper.map(t, User.class);
    }
}
