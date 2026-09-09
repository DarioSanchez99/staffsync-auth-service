package com.staffsync.auth.domain.port.in;

import com.staffsync.auth.domain.model.User;

public interface AuthUseCase {

    record LoginCommand(String email, String password) {}

    record TokenResponse(String token, java.util.UUID userId, String role, java.util.List<String> permissions) {}

    TokenResponse login(LoginCommand command);
}
