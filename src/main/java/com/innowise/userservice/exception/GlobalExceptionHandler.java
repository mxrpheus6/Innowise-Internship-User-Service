package com.innowise.userservice.exception;

import com.innowise.userservice.dto.exception.ExceptionDto;
import com.innowise.userservice.dto.exception.Validation;
import com.innowise.userservice.dto.exception.ValidationResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<Validation> validations = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new Validation(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ValidationResponse(validations));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionDto(LocalDateTime.now(), e.getMessage()));
    }

    @ExceptionHandler(CardInfoNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleCardInfoNotFoundException(CardInfoNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ExceptionDto(LocalDateTime.now(), e.getMessage()));
    }

}
