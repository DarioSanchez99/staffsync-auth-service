package com.staffsync.auth.domain.model;

import java.util.Set;
import java.util.UUID;

/**
 * Pure domain object – no JPA annotations.
 */
public record Role(UUID id, String name, Set<Permission> permissions) {
}
