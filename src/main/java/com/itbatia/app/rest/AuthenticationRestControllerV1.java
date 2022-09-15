package com.itbatia.app.rest;

import com.itbatia.app.dto.AuthenticationDTO;
import com.itbatia.app.dto.RegistrationDTO;
import com.itbatia.app.model.*;
import com.itbatia.app.security.JwtTokenProvider;
import com.itbatia.app.security.UserDetailsImpl;
import com.itbatia.app.service.UserService;
import com.itbatia.app.util.validators.UserValidator;
import com.itbatia.app.util.exceptions.UserNotRegisteredException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationRestControllerV1 {

    private final JwtTokenProvider jwtTokenProvider;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserValidator userValidator;

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
        log.info("IN performRegistration - User with username '{}' registered!", user.getUsername());

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
        User user = userService.findByUsername(userDetails.getUsername()).get();

        String token = jwtTokenProvider.generateToken(request.getUsername(), user.getLastName(), user.getRole().name());
        log.info("IN authenticate - User '{}' {} authenticated!", user.getUsername(), user.getRole());

        Map<Object, Object> response = new HashMap<>();
        response.put("username", request.getUsername());
        response.put("jwt-token", token);
        return ResponseEntity.ok(response);
    }
}
