/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.Critical
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.upgrade;

import com.atlassian.analytics.api.annotations.Critical;
import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
@EventName(value="confluence.filestructure.log")
@Critical
public class AttachmentMigrationAnalyticsEvent {
    private final int numDuplicates;
    private final int numFailedToMigrate;
    private final int numCores;
    private final String migrationResult;

    public AttachmentMigrationAnalyticsEvent(int numDuplicates, int numFailedToMigrate, int numCores, String migrationResult) {
        this.numDuplicates = numDuplicates;
        this.numFailedToMigrate = numFailedToMigrate;
        this.numCores = numCores;
        this.migrationResult = migrationResult;
    }

    public int getNumDuplicates() {
        return this.numDuplicates;
    }

    public int getNumFailedToMigrate() {
        return this.numFailedToMigrate;
    }

    public int getNumCores() {
        return this.numCores;
    }

    public String getMigrationResult() {
        return this.migrationResult;
    }
}

