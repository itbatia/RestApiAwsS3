package com.itbatia.app.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.itbatia.app.model.*;
import com.itbatia.app.repository.EventRepository;
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
public class FileEntityServiceTest {

    @InjectMocks
    private FileService fileService;

    @Mock
    private FileRepository fileRepositoryMock;
    @Mock
    private AmazonS3 s3clientMock;
    @Mock
    private EventService eventServiceMock;
    @Mock
    private EventRepository eventRepositoryMock;

    private final static String LOCATION_IN_BUCKET = "folder1/folder2/fileName.docx";
    private FileEntity expectedFile;

    private FileEntity getFile() {
        return FileEntity.builder()
                .bucketName("bucketName")
                .fileName("fileName")
                .location("https://bucketName.s3.amazonaws.com/folder1/folder2/fileName.docx")
                .build();
    }

    private PutObjectResult getPutObjectResult() {
        return new PutObjectResult();
    }

    @BeforeEach
    public void setUp() {
        expectedFile = getFile();
    }

    @Test
    public void getById() {
        when(fileRepositoryMock.findByIdAndStatus(anyLong(), eq(Status.ACTIVE))).thenReturn(Optional.of(getFile()));

        fileService.getById(1L);

        verify(fileRepositoryMock).findByIdAndStatus(1L, Status.ACTIVE);
        verify(fileRepositoryMock, times(1)).findByIdAndStatus(1L, Status.ACTIVE);
        verify(fileRepositoryMock, never()).findByIdAndStatus(2L, Status.ACTIVE);
    }

    @Test
    public void getAllFiles() {
        fileService.getAllFiles();

        verify(fileRepositoryMock).findAllByStatus(Status.ACTIVE);
        verify(fileRepositoryMock, never()).findAllByStatus(Status.REMOVED);
        verify(fileRepositoryMock, times(1)).findAllByStatus(Status.ACTIVE);
    }

    @Test
    public void uploadFile() {
        when(s3clientMock.putObject(any(), any(), any(), any())).thenReturn(getPutObjectResult());
        when(fileRepositoryMock.save(any(FileEntity.class))).thenReturn(getFile());
        doNothing().when(eventServiceMock).createEvent(any(FileEntity.class), eq(Action.CREATION));

        fileService.uploadFile(getFile().getFileName(), "".getBytes());

        verify(fileRepositoryMock).save(any(FileEntity.class));
        verify(fileRepositoryMock, times(1)).save(any(FileEntity.class));
        verify(s3clientMock).putObject(any(), any(), any(), any());
        verify(s3clientMock, times(1)).putObject(any(), any(), any(), any());
        verify(eventServiceMock).createEvent(any(FileEntity.class), any(Action.class));
        verify(eventServiceMock, never()).createEvent(any(FileEntity.class), eq(Action.DELETION));
        verify(eventRepositoryMock, never()).save(any(Event.class));
    }

    @Test
    public void deleteFile() {
        when(fileRepositoryMock.findByIdAndStatus(anyLong(), eq(Status.ACTIVE))).thenReturn(Optional.of(expectedFile));
        doNothing().when(eventServiceMock).createEvent(any(FileEntity.class), eq(Action.DELETION));
        doNothing().when(s3clientMock).deleteObject(anyString(), anyString());

        fileService.deleteFile(1L);

        verify(fileRepositoryMock).findByIdAndStatus(1L, Status.ACTIVE);
        verify(fileRepositoryMock, times(1)).findByIdAndStatus(1L, Status.ACTIVE);
        verify(fileRepositoryMock, never()).findByIdAndStatus(2L, Status.ACTIVE);
        verify(s3clientMock).deleteObject(getFile().getBucketName(), LOCATION_IN_BUCKET);
        verify(s3clientMock, times(1)).deleteObject(getFile().getBucketName(), LOCATION_IN_BUCKET);
        verify(eventServiceMock).createEvent(any(FileEntity.class), any(Action.class));
        verify(eventServiceMock, never()).createEvent(any(FileEntity.class), eq(Action.CREATION));
        verify(eventRepositoryMock, never()).save(any(Event.class));

        assertEquals(expectedFile.getStatus(), Status.REMOVED);
    }
}