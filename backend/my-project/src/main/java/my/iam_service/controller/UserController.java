package my.iam_service.controller;

import my.iam_service.model.constants.ApiLogMessage;
import my.iam_service.model.dto.user.UserDTO;
import my.iam_service.model.dto.user.UserSearchDTO;
import my.iam_service.model.request.user.NewUserRequest;
import my.iam_service.model.request.user.UpdateUserRequest;
import my.iam_service.model.request.user.UserSearchRequest;
import my.iam_service.model.response.IamResponse;
import my.iam_service.model.response.PaginationResponse;
import my.iam_service.service.UserService;
import my.iam_service.utils.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("${end.points.users}")
public class UserController {
    private final UserService userService;

    @GetMapping("${end.points.id}")
    @Operation(summary = "Get User by ID", description = "Retrieves user details by their unique identifier")
    public ResponseEntity<IamResponse<UserDTO>> getUserById(
            @PathVariable(name = "id") Integer userId) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        IamResponse<UserDTO> response = userService.getById(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("${end.points.create}")
    @Operation(summary = "Create a new User [only for Admins]", description = "Registers a new user in the system")
    public ResponseEntity<IamResponse<UserDTO>> createUser(
            @RequestBody @Valid NewUserRequest request) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        IamResponse<UserDTO> createdUser = userService.createUser(request);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/make+admin/{id}")
    @Operation(summary = "Make user an admin", description = "Make admin")
    public ResponseEntity<IamResponse<UserDTO>> makeAdmin(@PathVariable(name = "id") Integer userId) {

        IamResponse<UserDTO> response = userService.changeRoleToAdmin(userId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/unban/{id}")
    @Operation(summary = "Make user an admin", description = "Make admin")
    public ResponseEntity<IamResponse<UserDTO>> unbanUser(@PathVariable(name = "id") Integer userId) {

        IamResponse<UserDTO> response = userService.unbanUser(userId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("${end.points.id}")
    @Operation(summary = "Update User", description = "Updates an existing user by their ID")
    public ResponseEntity<IamResponse<UserDTO>> updateUserById(
            @PathVariable(name = "id") Integer userId,
            @RequestBody @Valid UpdateUserRequest request) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        IamResponse<UserDTO> updatedPost = userService.updateUser(userId, request);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("${end.points.id}")
    @Operation(summary = "Delete User", description = "Marks a user as deleted without removing them from the database")
    public ResponseEntity<Void> softDeleteUser(
            @PathVariable(name = "id") Integer userId
    ) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        userService.softDeleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("${end.points.all}")
    @Operation(summary = "Get all Users", description = "Retrieves a paginated list of all registered users")
    public ResponseEntity<IamResponse<PaginationResponse<UserSearchDTO>>> getAllUsers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "100") int limit) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        Pageable pageable = PageRequest.of(page, limit, Sort.by("id").descending());
        IamResponse<PaginationResponse<UserSearchDTO>> response = userService.findAllUsers(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all/deleted")
    @Operation(summary = "Get all deleted Users", description = "Retrieves a paginated list of all registered deleted users")
    public ResponseEntity<IamResponse<PaginationResponse<UserSearchDTO>>> getAllDeletedUsers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        Pageable pageable = PageRequest.of(page, limit, Sort.by("id").descending());
        IamResponse<PaginationResponse<UserSearchDTO>> response = userService.findAllDeletedUsers(pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("${end.points.search}")
    @Operation(summary = "Search Users", description = "Filters users based on search criteria and pagination settings")
    public ResponseEntity<IamResponse<PaginationResponse<UserSearchDTO>>> searchUsers(
            @RequestBody @Valid UserSearchRequest request,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        Pageable pageable = PageRequest.of(page, limit);
        IamResponse<PaginationResponse<UserSearchDTO>> response = userService.searchUsers(request, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("${end.points.info}")
    @Operation(summary = "Get user info", description = "Get user info")
    public ResponseEntity<IamResponse<UserDTO>> getUserData(Principal principal) {
        IamResponse<UserDTO> response = userService.getUserInfo(principal.getName());
        return ResponseEntity.ok(response);
    }
}
