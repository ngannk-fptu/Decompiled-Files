/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.schedule.db.limit;

import com.atlassian.audit.ao.dao.AuditEntityDao;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbLimiterJobRunner
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(DbLimiterJobRunner.class);
    private final AuditEntityDao auditEntityDao;
    private final int rowsLimit;
    private final int rowsLimitBuffer;

    public DbLimiterJobRunner(AuditEntityDao auditEntityDao, int dbRowLimit, int rowsLimitBuffer) {
        this.auditEntityDao = auditEntityDao;
        this.rowsLimit = dbRowLimit;
        this.rowsLimitBuffer = rowsLimitBuffer;
    }

    @Nonnull
    public JobRunnerResponse runJob(JobRunnerRequest jobRunnerRequest) {
        try {
            log.info("DbLimiterJobRunner Started");
            int rowsToRetain = this.rowsLimit - this.rowsLimitBuffer;
            if (rowsToRetain < 10000) {
                log.warn("System property {}={} is too small, {} is used to truncate the audit DB", new Object[]{"plugin.audit.db.limit.rows", this.rowsLimit, 10000});
            }
            this.auditEntityDao.retainRecent(Math.max(rowsToRetain, 10000));
            log.info("DbLimiterJobRunner Finished");
            return JobRunnerResponse.success();
        }
        catch (RuntimeException e) {
            log.error("Failed to execute DbLimiterJob", (Throwable)e);
            return JobRunnerResponse.failed((Throwable)e);
        }
    }
}

