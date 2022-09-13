package com.itbatia.app.service;

import com.amazonaws.services.s3.AmazonS3;
import com.itbatia.app.model.*;
import com.itbatia.app.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileServiceTest {

    @InjectMocks
    private FileService fileService;

    @Mock
    private FileRepository fileRepositoryMock;
    @Mock
    private AmazonS3 s3clientMock;
    @Mock
    private EventService eventServiceMock;

    private final static String BUCKET_NAME = "bucketName";
    private final static String LOCATION_IN_BUCKET = "folder1/folder2/fileName.docx";
    private final static java.io.File FILE_INPUT = new java.io.File("C:/Users/Desktop/fileName.docx");
    private File expectedFile;

    private File getTestFile() {
        return File.builder()
                .bucketName("bucketName")
                .locationInBucket("folder1/folder2/fileName.docx")
                .pathToSourceFile("C:/Users/Desktop/fileName.docx")
                .build();
    }

    private File getExpectedFile() {
        return File.builder()
                .bucketName("bucketName")
                .location("https://bucketName.s3.amazonaws.com/folder1/folder2/fileName.docx")
                .build();
    }

    @BeforeEach
    public void setUp() {
        expectedFile = getExpectedFile();
    }

    @Test
    public void uploadFile() {
        when(fileRepositoryMock.save(any(File.class))).thenReturn(getExpectedFile());
        File fileActual = fileService.uploadFile(getTestFile());

        verify(fileRepositoryMock).save(any(File.class));
        verify(fileRepositoryMock, times(1)).save(any(File.class));
        verify(s3clientMock).putObject(BUCKET_NAME, LOCATION_IN_BUCKET, FILE_INPUT);
        verify(s3clientMock, times(1)).putObject(BUCKET_NAME, LOCATION_IN_BUCKET, FILE_INPUT);
        verify(eventServiceMock).createEvent(any(File.class), any(Action.class));
        assertEquals(getExpectedFile(), fileActual);
    }

    @Test
    public void getAllFiles() {
        fileService.getAllFiles();

        verify(fileRepositoryMock).findAllByStatus(Status.ACTIVE);
        verify(fileRepositoryMock, times(1)).findAllByStatus(Status.ACTIVE);
    }

    @Test
    public void getById() {
        when(fileRepositoryMock.findByIdAndStatus(1L, Status.ACTIVE)).thenReturn(Optional.of(getExpectedFile()));
        File actualFile = fileService.getById(1L);

        verify(fileRepositoryMock).findByIdAndStatus(1L, Status.ACTIVE);
        verify(fileRepositoryMock, never()).findByIdAndStatus(2L, Status.ACTIVE);
        assertEquals(getExpectedFile(), actualFile);
    }

    @Test
    public void getByBucketName() {
        fileService.getByBucketName(BUCKET_NAME);

        verify(fileRepositoryMock).findByBucketNameAndStatus(BUCKET_NAME, Status.ACTIVE);
        verify(fileRepositoryMock, times(1)).findByBucketNameAndStatus(BUCKET_NAME, Status.ACTIVE);
    }

    @Test
    public void deleteFile() {
        when(fileRepositoryMock.findByIdAndStatus(1L, Status.ACTIVE)).thenReturn(Optional.of(expectedFile));
        fileService.deleteFile(1L);

        verify(s3clientMock, times(1)).deleteObject(BUCKET_NAME, LOCATION_IN_BUCKET);
        verify(eventServiceMock).createEvent(expectedFile, Action.DELETION);
        assertEquals(expectedFile.getStatus(), Status.REMOVED);
    }
}