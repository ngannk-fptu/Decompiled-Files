/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.confluence.impl.retention.status;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.impl.retention.status.TrashCleanupJobStatus;
import com.atlassian.confluence.impl.retention.status.TrashCleanupJobStatusManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;

public class DefaultTrashCleanupJobStatusManager
implements TrashCleanupJobStatusManager {
    static final String CURRENT_TRASH_CLEANUP_JOB_STATUS_KEY = "retentionRules:trash:currentJob";
    private final BandanaManager bandanaManager;
    private final ObjectMapper objectMapper;

    public DefaultTrashCleanupJobStatusManager(BandanaManager bandanaManager) {
        this(bandanaManager, new ObjectMapper());
    }

    @VisibleForTesting
    DefaultTrashCleanupJobStatusManager(BandanaManager bandanaManager, ObjectMapper objectMapper) {
        this.bandanaManager = bandanaManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public void setCurrentStatus(TrashCleanupJobStatus status) {
        try {
            this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, CURRENT_TRASH_CLEANUP_JOB_STATUS_KEY, (Object)this.objectMapper.writeValueAsString((Object)status));
        }
        catch (IOException e) {
            throw new RuntimeException("Cannot store current job status", e);
        }
    }

    @Override
    public TrashCleanupJobStatus getCurrentStatus() {
        Object currentStatus = this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, CURRENT_TRASH_CLEANUP_JOB_STATUS_KEY);
        if (currentStatus == null) {
            return new TrashCleanupJobStatus();
        }
        try {
            return (TrashCleanupJobStatus)this.objectMapper.readValue((String)currentStatus, TrashCleanupJobStatus.class);
        }
        catch (IOException e) {
            throw new RuntimeException("Error reading current job status", e);
        }
    }
}

