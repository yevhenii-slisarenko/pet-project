package my.iam_service.model.constants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ApiEmailMessage {
    SUBJECT_CONFIRM_REGISTRATION("Confirm your registration"),
    CONFIRM_REGISTRATION("""
            Hi %s,

            We received a request to confirm your registration.

            Please click the link below to complete your registration:
            %s

            If you didn't request this, simply ignore this message
            """);

    private final String message;

    public String getMessage(String... args) {
        return String.format(message, (Object[]) args);
    }
}
