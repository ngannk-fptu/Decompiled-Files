/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  com.atlassian.whisper.plugin.api.FetchScheduler
 *  com.atlassian.whisper.plugin.api.MessageFetchService
 *  com.atlassian.whisper.plugin.api.MessagesExpiryService
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.whisper.plugin.fetch;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.whisper.plugin.api.FetchScheduler;
import com.atlassian.whisper.plugin.api.MessageFetchService;
import com.atlassian.whisper.plugin.api.MessagesExpiryService;
import com.atlassian.whisper.plugin.fetch.FetchJob;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@ExportAsService
public class DefaultFetchScheduler
implements LifecycleAware,
FetchScheduler {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultFetchScheduler.class);
    private static final String JOB_KEY = FetchJob.class.getName();
    private static final JobId JOB_ID = JobId.of((String)JOB_KEY);
    private static final long PROPERTY_SCHEDULER_INTERVAL = DefaultFetchScheduler.getProperty("atlassian.whisper.fetch.scheduler.interval", TimeUnit.HOURS.toMillis(6L));
    private static final long PROPERTY_SCHEDULER_DELAY = DefaultFetchScheduler.getProperty("atlassian.whisper.fetch.scheduler.delay", TimeUnit.HOURS.toMillis(1L));
    private final SchedulerService schedulerService;
    private final MessageFetchService fetchService;
    private final MessagesExpiryService messagesExpiryService;

    @Inject
    public DefaultFetchScheduler(@ComponentImport SchedulerService schedulerService, MessageFetchService fetchService, MessagesExpiryService messagesExpiryService) {
        this.schedulerService = schedulerService;
        this.fetchService = fetchService;
        this.messagesExpiryService = messagesExpiryService;
    }

    private JobConfig getJobConfig() {
        return JobConfig.forJobRunnerKey((JobRunnerKey)JobRunnerKey.of((String)JOB_KEY)).withSchedule(Schedule.forInterval((long)PROPERTY_SCHEDULER_INTERVAL, (Date)new Date(System.currentTimeMillis() + PROPERTY_SCHEDULER_DELAY))).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER);
    }

    public void schedule() {
        this.onStart();
    }

    public void unschedule() {
        this.onStop();
    }

    public void onStart() {
        JobRunnerKey jobRunnerKey = JobRunnerKey.of((String)JOB_KEY);
        LOG.debug("Registering and scheduling FetchJob");
        this.schedulerService.registerJobRunner(jobRunnerKey, (JobRunner)new FetchJob(this.fetchService));
        try {
            this.schedulerService.scheduleJob(JOB_ID, this.getJobConfig());
            LOG.debug("FetchJob schedule successfully completed");
        }
        catch (SchedulerServiceException e) {
            LOG.error("Error was thrown during scheduling an FetchJob", (Throwable)e);
        }
    }

    public void onStop() {
        this.schedulerService.unscheduleJob(JOB_ID);
        JobRunnerKey jobRunnerKey = JobRunnerKey.of((String)JOB_KEY);
        this.schedulerService.unregisterJobRunner(jobRunnerKey);
    }

    private static long getProperty(String property, long defaultMillis) {
        return Long.getLong(property, defaultMillis);
    }
}

