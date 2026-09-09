package com.staffsync.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Pure domain object – no JPA annotations.
 */
public record User(
        UUID id,
        String email,
        String passwordHash,
        String name,
        Role role,
        Instant createdAt
) {
}
