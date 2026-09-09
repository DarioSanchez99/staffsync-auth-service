package com.staffsync.auth.service;

import com.staffsync.auth.domain.model.Permission;
import com.staffsync.auth.domain.model.Role;
import com.staffsync.auth.domain.model.User;
import com.staffsync.auth.domain.port.in.AuthUseCase;
import com.staffsync.auth.domain.port.out.UserRepository;
import com.staffsync.auth.domain.service.AuthService;
import com.staffsync.auth.infrastructure.config.JwtConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    private AuthService authService;
    private PasswordEncoder passwordEncoder;

    private static final String RAW_PASSWORD = "password123";
    private static final String WRONG_PASSWORD = "wrongPassword";

    private User testUser;
    private JwtConfig jwtConfig;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        jwtConfig = new JwtConfig("testSecretKeyForTestingAtLeast32Chars!!", 3600000L);
        authService = new AuthService(userRepository, jwtConfig, passwordEncoder);

        Permission perm = new Permission(UUID.randomUUID(), Permission.EMPLOYEE_READ);
        Role role = new Role(UUID.randomUUID(), "EMPLOYEE", Set.of(perm));
        testUser = new User(
                UUID.randomUUID(),
                "test@example.com",
                passwordEncoder.encode(RAW_PASSWORD),
                "Test User",
                role,
                Instant.now()
        );
    }

    @Test
    void login_happyPath_returnsTokenResponse() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        AuthUseCase.TokenResponse result = authService.login(
                new AuthUseCase.LoginCommand("test@example.com", RAW_PASSWORD)
        );

        assertThat(result).isNotNull();
        assertThat(result.token()).isNotBlank();
        assertThat(result.userId()).isEqualTo(testUser.id());
        assertThat(result.role()).isEqualTo("EMPLOYEE");
        assertThat(result.permissions()).contains(Permission.EMPLOYEE_READ);
    }

    @Test
    void login_wrongPassword_throwsIllegalArgumentException() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> authService.login(
                new AuthUseCase.LoginCommand("test@example.com", WRONG_PASSWORD)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void login_unknownEmail_throwsIllegalArgumentException() {
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(
                new AuthUseCase.LoginCommand("nobody@example.com", RAW_PASSWORD)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid credentials");
    }
}
