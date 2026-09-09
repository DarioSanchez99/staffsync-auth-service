package com.staffsync.auth.domain.port.in;

import com.staffsync.auth.domain.model.Permission;
import com.staffsync.auth.domain.model.Role;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface RoleUseCase {

    record CreateRoleCommand(String name, Set<UUID> permissionIds) {}

    List<Role> listAll();

    Role create(CreateRoleCommand command);

    Role setPermissions(UUID roleId, Set<UUID> permissionIds);

    void delete(UUID roleId);

    List<Permission> listAllPermissions();
}
