package com.itbatia.app.util.mappers;

import com.itbatia.app.dto.EventDTO;
import com.itbatia.app.model.Event;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class EventMapper {

    public EventDTO convertToEventDTO(Event event){

        EventDTO eventDTO = new EventDTO();

        eventDTO.setFileId(event.getFile().getId());
        eventDTO.setLocation(event.getFile().getLocation());
        eventDTO.setAction(event.getAction());
        eventDTO.setDate(event.getDate().format(getFormatter()));

        return eventDTO;
    }

    public DateTimeFormatter getFormatter() {
        return DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm", Locale.ROOT);
    }
}
