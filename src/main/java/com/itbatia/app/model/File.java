package com.itbatia.app.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "files")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    //absolute path in S3
    @Column(name = "location")
    private String location;

    @Column(name = "bucket_name")
    private String bucketName;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    //source path of the upload file
    @Transient
    private String pathToSourceFile;

    //destination path for download file
    @Transient
    private String pathToDestinationFile;

    @Transient
    private String locationInBucket;
}
