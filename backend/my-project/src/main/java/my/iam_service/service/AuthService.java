package my.iam_service.service;

import my.iam_service.model.request.user.ChangePasswordRequest;
import my.iam_service.model.request.user.LoginRequest;
import my.iam_service.model.dto.user.UserProfileDTO;
import my.iam_service.model.request.user.RegistrationUserRequest;
import my.iam_service.model.response.IamResponse;

public interface AuthService {

    IamResponse<UserProfileDTO> login(LoginRequest request);

    IamResponse<UserProfileDTO> refreshAccessToken(String refreshToken);

    IamResponse<String> registerUser(RegistrationUserRequest request);

    IamResponse<String> changePassword(ChangePasswordRequest request);
}
