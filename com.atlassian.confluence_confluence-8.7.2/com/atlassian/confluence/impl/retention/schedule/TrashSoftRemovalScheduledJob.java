/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.retention.RetentionFeatureChecker
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.retention.schedule;

import com.atlassian.confluence.api.service.retention.RetentionFeatureChecker;
import com.atlassian.confluence.impl.retention.RetentionType;
import com.atlassian.confluence.impl.retention.manager.TrashRemovalManager;
import com.atlassian.confluence.impl.retention.rules.RetentionRulesChecker;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrashSoftRemovalScheduledJob
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(TrashSoftRemovalScheduledJob.class);
    private final RetentionFeatureChecker retentionFeatureChecker;
    private final RetentionRulesChecker rulesChecker;
    private final TrashRemovalManager trashRemovalManager;
    static final int BATCH_SIZE = Integer.getInteger("confluence.trash.soft.removal.batch.size", 100);

    public TrashSoftRemovalScheduledJob(RetentionFeatureChecker retentionFeatureChecker, RetentionRulesChecker rulesChecker, TrashRemovalManager trashRemovalManager) {
        this.retentionFeatureChecker = retentionFeatureChecker;
        this.rulesChecker = rulesChecker;
        this.trashRemovalManager = trashRemovalManager;
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest ignored) {
        if (this.retentionFeatureChecker.isFeatureAvailable()) {
            if (this.rulesChecker.hasDeletingRule(RetentionType.TRASH)) {
                this.trashRemovalManager.softRemove(BATCH_SIZE);
            } else {
                log.info("There is no deleting rules for trash. Skipping soft removal job.");
            }
        }
        return JobRunnerResponse.success();
    }
}

