/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.provider.api.authorization.AuthorizationService
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.core.jobs;

import com.atlassian.oauth2.common.jobs.LifecycleAwareJob;
import com.atlassian.oauth2.provider.api.authorization.AuthorizationService;
import com.atlassian.oauth2.provider.core.plugin.PluginChecker;
import com.atlassian.oauth2.provider.core.properties.SystemProperty;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveExpiredAuthorizationsJob
extends LifecycleAwareJob {
    private static final Logger logger = LoggerFactory.getLogger(RemoveExpiredAuthorizationsJob.class);
    private static final JobId JOB_ID = JobId.of((String)RemoveExpiredAuthorizationsJob.class.getName());
    private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)RemoveExpiredAuthorizationsJob.class.getSimpleName());
    private final AuthorizationService authorizationService;
    private final PluginChecker pluginChecker;

    public RemoveExpiredAuthorizationsJob(SchedulerService schedulerService, AuthorizationService authorizationService, PluginChecker pluginChecker) {
        super(schedulerService);
        this.authorizationService = authorizationService;
        this.pluginChecker = pluginChecker;
    }

    @Override
    protected JobRunnerResponse job() {
        if (this.pluginChecker.isOAuth2ProviderPluginEnabled()) {
            logger.debug("Running remove expired authorizations job.");
            this.authorizationService.removeExpiredAuthorizations(SystemProperty.MAX_AUTHORIZATION_CODE_LIFETIME.getValue());
        }
        return JobRunnerResponse.success();
    }

    @Override
    protected JobId getJobId() {
        return JOB_ID;
    }

    @Override
    protected JobRunnerKey getJobRunnerKey() {
        return JOB_RUNNER_KEY;
    }

    @Override
    protected JobConfig getJobConfig() {
        return JobConfig.forJobRunnerKey((JobRunnerKey)JOB_RUNNER_KEY).withSchedule(Schedule.forCronExpression((String)SystemProperty.PRUNE_EXPIRED_AUTHORIZATIONS_SCHEDULE.getValue())).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER);
    }
}

