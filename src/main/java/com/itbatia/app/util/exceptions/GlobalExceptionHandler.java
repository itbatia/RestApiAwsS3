package com.itbatia.app.util.exceptions;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(FileNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        log.error(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(FileNotUploadedException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        log.error(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        log.error(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(AmazonS3Exception e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        log.error(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(IOException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        log.error(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(UsernameNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        log.error(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(UserNotUpdatedException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        log.error(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(AuthenticationException e) {
        String message = e.getMessage() + ". Incorrect username or password!";
        ErrorResponse errorResponse = new ErrorResponse(message);
        log.error(message);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(UserNotRegisteredException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        log.error(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
