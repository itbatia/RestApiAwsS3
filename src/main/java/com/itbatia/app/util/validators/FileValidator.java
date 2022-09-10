package com.itbatia.app.util.validators;

import com.itbatia.app.model.File;
import com.itbatia.app.model.Status;
import com.itbatia.app.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

import static com.itbatia.app.util.Utility.getFileNameFromLocationInBucket;

@Component
public class FileValidator implements Validator {

    private final FileRepository fileRepository;

    @Autowired
    public FileValidator(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(File.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        File file = (File) target;
        String fileName = getFileNameFromLocationInBucket(file.getLocationInBucket());
        List<File> files = fileRepository.findByBucketNameAndFileNameAndStatus(file.getBucketName(), fileName, Status.ACTIVE);

        if(!files.isEmpty()){
            errors.rejectValue("bucketName", "", "В бакете '"
                    + file.getBucketName() + "' файл с названием '" + fileName + "' уже существует!");
        }
    }
}
