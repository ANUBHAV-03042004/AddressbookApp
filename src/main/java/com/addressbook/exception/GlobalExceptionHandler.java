package com.addressbook.exception;

import com.addressbook.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AddressBookNotFoundException.class)
    public ResponseEntity<ResponseDTO<String>> handleAddressBookNotFound(AddressBookNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseDTO<>(ex.getMessage(), null));
    }

    @ExceptionHandler(DuplicateAddressBookException.class)
    public ResponseEntity<ResponseDTO<String>> handleDuplicateAddressBook(DuplicateAddressBookException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ResponseDTO<>(ex.getMessage(), null));
    }

    @ExceptionHandler(ContactNotFoundException.class)
    public ResponseEntity<ResponseDTO<String>> handleContactNotFound(ContactNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseDTO<>(ex.getMessage(), null));
    }

    @ExceptionHandler(DuplicateContactException.class)
    public ResponseEntity<ResponseDTO<String>> handleDuplicateContact(DuplicateContactException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ResponseDTO<>(ex.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDTO<>("Validation failed", errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<String>> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseDTO<>("Something went wrong: " + ex.getMessage(), null));
    }
}
