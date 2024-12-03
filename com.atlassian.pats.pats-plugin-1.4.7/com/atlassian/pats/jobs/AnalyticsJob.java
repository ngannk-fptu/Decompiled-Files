/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 */
package com.atlassian.pats.jobs;

import com.atlassian.pats.db.TokenRepository;
import com.atlassian.pats.events.TokenEventPublisher;
import com.atlassian.pats.jobs.AbstractJob;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;

public class AnalyticsJob
extends AbstractJob {
    private static final Schedule ONCE_A_DAY_SCHEDULE = Schedule.forCronExpression((String)"0 0 23 * * ?");
    private static final Schedule EVERY_30_SECONDS_SCHEDULE = Schedule.forCronExpression((String)"0/30 * * * * ?");
    public static final String DEBUG_ANALYTICS_SYSTEM_PROPERTY = "atlassian.dev.mode";
    private final TokenRepository tokenRepository;
    private final TokenEventPublisher tokenEventPublisher;

    public AnalyticsJob(SchedulerService schedulerService, TokenRepository tokenRepository, TokenEventPublisher tokenEventPublisher) {
        super(schedulerService);
        this.tokenRepository = tokenRepository;
        this.tokenEventPublisher = tokenEventPublisher;
    }

    @Override
    protected Schedule getSchedule() {
        return Boolean.getBoolean(DEBUG_ANALYTICS_SYSTEM_PROPERTY) ? EVERY_30_SECONDS_SCHEDULE : ONCE_A_DAY_SCHEDULE;
    }

    @Override
    protected void doJob() {
        long amountOfTokens = this.tokenRepository.count();
        this.tokenEventPublisher.tokenSummaryEvent(amountOfTokens);
    }

    @Override
    protected RunMode getRunMode() {
        return RunMode.RUN_ONCE_PER_CLUSTER;
    }
}

