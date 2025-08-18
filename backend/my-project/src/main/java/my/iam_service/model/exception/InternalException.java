package my.iam_service.model.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InternalException extends RuntimeException {

    public InternalException(String message) {
        super(message);
    }

}
