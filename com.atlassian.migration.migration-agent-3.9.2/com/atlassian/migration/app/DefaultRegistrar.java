/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.app.dto.AppContainerDetails
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  lombok.Generated
 *  org.apache.commons.lang3.StringUtils
 *  org.jetbrains.annotations.NotNull
 *  org.osgi.framework.BundleContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.app;

import com.atlassian.migration.agent.service.analytics.DefaultAppAnalyticsEventService;
import com.atlassian.migration.app.AbstractCloudMigrationRegistrar;
import com.atlassian.migration.app.AppMigrationDarkFeatures;
import com.atlassian.migration.app.DefaultAppMigrationServiceClient;
import com.atlassian.migration.app.dto.AppContainerDetails;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.Generated;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRegistrar
extends AbstractCloudMigrationRegistrar
implements JobRunner {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(DefaultRegistrar.class);
    private static final JobRunnerKey RUNNER_KEY = JobRunnerKey.of((String)"app-mig-job-runner");
    private static final String TRANSFER_ID = "transferId";
    private final DefaultAppMigrationServiceClient appMigrationServiceClient;
    private final SchedulerService schedulerService;
    private final DefaultAppAnalyticsEventService defaultAppAnalyticsEventService;

    public DefaultRegistrar(DefaultAppMigrationServiceClient appMigrationServiceClient, SchedulerService schedulerService, BundleContext bundleContext, DefaultAppAnalyticsEventService defaultAppAnalyticsEventService, AppMigrationDarkFeatures appMigrationDarkFeatures) {
        super(bundleContext, appMigrationDarkFeatures, appMigrationServiceClient, defaultAppAnalyticsEventService);
        this.appMigrationServiceClient = appMigrationServiceClient;
        this.schedulerService = schedulerService;
        this.defaultAppAnalyticsEventService = defaultAppAnalyticsEventService;
    }

    @Override
    public void startMigration(@NotNull String cloudId, @NotNull String migrationId, @NotNull Set<AppContainerDetails> appContainerDetails) {
        log.debug("Starting app-migration for cloudId={}, migrationId={}, appContainerDetails.size={}", new Object[]{cloudId, migrationId, appContainerDetails.size()});
        if (appContainerDetails.isEmpty()) {
            log.info("No app containers for app-migration for migrationId {}", (Object)migrationId);
            return;
        }
        super.startMigration(cloudId, migrationId, appContainerDetails);
    }

    @Override
    protected void queueExecution(String transferId) {
        log.info("Queueing job for app-migration for transferId {}", (Object)StringUtils.abbreviate((String)transferId, (int)21));
        try {
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put(TRANSFER_ID, transferId);
            this.schedulerService.scheduleJob(JobId.of((String)UUID.randomUUID().toString()), JobConfig.forJobRunnerKey((JobRunnerKey)RUNNER_KEY).withRunMode(RunMode.RUN_LOCALLY).withParameters(parameters));
        }
        catch (SchedulerServiceException e) {
            throw new RuntimeException("Failed to queue app transfer execution: " + e.getMessage());
        }
    }

    public JobRunnerResponse runJob(JobRunnerRequest request) {
        try {
            String transferId = ((Serializable)request.getJobConfig().getParameters().get(TRANSFER_ID)).toString();
            log.info("Running job for app-migration for jobId {} and transferId {} ", (Object)request.getJobId(), (Object)StringUtils.abbreviate((String)transferId, (int)21));
            this.executeTransfer(transferId);
            return JobRunnerResponse.success();
        }
        catch (Exception e) {
            return JobRunnerResponse.failed((String)("Failed to run job: " + e.getMessage()));
        }
    }

    @PostConstruct
    public void postConstruct() {
        this.schedulerService.registerJobRunner(RUNNER_KEY, (JobRunner)this);
    }

    @PreDestroy
    public void preDestroy() {
        this.schedulerService.unregisterJobRunner(RUNNER_KEY);
    }

    @Generated
    public DefaultAppMigrationServiceClient getAppMigrationServiceClient() {
        return this.appMigrationServiceClient;
    }

    @Generated
    public DefaultAppAnalyticsEventService getDefaultAppAnalyticsEventService() {
        return this.defaultAppAnalyticsEventService;
    }
}

