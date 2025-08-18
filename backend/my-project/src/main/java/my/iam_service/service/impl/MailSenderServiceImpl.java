package my.iam_service.service.impl;

import my.iam_service.model.constants.ApiConstants;
import my.iam_service.model.constants.ApiEmailMessage;
import my.iam_service.model.constants.ApiErrorMessage;
import my.iam_service.model.entity.EmailVerificationToken;
import my.iam_service.model.entity.User;
import my.iam_service.model.enums.RegistrationStatus;
import my.iam_service.model.exception.NotFoundException;
import my.iam_service.repository.EmailVerificationTokenRepository;
import my.iam_service.repository.UserRepository;
import my.iam_service.service.MailSenderService;
import my.iam_service.utils.ApiUtils;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MailSenderServiceImpl implements MailSenderService {
    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @Value("${base.url}")
    private String baseUrl;
    @Value("${spring.mail.username}")
    private String senderEmail;
    @Value("${spring.mail.name}")
    private String senderName;

    @Override
    public EmailVerificationToken createToken(User user) {
        tokenRepository.deleteByUser(user);

        EmailVerificationToken token = new EmailVerificationToken();
        token.setUser(user);
        token.setCreated(LocalDateTime.now());
        token.setExpires(LocalDateTime.now().plusMinutes(30));
        token.setToken(ApiUtils.generateRandomToken(128, ApiConstants.EMAIL_VERIFICATION_TOKEN_CHARACTERS));

        return tokenRepository.save(token);
    }

    @Override
    public void validateToken(String tokenValue) {
        EmailVerificationToken token = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.EMAIL_VERIFICATION_TOKEN_NOT_FOUND.getMessage()));

        if (token.getExpires().isBefore(LocalDateTime.now())) {
            throw new NotFoundException(ApiErrorMessage.CONFIRMATION_LINK_EXPIRED.getMessage());
        }

        User user = token.getUser();
        user.setRegistrationStatus(RegistrationStatus.ACTIVE);
        userRepository.save(user);

        tokenRepository.delete(token);

    }

    @Override
    public void sendVerificationEmail(String email, String username, String token) {
        String subject = ApiEmailMessage.SUBJECT_CONFIRM_REGISTRATION.getMessage();
        String confirmationUrl = baseUrl + token;
        String body = ApiEmailMessage.CONFIRM_REGISTRATION.getMessage(username, confirmationUrl);

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body, false);

            helper.setFrom(senderEmail, senderName);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
