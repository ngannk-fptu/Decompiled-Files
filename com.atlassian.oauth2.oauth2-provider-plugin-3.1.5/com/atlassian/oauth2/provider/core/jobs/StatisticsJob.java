/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.oauth2.provider.api.client.Client
 *  com.atlassian.oauth2.provider.api.client.ClientService
 *  com.atlassian.oauth2.provider.api.event.OAuth2ProviderStatisticsEvent
 *  com.atlassian.oauth2.provider.api.token.TokenService
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  org.jetbrains.annotations.NotNull
 */
package com.atlassian.oauth2.provider.core.jobs;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.oauth2.common.jobs.LifecycleAwareJob;
import com.atlassian.oauth2.provider.api.client.Client;
import com.atlassian.oauth2.provider.api.client.ClientService;
import com.atlassian.oauth2.provider.api.event.OAuth2ProviderStatisticsEvent;
import com.atlassian.oauth2.provider.api.token.TokenService;
import com.atlassian.oauth2.provider.core.properties.SystemProperty;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import java.util.List;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class StatisticsJob
extends LifecycleAwareJob {
    @VisibleForTesting
    static final String APPLINKS_V4_ENABLED_PROPERTY = "atlassian.darkfeature.applinks.v4.ui";
    private static final JobId JOB_ID = JobId.of((String)StatisticsJob.class.getName());
    private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)StatisticsJob.class.getSimpleName());
    private static final String SCHEDULE_EVERY_DAY_AT_11_PM = "0 0 23 * * ?";
    private final ApplicationProperties applicationProperties;
    private final ClientService clientService;
    private final TokenService tokenService;
    private final EventPublisher eventPublisher;

    public StatisticsJob(SchedulerService schedulerService, ApplicationProperties applicationProperties, ClientService clientService, TokenService tokenService, EventPublisher eventPublisher) {
        super(schedulerService);
        this.applicationProperties = applicationProperties;
        this.clientService = clientService;
        this.tokenService = tokenService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected JobRunnerResponse job() {
        this.eventPublisher.publish((Object)this.createStatisticsEvent());
        return JobRunnerResponse.success();
    }

    @NotNull
    private OAuth2ProviderStatisticsEvent createStatisticsEvent() {
        List numberOfTokensPerIntegration = this.clientService.list().stream().map(this::getNumberOfTokensForClient).collect(Collectors.toList());
        return new OAuth2ProviderStatisticsEvent(this.applicationProperties.getPlatformId(), numberOfTokensPerIntegration, Boolean.getBoolean(APPLINKS_V4_ENABLED_PROPERTY), SystemProperty.TOKEN_VIA_BASIC_AUTHENTICATION.getValue().booleanValue());
    }

    private int getNumberOfTokensForClient(Client client) {
        return this.tokenService.findRefreshTokensForClientId(client.getClientId()).size();
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
        return JobConfig.forJobRunnerKey((JobRunnerKey)JOB_RUNNER_KEY).withSchedule(Schedule.forCronExpression((String)SCHEDULE_EVERY_DAY_AT_11_PM)).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER);
    }
}

