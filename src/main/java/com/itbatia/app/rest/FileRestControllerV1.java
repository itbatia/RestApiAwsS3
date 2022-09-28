package com.itbatia.app.rest;

import com.itbatia.app.dto.*;
import com.itbatia.app.dto.response.FilesResponse;
import com.itbatia.app.model.FileEntity;
import com.itbatia.app.service.FileService;
import com.itbatia.app.util.validators.FileValidator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileRestControllerV1 {

    private final FileService fileService;
    private final ModelMapper modelMapper;
    private final FileValidator fileValidator;

    // Получить все ссылки на скачивание всех файлов из S3:
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_MODERATOR', 'ROLE_ADMIN')")
    public FilesResponse getAll() {
        List<FileDTO> files = fileService.getAllFiles().stream().map(this::convertToFileDTO).toList();
        return new FilesResponse(files);
    }

    // Получить ссылку на скачивание файла по id:
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MODERATOR', 'ROLE_ADMIN')")
    public FileDTO getById(@PathVariable("id") long id) {
        return convertToFileDTO(fileService.getById(id));
    }

    // Загрузить файл в S3:
    @SneakyThrows
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> uploadFileToS3(@RequestParam("file") MultipartFile multipartFile){

        fileValidator.validate(multipartFile);

        String fileName = multipartFile.getOriginalFilename();
        FileEntity uploadedFile = fileService.uploadFile(fileName, multipartFile.getBytes());

        Map<Object, Object> response = new HashMap<>();
        response.put("id", uploadedFile.getId());
        response.put("location", uploadedFile.getLocation());

        return ResponseEntity.ok(response);
    }

    // Удалить файл из S3:
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> deleteFileFromS3(@PathVariable("id") Long id) {
        fileService.deleteFile(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private FileDTO convertToFileDTO(FileEntity file) {
        return modelMapper.map(file, FileDTO.class);
    }
}
