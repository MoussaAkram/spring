package com.utilisateurs.api.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ExeptionHandler {
    @ExceptionHandler(value = {UserNotFoundException.class})
    public ResponseEntity<Object> handleUserNotFoundExeption(UserNotFoundException e){
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        Exeption exeption = new Exeption(
                e.getMessage(),
                e,
                badRequest,
                ZonedDateTime.now(ZoneId.of("Z"))

        );
        return new ResponseEntity<>(exeption, badRequest);
    }
}
