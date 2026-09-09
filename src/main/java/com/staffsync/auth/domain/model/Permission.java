package com.staffsync.auth.domain.model;

import java.util.UUID;

/**
 * Pure domain object – no JPA annotations.
 */
public record Permission(UUID id, String name) {

    public static final String EMPLOYEE_READ    = "EMPLOYEE_READ";
    public static final String EMPLOYEE_WRITE   = "EMPLOYEE_WRITE";
    public static final String EMPLOYEE_DELETE  = "EMPLOYEE_DELETE";
    public static final String SCHEDULE_READ    = "SCHEDULE_READ";
    public static final String SCHEDULE_WRITE   = "SCHEDULE_WRITE";
    public static final String VACATION_REQUEST = "VACATION_REQUEST";
    public static final String VACATION_APPROVE = "VACATION_APPROVE";
    public static final String NOTIFICATION_READ = "NOTIFICATION_READ";
}
