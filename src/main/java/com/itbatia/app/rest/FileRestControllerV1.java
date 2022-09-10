package com.itbatia.app.rest;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.itbatia.app.dto.*;
import com.itbatia.app.model.File;
import com.itbatia.app.service.FileService;
import com.itbatia.app.util.validators.FileValidator;
import com.itbatia.app.util.exceptions.ErrorResponse;
import com.itbatia.app.util.exceptions.FileNotUploadedException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.itbatia.app.util.exceptions.ErrorsUtil.returnErrorsToClient;

@Slf4j
@RestController
@RequestMapping("/api/v1/files")
public class FileRestControllerV1 {

    private final FileService fileService;
    private final ModelMapper modelMapper;
    private final FileValidator fileValidator;

    @Autowired
    public FileRestControllerV1(FileService fileService, ModelMapper modelMapper,
                                FileValidator fileValidator) {
        this.fileService = fileService;
        this.modelMapper = modelMapper;
        this.fileValidator = fileValidator;
    }

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
    public FileDTO getById(@PathVariable("id") long id) throws FileNotFoundException {
        return convertToFileDTO(fileService.getById(id));
    }

    // Скачать файл на свой ПК по id:
    @GetMapping("/download/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> downloadFileFromS3(@PathVariable("id") long id,
                                   @RequestBody FileToDownloadDTO fileDTO) throws IOException {
        fileService.downloadFile(id, fileDTO.getPathToDestinationFile());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    // Получить список файлов бакета по bucketName:
    @GetMapping("/from_bucket")
    @PreAuthorize("hasAnyRole('ROLE_MODERATOR', 'ROLE_ADMIN')")
    public FilesResponse getByBucketName(@RequestBody BucketDTO bucketDTO) {
        String bucketName = bucketDTO.getBucketName();
        List<FileDTO> files = fileService.getByBucketName(bucketName).stream().map(this::convertToFileDTO).toList();
        return new FilesResponse(files);
    }

    // Загрузить файл в S3:
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> uploadFileToS3(@RequestBody @Valid FileToUploadDTO fileDTO,
                                            BindingResult bindingResult) {
        File file = convertToFile(fileDTO);

        fileValidator.validate(file, bindingResult);
        if(bindingResult.hasErrors()){
            throw new FileNotUploadedException(returnErrorsToClient(bindingResult));
        }

        File uploadedFile = fileService.uploadFile(file);

        Map<Object, Object> response = new HashMap<>();
        response.put("id", uploadedFile.getId());
        response.put("location", uploadedFile.getLocation());

        return ResponseEntity.ok(response);
    }

    // Удалить файл из S3:
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MODERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> deleteFileFromS3(@PathVariable("id") Long id) throws FileNotFoundException {
        fileService.deleteFile(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(FileNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(FileNotUploadedException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(AmazonS3Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(IOException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.EXPECTATION_FAILED);
    }

    private FileDTO convertToFileDTO(File file) {
        return modelMapper.map(file, FileDTO.class);
    }

    private File convertToFile(FileToUploadDTO fileDTO) {
        return modelMapper.map(fileDTO, File.class);
    }
}
