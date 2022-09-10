package com.itbatia.app.repository;

import com.itbatia.app.model.File;
import com.itbatia.app.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    Optional<File> findByIdAndStatus(Long id, Status status);

    List<File> findAllByStatus(Status status);

    List<File> findByBucketNameAndStatus(String bucketName, Status status);

    List<File> findByBucketNameAndFileNameAndStatus(String bucketName, String fileName, Status status);
}
