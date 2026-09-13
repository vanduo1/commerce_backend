package com.sara.commerce_platform.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<String> handleOptimisticLocking(
            ObjectOptimisticLockingFailureException e
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("The system is processing multiple transactions. This product has just been purchased by someone else. Please try again!");
    }
}
