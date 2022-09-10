package com.itbatia.app.dto;

import com.itbatia.app.model.Role;
import lombok.Data;

@Data
public class UserDTO {

    private String username;
    private String fullName;
    private Role role;
    private Integer countEvents;
}
