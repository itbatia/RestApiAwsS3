package com.itbatia.app.service;

import com.itbatia.app.model.*;
import com.itbatia.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.ROLE_USER);
        User registeredUser = userRepository.save(user);

        log.info("IN register - User: {} successfully registered!", registeredUser);
    }

    public User getById(Long id) {
        User user = getUserFromDB(id, "getById");
        log.info("IN getById - User: '{}' found by id={}", user.getUsername(), id);
        return user;
    }

    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        log.info("IN getAllUsers - {} users found", users.size());
        return users;
    }

    @Transactional
    public void update(Long id, User userToUpdate) {
        User user = getUserFromDB(id, "update");
        user.setFirstName(userToUpdate.getFirstName());
        user.setLastName(userToUpdate.getLastName());
        user.setRole(userToUpdate.getRole());

        log.info("IN update - User: '{}' with id={} successfully updated", user.getUsername(), id);
    }

    @Transactional
    public void updateYourself(User userToUpdate) {
        User user = userRepository.findById(userToUpdate.getId()).get();

        userToUpdate.setRole(user.getRole());
        userToUpdate.setPassword(passwordEncoder.encode(userToUpdate.getPassword()));

        userRepository.save(userToUpdate);

        log.info("IN updateYourself - User: '{}' with id={} successfully updated!",
                userToUpdate.getUsername(), user.getId());
    }

    @Transactional
    public void delete(Long id) {
        User user = getUserFromDB(id, "delete");
        userRepository.deleteById(id);

        log.info("IN delete - User '{}' with id={} successfully deleted!", user.getUsername(), id);
    }

    protected User getUserFromDB(Long id, String methodName) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            log.warn("IN {} - User not found by id={}", methodName, id);
            throw new UsernameNotFoundException("User doesn't exists");
        }
        return user;
    }
}
