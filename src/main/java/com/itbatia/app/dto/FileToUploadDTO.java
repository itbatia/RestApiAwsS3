package com.itbatia.app.dto;

import lombok.Data;

@Data
public class FileToUploadDTO {

    private String bucketName;
    private String pathToSourceFile;
    private String locationInBucket;
}
