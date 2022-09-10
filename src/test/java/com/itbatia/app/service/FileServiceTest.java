package com.itbatia.app.service;

import com.amazonaws.services.s3.AmazonS3;
import com.itbatia.app.model.Action;
import com.itbatia.app.model.File;
import com.itbatia.app.model.Status;
import com.itbatia.app.repository.FileRepository;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileServiceTest {

    @Autowired
    private FileService fileService;

    @MockBean
    private FileRepository fileRepositoryMock;
    @MockBean
    private AmazonS3 s3clientMock;
    @MockBean
    private EventService eventServiceMock;

    private Status status;
    private File expectedFile;
    private File testFile;
    private String bucketName;
    private String locationInBucket;
    private java.io.File fileInput;


    @Autowired
    @Before
    public void setUp() {
        status = Status.ACTIVE;
        bucketName = "bucketName";
        locationInBucket = "folder1/folder2/fileName.docx";
        fileInput = new java.io.File("C:/Users/Desktop/fileName.docx");

        testFile = new File();
        testFile.setBucketName("bucketName");
        testFile.setLocationInBucket("folder1/folder2/fileName.docx");
        testFile.setPathToSourceFile("C:/Users/Desktop/fileName.docx");

        expectedFile = new File();
        expectedFile.setLocation("https://bucketName.s3.amazonaws.com/folder1/folder2/fileName.docx");
        expectedFile.setBucketName("bucketName");
    }

    @Test
    public void uploadFile() {
        when(fileRepositoryMock.save(testFile)).thenReturn(expectedFile);
        File fileActual = fileService.uploadFile(testFile);

        verify(fileRepositoryMock).save(testFile);
        verify(fileRepositoryMock, times(1)).save(testFile);
        verify(s3clientMock).putObject(bucketName, locationInBucket, fileInput);
        verify(s3clientMock, times(1)).putObject(bucketName, locationInBucket, fileInput);
        verify(eventServiceMock).createEvent(expectedFile, Action.CREATION);
        assertEquals(expectedFile, fileActual);

        // 1) проверяем, что с методом save() было взаимодействие
        // 2) проверяем, что метод save() был вызван 1 раз
        // 3) проверяем, что с методом putObject() было взаимодействие
        // 4) проверяем, что метод putObject() был вызван 1 раз
        // 5) проверяем, что с методом createEvent() было взаимодействие
        // 6) сравниваем ожидаемые и актуальные данные
    }

    @Test
    public void getAllFiles() {
        fileService.getAllFiles();

        verify(fileRepositoryMock).findAllByStatus(status);
        verify(fileRepositoryMock, times(1)).findAllByStatus(status);

        // 1) проверяем, что с методом findAllByStatus() было взаимодействие
        // 2) проверяем, что метод findAllByStatus() был вызван 1 раз
    }

    @Test
    public void getById() {
        when(fileRepositoryMock.findByIdAndStatus(1L, status)).thenReturn(Optional.of(expectedFile));
        File actualFile = fileService.getById(1L);

        verify(fileRepositoryMock).findByIdAndStatus(1L, status);
        verify(fileRepositoryMock, never()).findByIdAndStatus(2L, status);
        assertEquals(expectedFile, actualFile);
        // 1) проверяем, что с методом findByIdAndStatus() было взаимодействие
        // 2) проверяем, что метод findByIdAndStatus с параметром "2" не вызывался
        // 3) сравниваем ожидаемые и актуальные данные
    }

    @Test
    public void getByBucketName() {
        fileService.getByBucketName(bucketName);

        verify(fileRepositoryMock).findByBucketNameAndStatus(bucketName, status);
        verify(fileRepositoryMock, times(1)).findByBucketNameAndStatus(bucketName, status);

        // 1) проверяем, что с методом findByBucketNameAndStatus() было взаимодействие
        // 2) проверяем, что метод findByBucketNameAndStatus() был вызван 1 раз
    }

    @Test
    public void deleteFile() {
        when(fileRepositoryMock.findByIdAndStatus(1L, status)).thenReturn(Optional.of(expectedFile));
        fileService.deleteFile(1L);

        verify(s3clientMock, times(1)).deleteObject(bucketName, locationInBucket);
        verify(eventServiceMock).createEvent(expectedFile, Action.DELETION);
        assertEquals(expectedFile.getStatus(), Status.REMOVED);

        // 1) проверяем, что метод deleteObject() был вызван 1 раз
        // 2) проверяем, что с методом createEvent() было взаимодействие
        // 3) проверяем, что статус удаляемого файла изменился на REMOVED
    }
}