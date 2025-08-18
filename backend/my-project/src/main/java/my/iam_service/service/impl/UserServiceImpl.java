package my.iam_service.service.impl;

import my.iam_service.mapper.UserMapper;
import my.iam_service.model.constants.ApiErrorMessage;
import my.iam_service.model.dto.user.UserDTO;
import my.iam_service.model.dto.user.UserSearchDTO;
import my.iam_service.model.entity.Role;
import my.iam_service.model.entity.User;
import my.iam_service.model.enums.RegistrationStatus;
import my.iam_service.model.exception.DataExistException;
import my.iam_service.model.exception.NotFoundException;
import my.iam_service.model.request.user.NewUserRequest;
import my.iam_service.model.request.user.UpdateUserRequest;
import my.iam_service.model.request.user.UserSearchRequest;
import my.iam_service.model.response.IamResponse;
import my.iam_service.model.response.PaginationResponse;
import my.iam_service.repository.RoleRepository;
import my.iam_service.repository.UserRepository;
import my.iam_service.repository.criteria.UserSearchCriteria;
import my.iam_service.security.validation.AccessValidator;
import my.iam_service.service.UserService;
import my.iam_service.service.model.IamServiceUserRole;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AccessValidator accessValidator;

    @Override
    @Transactional(readOnly = true)
    public IamResponse<UserDTO> getById(@NotNull Integer userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId)));

        return IamResponse.createSuccessful(userMapper.toDto(user));
    }

    @Override
    @Transactional
    public IamResponse<UserDTO> createUser(@NotNull NewUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DataExistException(ApiErrorMessage.EMAIL_ALREADY_EXISTS.getMessage(request.getEmail()));
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DataExistException(ApiErrorMessage.USERNAME_ALREADY_EXISTS.getMessage(request.getUsername()));
        }

        Role userRole = roleRepository.findByName(IamServiceUserRole.USER.getRole())
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USER_ROLE_NOT_FOUND.getMessage()));

        User user = userMapper.createUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);


        return IamResponse.createSuccessful(userMapper.toDto(savedUser));
    }

    @Override
    @Transactional
    public IamResponse<UserDTO> updateUser(@NotNull Integer userId, UpdateUserRequest request) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId)));

        accessValidator.validateAdminOrOwnerAccess(userId);

        if (!user.getUsername().equals(request.getUsername()) && userRepository.existsByUsername(request.getUsername())) {
            throw new DataExistException(ApiErrorMessage.USERNAME_ALREADY_EXISTS.getMessage(request.getUsername()));
        }

        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new DataExistException(ApiErrorMessage.EMAIL_ALREADY_EXISTS.getMessage(request.getEmail()));
        }

        userMapper.updateUser(user, request);
        user = userRepository.save(user);


        return IamResponse.createSuccessful(userMapper.toDto(user));
    }

    @Override
    @Transactional
    public IamResponse<UserDTO> changeRoleToAdmin(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Role adminRole = roleRepository.findByName(IamServiceUserRole.ADMIN.getRole())
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USER_ROLE_NOT_FOUND.getMessage()));

        accessValidator.validateAdminAccess();
        boolean isSuperAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(IamServiceUserRole.SUPER_ADMIN.getRole()));

        if (isSuperAdmin) {
            throw new AccessDeniedException("Can't change super admin role");
        }

        user.getRoles().clear();

        user.getRoles().add(adminRole);

        userRepository.save(user);

        return IamResponse.createSuccessful(userMapper.toDto(user));

    }

    @Override
    @Transactional
    public IamResponse<UserDTO> unbanUser(Integer userId) {
        User user = userRepository.findByIdAndDeletedTrue(userId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId)));

        accessValidator.validateAdminAccess();

        user.setDeleted(false);
        userRepository.save(user);

        return IamResponse.createSuccessful(userMapper.toDto(user));
    }

    @Override
    @Transactional
    public void softDeleteUser(Integer userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(userId)));

        accessValidator.validateAdminOrOwnerAccess(userId);
        boolean isSuperAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals(IamServiceUserRole.SUPER_ADMIN.getRole()));

        if (isSuperAdmin) {
            throw new AccessDeniedException("Can't change super admin role");
        }

        user.setDeleted(true);
        userRepository.save(user);

    }

    @Override
    @Transactional(readOnly = true)
    public IamResponse<PaginationResponse<UserSearchDTO>> findAllUsers(Pageable pageable) {

        //accessValidator.validateAdminAccess();

        Page<UserSearchDTO> users = userRepository.findAllByDeletedFalseAndRegistrationStatus(RegistrationStatus.ACTIVE,pageable)
                .map(userMapper::toUserSearchDto);

        PaginationResponse<UserSearchDTO> paginationResponse = new PaginationResponse<>(
                users.getContent(),
                new PaginationResponse.Pagination(
                        users.getTotalElements(),
                        pageable.getPageSize(),
                        users.getNumber() + 1,
                        users.getTotalPages()
                )
        );

        return IamResponse.createSuccessful(paginationResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public IamResponse<PaginationResponse<UserSearchDTO>> findAllDeletedUsers(Pageable pageable) {

        //accessValidator.validateAdminAccess();

        Page<UserSearchDTO> users = userRepository.findAllByDeletedTrueAndRegistrationStatus(RegistrationStatus.ACTIVE,pageable)
                .map(userMapper::toUserSearchDto);

        PaginationResponse<UserSearchDTO> paginationResponse = new PaginationResponse<>(
                users.getContent(),
                new PaginationResponse.Pagination(
                        users.getTotalElements(),
                        pageable.getPageSize(),
                        users.getNumber() + 1,
                        users.getTotalPages()
                )
        );

        return IamResponse.createSuccessful(paginationResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public IamResponse<PaginationResponse<UserSearchDTO>> searchUsers(UserSearchRequest request, Pageable pageable) {
        Specification<User> specification = new UserSearchCriteria(request);

        Page<UserSearchDTO> usersPage = userRepository.findAll(specification, pageable)
                .map(userMapper::toUserSearchDto);

        PaginationResponse<UserSearchDTO> response = PaginationResponse.<UserSearchDTO>builder()
                .content(usersPage.getContent())
                .pagination(PaginationResponse.Pagination.builder()
                        .total(usersPage.getTotalElements())
                        .limit(pageable.getPageSize())
                        .page(usersPage.getNumber() + 1)
                        .pages(usersPage.getTotalPages())
                        .build())
                .build();

        return IamResponse.createSuccessful(response);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return getUserDetails(email, userRepository);
    }

    static UserDetails getUserDetails(String email, UserRepository userRepository) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ApiErrorMessage.EMAIL_NOT_FOUND.getMessage()));

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public IamResponse<UserDTO> getUserInfo(String username) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    UserDTO userDto = userMapper.toDto(user);
                    return IamResponse.createSuccessful(userDto);
                })
                .orElse(IamResponse.createFailed(ApiErrorMessage.USER_NOT_FOUND_BY_ID.getMessage(username)));
    }
}
