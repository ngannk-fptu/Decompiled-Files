/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 */
package com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.events;

import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.events.AbstractRestoreEvent;

public class RestoreInProgressEvent
extends AbstractRestoreEvent {
    private static final long serialVersionUID = -5273196947211674276L;
    private final JobScope jobScope;
    private final long processedObjects;
    private final long totalNumberOfObjects;
    private final boolean databaseLocked;
    private final boolean isDisplayJohnson;

    public RestoreInProgressEvent(Object src, JobScope jobScope, long processedObjects, long totalNumberOfObjects, boolean databaseLocked, boolean isDisplayJohnson) {
        super(src, jobScope);
        this.jobScope = jobScope;
        this.processedObjects = processedObjects;
        this.totalNumberOfObjects = totalNumberOfObjects;
        this.databaseLocked = databaseLocked;
        this.isDisplayJohnson = isDisplayJohnson;
    }

    public long getProcessedObjects() {
        return this.processedObjects;
    }

    public long getTotalNumberOfObjects() {
        return this.totalNumberOfObjects;
    }

    @Override
    public JobScope getJobScope() {
        return this.jobScope;
    }

    public boolean isDatabaseLocked() {
        return this.databaseLocked;
    }

    public boolean isDisplayJohnson() {
        return this.isDisplayJohnson;
    }
}

