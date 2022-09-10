package com.itbatia.app.rest;

import com.amazonaws.services.s3.AmazonS3;
import com.itbatia.app.dto.AuthenticationDTO;
import com.itbatia.app.dto.RegistrationDTO;
import com.itbatia.app.model.Role;
import com.itbatia.app.model.User;
import com.itbatia.app.security.JwtTokenProvider;
import com.itbatia.app.security.UserDetailsImpl;
import com.itbatia.app.service.UserService;
import com.itbatia.app.util.exceptions.ErrorResponse;
import com.itbatia.app.util.validators.UserValidator;
import com.itbatia.app.util.exceptions.UserNotRegisteredException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import static com.itbatia.app.util.exceptions.ErrorsUtil.returnErrorsToClient;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationRestControllerV1 {

    private final JwtTokenProvider jwtTokenProvider;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserValidator userValidator;

    @Autowired
    public AuthenticationRestControllerV1(JwtTokenProvider jwtTokenProvider, ModelMapper modelMapper,
                                          AuthenticationManager authenticationManager, UserService userService,
                                          UserValidator userValidator, AmazonS3 amazonS3) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.userValidator = userValidator;
    }

    @PostMapping("/registration")
    public ResponseEntity<?> performRegistration(@RequestBody @Valid RegistrationDTO registrationDTO,
                                                   BindingResult bindingResult) {
        User user = convertToUser(registrationDTO);

        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new UserNotRegisteredException(returnErrorsToClient(bindingResult));
        }

        userService.register(user);

        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getLastName(), Role.ROLE_USER.name());

        Map<Object, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("jwt-token", token);
        return ResponseEntity.ok(response);
    }

    private User convertToUser(RegistrationDTO registrationDTO) {
        return modelMapper.map(registrationDTO, User.class);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationDTO request) throws AuthenticationException {

        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

        Authentication authentication = authenticationManager.authenticate(authInputToken);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();

        String token = jwtTokenProvider.generateToken(request.getUsername(), user.getLastName(), user.getRole().name());

        Map<Object, Object> response = new HashMap<>();
        response.put("username", request.getUsername());
        response.put("jwt-token", token);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(AuthenticationException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage() + ". Incorrect username or password!");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(UserNotRegisteredException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
