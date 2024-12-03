/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

import java.util.Set;

public class BulkAddFailedException
extends Exception {
    private final Set<String> failedUsers;
    private final Set<String> existingUsers;

    public BulkAddFailedException(Set<String> failedUsers, Set<String> existingUsers) {
        this.failedUsers = failedUsers;
        this.existingUsers = existingUsers;
    }

    public BulkAddFailedException(String message, Set<String> failedUsers, Set<String> existingUsers) {
        super(message);
        this.failedUsers = failedUsers;
        this.existingUsers = existingUsers;
    }

    public BulkAddFailedException(String message, Set<String> failedUsers, Set<String> existingUsers, Throwable throwable) {
        super(message, throwable);
        this.failedUsers = failedUsers;
        this.existingUsers = existingUsers;
    }

    public Set<String> getFailedUsers() {
        return this.failedUsers;
    }

    public Set<String> getExistingUsers() {
        return this.existingUsers;
    }
}

