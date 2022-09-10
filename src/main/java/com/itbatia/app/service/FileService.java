package com.itbatia.app.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.itbatia.app.model.*;
import com.itbatia.app.repository.FileRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static com.itbatia.app.util.Utility.getFileNameFromLocationInBucket;

@Slf4j
@Service
@Transactional(readOnly = true)
public class FileService {

    private final FileRepository fileRepository;
    private final AmazonS3 s3client;
    private final EventService eventService;

    @Autowired
    public FileService(FileRepository fileRepository, AmazonS3 s3client, EventService eventService) {
        this.fileRepository = fileRepository;
        this.s3client = s3client;
        this.eventService = eventService;
    }

    public List<File> getAllFiles() {
        List<File> files = fileRepository.findAllByStatus(Status.ACTIVE);
        log.info("IN getAllFiles - Getting information about all storage files. Count files: {}", files.size());
        return files;
    }

    public File getById(Long id) {
        File file = getFileFromDB(id, "getById");

        log.info("IN getById - File: '{}' found by id: {}", file.getFileName(), id);

        return file;
    }

    public List<File> getByBucketName(String bucketName) {
        List<File> files = fileRepository.findByBucketNameAndStatus(bucketName, Status.ACTIVE);
        log.info("IN getByBucketName - Getting information about files from bucket '{}'." +
                " Count files: {}", bucketName, files.size());
        return files;
    }

    @Transactional
    public File uploadFile(File file) {
        s3client.putObject(
                file.getBucketName(),
                file.getLocationInBucket(),
                new java.io.File(file.getPathToSourceFile()));
        log.info("IN uploadFile - File '{}' successfully uploaded to S3!", file.getPathToSourceFile());

        enrichFileForDB(file);
        File uploadedFile = fileRepository.save(file);

        eventService.createEvent(uploadedFile, Action.CREATION);

        return uploadedFile;
    }

    @Transactional
    public void downloadFile(Long id, String pathToDestinationFile) throws IOException {
        File fileToDownload = getFileFromDB(id, "downloadFile");

        String bucketName = fileToDownload.getBucketName();
        String locationInBucket = getLocationInBucket(fileToDownload);

        S3Object s3object = s3client.getObject(bucketName, locationInBucket);
        S3ObjectInputStream inputStream = s3object.getObjectContent();
        java.io.File fileIO = new java.io.File(pathToDestinationFile);
        FileCopyUtils.copy(inputStream, new FileOutputStream(fileIO));

        log.info("IN downloadFile - Downloading file with id {}", id);
    }

    @Transactional
    public void deleteFile(Long id) {
        File fileToDelete = getFileFromDB(id, "deleteFile");

        String bucketName = fileToDelete.getBucketName();
        String locationInBucket = getLocationInBucket(fileToDelete);

        s3client.deleteObject(bucketName, locationInBucket);
        fileToDelete.setStatus(Status.REMOVED);
        log.info("IN deleteFile - File with id={} successfully deleted!", id);

        eventService.createEvent(fileToDelete, Action.DELETION);
    }

    @SneakyThrows
    private File getFileFromDB(Long id, String methodName)  {
        File file = fileRepository.findByIdAndStatus(id, Status.ACTIVE).orElse(null);
        if (file == null) {
            log.warn("IN {} - File not found by id {}", methodName, id);
            throw new FileNotFoundException("File doesn't exists");
        }
        return file;
    }

    private String getLocationInBucket(File file) {
        String location = file.getLocation();
        int index = location.lastIndexOf("com/");
        return location.substring(index + 4);
    }

    private void enrichFileForDB(File file) {
        String location = "https://" + file.getBucketName() + ".s3.amazonaws.com/" + file.getLocationInBucket();
        String fileName = getFileNameFromLocationInBucket(file.getLocationInBucket());

        file.setFileName(fileName);
        file.setLocation(location);
        file.setStatus(Status.ACTIVE);
    }
}
