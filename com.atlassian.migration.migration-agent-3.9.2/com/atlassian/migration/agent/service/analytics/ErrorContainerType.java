/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.analytics;

import lombok.Generated;

public enum ErrorContainerType {
    MIGRATION_ERROR("migrationId"),
    PREFLIGHT_ERROR("executionId");

    private final String name;

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    private ErrorContainerType(String name) {
        this.name = name;
    }
}

