package com.staffsync.auth.infrastructure.adapter.in.web;

import com.staffsync.auth.domain.port.in.AuthUseCase;
import com.staffsync.auth.domain.port.in.UserManagementUseCase;
import com.staffsync.auth.generated.api.AuthApi;
import com.staffsync.auth.generated.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthUseCase authUseCase;
    private final UserManagementUseCase userManagementUseCase;

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest) {
        AuthUseCase.TokenResponse result = authUseCase.login(
                new AuthUseCase.LoginCommand(loginRequest.getEmail(), loginRequest.getPassword())
        );
        LoginResponse response = new LoginResponse()
                .token(result.token())
                .userId(result.userId())
                .role(result.role())
                .permissions(result.permissions());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<TokenResponse> register(RegisterRequest registerRequest) {
        UUID roleId = registerRequest.getRoleId(); // already UUID
        UserManagementUseCase.TokenResponse result = userManagementUseCase.register(
                new UserManagementUseCase.RegisterCommand(
                        registerRequest.getEmail(),
                        registerRequest.getPassword(),
                        registerRequest.getName(),
                        roleId
                )
        );
        TokenResponse response = new TokenResponse()
                .token(result.token())
                .userId(result.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<UserResponse> getCurrentUser(UUID xUserId) {
        com.staffsync.auth.domain.model.User user = userManagementUseCase.findById(xUserId);
        return ResponseEntity.ok(toUserResponse(user));
    }

    @PutMapping("/auth/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody Map<String, String> body) {
        userManagementUseCase.changePassword(userId, body.get("currentPassword"), body.get("newPassword"));
        return ResponseEntity.noContent().build();
    }

    private UserResponse toUserResponse(com.staffsync.auth.domain.model.User user) {
        UserResponse response = new UserResponse()
                .id(user.id())
                .email(user.email())
                .name(user.name())
                .createdAt(user.createdAt() != null
                        ? OffsetDateTime.ofInstant(user.createdAt(), ZoneOffset.UTC)
                        : null);
        if (user.role() != null) {
            RoleResponse roleResponse = new RoleResponse()
                    .id(user.role().id())
                    .name(user.role().name())
                    .permissions(user.role().permissions().stream()
                            .map(p -> new PermissionResponse().id(p.id()).name(p.name()))
                            .toList());
            response.role(roleResponse);
        }
        return response;
    }
}
