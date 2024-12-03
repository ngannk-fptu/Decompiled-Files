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

public class TrashHardRemovalScheduledJob
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(TrashHardRemovalScheduledJob.class);
    private final RetentionFeatureChecker retentionFeatureChecker;
    private final RetentionRulesChecker retentionRulesChecker;
    private final TrashRemovalManager trashRemovalManager;

    public TrashHardRemovalScheduledJob(RetentionFeatureChecker retentionFeatureChecker, RetentionRulesChecker retentionRulesChecker, TrashRemovalManager trashRemovalManager) {
        this.retentionFeatureChecker = retentionFeatureChecker;
        this.retentionRulesChecker = retentionRulesChecker;
        this.trashRemovalManager = trashRemovalManager;
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        if (this.retentionFeatureChecker.isFeatureAvailable()) {
            if (this.retentionRulesChecker.hasDeletingRule(RetentionType.TRASH)) {
                log.warn("Running hard removal of trash. This may take a long time depending on number of trash in Confluence.");
                this.trashRemovalManager.hardRemove();
                log.warn("Hard removal of trash is done.");
            } else {
                log.info("There is no deleting rules for trash. Skipping hard removal job");
            }
        }
        return JobRunnerResponse.success();
    }
}

