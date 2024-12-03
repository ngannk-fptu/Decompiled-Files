/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.retention.status;

import com.atlassian.confluence.impl.retention.status.TrashCleanupJobStatus;

public interface TrashCleanupJobStatusManager {
    public void setCurrentStatus(TrashCleanupJobStatus var1);

    public TrashCleanupJobStatus getCurrentStatus();
}

