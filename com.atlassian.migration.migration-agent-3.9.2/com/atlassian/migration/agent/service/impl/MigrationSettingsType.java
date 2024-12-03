/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.impl;

public enum MigrationSettingsType {
    CLOUD_TYPE("cloudType"),
    CONCURRENCY("concurrency");

    private final String type;

    private MigrationSettingsType(String type) {
        this.type = type;
    }

    public static MigrationSettingsType getByType(String value) {
        for (MigrationSettingsType migrationSettingsType : MigrationSettingsType.values()) {
            if (!migrationSettingsType.type.equalsIgnoreCase(value)) continue;
            return migrationSettingsType;
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}

