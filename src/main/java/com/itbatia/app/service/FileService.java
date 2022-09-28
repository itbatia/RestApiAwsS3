package com.itbatia.app.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.itbatia.app.model.*;
import com.itbatia.app.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FileService {

    @Value("${aws_bucket_name}")
    private String BUCKET_NAME;

    @Value("${aws_location_in_bucket}")
    private String LOCATION_IN_BUCKET;

    private final FileRepository fileRepository;
    private final AmazonS3 s3client;
    private final EventService eventService;

    public List<FileEntity> getAllFiles() {
        List<FileEntity> files = fileRepository.findAllByStatus(Status.ACTIVE);
        log.info("IN getAllFiles - Getting information about all storage files. Count files: {}", files.size());
        return files;
    }

    public FileEntity getById(Long id) {
        FileEntity file = getFileFromDB(id, "getById");

        log.info("IN getById - File: '{}' found by id: {}", file.getFileName(), id);

        return file;
    }

    @Transactional
    public FileEntity uploadFile(String fileName, byte[] array) {
        InputStream inputStream = new ByteArrayInputStream(array);

        CompletableFuture.runAsync(() ->
                s3client.putObject(BUCKET_NAME, LOCATION_IN_BUCKET + fileName, inputStream, new ObjectMetadata())
        );
        log.info("IN uploadFile - File '{}' successfully uploaded to S3!", fileName);

        FileEntity uploadedFile = saveFileToDB(fileName);
        eventService.createEvent(uploadedFile, Action.CREATION);

        return uploadedFile;
    }

    @Transactional
    public void deleteFile(Long id) {
        FileEntity fileToDelete = getFileFromDB(id, "deleteFile");

        String bucketName = fileToDelete.getBucketName();
        String locationInBucket = getLocationInBucket(fileToDelete);

        s3client.deleteObject(bucketName, locationInBucket);
        fileToDelete.setStatus(Status.REMOVED);
        log.info("IN deleteFile - File with id={} successfully deleted!", id);

        eventService.createEvent(fileToDelete, Action.DELETION);
    }

    @SneakyThrows
    private FileEntity getFileFromDB(Long id, String methodName) {
        FileEntity file = fileRepository.findByIdAndStatus(id, Status.ACTIVE).orElse(null);
        if (file == null) {
            log.warn("IN {} - File not found by id {}", methodName, id);
            throw new FileNotFoundException("File doesn't exists");
        }
        return file;
    }

    private String getLocationInBucket(FileEntity file) {
        String location = file.getLocation();
        int index = location.lastIndexOf("com/");
        return location.substring(index + 4);
    }

    private FileEntity saveFileToDB(String fileName) {
        String location = "https://" + BUCKET_NAME + ".s3.amazonaws.com/" + LOCATION_IN_BUCKET + fileName;

        FileEntity file = new FileEntity();

        file.setBucketName(BUCKET_NAME);
        file.setFileName(fileName);
        file.setLocation(location);
        file.setStatus(Status.ACTIVE);

        return fileRepository.save(file);
    }
}
