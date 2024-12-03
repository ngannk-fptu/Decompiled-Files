/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.status.service.systeminfo;

import com.atlassian.confluence.status.service.systeminfo.ClusteredDatabasePlatformType;

public class ClusteredDatabasePlatformMetadata {
    private String databaseVersion;
    private ClusteredDatabasePlatformType databaseType;
    private int databaseMemberCount;

    public ClusteredDatabasePlatformMetadata(ClusteredDatabasePlatformType databaseType, String databaseVersion, int databaseMemberCount) {
        this.databaseType = databaseType;
        this.databaseVersion = databaseVersion;
        this.databaseMemberCount = databaseMemberCount;
    }

    public String getDatabaseVersion() {
        return this.databaseVersion;
    }

    public ClusteredDatabasePlatformType getDatabaseType() {
        return this.databaseType;
    }

    public int getDatabaseMemberCount() {
        return this.databaseMemberCount;
    }
}

