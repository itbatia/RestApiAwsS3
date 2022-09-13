package com.itbatia.app.service;

import com.itbatia.app.model.*;
import com.itbatia.app.repository.EventRepository;
import com.itbatia.app.util.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final Utility utility;

    public void createEvent(File file, Action action) {
        Event event = new Event();
        event.setAction(action);
        event.setDate(LocalDateTime.now());
        event.setUserId(utility.getUserFromContext().getId());
        event.setFile(file);
        eventRepository.save(event);
    }

    public List<Event> getUserEvents(long id) {
        return eventRepository.findAllByUserId(id);
    }
}
