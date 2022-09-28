package com.itbatia.app.rest;

import com.itbatia.app.dto.*;
import com.itbatia.app.dto.response.EventsResponse;
import com.itbatia.app.service.EventService;
import com.itbatia.app.util.mappers.EventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/events")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class EventRestControllerV1 {

    private final EventService eventService;
    private final EventMapper eventMapper;

    @GetMapping("/{id}")
    public EventsResponse getUserEvents(@PathVariable("id") long id) {
        List<EventDTO> events = eventService.getUserEvents(id).stream().map(eventMapper::convertToEventDTO).toList();
        return new EventsResponse(events);
    }
}
