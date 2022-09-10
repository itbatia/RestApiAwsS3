package com.itbatia.app.dto;

import com.itbatia.app.model.Action;
import lombok.Data;

@Data
public class EventDTO {

    private Long fileId;
    private String location;
    private Action action;
    private String date;
}
