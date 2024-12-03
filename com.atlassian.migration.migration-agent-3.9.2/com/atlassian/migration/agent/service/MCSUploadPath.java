/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service;

import lombok.Generated;

public enum MCSUploadPath {
    MIGRATION_ID("migrationId"),
    MIGRATION_SCOPE_ID("migrationScopeId");

    private final String value;

    @Generated
    private MCSUploadPath(String value) {
        this.value = value;
    }

    @Generated
    public String getValue() {
        return this.value;
    }
}

