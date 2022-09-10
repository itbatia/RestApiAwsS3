package com.itbatia.app.rest;

import com.itbatia.app.dto.EventDTO;
import com.itbatia.app.dto.EventsResponse;
import com.itbatia.app.service.EventService;
import com.itbatia.app.util.exceptions.ErrorResponse;
import com.itbatia.app.util.mappers.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/events")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class EventRestControllerV1 {

    private final EventService eventService;
    private final EventMapper eventMapper;

    @Autowired
    public EventRestControllerV1(EventService eventService, EventMapper eventMapper) {
        this.eventService = eventService;
        this.eventMapper = eventMapper;
    }

    @GetMapping("/{id}")
    public EventsResponse getUserEvents(@PathVariable("id") long id) {
        List<EventDTO> events = eventService.getUserEvents(id).stream().map(eventMapper::convertToEventDTO).toList();
        return new EventsResponse(events);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
