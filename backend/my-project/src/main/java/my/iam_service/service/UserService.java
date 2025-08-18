package my.iam_service.service;

import my.iam_service.model.dto.user.UserDTO;
import my.iam_service.model.dto.user.UserSearchDTO;
import my.iam_service.model.request.user.NewUserRequest;
import my.iam_service.model.request.user.UpdateUserRequest;
import my.iam_service.model.request.user.UserSearchRequest;
import my.iam_service.model.response.IamResponse;
import my.iam_service.model.response.PaginationResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;

public interface UserService extends UserDetailsService {

    IamResponse<UserDTO> getById(@NotNull Integer userId);

    IamResponse<UserDTO> createUser(@NotNull NewUserRequest request);

    IamResponse<UserDTO> updateUser(@NotNull Integer postId, @NotNull UpdateUserRequest request);

    void softDeleteUser(Integer userId);

    IamResponse<PaginationResponse<UserSearchDTO>> findAllUsers(Pageable pageable);

    @Transactional(readOnly = true)
    IamResponse<PaginationResponse<UserSearchDTO>> findAllDeletedUsers(Pageable pageable);

    IamResponse<PaginationResponse<UserSearchDTO>> searchUsers(UserSearchRequest request, Pageable pageable);

    IamResponse<UserDTO> getUserInfo(String username);

    IamResponse<UserDTO> changeRoleToAdmin(Integer userId);

    IamResponse<UserDTO> unbanUser(Integer userId);
}
