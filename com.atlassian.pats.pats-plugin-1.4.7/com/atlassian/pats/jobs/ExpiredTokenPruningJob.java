/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.pats.jobs;

import com.atlassian.pats.core.properties.SystemProperty;
import com.atlassian.pats.db.TokenDTO;
import com.atlassian.pats.db.TokenRepository;
import com.atlassian.pats.events.TokenEventPublisher;
import com.atlassian.pats.jobs.AbstractJob;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpiredTokenPruningJob
extends AbstractJob {
    private static final Logger logger = LoggerFactory.getLogger(ExpiredTokenPruningJob.class);
    private final TokenEventPublisher tokenEventPublisher;
    private final Clock utcClock;
    private final TokenRepository tokenRepository;

    public ExpiredTokenPruningJob(SchedulerService schedulerService, Clock utcClock, TokenEventPublisher tokenEventPublisher, TokenRepository tokenRepository) {
        super(schedulerService);
        this.utcClock = utcClock;
        this.tokenEventPublisher = tokenEventPublisher;
        this.tokenRepository = tokenRepository;
    }

    @Override
    protected void doJob() {
        logger.info("Pruning expired personal tokens");
        List<TokenDTO> deletedTokens = this.tokenRepository.deleteByExpiringAtIsBefore(Timestamp.from(this.utcClock.instant().minus(Duration.ofDays(SystemProperty.PRUNING_DELAY_DAYS.getValue().intValue()))));
        deletedTokens.forEach(token -> {
            this.tokenEventPublisher.tokenDeletedEvent((TokenDTO)token, null);
            logger.debug("Removed expired token [{}]", token);
        });
    }

    @Override
    protected Schedule getSchedule() {
        return Schedule.forCronExpression((String)SystemProperty.PRUNING_SCHEDULE_CRON.getValue());
    }

    @Override
    protected RunMode getRunMode() {
        return RunMode.RUN_ONCE_PER_CLUSTER;
    }
}

