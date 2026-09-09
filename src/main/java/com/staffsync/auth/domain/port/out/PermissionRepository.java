package com.staffsync.auth.domain.port.out;

import com.staffsync.auth.domain.model.Permission;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface PermissionRepository {
    Permission save(Permission permission);
    Optional<Permission> findById(UUID id);
    Optional<Permission> findByName(String name);
    List<Permission> findAll();
    Set<Permission> findAllByIds(Set<UUID> ids);
    boolean existsByName(String name);
}
