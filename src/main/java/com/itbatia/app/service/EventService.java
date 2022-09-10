package com.itbatia.app.service;

import com.itbatia.app.model.*;
import com.itbatia.app.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.itbatia.app.util.Utility.getUserFromContext;

@Service
public class EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void createEvent(File file, Action action) {
        Event event = new Event();
        event.setAction(action);
        event.setDate(LocalDateTime.now());
        event.setUserId(getUserFromContext().getId());
        event.setFile(file);
        eventRepository.save(event);
    }

    public List<Event> getUserEvents(long id) {
        return eventRepository.findAllByUserId(id);
    }
}
