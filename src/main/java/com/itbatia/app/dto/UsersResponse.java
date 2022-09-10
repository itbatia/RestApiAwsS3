package com.itbatia.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UsersResponse {
    List<UserDTO> users;
}
