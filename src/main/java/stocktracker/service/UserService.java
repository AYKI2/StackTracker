package stocktracker.service;

import stocktracker.model.dto.request.AuthRequest;
import stocktracker.model.dto.request.RegisterRequest;
import stocktracker.model.dto.request.TokenRequest;
import stocktracker.model.dto.response.AuthResponse;

public interface UserService {
    AuthResponse authenticate(AuthRequest request);
    AuthResponse register(RegisterRequest request);
    AuthResponse refreshToken(TokenRequest request);
}
