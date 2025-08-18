package my.iam_service.repository;

import aj.org.objectweb.asm.commons.Remapper;
import my.iam_service.model.entity.User;
import my.iam_service.model.enums.RegistrationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<User> findByIdAndDeletedFalse (Integer id);

    Optional<User> findUserByEmailAndDeletedFalse(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    void deleteByUsernameAndRegistrationStatus(String username, RegistrationStatus status);
    void deleteByEmailAndRegistrationStatus(String email, RegistrationStatus status);

    Page<User> findAllByDeletedFalse(Pageable pageable);

    Page <User> findAllByDeletedFalseAndRegistrationStatus(RegistrationStatus registrationStatus, Pageable pageable);

    Page <User> findAllByDeletedTrueAndRegistrationStatus(RegistrationStatus registrationStatus, Pageable pageable);

    Optional<User> findByIdAndDeletedTrue(Integer userId);
}

