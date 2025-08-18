package my.iam_service.service.impl;

import my.iam_service.mapper.UserMapper;
import my.iam_service.model.constants.ApiErrorMessage;
import my.iam_service.model.constants.ApiLogMessage;
import my.iam_service.model.constants.ApiMessage;
import my.iam_service.model.entity.EmailVerificationToken;
import my.iam_service.model.entity.Role;
import my.iam_service.model.enums.RegistrationStatus;
import my.iam_service.model.exception.InvalidPasswordException;
import my.iam_service.model.exception.NotFoundException;
import my.iam_service.model.request.user.ChangePasswordRequest;
import my.iam_service.model.request.user.LoginRequest;
import my.iam_service.model.dto.user.UserProfileDTO;
import my.iam_service.model.entity.RefreshToken;
import my.iam_service.model.entity.User;
import my.iam_service.model.exception.InvalidDataException;
import my.iam_service.model.request.user.RegistrationUserRequest;
import my.iam_service.model.response.IamResponse;
import my.iam_service.repository.RoleRepository;
import my.iam_service.repository.UserRepository;
import my.iam_service.security.JwtTokenProvider;
import my.iam_service.security.validation.AccessValidator;
import my.iam_service.service.AuthService;
import my.iam_service.service.MailSenderService;
import my.iam_service.service.RefreshTokenService;
import my.iam_service.service.model.IamServiceUserRole;
import my.iam_service.utils.ApiUtils;
import my.iam_service.utils.PasswordUtils;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccessValidator accessValidator;
    private final ApiUtils apiUtils;
    private final MailSenderService mailSenderService;

    @Override
    @Transactional
    public IamResponse<UserProfileDTO> login(@NotNull LoginRequest request) {
        User user = userRepository.findUserByEmailAndDeletedFalse(request.getEmail())
                .orElseThrow(() -> new InvalidDataException(ApiErrorMessage.INVALID_USER_OR_PASSWORD.getMessage()));

        if (user.getRegistrationStatus() != RegistrationStatus.ACTIVE) {
            throw new InvalidDataException(ApiErrorMessage.CONFIRM_YOUR_EMAIL.getMessage());
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new InvalidDataException(ApiErrorMessage.INVALID_USER_OR_PASSWORD.getMessage());
        }

        RefreshToken refreshToken = refreshTokenService.generateOrUpdateRefreshToken(user);
        String token = jwtTokenProvider.generateToken(user);
        UserProfileDTO userProfileDTO = userMapper.toUserProfileDto(user, token, refreshToken.getToken());
        userProfileDTO.setToken(token);

        return IamResponse.createSuccessfulWithNewToken(userProfileDTO);
    }

    @Override
    @Transactional
    public IamResponse<UserProfileDTO> refreshAccessToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenService.validateAndRefreshToken(refreshTokenValue);
        User user = refreshToken.getUser();

        String accessToken = jwtTokenProvider.generateToken(user);

        return IamResponse.createSuccessfulWithNewToken(
                userMapper.toUserProfileDto(user, accessToken, refreshToken.getToken())
        );
    }

    @Override
    @Transactional
    public IamResponse<String> registerUser(@NotNull RegistrationUserRequest request) {
        userRepository.deleteByUsernameAndRegistrationStatus(request.getUsername(), RegistrationStatus.PENDING_CONFIRMATION);
        userRepository.deleteByEmailAndRegistrationStatus(request.getEmail(), RegistrationStatus.PENDING_CONFIRMATION);

        accessValidator.validateNewUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getConfirmPassword()
        );

        Role userRole = roleRepository.findByName(IamServiceUserRole.USER.getRole())
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USER_ROLE_NOT_FOUND.getMessage()));

        User newUser = userMapper.fromDto(request);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setLastLogin(LocalDateTime.now());
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        newUser.setRoles(roles);
        userRepository.save(newUser);

        EmailVerificationToken token = mailSenderService.createToken(newUser);
        mailSenderService.sendVerificationEmail(newUser.getEmail(), newUser.getUsername(), token.getToken());

        return IamResponse.createSuccessful(ApiMessage.REGISTRATION_SUCCESSFUL.getMessage());
    }

    @Override
    @Transactional
    public IamResponse<String> changePassword(ChangePasswordRequest request) {
        Integer userId = apiUtils.getUserIdFromAuthentication();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId)));

        if (PasswordUtils.isNotValidPassword(request.getPassword())) {
            throw new InvalidPasswordException(ApiErrorMessage.INVALID_PASSWORD.getMessage());
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return IamResponse.createSuccessful(ApiLogMessage.PASSWORD_CHANGED_SUCCESSFULLY.getValue());
    }
}
