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
import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private User getUser() {
        return User.builder()
                .firstName("Ivan")
                .lastName("Ivanov")
                .role(Role.ROLE_USER)
                .password("123456789")
                .build();
    }

    private User expectedUser;

    @BeforeEach
    public void setUp() {
        expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setPassword("123");
    }

    @Test
    public void register() {
        when(userRepositoryMock.save(any(User.class))).thenReturn(getUser());
        when(passwordEncoderMock.encode(anyString())).thenReturn(anyString());

        userService.register(expectedUser);

        verify(userRepositoryMock).save(expectedUser);
        verify(userRepositoryMock, times(1)).save(expectedUser);
        assertEquals(expectedUser.getRole(), Role.ROLE_USER);
        assertNotNull(expectedUser.getPassword());
        assertNotEquals(expectedUser.getPassword(), "123");
    }

    @Test
    public void getById() {
        when(userRepositoryMock.findById(anyLong())).thenReturn(Optional.of(getUser()));

        userService.getById(1L);

        verify(userRepositoryMock).findById(1L);
        verify(userRepositoryMock, times(1)).findById(1L);
        verify(userRepositoryMock, never()).findById(2L);
    }

    @Test
    public void getAllUsers() {
        userService.getAllUsers();

        verify(userRepositoryMock).findAll();
        verify(userRepositoryMock, times(1)).findAll();
    }

    @Test
    public void update() {
        when(userRepositoryMock.findById(anyLong())).thenReturn(Optional.of(expectedUser));

        userService.update(1L, getUser());

        verify(userRepositoryMock).findById(1L);
        verify(userRepositoryMock, times(1)).findById(1L);
        verify(userRepositoryMock, never()).findById(2L);
        assertEquals(expectedUser.getFirstName(), getUser().getFirstName());
        assertEquals(expectedUser.getLastName(), getUser().getLastName());
        assertEquals(expectedUser.getRole(), getUser().getRole());
    }

    @Test
    public void updateYourself() {
        when(userRepositoryMock.findById(anyLong())).thenReturn(Optional.of(getUser()));
        when(passwordEncoderMock.encode(anyString())).thenReturn(anyString());
        when(userRepositoryMock.save(expectedUser)).thenReturn(any(User.class));

        userService.updateYourself(expectedUser);

        verify(userRepositoryMock).findById(expectedUser.getId());
        assertEquals(expectedUser.getRole(), getUser().getRole());
        assertNotNull(expectedUser.getPassword());
        assertNotEquals(expectedUser.getPassword(), "123");
    }

    @Test
    public void delete() {
        when(userRepositoryMock.findById(anyLong())).thenReturn(Optional.of(getUser()));
        doNothing().when(userRepositoryMock).deleteById(anyLong());

        userService.delete(1L);

        verify(userRepositoryMock).findById(1L);
        verify(userRepositoryMock, times(1)).findById(1L);
        verify(userRepositoryMock, never()).findById(2L);
        verify(userRepositoryMock).deleteById(1L);
        verify(userRepositoryMock, times(1)).deleteById(1L);
        verify(userRepositoryMock, never()).deleteById(2L);
    }
}