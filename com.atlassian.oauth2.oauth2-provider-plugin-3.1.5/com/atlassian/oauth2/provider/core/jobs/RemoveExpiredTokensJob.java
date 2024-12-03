/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.provider.api.token.TokenService
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
import com.atlassian.oauth2.provider.api.token.TokenService;
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

public class RemoveExpiredTokensJob
extends LifecycleAwareJob {
    private static final Logger logger = LoggerFactory.getLogger(RemoveExpiredTokensJob.class);
    private static final JobId JOB_ID = JobId.of((String)RemoveExpiredTokensJob.class.getName());
    private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)RemoveExpiredTokensJob.class.getSimpleName());
    private final TokenService tokenService;
    private final PluginChecker pluginChecker;

    public RemoveExpiredTokensJob(SchedulerService schedulerService, TokenService tokenService, PluginChecker pluginChecker) {
        super(schedulerService);
        this.tokenService = tokenService;
        this.pluginChecker = pluginChecker;
    }

    @Override
    protected JobRunnerResponse job() {
        if (this.pluginChecker.isOAuth2ProviderPluginEnabled()) {
            logger.debug("Running remove expired tokens job.");
            this.tokenService.removeExpiredAccessTokens(SystemProperty.MAX_ACCESS_TOKEN_LIFETIME.getValue());
            this.tokenService.removeExpiredRefreshTokens(SystemProperty.MAX_REFRESH_TOKEN_LIFETIME.getValue());
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
        return JobConfig.forJobRunnerKey((JobRunnerKey)JOB_RUNNER_KEY).withSchedule(Schedule.forCronExpression((String)SystemProperty.PRUNE_EXPIRED_TOKENS_SCHEDULE.getValue())).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER);
    }
}

