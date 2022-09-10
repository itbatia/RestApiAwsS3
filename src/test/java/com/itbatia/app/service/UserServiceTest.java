package com.itbatia.app.service;

import com.itbatia.app.model.Role;
import com.itbatia.app.model.User;
import com.itbatia.app.repository.UserRepository;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepositoryMock;

    private User testUser;
    private User testUser2;
    private User expectedUser;

    @Autowired
    @Before
    public void setUp() {
        testUser = new User();
        testUser.setPassword("12345");

        testUser2 = new User();
        testUser2.setFirstName("Ivan");
        testUser2.setLastName("Ivanov");
        testUser2.setRole(Role.ROLE_USER);
        testUser2.setPassword("12345");

        expectedUser = new User();
        expectedUser.setId(1L);
    }

    @Test
    public void register() {
        when(userRepositoryMock.save(testUser)).thenReturn(expectedUser);
        userService.register(testUser);

        verify(userRepositoryMock).save(testUser);
        verify(userRepositoryMock, times(1)).save(testUser);
        assertEquals(testUser.getRole(), Role.ROLE_USER);
        assertNotEquals(testUser.getPassword(), "12345");

        // 1) проверяем, что с методом save() было взаимодействие
        // 2) проверяем, что метод save() был вызван 1 раз
        // 3) проверяем, что тестируемому юзеру назначается роль ROLE_USER
        // 4) проверяем, что пароль закодировался, а значит изменился
    }

    @Test
    public void getById() {
        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(expectedUser));
        User actualUser = userService.getById(1L);

        verify(userRepositoryMock).findById(1L);
        verify(userRepositoryMock, never()).findById(2L);
        assertEquals(expectedUser, actualUser);

        // 1) проверяем, что с методом findById() было взаимодействие
        // 2) проверяем, что метод findById с параметром "2" не вызывался
        // 3) сравниваем ожидаемые и актуальные данные
    }

    @Test
    public void getAllUsers() {
        userService.getAllUsers();

        verify(userRepositoryMock).findAll();
        verify(userRepositoryMock, times(1)).findAll();

        // 1) проверяем, что с методом findAll() было взаимодействие
        // 2) проверяем, что метод findAll() был вызван 1 раз
    }

    @Test
    public void update() {
        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(expectedUser));
        userService.update(1L, testUser2);

        verify(userRepositoryMock).findById(1L);
        assertEquals(expectedUser.getFirstName(), testUser2.getFirstName());
        assertEquals(expectedUser.getLastName(), testUser2.getLastName());
        assertEquals(expectedUser.getRole(), testUser2.getRole());

        // 1) проверяем, что с методом findById() было взаимодействие
        // 2, 3, 4) сравниваем ожидаемые и актуальные данные
    }

    @Test
    public void updateYourself() {
        when(userRepositoryMock.findById(expectedUser.getId())).thenReturn(Optional.of(testUser2));
        userService.updateYourself(expectedUser);

        verify(userRepositoryMock).findById(1L);
        assertEquals(expectedUser.getRole(), testUser2.getRole());
        assertNotNull(expectedUser.getPassword());
        assertNotEquals(expectedUser.getPassword(), "12345");

        // 1) проверяем, что с методом findById() было взаимодействие
        // 2) проверяем, что роль была присвоена для обновления в БД
        // 3) проверяем, что пароль был присвоен для обновления в БД
        // 4) проверяем, что пароль закодировался, а значит изменился
    }

    @Test
    public void delete() {
        when(userRepositoryMock.findById(1L)).thenReturn(Optional.of(expectedUser));
        userService.delete(1L);

        verify(userRepositoryMock).findById(1L);
        verify(userRepositoryMock, never()).findById(2L);
        verify(userRepositoryMock).deleteById(1L);
        verify(userRepositoryMock, never()).deleteById(2L);

        // 1) проверяем, что с методом findById() было взаимодействие
        // 2) проверяем, что метод findById с параметром "2" не вызывался
        // 1) проверяем, что с методом deleteById() было взаимодействие
        // 2) проверяем, что метод deleteById с параметром "2" не вызывался
    }
}