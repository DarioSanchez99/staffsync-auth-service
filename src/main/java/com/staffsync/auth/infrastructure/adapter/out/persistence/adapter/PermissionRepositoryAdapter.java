package com.staffsync.auth.infrastructure.adapter.out.persistence.adapter;

import com.staffsync.auth.domain.model.Permission;
import com.staffsync.auth.domain.port.out.PermissionRepository;
import com.staffsync.auth.infrastructure.adapter.out.persistence.mapper.PermissionMapper;
import com.staffsync.auth.infrastructure.adapter.out.persistence.repository.PermissionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PermissionRepositoryAdapter implements PermissionRepository {

    private final PermissionJpaRepository jpaRepository;
    private final PermissionMapper mapper;

    @Override
    public Permission save(Permission permission) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(permission)));
    }

    @Override
    public Optional<Permission> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Permission> findByName(String name) {
        return jpaRepository.findByName(name).map(mapper::toDomain);
    }

    @Override
    public List<Permission> findAll() {
        return mapper.toDomainList(jpaRepository.findAll());
    }

    @Override
    public Set<Permission> findAllByIds(Set<UUID> ids) {
        return mapper.toDomainSet(jpaRepository.findAllByIdIn(ids));
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }
}
