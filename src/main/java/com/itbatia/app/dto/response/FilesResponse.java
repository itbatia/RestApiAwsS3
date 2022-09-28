package com.itbatia.app.dto.response;

import com.itbatia.app.dto.FileDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FilesResponse {
    private List<FileDTO> files;
}
