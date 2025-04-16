package stocktracker.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import stocktracker.config.helper.CustomUserDetailsService;
import stocktracker.config.jwt.JwtUtils;
import stocktracker.controller.AuthController;
import stocktracker.exception.AlreadyExistException;
import stocktracker.exception.UnauthorizedException;
import stocktracker.model.dto.request.AuthRequest;
import stocktracker.model.dto.request.RegisterRequest;
import stocktracker.model.dto.request.TokenRequest;
import stocktracker.model.dto.response.AuthResponse;
import stocktracker.model.entity.User;
import stocktracker.model.enums.Role;
import stocktracker.repository.UserRepository;
import stocktracker.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationManager authenticationManager;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, CustomUserDetailsService customUserDetailsService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.customUserDetailsService = customUserDetailsService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthResponse authenticate(AuthRequest request){
        logger.info("Поиск и аутентификация пользователя с именем: {}", request.username());

        try{
            final UserDetails user = customUserDetailsService.loadUserByUsername(request.username());

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));

            if (!passwordEncoder.matches(request.password(), user.getPassword())){
                logger.error("Неверный пароль для пользователя: {}", request.username());
                throw new BadCredentialsException("Неверный пароль!");
            }

            final String accessToken = jwtUtils.generateToken(user.getUsername(), getUserRole(user));
            logger.info("Сгенерирован JWT токен доступа для пользователя: {}", request.username());

            final String refreshToken  = jwtUtils.generateRefreshToken(user.getUsername(), getUserRole(user));
            logger.info("Сгенерирован JWT токен обновления для пользователя: {}", request.username());

            return new AuthResponse(accessToken, refreshToken);
        } catch (UsernameNotFoundException | UnauthorizedException | BadCredentialsException e) {
            logger.error("Ошибка аутентификации для пользователя: {} - {}", request.username(), e.getMessage());
            throw new BadCredentialsException("Неверный логин или пароль!");
        }
    }

    @Override
    public AuthResponse register(RegisterRequest request){
        logger.info("Регистрация пользователя с username: {}", request.username());

        if(userRepository.existsByUsername(request.username())){
            logger.warn("Пользователь с именем {} уже существует!", request.username());
            throw new AlreadyExistException("Такой username уже существует!");
        }

        User user = new User(request.username(),
                passwordEncoder.encode(request.password()),
                Role.DELIVERY);
        userRepository.save(user);

        logger.info("Пользователь {} успешно зарегистрирован!", request.username());

        final String accessToken = jwtUtils.generateToken(user.getUsername(), user.getRole().name());
        logger.info("Сгенерирован JWT токен доступа для пользователя: {}", request.username());

        final String refreshToken  = jwtUtils.generateRefreshToken(user.getUsername(), user.getRole().name());
        logger.info("Сгенерирован JWT токен обновления для пользователя: {}", request.username());

        return new AuthResponse(accessToken,refreshToken);
    }

    @Override
    public AuthResponse refreshToken(TokenRequest request) {
        if(jwtUtils.isTokenExpired(request.refreshToken())){
            logger.warn("RefreshToken просрочен: {}", request);
            throw new UnauthorizedException("Срок действия refreshToken истек!");
        }

        // Проверка типа токена
        // Проверка, что это именно refresh token
        String tokenType = jwtUtils.extractTokenType(request.refreshToken());
        if (!"refresh".equals(tokenType)) {
            logger.error("Неверный тип токена: {}", tokenType);
            throw new UnauthorizedException("Передан не refresh-токен");
        }

        // Извлечение имени пользователя
        String username = jwtUtils.extractUsername(request.refreshToken());
        if (username == null || username.isEmpty()) {
            logger.error("Не удалось извлечь имя пользователя из токена");
            throw new UnauthorizedException("Неверный токен");
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        String newAccessToken = jwtUtils.generateToken(
                userDetails.getUsername(),
                getUserRole(userDetails));
        logger.info("Сгенерирован новый accessToken для пользователя: {}", username);

        return new AuthResponse(newAccessToken, request.refreshToken());
    }

    public String getUserRole(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new IllegalStateException("Роль пользователя не найдена!"));
    }
}
