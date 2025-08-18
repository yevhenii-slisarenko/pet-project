package my.iam_service.repository;

import my.iam_service.model.entity.EmailVerificationToken;
import my.iam_service.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Integer> {
    Optional<EmailVerificationToken> findByToken(String token);

    void deleteByUser(User user);
}
