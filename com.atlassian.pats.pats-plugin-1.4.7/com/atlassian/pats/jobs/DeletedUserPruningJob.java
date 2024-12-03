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

import com.atlassian.pats.api.TokenService;
import com.atlassian.pats.checker.ProductUserProvider;
import com.atlassian.pats.core.properties.SystemProperty;
import com.atlassian.pats.db.Tables;
import com.atlassian.pats.db.TokenRepository;
import com.atlassian.pats.jobs.AbstractJob;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeletedUserPruningJob
extends AbstractJob {
    private static final Logger logger = LoggerFactory.getLogger(DeletedUserPruningJob.class);
    private final TokenService tokenService;
    private final TokenRepository tokenRepository;
    private final ProductUserProvider productUserProvider;

    public DeletedUserPruningJob(SchedulerService schedulerService, TokenService tokenService, TokenRepository tokenRepository, ProductUserProvider productUserProvider) {
        super(schedulerService);
        this.tokenService = tokenService;
        this.tokenRepository = tokenRepository;
        this.productUserProvider = productUserProvider;
    }

    @Override
    protected void doJob() {
        this.tokenRepository.getDistinctUserKeys().stream().filter(this.productUserProvider::isUserDeleted).forEach(deletedUserKey -> {
            logger.debug("Removing tokens for deleted userKey [" + deletedUserKey + "]");
            this.tokenService.delete(null, Tables.TOKEN.userKey.eq(deletedUserKey));
        });
    }

    @Override
    protected Schedule getSchedule() {
        return Schedule.forCronExpression((String)SystemProperty.DELETED_USER_PRUNING_SCHEDULE_CRON.getValue());
    }

    @Override
    protected RunMode getRunMode() {
        return RunMode.RUN_ONCE_PER_CLUSTER;
    }
}

