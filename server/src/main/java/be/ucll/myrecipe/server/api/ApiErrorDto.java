package be.ucll.myrecipe.server.api;

import org.springframework.http.HttpStatus;

import java.util.List;

public class ApiErrorDto {

    private final HttpStatus status;
    private final String message;
    private final List<String> errors;

    public ApiErrorDto(HttpStatus status, String message) {
        this(status, message, null);
    }

    public ApiErrorDto(HttpStatus status, String message, List<String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getErrors() {
        return errors;
    }
}
