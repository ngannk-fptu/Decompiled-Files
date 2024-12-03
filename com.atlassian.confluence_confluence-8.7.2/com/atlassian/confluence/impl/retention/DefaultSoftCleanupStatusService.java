/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.api.model.retention.SoftCleanupJobStatus
 *  com.atlassian.confluence.api.service.retention.SoftCleanupStatusService
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.retention;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.api.model.retention.SoftCleanupJobStatus;
import com.atlassian.confluence.api.service.retention.SoftCleanupStatusService;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSoftCleanupStatusService
implements SoftCleanupStatusService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultSoftCleanupStatusService.class);
    public static final String VERSIONS_REMOVAL_STATUS_KEY = "com.atlassian.confluence.impl.content.retentionrules:versions-removal-job-status";
    private final BandanaManager bandanaManager;
    private ObjectMapper objectMapper;

    public DefaultSoftCleanupStatusService(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
        this.objectMapper = new ObjectMapper();
    }

    public SoftCleanupJobStatus getCurrentStatus() {
        Object status = this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, VERSIONS_REMOVAL_STATUS_KEY);
        return this.getExistingVersionsRemovalStatus(status);
    }

    public void setCurrentStatus(SoftCleanupJobStatus status) {
        try {
            this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, VERSIONS_REMOVAL_STATUS_KEY, (Object)this.objectMapper.writeValueAsString((Object)status));
        }
        catch (IOException e) {
            logger.error("Error writing VersionsRemovalStatus: {}", (Object)e.getMessage());
        }
    }

    private SoftCleanupJobStatus getExistingVersionsRemovalStatus(Object status) {
        logger.debug("Existing VersionsRemovalStatus is: {}", status);
        if (status != null) {
            try {
                return (SoftCleanupJobStatus)this.objectMapper.readValue((String)status, SoftCleanupJobStatus.class);
            }
            catch (IOException e) {
                logger.error("Error parsing VersionsRemovalStatus: {}", (Object)e.getMessage());
            }
        }
        logger.debug("Returning default VersionsRemovalStatus");
        return new SoftCleanupJobStatus();
    }
}

