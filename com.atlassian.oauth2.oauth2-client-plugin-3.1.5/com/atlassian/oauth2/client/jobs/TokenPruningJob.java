/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.oauth2.client.api.storage.event.ClientTokenDeletedEvent
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
package com.atlassian.oauth2.client.jobs;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.oauth2.client.api.storage.event.ClientTokenDeletedEvent;
import com.atlassian.oauth2.client.properties.SystemProperty;
import com.atlassian.oauth2.client.storage.token.dao.ClientTokenStore;
import com.atlassian.oauth2.common.jobs.LifecycleAwareJob;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import java.time.Clock;
import java.util.List;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenPruningJob
extends LifecycleAwareJob {
    private static final Logger logger = LoggerFactory.getLogger(TokenPruningJob.class);
    private static final JobId JOB_ID = JobId.of((String)TokenPruningJob.class.getName());
    private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)TokenPruningJob.class.getSimpleName());
    private final ClientTokenStore clientTokenStore;
    private final Clock clock;
    private final EventPublisher eventPublisher;

    public TokenPruningJob(SchedulerService schedulerService, ClientTokenStore clientTokenStore, Clock clock, EventPublisher eventPublisher) {
        super(schedulerService);
        this.clientTokenStore = clientTokenStore;
        this.clock = clock;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected JobRunnerResponse job() {
        logger.info("Pruning expired OAuth 2.0 tokens");
        List<String> removedExpiredTokens = this.clientTokenStore.deleteTokensExpiringBefore(this.clock.instant().minus(SystemProperty.MAX_CLOCK_SKEW.getValue()));
        List<String> removedInvalidTokens = this.clientTokenStore.deleteTokensUnrecoverableSince(this.clock.instant().minus(SystemProperty.LIFETIME_OF_INVALID_TOKEN.getValue()));
        Stream.concat(removedExpiredTokens.stream(), removedInvalidTokens.stream()).forEach(tokenId -> this.eventPublisher.publish((Object)new ClientTokenDeletedEvent(tokenId)));
        logger.info("Number of removed expired tokens {} and number of removed invalid tokens {}", (Object)removedExpiredTokens.size(), (Object)removedInvalidTokens.size());
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

