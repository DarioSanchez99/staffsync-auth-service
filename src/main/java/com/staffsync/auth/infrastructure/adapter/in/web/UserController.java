package com.staffsync.auth.infrastructure.adapter.in.web;

import com.staffsync.auth.domain.model.User;
import com.staffsync.auth.domain.port.in.UserManagementUseCase;
import com.staffsync.auth.generated.api.UsersApi;
import com.staffsync.auth.generated.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController implements UsersApi {

    private final UserManagementUseCase userManagementUseCase;

    @Override
    public ResponseEntity<List<UserResponse>> listUsers(String xUserRole) {
        if (!"ADMIN".equalsIgnoreCase(xUserRole)) {
            return ResponseEntity.status(403).build();
        }
        List<UserResponse> users = userManagementUseCase.listAll().stream()
                .map(this::toUserResponse)
                .toList();
        return ResponseEntity.ok(users);
    }

    @Override
    public ResponseEntity<UserResponse> assignRole(UUID id, String xUserRole, AssignRoleRequest assignRoleRequest) {
        UUID roleId = assignRoleRequest.getRoleId(); // already UUID
        User updated = userManagementUseCase.assignRole(id, roleId);
        return ResponseEntity.ok(toUserResponse(updated));
    }

    private UserResponse toUserResponse(User user) {
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
