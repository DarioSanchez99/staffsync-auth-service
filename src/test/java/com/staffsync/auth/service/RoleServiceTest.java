package com.staffsync.auth.service;

import com.staffsync.auth.domain.model.Permission;
import com.staffsync.auth.domain.model.Role;
import com.staffsync.auth.domain.port.out.PermissionRepository;
import com.staffsync.auth.domain.port.out.RoleRepository;
import com.staffsync.auth.domain.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    private RoleService roleService;

    private UUID roleId;
    private UUID permReadId;
    private UUID permWriteId;
    private Permission permRead;
    private Permission permWrite;
    private Role existingRole;

    @BeforeEach
    void setUp() {
        roleService = new RoleService(roleRepository, permissionRepository);

        roleId     = UUID.randomUUID();
        permReadId = UUID.randomUUID();
        permWriteId = UUID.randomUUID();
        permRead  = new Permission(permReadId,  Permission.EMPLOYEE_READ);
        permWrite = new Permission(permWriteId, Permission.EMPLOYEE_WRITE);

        Set<Permission> initialPerms = new HashSet<>();
        initialPerms.add(permRead);
        existingRole = new Role(roleId, "TEST_ROLE", initialPerms);
    }

    @Test
    void setPermissions_addPermission_roleHasBothPermissions() {
        Set<UUID> newPermIds = Set.of(permReadId, permWriteId);
        Set<Permission> newPerms = Set.of(permRead, permWrite);

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));
        when(permissionRepository.findAllByIds(newPermIds)).thenReturn(newPerms);
        when(roleRepository.save(any(Role.class))).thenAnswer(inv -> inv.getArgument(0));

        Role result = roleService.setPermissions(roleId, newPermIds);

        assertThat(result.permissions()).hasSize(2);
        assertThat(result.permissions()).extracting(Permission::name)
                .containsExactlyInAnyOrder(Permission.EMPLOYEE_READ, Permission.EMPLOYEE_WRITE);
    }

    @Test
    void setPermissions_removePermission_roleHasOnlyOnePermission() {
        Set<UUID> reducedPermIds = Set.of(permReadId);
        Set<Permission> reducedPerms = Set.of(permRead);

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));
        when(permissionRepository.findAllByIds(reducedPermIds)).thenReturn(reducedPerms);
        when(roleRepository.save(any(Role.class))).thenAnswer(inv -> inv.getArgument(0));

        Role result = roleService.setPermissions(roleId, reducedPermIds);

        assertThat(result.permissions()).hasSize(1);
        assertThat(result.permissions()).extracting(Permission::name)
                .containsExactly(Permission.EMPLOYEE_READ);
    }
}
