package my.iam_service.security.validation;

import my.iam_service.model.request.user.ChangePasswordRequest;
import my.iam_service.model.request.user.RegistrationUserRequest;
import my.iam_service.utils.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj instanceof RegistrationUserRequest request) {
            return request.getPassword().equals(request.getConfirmPassword());
        }
        if (obj instanceof ChangePasswordRequest request) {
            return request.getPassword().equals(request.getConfirmPassword());
        }
        return false;
    }
}
