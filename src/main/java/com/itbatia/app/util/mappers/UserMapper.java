package com.itbatia.app.util.mappers;

import com.itbatia.app.dto.*;
import com.itbatia.app.model.*;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO convertToUserDTO(User user){

        UserDTO userDTO = new UserDTO();

        userDTO.setUsername(user.getUsername());
        userDTO.setFullName(user.getLastName() + " " + user.getFirstName());
        userDTO.setRole(user.getRole());
        userDTO.setCountEvents(user.getEvents().size());

        return userDTO;
    }
}
