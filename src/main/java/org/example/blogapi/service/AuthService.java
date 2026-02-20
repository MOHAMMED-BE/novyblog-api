package org.example.blogapi.service;


import org.example.blogapi.api.dto.auth.RegisterRequest;
import org.example.blogapi.domain.entity.User;
import org.example.blogapi.domain.enums.Role;
import org.example.blogapi.domain.repository.UserRepository;
import org.example.blogapi.security.JwtService;
import org.example.blogapi.api.dto.auth.LoginRequest;
import org.example.blogapi.api.dto.auth.LoginResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateAccessToken(user);

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.accessTokenExpiresInSeconds(),
                user.getEmail(),
                user.getFullName(),
                user.getRoles().stream().map(Enum::name).toList()
        );
    }

    @Transactional
    public void register(RegisterRequest request) {

        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email().toLowerCase())
                .password(passwordEncoder.encode(request.password()))
                .roles(Set.of(Role.USER)) // default role
                .enabled(true)
                .accountNonLocked(true)
                .build();

        userRepository.save(user);
    }
}
