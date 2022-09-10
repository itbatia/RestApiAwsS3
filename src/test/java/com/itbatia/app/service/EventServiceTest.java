package com.itbatia.app.service;

import com.itbatia.app.model.*;
import com.itbatia.app.repository.EventRepository;
import com.itbatia.app.util.Utility;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EventServiceTest {

    @Autowired
    private EventService eventService;

    @MockBean
    private EventRepository eventRepositoryMock;

    private Event event;
    private Event expectedEvent;
    private File file;
    private Action action;
    private User user;
    private LocalDateTime date;

    @Autowired
    @Before
    public void setUp() {
        event = new Event();

        user = new User();
        user.setId(1L);

        file = new File();
        action = Action.CREATION;
        date = LocalDateTime.now();

        expectedEvent = new Event();
        expectedEvent.setAction(action);
        expectedEvent.setDate(date);
        expectedEvent.setFile(file);
        expectedEvent.setUserId(user.getId());
    }

    @Test
    public void createEvent() {
        when(eventRepositoryMock.save(event)).thenReturn(expectedEvent);

        mockStatic(Utility.class).when(Utility::getUserFromContext).thenReturn(user);
        mockStatic(LocalDateTime.class).when(LocalDateTime::now).thenReturn(date);

        eventService.createEvent(file, action);

        verify(eventRepositoryMock).save(expectedEvent);
        verify(eventRepositoryMock, times(1)).save(expectedEvent);

        // 1) проверяем, что с методом save() было взаимодействие
        // 2) проверяем, что метод save() был вызван 1 раз
    }

    @Test
    public void getUserEvents() {
        when(eventRepositoryMock.findAllByUserId(1L)).thenReturn(new ArrayList<>());
        eventService.getUserEvents(1L);

        verify(eventRepositoryMock).findAllByUserId(1L);
        verify(eventRepositoryMock, never()).findAllByUserId(2L);

        // 1) проверяем, что с методом findAllByUserId() было взаимодействие
        // 2) проверяем, что метод findAllByUserId с параметром "2" не вызывался
    }
}