/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.newexport;

import com.atlassian.migration.agent.newexport.CSVExportTaskContext;
import lombok.Generated;

public class SpaceCSVExportTaskContext
extends CSVExportTaskContext {
    private final long spaceId;
    private final String spaceKey;
    private final boolean usersCreatedInUMS;

    public SpaceCSVExportTaskContext(long spaceId, String spaceKey, String cloudId, String planId, String taskId, String tempDirFilePath, boolean usersCreatedInUMS) {
        super(cloudId, planId, taskId, tempDirFilePath);
        this.spaceId = spaceId;
        this.spaceKey = spaceKey;
        this.usersCreatedInUMS = usersCreatedInUMS;
    }

    @Generated
    public long getSpaceId() {
        return this.spaceId;
    }

    @Generated
    public String getSpaceKey() {
        return this.spaceKey;
    }

    @Generated
    public boolean isUsersCreatedInUMS() {
        return this.usersCreatedInUMS;
    }
}

