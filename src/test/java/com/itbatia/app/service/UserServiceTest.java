package com.itbatia.app.service;

import com.itbatia.app.model.*;
import com.itbatia.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private PasswordEncoder passwordEncoderMock;

    private User getExpectedUser() {
        return User.builder()
                .firstName("Ivan")
                .lastName("Ivanov")
                .role(Role.ROLE_USER)
                .password("12345")
                .build();
    }

    private User getUserToEnter() {
        return User.builder()
                .build();
    }

    private User getUserToExit() {
        return User.builder()
                .id(1L)
                .build();
    }

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("12345");
    }

    @Test
    public void register() {
        when(userRepositoryMock.save(any(User.class))).thenReturn(any(User.class));
        when(userRepositoryMock.save(getUserToEnter())).thenReturn(getUserToExit());
        when(passwordEncoderMock.encode(anyString())).thenReturn(anyString());
        userService.register(testUser);

        verify(userRepositoryMock).save(testUser);
        verify(userRepositoryMock, times(1)).save(testUser);
        assertEquals(testUser.getRole(), Role.ROLE_USER);
        assertNotEquals(testUser.getPassword(), "12345");
    }

    @Test
    public void getById() {
        when(userRepositoryMock.findById(eq(1L))).thenReturn(Optional.of(getExpectedUser()));
        User actualUser = userService.getById(1L);

        verify(userRepositoryMock).findById(1L);
        verify(userRepositoryMock, never()).findById(2L);
        assertEquals(getExpectedUser(), actualUser);
    }

    @Test
    public void getAllUsers() {
        userService.getAllUsers();

        verify(userRepositoryMock).findAll();
        verify(userRepositoryMock, times(1)).findAll();
    }

    @Test
    public void update() {
        when(userRepositoryMock.findById(eq(1L))).thenReturn(Optional.of(testUser));
        userService.update(1L, getExpectedUser());

        verify(userRepositoryMock).findById(1L);
        assertEquals(getExpectedUser().getFirstName(), testUser.getFirstName());
        assertEquals(getExpectedUser().getLastName(), testUser.getLastName());
        assertEquals(getExpectedUser().getRole(), testUser.getRole());
    }

    @Test
    public void updateYourself() {
        when(userRepositoryMock.findById(testUser.getId())).thenReturn(Optional.of(getExpectedUser()));
        when(passwordEncoderMock.encode("12345")).thenReturn(anyString());
        userService.updateYourself(testUser);

        verify(userRepositoryMock).findById(testUser.getId());
        assertEquals(getExpectedUser().getRole(), testUser.getRole());
        assertNotNull(testUser.getPassword());
        assertNotEquals(testUser.getPassword(), "12345");
    }

    @Test
    public void delete() {
        when(userRepositoryMock.findById(anyLong())).thenReturn(Optional.of(getExpectedUser()));
        userService.delete(1L);

        verify(userRepositoryMock).findById(1L);
        verify(userRepositoryMock, never()).findById(2L);
        verify(userRepositoryMock).deleteById(1L);
        verify(userRepositoryMock, never()).deleteById(2L);
    }
}