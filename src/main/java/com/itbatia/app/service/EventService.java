package com.itbatia.app.service;

import com.itbatia.app.model.*;
import com.itbatia.app.repository.EventRepository;
import com.itbatia.app.util.Utility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserService userService;
    private final Utility utility;

    public void createEvent(FileEntity file, Action action) {
        Event event = new Event();
        event.setAction(action);
        event.setDate(LocalDateTime.now());
        event.setUserId(utility.getUserFromContext().getId());
        event.setFile(file);
        eventRepository.save(event);
    }

    public List<Event> getUserEvents(long id) {
        userService.getUserFromDB(id, "getUserEvents");
        return eventRepository.findAllByUserId(id);
    }
}
