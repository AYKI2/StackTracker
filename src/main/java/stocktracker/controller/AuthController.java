package stocktracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stocktracker.model.dto.request.AuthRequest;
import stocktracker.model.dto.request.RegisterRequest;
import stocktracker.model.dto.request.TokenRequest;
import stocktracker.model.dto.response.AuthResponse;
import stocktracker.service.UserService;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication controller", description = "Контроллер для управления процессами аутентификации и авторизации.")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(
            summary = "Регистрация пользователя",
            description = "Создает нового пользователя с указанными данными. Возвращает access и refresh токены для авторизации."
    )
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Аутентификация пользователя",
            description = "Позволяет авторизоваться существующему пользователю. Возвращает access и refresh токены."
    )
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(userService.authenticate(request));
    }

    @PostMapping("/refresh-token")
    @Operation(
            summary = "Обновление токена",
            description = "Обновляет access токен с использованием refresh токена. Возвращает новый access токен и старый refresh токен."
    )
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody @Valid TokenRequest request) {
        return ResponseEntity.ok(userService.refreshToken(request));
    }
}
