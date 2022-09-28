package com.itbatia.app.dto.response;

import com.itbatia.app.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UsersResponse {
    List<UserDTO> users;
}
