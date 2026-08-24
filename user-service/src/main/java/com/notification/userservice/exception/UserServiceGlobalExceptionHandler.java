package com.notification.userservice.exception;

import com.notification.userservice.exception.csv.CsvHeadersException;
import com.notification.userservice.exception.csv.CsvProcessingException;
import com.notification.userservice.exception.csv.CsvValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class UserServiceGlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleNotFound(UserNotFoundException e) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        pd.setTitle("User Not Found");
        return pd;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException e) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, e.getMessage());
        pd.setTitle("Bad Credentials");
        return pd;
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ProblemDetail handleAlreadyExists(ResourceAlreadyExistsException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problem.setTitle("Resource not found");
        return problem;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Invalid Parameter");
        pd.setDetail(String.format("Parameter '%s' should be of type %s",
                e.getName(),
                e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown"));
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException e) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation Failed");
        pd.setDetail("Check 'errors' field");

        Map<String,String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach((error-> errors.put(error.getField(),
                error.getDefaultMessage())));

        pd.setProperty("errors", errors);
        return pd;
    }

    @ExceptionHandler(ValidationException.class)
    public ProblemDetail handleValidation(ValidationException e) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle(e.getMessage());
        pd.setDetail("Check 'errors' field");
        pd.setProperty("errors", e.getErrors());
        return pd;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Malformed JSON");
        pd.setDetail("Invalid value in request body: " + e.getMessage());
        return pd;
    }

    @ExceptionHandler(CsvValidationException.class)
    public ProblemDetail handleCsvValidation(CsvValidationException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        problem.setTitle("CSV Validation Failed");
        problem.setProperty("errors", e.getErrors());
        return problem;
    }

    @ExceptionHandler(CsvProcessingException.class)
    public ProblemDetail handleCsvProcessing(CsvProcessingException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        problem.setTitle("CSV Processing Failed");
        return problem;
    }

    @ExceptionHandler(CsvHeadersException.class)
    public ProblemDetail handleCsvHeadersException(CsvHeadersException e) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
        pd.setTitle("CSV Headers Missed");
        pd.setProperty("actualHeaders", e.getActualHeaders());
        pd.setProperty("missingHeaders", e.getMissingHeaders());
        return pd;
    }


}