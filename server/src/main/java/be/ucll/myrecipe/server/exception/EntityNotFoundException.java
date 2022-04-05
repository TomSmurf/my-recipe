package be.ucll.myrecipe.server.exception;

import org.springframework.util.StringUtils;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Class<?> clazz, Object id) {
        super(StringUtils.capitalize(clazz.getSimpleName()) + " was not found for id: " + id);
    }

}
