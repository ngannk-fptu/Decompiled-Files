/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.config.JobId
 *  javax.inject.Named
 *  lombok.Generated
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.service.impl.SpaceStatisticCalculationService;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.config.JobId;
import javax.inject.Named;
import lombok.Generated;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named(value="SpaceStatisticCalculationExecutor")
public class SpaceStatisticCalculationIntervalExecutor
implements JobRunner {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(SpaceStatisticCalculationIntervalExecutor.class);
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;
    private final SpaceStatisticCalculationService spaceStatisticCalculationService;

    public SpaceStatisticCalculationIntervalExecutor(SpaceStatisticCalculationService spaceStatisticCalculationService, MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        this.spaceStatisticCalculationService = spaceStatisticCalculationService;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
    }

    public JobRunnerResponse runJob(@NotNull JobRunnerRequest request) {
        if (this.migrationDarkFeaturesManager.isNewSpaceSelectorEnabled() && !this.spaceStatisticCalculationService.hasMigrationsRunning()) {
            JobId jobId = request.getJobId();
            log.info("Running space statistic calculation job. jobId: {}", (Object)jobId);
            try {
                this.spaceStatisticCalculationService.runSpaceStatisticCalculation(jobId, true, false);
                this.spaceStatisticCalculationService.removeStatsForDeletedSpaces();
                return JobRunnerResponse.success((String)"Ran space statistic calculation.");
            }
            catch (Exception e) {
                String errorMessage = "Failed to run space statistic calculation.";
                log.error(errorMessage, (Throwable)e);
                return JobRunnerResponse.failed((String)(errorMessage + e.getMessage()));
            }
        }
        String message = "Skipped space statistic calculation as feature is disabled or a migration is happening.";
        log.info(message);
        return JobRunnerResponse.success((String)message);
    }
}

