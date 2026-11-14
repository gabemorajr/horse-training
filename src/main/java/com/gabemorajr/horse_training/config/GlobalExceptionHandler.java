package com.gabemorajr.horse_training.config;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

// Catches exceptions thrown from ANY controller in the app and turns them
// into consistent error responses, using Spring's standard ProblemDetail format.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Errors your services throw on purpose (404 not found, 409 already in
    // training, 400 bad dates). Shows the message you wrote.
    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleResponseStatusException(ResponseStatusException ex) {
        return ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getReason());
    }

    // The database rejected a change, e.g. the one-active-stay unique index in
    // a race condition. Without this, the caller would get a generic 500.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                "This change conflicts with existing data, for example a horse that is already in training.");
    }
}