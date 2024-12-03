/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.user;

public class UsersMigrationException
extends RuntimeException {
    public UsersMigrationException(String message) {
        super(message);
    }

    public UsersMigrationException(String message, Throwable cause) {
        super(message, cause);
    }
}

