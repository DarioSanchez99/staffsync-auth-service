package com.staffsync.auth.infrastructure.adapter.out.persistence.adapter;

import com.staffsync.auth.domain.model.User;
import com.staffsync.auth.domain.port.out.UserRepository;
import com.staffsync.auth.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.staffsync.auth.infrastructure.adapter.out.persistence.mapper.RoleMapper;
import com.staffsync.auth.infrastructure.adapter.out.persistence.mapper.UserMapper;
import com.staffsync.auth.infrastructure.adapter.out.persistence.repository.RoleJpaRepository;
import com.staffsync.auth.infrastructure.adapter.out.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    @Override
    public User save(User user) {
        UserEntity entity = jpaRepository.findById(user.id() != null ? user.id() : UUID.randomUUID())
                .orElse(new UserEntity());
        entity.setId(user.id());
        entity.setEmail(user.email());
        entity.setPasswordHash(user.passwordHash());
        entity.setName(user.name());
        entity.setCreatedAt(user.createdAt());
        if (user.role() != null) {
            entity.setRole(roleJpaRepository.findById(user.role().id()).orElse(null));
        } else {
            entity.setRole(null);
        }
        return userMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id).map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(userMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return userMapper.toDomainList(jpaRepository.findAll());
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}
