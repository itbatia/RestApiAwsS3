package com.itbatia.app.rest;

import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Principal;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.auth.policy.actions.S3Actions;
import com.amazonaws.auth.policy.resources.S3ObjectResource;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.itbatia.app.dto.BucketDTO;
import com.itbatia.app.util.exceptions.BucketNotCreationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.itbatia.app.util.exceptions.ErrorsUtil.returnErrorsToClient;

@Slf4j
@RestController
@RequestMapping("/api/v1/buckets")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class BucketRestControllerV1 {

    private final AmazonS3 s3client;

    @PostMapping("/create")
    public ResponseEntity<?> createBucket(@RequestBody @Valid BucketDTO bucketDTO, BindingResult bindingResult) {
        String bucketName = bucketDTO.getBucketName();

        if(bindingResult.hasErrors()){
            throw new BucketNotCreationException(returnErrorsToClient(bindingResult));
        }

        if (s3client.doesBucketExistV2(bucketName)) {
            log.warn("IN createBucket - Bucket {} already exists, use a different name", bucketName);
            throw new BucketNotCreationException("Bucket name '" + bucketName + "' is already exists, use a different name!");
        }
        s3client.createBucket(bucketName);

        log.info("IN createBucket - Bucket with name '" + bucketName + "' successfully created!");
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/access/open")
    public ResponseEntity<?> openAccessToBucket(@RequestBody BucketDTO bucketDTO) {
        String bucketName = bucketDTO.getBucketName();

        Statement allowPublicReadStatement = new Statement(Statement.Effect.Allow)
                .withPrincipals(Principal.AllUsers)
                .withActions(S3Actions.GetObject)
                .withResources(new S3ObjectResource(bucketName, "*"));

        Policy policy = new Policy().withStatements(allowPublicReadStatement);
        s3client.setBucketPolicy(bucketName, policy.toJson());

        log.info("IN openAccessToBucket - Access to the bucket '" + bucketName + "' is open.");
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/access/close")
    public ResponseEntity<?> closeAccessToBucket(@RequestBody BucketDTO bucketDTO) {
        String bucketName = bucketDTO.getBucketName();

        s3client.deleteBucketPolicy(bucketName);

        log.info("IN closeAccessToBucket - Access to the bucket '" + bucketName + "' is close.");
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping
    public List<Bucket> getAllBuckets() {
        List<Bucket> buckets = s3client.listBuckets();
        log.info("IN getAllBuckets - buckets: {}", buckets);
        return buckets;
    }

    @GetMapping("/names")
    public List<String> getBucketsNames() {
        List<String> buckets = s3client.listBuckets().stream().map(Bucket::getName).collect(Collectors.toList());
        log.info("IN getBucketsNames - buckets names: {}", buckets);
        return buckets;
    }

    @DeleteMapping("/delete/empty")
    public ResponseEntity<?> deleteEmptyBucket(@RequestBody BucketDTO bucketDTO) {
        String bucketName = bucketDTO.getBucketName();
        s3client.deleteBucket(bucketName);

        log.info("IN deleteEmptyBucket - bucket '" + bucketName + "' successfully deleted!");
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/delete/with_resources")
    public ResponseEntity<?> deleteBucketWithResources(@RequestBody BucketDTO bucketDTO) {
        String bucketName = bucketDTO.getBucketName();

        ObjectListing objectListing = s3client.listObjects(bucketName);
        while (true) {
            for (S3ObjectSummary summary : objectListing.getObjectSummaries()) {
                s3client.deleteObject(bucketName, summary.getKey());
            }
            if (objectListing.isTruncated()) {
                objectListing = s3client.listNextBatchOfObjects(objectListing);
            } else {
                break;
            }
        }
        s3client.deleteBucket(bucketName);

        log.info("IN deleteBucketWithResources - bucket '" + bucketName + "' successfully deleted!");
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
// {
//     "Id":"Policy1662136523491",
//     "Version":"2012-10-17",
//     "Statement":[
//         {
//             "Sid":"Stmt1662136510851",
//             "Action":["s3:GetObject"],
//             "Effect":"Allow",
//             "Resource":"arn:aws:s3:::first-test-one",
//             "Principal":"*"
//         }
//     ]
// }