package com.itbatia.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Builder
@Table(name = "files")
@AllArgsConstructor
@NoArgsConstructor
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "location")
    private String location;

    @Column(name = "bucket_name")
    private String bucketName;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Transient
    private String pathToSourceFile;

    @Transient
    private String locationInBucket;
}
