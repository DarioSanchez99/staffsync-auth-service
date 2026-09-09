package com.staffsync.auth.domain.service;

import com.staffsync.auth.domain.model.Permission;
import com.staffsync.auth.domain.model.User;
import com.staffsync.auth.domain.port.in.AuthUseCase;
import com.staffsync.auth.domain.port.out.TokenPort;
import com.staffsync.auth.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final UserRepository userRepository;
    private final TokenPort tokenPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public TokenResponse login(LoginCommand command) {
        User user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(command.password(), user.passwordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = tokenPort.generateToken(user);
        String roleName = user.role() != null ? user.role().name() : null;
        List<String> permissions = user.role() != null
                ? user.role().permissions().stream()
                        .map(Permission::name)
                        .collect(Collectors.toList())
                : List.of();

        return new TokenResponse(token, user.id(), roleName, permissions);
    }
}
