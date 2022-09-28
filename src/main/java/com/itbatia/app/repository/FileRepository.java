package com.itbatia.app.repository;

import com.itbatia.app.model.FileEntity;
import com.itbatia.app.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    Optional<FileEntity> findByIdAndStatus(Long id, Status status);

    List<FileEntity> findAllByStatus(Status status);

    List<FileEntity> findByBucketNameAndFileNameAndStatus(String bucketName, String fileName, Status status);
}
