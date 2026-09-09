package com.staffsync.auth.infrastructure.adapter.out.persistence.adapter;

import com.staffsync.auth.domain.model.Role;
import com.staffsync.auth.domain.port.out.RoleRepository;
import com.staffsync.auth.infrastructure.adapter.out.persistence.entity.RoleEntity;
import com.staffsync.auth.infrastructure.adapter.out.persistence.mapper.PermissionMapper;
import com.staffsync.auth.infrastructure.adapter.out.persistence.mapper.RoleMapper;
import com.staffsync.auth.infrastructure.adapter.out.persistence.repository.PermissionJpaRepository;
import com.staffsync.auth.infrastructure.adapter.out.persistence.repository.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepository {

    private final RoleJpaRepository jpaRepository;
    private final PermissionJpaRepository permissionJpaRepository;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    @Override
    public Role save(Role role) {
        RoleEntity entity;
        if (role.id() != null) {
            entity = jpaRepository.findById(role.id()).orElse(new RoleEntity());
        } else {
            entity = new RoleEntity();
        }
        entity.setId(role.id());
        entity.setName(role.name());
        entity.setPermissions(
                role.permissions().stream()
                        .map(permissionMapper::toEntity)
                        .collect(Collectors.toSet())
        );
        return roleMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Role> findById(UUID id) {
        return jpaRepository.findById(id).map(roleMapper::toDomain);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return jpaRepository.findByName(name).map(roleMapper::toDomain);
    }

    @Override
    public List<Role> findAll() {
        return roleMapper.toDomainList(jpaRepository.findAll());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }
}
