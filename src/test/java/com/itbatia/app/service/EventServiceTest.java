package com.itbatia.app.service;

import com.itbatia.app.model.*;
import com.itbatia.app.repository.EventRepository;
import com.itbatia.app.util.Utility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepositoryMock;
    @Mock
    private Utility utility;

    private User getUser() {
        return User.builder()
                .id(1L)
                .build();
    }

    private Event getExpectedEvent() {
        return Event.builder()
                .file(file)
                .date(date)
                .action(Action.CREATION)
                .userId(1L)
                .build();
    }

    private LocalDateTime date;
    private File file;
    private Action action;

    @BeforeEach
    public void setUp() {
        date = LocalDateTime.now();
        file = new File();
        action = Action.CREATION;
    }

    @Test
    public void createEvent() {
        when(utility.getUserFromContext()).thenReturn(getUser());
        mockStatic(LocalDateTime.class).when(LocalDateTime::now).thenReturn(date);

        eventService.createEvent(file, action);

        verify(eventRepositoryMock).save(getExpectedEvent());
        verify(eventRepositoryMock, times(1)).save(getExpectedEvent());
    }

    @Test
    public void getUserEvents() {
        when(eventRepositoryMock.findAllByUserId(anyLong())).thenReturn(anyList());
        eventService.getUserEvents(1L);

        verify(eventRepositoryMock).findAllByUserId(anyLong());
        verify(eventRepositoryMock, never()).findAllByUserId(eq(2L));
    }
}