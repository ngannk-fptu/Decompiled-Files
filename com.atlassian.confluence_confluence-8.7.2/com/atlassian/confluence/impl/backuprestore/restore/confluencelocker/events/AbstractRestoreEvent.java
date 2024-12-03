/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobScope
 */
package com.atlassian.confluence.impl.backuprestore.restore.confluencelocker.events;

import com.atlassian.confluence.api.model.backuprestore.JobScope;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;

public abstract class AbstractRestoreEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = 1492478921836752969L;
    private final JobScope jobScope;

    protected AbstractRestoreEvent(Object src, JobScope jobScope) {
        super(src);
        this.jobScope = jobScope;
    }

    public JobScope getJobScope() {
        return this.jobScope;
    }
}

