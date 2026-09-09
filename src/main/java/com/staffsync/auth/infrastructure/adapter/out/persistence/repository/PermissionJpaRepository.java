package com.staffsync.auth.infrastructure.adapter.out.persistence.repository;

import com.staffsync.auth.infrastructure.adapter.out.persistence.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PermissionJpaRepository extends JpaRepository<PermissionEntity, UUID> {
    Optional<PermissionEntity> findByName(String name);
    boolean existsByName(String name);
    Set<PermissionEntity> findAllByIdIn(Set<UUID> ids);
}
