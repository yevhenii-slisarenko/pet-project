package my.iam_service.controller;

import my.iam_service.model.constants.ApiLogMessage;
import my.iam_service.model.constants.ApiMessage;
import my.iam_service.model.request.user.ChangePasswordRequest;
import my.iam_service.model.request.user.LoginRequest;
import my.iam_service.model.dto.user.UserProfileDTO;
import my.iam_service.model.request.user.RegistrationUserRequest;
import my.iam_service.model.response.IamResponse;
import my.iam_service.service.AuthService;
import my.iam_service.service.MailSenderService;
import my.iam_service.utils.ApiUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("${end.points.auth}")
public class AuthController {
    private final AuthService authService;
    private final MailSenderService mailSenderService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful authorization",
                content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{ \"token\": \"eyJhbGcIoIJIuz...\" }")))
    })
    @PostMapping("${end.points.login}")
    @Operation(summary = "User login", description = "Authenticates the user and returns an access/refresh token")
    public ResponseEntity<?> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletResponse response) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        IamResponse<UserProfileDTO> result = authService.login(request);
        Cookie authorizationCookie = ApiUtils.createAuthCookie(result.getPayload().getToken());
        response.addCookie(authorizationCookie);

        return ResponseEntity.ok(result);
    }

    @GetMapping("${end.points.refresh.token}")
    @Operation(summary = "Refresh access token", description = "Generates new access token using provided refresh token")
    public ResponseEntity<IamResponse<UserProfileDTO>> refreshToken(
            @RequestParam(name = "token") String refreshToken,
            HttpServletResponse response) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        IamResponse<UserProfileDTO> result = authService.refreshAccessToken(refreshToken);
        Cookie authorizationCookie = ApiUtils.createAuthCookie(result.getPayload().getToken());
        response.addCookie(authorizationCookie);

        return ResponseEntity.ok(result);
    }

    @PostMapping("${end.points.register}")
    @Operation(summary = "Register a new user", description = "Creates new user and returns authentication details")
    public ResponseEntity<?> register(
            @RequestBody @Valid RegistrationUserRequest request) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        IamResponse<String> result = authService.registerUser(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("${end.points.password.reset}")
    @Operation(summary = "Change user password", description = "Allows authenticated user to change their password")
    public ResponseEntity<IamResponse<String>> changePassword(
            @RequestBody @Valid ChangePasswordRequest request) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        IamResponse<String> result = authService.changePassword(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("${end.points.logout}")
    @Operation(summary = "Logout", description = "Logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        Cookie authorizationCookie = ApiUtils.blockAuthCookie();
        response.addCookie(authorizationCookie);

        return ResponseEntity.ok().build();
    }

    @GetMapping("${end.points.confirm}")
    @Operation(summary = "Email confirmation", description = "Email confirmation")
    public ResponseEntity<IamResponse<String>> registerConfirmation(
            @RequestParam String token) {
        log.trace(ApiLogMessage.NAME_OF_CURRENT_METHOD.getValue(), ApiUtils.getMethodName());

        mailSenderService.validateToken(token);
        return ResponseEntity.ok(IamResponse.createSuccessful(ApiMessage.EMAIL_CONFIRMED.getMessage()));
    }
}
