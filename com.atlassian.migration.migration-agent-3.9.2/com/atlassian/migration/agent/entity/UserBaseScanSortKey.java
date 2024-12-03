/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.entity;

public enum UserBaseScanSortKey {
    USERNAME("userName"),
    CURRENT_EMAIL("email"),
    LAST_AUTHENTICATED("lastAuthenticated"),
    DIRECTORY_NAME("directoryName"),
    ON_MIGRATION("lastAuthenticated");

    final String databaseColumnToSortBy;

    private UserBaseScanSortKey(String databaseColumnToSortBy) {
        this.databaseColumnToSortBy = databaseColumnToSortBy;
    }

    public String getDatabaseColumnToSortBy() {
        return this.databaseColumnToSortBy;
    }
}

