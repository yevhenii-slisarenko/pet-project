package my.iam_service.service;

import my.iam_service.model.entity.EmailVerificationToken;
import my.iam_service.model.entity.User;

public interface MailSenderService {
    EmailVerificationToken createToken(User user);

    void validateToken(String token);

    void sendVerificationEmail(String email, String username, String token);
}
