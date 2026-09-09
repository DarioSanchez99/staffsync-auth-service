package com.staffsync.auth.infrastructure.config;

import com.staffsync.auth.domain.model.Permission;
import com.staffsync.auth.domain.model.Role;
import com.staffsync.auth.domain.model.User;
import com.staffsync.auth.domain.port.out.PermissionRepository;
import com.staffsync.auth.domain.port.out.RoleRepository;
import com.staffsync.auth.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner seedData() {
        return args -> {
            // Only seed if permissions table is empty
            List<Permission> existingPermissions = permissionRepository.findAll();
            if (!existingPermissions.isEmpty()) {
                log.info("Data already seeded, skipping initialization.");
                return;
            }

            log.info("Seeding initial data...");

            // 1. Create permissions
            List<String> permissionNames = List.of(
                    Permission.EMPLOYEE_READ,
                    Permission.EMPLOYEE_WRITE,
                    Permission.EMPLOYEE_DELETE,
                    Permission.SCHEDULE_READ,
                    Permission.SCHEDULE_WRITE,
                    Permission.VACATION_REQUEST,
                    Permission.VACATION_APPROVE,
                    Permission.NOTIFICATION_READ
            );

            var savedPermissions = permissionNames.stream()
                    .map(name -> permissionRepository.save(new Permission(null, name)))
                    .collect(Collectors.toMap(Permission::name, Function.identity()));

            // 2. Create roles
            Set<Permission> allPermissions = Set.copyOf(savedPermissions.values());
            Set<Permission> managerPermissions = Set.of(
                    savedPermissions.get(Permission.EMPLOYEE_READ),
                    savedPermissions.get(Permission.SCHEDULE_READ),
                    savedPermissions.get(Permission.SCHEDULE_WRITE),
                    savedPermissions.get(Permission.VACATION_APPROVE),
                    savedPermissions.get(Permission.NOTIFICATION_READ)
            );
            Set<Permission> employeePermissions = Set.of(
                    savedPermissions.get(Permission.EMPLOYEE_READ),
                    savedPermissions.get(Permission.SCHEDULE_READ),
                    savedPermissions.get(Permission.VACATION_REQUEST),
                    savedPermissions.get(Permission.NOTIFICATION_READ)
            );

            Role adminRole    = roleRepository.save(new Role(null, "ADMIN",    allPermissions));
            Role managerRole  = roleRepository.save(new Role(null, "MANAGER",  managerPermissions));
            Role employeeRole = roleRepository.save(new Role(null, "EMPLOYEE", employeePermissions));

            // 3. Create default admin user
            if (!userRepository.existsByEmail("admin@staffsync.com")) {
                User admin = new User(
                        UUID.randomUUID(),
                        "admin@staffsync.com",
                        passwordEncoder.encode("admin123"),
                        "System Admin",
                        adminRole,
                        Instant.now()
                );
                userRepository.save(admin);
                log.info("Default admin user created: admin@staffsync.com");
            }

            log.info("Data seeding complete. Permissions: {}, Roles: ADMIN/MANAGER/EMPLOYEE", permissionNames.size());
        };
    }
}
