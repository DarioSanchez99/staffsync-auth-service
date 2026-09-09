package com.staffsync.auth.domain.service;

import com.staffsync.auth.domain.model.Role;
import com.staffsync.auth.domain.model.User;
import com.staffsync.auth.domain.port.in.UserManagementUseCase;
import com.staffsync.auth.domain.port.out.RoleRepository;
import com.staffsync.auth.domain.port.out.TokenPort;
import com.staffsync.auth.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserManagementService implements UserManagementUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenPort tokenPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public TokenResponse register(RegisterCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new IllegalStateException("Email already registered: " + command.email());
        }

        Role role = null;
        if (command.roleId() != null) {
            role = roleRepository.findById(command.roleId())
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: " + command.roleId()));
        } else {
            // Default to EMPLOYEE role if exists
            role = roleRepository.findByName("EMPLOYEE").orElse(null);
        }

        User user = new User(
                UUID.randomUUID(),
                command.email(),
                passwordEncoder.encode(command.password()),
                command.name(),
                role,
                Instant.now()
        );

        User saved = userRepository.save(user);
        String token = tokenPort.generateToken(saved);
        return new TokenResponse(token, saved.id());
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    @Override
    public List<User> listAll() {
        return userRepository.findAll();
    }

    @Override
    public User assignRole(UUID userId, UUID roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleId));

        User updated = new User(user.id(), user.email(), user.passwordHash(), user.name(), role, user.createdAt());
        return userRepository.save(updated);
    }
}
