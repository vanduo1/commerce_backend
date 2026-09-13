package com.sara.commerce_platform.auth;

import com.sara.commerce_platform.auth.dto.LoginRequest;
import com.sara.commerce_platform.auth.dto.AuthResponse;
import com.sara.commerce_platform.auth.dto.RegisterRequest;
import com.sara.commerce_platform.common.exception.EmailAlreadyExistsException;
import com.sara.commerce_platform.user.UserMapper;
import com.sara.commerce_platform.user.UserRepository;
import com.sara.commerce_platform.user.dto.UserResponse;
import com.sara.commerce_platform.user.entity.User;
import com.sara.commerce_platform.user.entity.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final InvalidatedTokenRepository invalidatedTokenRepository;


    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, JwtService jwtService, InvalidatedTokenRepository invalidatedTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.invalidatedTokenRepository = invalidatedTokenRepository;
    }

    public UserResponse register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }
        User user = userMapper.toUser(request);

        user.setPassword(passwordEncoder.encode(request.password()));

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

        // demo02@email     123
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email()).orElseThrow();

        String token = jwtService.generateToken(new UserPrincipal(user));

        return new AuthResponse(token);
    }

    public void logout(String token) {
        try {
            System.out.println(token);
            // parse token to get expiration date
            Date expirationDate = jwtService.extractExpirationDate(token);

            // save token to Blacklist
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(token)
                    .expiryTime(expirationDate)
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
