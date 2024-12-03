/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.events;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;

public class TerminateBackupRestoreJobRequest
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = 1661913089274082898L;
    private final long jobId;

    public TerminateBackupRestoreJobRequest(Object src, long jobId) {
        super(src);
        this.jobId = jobId;
    }

    public long getJobId() {
        return this.jobId;
    }
}

