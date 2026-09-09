package com.staffsync.auth.domain.service;

import com.staffsync.auth.domain.model.Permission;
import com.staffsync.auth.domain.model.Role;
import com.staffsync.auth.domain.port.in.RoleUseCase;
import com.staffsync.auth.domain.port.out.PermissionRepository;
import com.staffsync.auth.domain.port.out.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService implements RoleUseCase {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public List<Role> listAll() {
        return roleRepository.findAll();
    }

    @Override
    public Role create(CreateRoleCommand command) {
        if (roleRepository.existsByName(command.name())) {
            throw new IllegalStateException("Role already exists: " + command.name());
        }

        Set<Permission> permissions = command.permissionIds() != null
                ? permissionRepository.findAllByIds(command.permissionIds())
                : Set.of();

        Role role = new Role(UUID.randomUUID(), command.name(), permissions);
        return roleRepository.save(role);
    }

    @Override
    public Role setPermissions(UUID roleId, Set<UUID> permissionIds) {
        Role existing = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleId));

        Set<Permission> permissions = permissionRepository.findAllByIds(permissionIds);
        Role updated = new Role(existing.id(), existing.name(), permissions);
        return roleRepository.save(updated);
    }

    @Override
    public void delete(UUID roleId) {
        if (!roleRepository.findById(roleId).isPresent()) {
            throw new IllegalArgumentException("Role not found: " + roleId);
        }
        roleRepository.deleteById(roleId);
    }

    @Override
    public List<Permission> listAllPermissions() {
        return permissionRepository.findAll();
    }
}
