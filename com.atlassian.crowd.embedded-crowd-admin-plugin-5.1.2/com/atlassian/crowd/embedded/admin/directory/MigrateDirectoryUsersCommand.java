/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.admin.directory;

public final class MigrateDirectoryUsersCommand {
    private long fromDirectoryId;
    private long toDirectoryId;
    private long totalCount;
    private long migratedCount;
    private boolean testSuccessful;

    public long getFromDirectoryId() {
        return this.fromDirectoryId;
    }

    public void setFromDirectoryId(long fromDirectoryId) {
        this.fromDirectoryId = fromDirectoryId;
    }

    public long getToDirectoryId() {
        return this.toDirectoryId;
    }

    public void setToDirectoryId(long toDirectoryId) {
        this.toDirectoryId = toDirectoryId;
    }

    public long getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getMigratedCount() {
        return this.migratedCount;
    }

    public void setMigratedCount(long migratedCount) {
        this.migratedCount = migratedCount;
    }

    public boolean isTestSuccessful() {
        return this.testSuccessful;
    }

    public void setTestSuccessful(boolean testSuccessful) {
        this.testSuccessful = testSuccessful;
    }
}

