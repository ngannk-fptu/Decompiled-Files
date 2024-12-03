/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.Schedule
 *  com.atlassian.scheduler.cron.CronExpressionValidator
 *  com.atlassian.scheduler.cron.CronSyntaxException
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.util;

import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.scheduler.cron.CronExpressionValidator;
import com.atlassian.scheduler.cron.CronSyntaxException;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class CronExpressionValidatorImpl
implements CronExpressionValidator {
    private static final Logger log = LoggerFactory.getLogger(CronExpressionValidatorImpl.class);
    private final SchedulerService schedulerService;

    @Inject
    public CronExpressionValidatorImpl(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public boolean isValid(String cronExpression) {
        try {
            this.validate(cronExpression);
            return true;
        }
        catch (CronSyntaxException e) {
            log.debug("Invalid cron expression", (Throwable)e);
            return false;
        }
    }

    public void validate(String cronExpression) throws CronSyntaxException {
        try {
            this.schedulerService.calculateNextRunTime(Schedule.forCronExpression((String)cronExpression));
        }
        catch (CronSyntaxException e) {
            throw e;
        }
        catch (SchedulerServiceException e) {
            throw new RuntimeException(e);
        }
    }
}

