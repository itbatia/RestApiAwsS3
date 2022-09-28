package com.itbatia.app.util.validators;

import com.itbatia.app.model.*;
import com.itbatia.app.repository.FileRepository;
import com.itbatia.app.util.exceptions.UserNotUpdatedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FileValidator {

    @Value("${aws_bucket_name}")
    private String BUCKET_NAME;

    private final FileRepository fileRepository;

    public void validate(MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        List<FileEntity> files = fileRepository.findByBucketNameAndFileNameAndStatus(BUCKET_NAME, fileName, Status.ACTIVE);

        if (!files.isEmpty()) {
            throw new UserNotUpdatedException("Файл с названием '" + fileName + "' уже существует!");
        }
    }
}
