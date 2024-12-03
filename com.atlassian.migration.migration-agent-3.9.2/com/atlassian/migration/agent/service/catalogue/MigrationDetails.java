/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.catalogue;

import lombok.Generated;

public class MigrationDetails {
    public final String migrationScopeId;
    public final String migrationId;

    @Generated
    public MigrationDetails(String migrationScopeId, String migrationId) {
        this.migrationScopeId = migrationScopeId;
        this.migrationId = migrationId;
    }
}

