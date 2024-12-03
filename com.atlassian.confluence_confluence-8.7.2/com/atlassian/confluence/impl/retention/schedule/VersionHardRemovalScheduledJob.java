/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.retention.GlobalRetentionPolicy
 *  com.atlassian.confluence.api.model.retention.RetentionPolicy
 *  com.atlassian.confluence.api.service.retention.RetentionFeatureChecker
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.retention.schedule;

import com.atlassian.confluence.api.model.retention.GlobalRetentionPolicy;
import com.atlassian.confluence.api.model.retention.RetentionPolicy;
import com.atlassian.confluence.api.service.retention.RetentionFeatureChecker;
import com.atlassian.confluence.impl.retention.RetentionType;
import com.atlassian.confluence.impl.retention.VersionRemovalService;
import com.atlassian.confluence.impl.retention.manager.GlobalRetentionPolicyManager;
import com.atlassian.confluence.impl.retention.rules.RetentionRulesChecker;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VersionHardRemovalScheduledJob
implements JobRunner {
    private static final Logger log = LoggerFactory.getLogger(VersionHardRemovalScheduledJob.class);
    private final VersionRemovalService versionRemovalService;
    private final GlobalRetentionPolicyManager globalRetentionPolicyManager;
    private final RetentionFeatureChecker retentionFeatureChecker;
    private final RetentionRulesChecker retentionRulesChecker;

    public VersionHardRemovalScheduledJob(VersionRemovalService versionRemovalService, GlobalRetentionPolicyManager globalRetentionPolicyManager, RetentionFeatureChecker retentionFeatureChecker, RetentionRulesChecker retentionRulesChecker) {
        this.versionRemovalService = versionRemovalService;
        this.globalRetentionPolicyManager = globalRetentionPolicyManager;
        this.retentionFeatureChecker = retentionFeatureChecker;
        this.retentionRulesChecker = retentionRulesChecker;
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest jobRunnerRequest) {
        if (this.retentionFeatureChecker.isFeatureAvailable()) {
            if (this.retentionRulesChecker.hasDeletingRule(RetentionType.HISTORICAL_VERSION)) {
                GlobalRetentionPolicy globalRetentionPolicy = this.globalRetentionPolicyManager.getPolicy();
                this.versionRemovalService.hardRemoveVersions((RetentionPolicy)globalRetentionPolicy);
            } else {
                log.info("There is no deleting rules for historical versions. Skipping hard removal job");
            }
        }
        return JobRunnerResponse.success();
    }
}

