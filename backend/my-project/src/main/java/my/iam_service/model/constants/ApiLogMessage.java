package my.iam_service.model.constants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ApiLogMessage {
    POST_INFO_BY_ID("Receiving post with ID: {}"),
    NAME_OF_CURRENT_METHOD("Current method: {}"),
    PASSWORD_CHANGED_SUCCESSFULLY("Password changed successfully"),
    ;

    private final String value;
}
