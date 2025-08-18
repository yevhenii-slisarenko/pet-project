package my.iam_service.security.validation;

import my.iam_service.model.constants.ApiErrorMessage;
import my.iam_service.model.entity.User;
import my.iam_service.model.enums.RegistrationStatus;
import my.iam_service.model.exception.DataExistException;
import my.iam_service.model.exception.InvalidDataException;
import my.iam_service.model.exception.InvalidPasswordException;
import my.iam_service.model.exception.NotFoundException;
import my.iam_service.repository.UserRepository;
import my.iam_service.service.model.IamServiceUserRole;
import my.iam_service.utils.ApiUtils;
import my.iam_service.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;

@Component
@RequiredArgsConstructor
public class AccessValidator {
    private final UserRepository userRepository;
    private final ApiUtils apiUtils;

    public void validateNewUser(String username, String email, String password, String confirmPassword) {
        userRepository.findByUsername(username).ifPresent(existingUser -> {
            if (existingUser.getRegistrationStatus() == RegistrationStatus.ACTIVE) {
                throw new DataExistException(ApiErrorMessage.USERNAME_ALREADY_EXISTS.getMessage(username));
            }
        });

        userRepository.findByEmail(email).ifPresent(existingUser -> {
            if (existingUser.getRegistrationStatus() == RegistrationStatus.ACTIVE) {
                throw new DataExistException(ApiErrorMessage.EMAIL_ALREADY_EXISTS.getMessage(email));
            }
        });

        if (!password.equals(confirmPassword)) {
            throw new InvalidDataException(ApiErrorMessage.MISMATCH_PASSWORDS.getMessage());
        }

        if (PasswordUtils.isNotValidPassword(password)) {
            throw new InvalidPasswordException(ApiErrorMessage.INVALID_PASSWORD.getMessage());
        }

    }

    public boolean isAdminOrSuperAdmin(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId)));

        return user.getRoles().stream()
                .map(role -> IamServiceUserRole.fromName(role.getName()))
                .anyMatch(role -> role == IamServiceUserRole.ADMIN || role == IamServiceUserRole.SUPER_ADMIN);
    }

    @SneakyThrows
    public void validateAdminOrOwnerAccess(Integer ownerId) {
        Integer currentUserId = apiUtils.getUserIdFromAuthentication();

        if (!currentUserId.equals(ownerId) && !isAdminOrSuperAdmin(currentUserId)) {
            throw new AccessDeniedException(ApiErrorMessage.HAVE_NO_ACCESS.getMessage());
        }
    }

    @SneakyThrows
    public void validateAdminAccess() {
        Integer currentUserId = apiUtils.getUserIdFromAuthentication();

        if (!isAdminOrSuperAdmin(currentUserId)) {
            throw new AccessDeniedException(ApiErrorMessage.HAVE_NO_ACCESS.getMessage());
        }
    }

}
