package be.ucll.myrecipe.server.exception;

import be.ucll.myrecipe.server.api.ApiErrorDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.stream.Collectors;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        var errors = new ArrayList<String>();

        for (var fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
        }

        for (var objectError : ex.getBindingResult().getGlobalErrors()) {
            errors.add(objectError.getObjectName() + ": " + objectError.getDefaultMessage());
        }

        var apiErrorDto = new ApiErrorDto(HttpStatus.BAD_REQUEST, "Constraint Violation", errors);
        return new ResponseEntity<>(apiErrorDto, headers, status);
    }

    @ExceptionHandler(javax.validation.ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolation(javax.validation.ConstraintViolationException ex) {
        var errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.toList());

        var apiErrorDto = new ApiErrorDto(HttpStatus.BAD_REQUEST, "Constraint Violation", errors);
        return new ResponseEntity<>(apiErrorDto, apiErrorDto.getStatus());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        var apiErrorDto = new ApiErrorDto(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage());
        return new ResponseEntity<>(apiErrorDto, apiErrorDto.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        var apiErrorDto = new ApiErrorDto(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage());
        return new ResponseEntity<>(apiErrorDto, apiErrorDto.getStatus());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ApiErrorDto> handleEntityNotFound(EntityNotFoundException ex) {
        var apiErrorDto = new ApiErrorDto(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(apiErrorDto, apiErrorDto.getStatus());
    }

    @ExceptionHandler(UserForbiddenException.class) // TODO: check AccessDeniedException
    protected ResponseEntity<ApiErrorDto> handleUserForbidden(UserForbiddenException ex) {
        var apiErrorDto = new ApiErrorDto(HttpStatus.FORBIDDEN, ex.getMessage());
        return new ResponseEntity<>(apiErrorDto, apiErrorDto.getStatus());
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiErrorDto> handleUncaught(Exception ex, WebRequest request) {
//        var apiError = new ApiErrorDto(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
//        return new ResponseEntity<>(apiError, apiError.getStatus());
//    }
}
