package com.itbatia.app.dto.response;

import com.itbatia.app.dto.EventDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EventsResponse {
    private List<EventDTO> events;
}
