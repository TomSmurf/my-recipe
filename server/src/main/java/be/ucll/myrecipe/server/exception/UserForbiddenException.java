package be.ucll.myrecipe.server.exception;

public class UserForbiddenException extends RuntimeException {

    public UserForbiddenException() {
        super("Access is denied");
    }
}
