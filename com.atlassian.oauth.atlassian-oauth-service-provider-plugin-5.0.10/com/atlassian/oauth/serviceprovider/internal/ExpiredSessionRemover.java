/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.oauth.serviceprovider.internal;

import com.atlassian.oauth.serviceprovider.ServiceProviderTokenStore;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ExpiredSessionRemover
implements JobRunner,
LifecycleAware {
    private static final Logger log = LoggerFactory.getLogger(ExpiredSessionRemover.class);
    private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)ExpiredSessionRemover.class.getName());
    private static final long REMOVE_INTERVAL = Integer.getInteger("oauth.session.removing.interval", 28800000).intValue();
    private static final String JOB_ID = "Service Provider Session Remover";
    private final ServiceProviderTokenStore tokenStore;
    private final SchedulerService schedulerService;

    @Autowired
    public ExpiredSessionRemover(@Qualifier(value="tokenStore") ServiceProviderTokenStore tokenStore, SchedulerService schedulerService) {
        this.tokenStore = tokenStore;
        this.schedulerService = schedulerService;
    }

    public JobRunnerResponse runJob(JobRunnerRequest request) {
        this.tokenStore.removeExpiredTokensAndNotify();
        this.tokenStore.removeExpiredSessionsAndNotify();
        log.debug("Expired oauth sessions removed");
        return JobRunnerResponse.success();
    }

    public void onStart() {
        this.schedulerService.registerJobRunner(JOB_RUNNER_KEY, (JobRunner)this);
        JobConfig config = JobConfig.forJobRunnerKey((JobRunnerKey)JOB_RUNNER_KEY).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(Schedule.forInterval((long)REMOVE_INTERVAL, (Date)new Date(System.currentTimeMillis() + REMOVE_INTERVAL)));
        try {
            this.schedulerService.scheduleJob(JobId.of((String)JOB_ID), config);
        }
        catch (SchedulerServiceException e) {
            log.error("Unable to schedule expired session remover job", (Throwable)e);
        }
    }

    public void onStop() {
        this.schedulerService.unregisterJobRunner(JOB_RUNNER_KEY);
    }
}

