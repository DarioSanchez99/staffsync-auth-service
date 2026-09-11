package com.staffsync.auth.domain.port.in;

import com.staffsync.auth.domain.model.User;

import java.util.List;
import java.util.UUID;

public interface UserManagementUseCase {

    record RegisterCommand(String email, String password, String name, UUID roleId) {}

    record TokenResponse(String token, UUID userId) {}

    TokenResponse register(RegisterCommand command);

    User findById(UUID id);

    List<User> listAll();

    User assignRole(UUID userId, UUID roleId);

    void changePassword(UUID userId, String currentPassword, String newPassword);
}
