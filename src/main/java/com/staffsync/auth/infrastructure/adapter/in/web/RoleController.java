package com.staffsync.auth.infrastructure.adapter.in.web;

import com.staffsync.auth.domain.model.Permission;
import com.staffsync.auth.domain.model.Role;
import com.staffsync.auth.domain.port.in.RoleUseCase;
import com.staffsync.auth.generated.api.RolesApi;
import com.staffsync.auth.generated.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class RoleController implements RolesApi {

    private final RoleUseCase roleUseCase;

    @Override
    public ResponseEntity<List<RoleResponse>> listRoles() {
        return ResponseEntity.ok(roleUseCase.listAll().stream()
                .map(this::toRoleResponse)
                .toList());
    }

    @Override
    public ResponseEntity<RoleResponse> createRole(CreateRoleRequest createRoleRequest) {
        Set<UUID> permissionIds = createRoleRequest.getPermissionIds() != null
                ? Set.copyOf(createRoleRequest.getPermissionIds()) // List<UUID> → Set<UUID>
                : Set.of();
        Role role = roleUseCase.create(new RoleUseCase.CreateRoleCommand(createRoleRequest.getName(), permissionIds));
        return ResponseEntity.status(HttpStatus.CREATED).body(toRoleResponse(role));
    }

    @Override
    public ResponseEntity<RoleResponse> setRolePermissions(UUID id, SetPermissionsRequest setPermissionsRequest) {
        Set<UUID> permissionIds = Set.copyOf(setPermissionsRequest.getPermissionIds()); // List<UUID> → Set<UUID>
        Role updated = roleUseCase.setPermissions(id, permissionIds);
        return ResponseEntity.ok(toRoleResponse(updated));
    }

    @Override
    public ResponseEntity<Void> deleteRole(UUID id) {
        roleUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<PermissionResponse>> listPermissions() {
        List<PermissionResponse> perms = roleUseCase.listAllPermissions().stream()
                .map(p -> new PermissionResponse().id(p.id()).name(p.name()))
                .toList();
        return ResponseEntity.ok(perms);
    }

    private RoleResponse toRoleResponse(Role role) {
        List<PermissionResponse> perms = role.permissions().stream()
                .map(p -> new PermissionResponse().id(p.id()).name(p.name()))
                .toList();
        return new RoleResponse()
                .id(role.id())
                .name(role.name())
                .permissions(perms);
    }
}
