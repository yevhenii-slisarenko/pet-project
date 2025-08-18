package my.iam_service.service;

import my.iam_service.model.entity.RefreshToken;
import my.iam_service.model.entity.User;

public interface RefreshTokenService {

    RefreshToken generateOrUpdateRefreshToken(User user);

    RefreshToken validateAndRefreshToken(String refreshToken);

}
