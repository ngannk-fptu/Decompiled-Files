/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.manager.audit;

import com.atlassian.crowd.manager.audit.InternalAuditService;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditLogPruner
implements JobRunner {
    private static final Logger logger = LoggerFactory.getLogger(AuditLogPruner.class);
    private final InternalAuditService internalAuditService;

    public AuditLogPruner(InternalAuditService internalAuditService) {
        this.internalAuditService = internalAuditService;
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        try {
            logger.debug("Cleaning stale audit log entries");
            this.internalAuditService.removeStaleEntries();
            return JobRunnerResponse.success();
        }
        catch (Exception e) {
            logger.warn("Could not remove stale audit log entries", (Throwable)e);
            return JobRunnerResponse.failed((Throwable)e);
        }
    }
}

