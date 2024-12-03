/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.Schedule
 *  com.atlassian.scheduler.status.JobDetails
 *  io.atlassian.util.concurrent.ThreadFactories
 *  org.joda.time.DateTime
 *  org.joda.time.Duration
 *  org.joda.time.ReadableInstant
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.upm.schedule;

import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.scheduler.status.JobDetails;
import com.atlassian.upm.lifecycle.UpmProductDataStartupComponent;
import com.atlassian.upm.lifecycle.UpmUntenantedStartupComponent;
import com.atlassian.upm.schedule.UpmScheduledJob;
import com.atlassian.upm.schedule.UpmScheduler;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.ReadableInstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class UpmSchedulerImpl
implements UpmScheduler,
UpmUntenantedStartupComponent,
UpmProductDataStartupComponent,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(UpmSchedulerImpl.class);
    private static final int THREAD_POOL_SIZE = 4;
    private static final Duration DUMMY_INTERVAL_FOR_NON_REPEATING_JOB = Duration.standardDays((long)100000L);
    private static final String COMPAT_JOB_ID_PREFIX = "CompatibilityPluginScheduler.JobId.";
    private static final String COMPAT_JOB_RUNNER_KEY_PREFIX = "CompatibilityPluginScheduler.JobRunnerKey.";
    private static final String OBSOLETE_JOB_HANDLER_KEY_SUFFIX = "-job-handler";
    private static final String JOB_RUNNER_KEY_SUFFIX = "-runner";
    private static final String JOB_KEY_SUFFIX = "-job";
    private final TransactionTemplate txTemplate;
    private final SchedulerService pluginScheduler;
    private final ScheduledExecutorService executor;
    private final CopyOnWriteArrayList<UpmScheduledJob> jobs;
    private final Phaser triggeredJobPhaser;
    private final AtomicBoolean started;
    private static final String UPM_SCHEDULER_INIT_DELAY_PROPERTY = "upm.scheduler.init.delay.minutes";
    private static final int UPM_SCHEDULER_INIT_DELAY_MINUTES = Integer.getInteger("upm.scheduler.init.delay.minutes", 0);

    public UpmSchedulerImpl(ThreadLocalDelegateExecutorFactory executorFactory, TransactionTemplate txTemplate, SchedulerService pluginScheduler) {
        this.txTemplate = Objects.requireNonNull(txTemplate, "txTemplate");
        this.pluginScheduler = Objects.requireNonNull(pluginScheduler, "pluginScheduler");
        this.executor = Objects.requireNonNull(executorFactory, "executorFactory").createScheduledExecutorService(Executors.newScheduledThreadPool(4, ThreadFactories.namedThreadFactory((String)"UpmScheduler")));
        this.jobs = new CopyOnWriteArrayList();
        this.triggeredJobPhaser = new Phaser(){

            @Override
            protected boolean onAdvance(int phase, int registeredParties) {
                return false;
            }
        };
        this.started = new AtomicBoolean(false);
    }

    @Override
    public void onStartupWithoutProductData() {
        this.started.compareAndSet(false, true);
        this.unregisterObsoleteJobs();
        this.removeObsoleteSchedulerCompatJobsAndHandlers();
        this.maybeRegisterJobHandlers();
    }

    public void destroy() {
        this.unregisterAllJobHandlers();
        this.executor.shutdown();
    }

    @Override
    public void onStartupWithProductData() {
        this.maybeRegisterJobHandlers();
        for (UpmScheduledJob job : this.jobs) {
            this.initiallySchedule(job);
        }
    }

    @Override
    public void registerJob(UpmScheduledJob job) {
        this.jobs.add(job);
        if (this.started.get()) {
            this.registerHandler(job);
        }
    }

    @Override
    public void unregisterJob(UpmScheduledJob job) {
        this.jobs.remove(job);
        this.unschedule(job);
        this.unregister(job);
    }

    private void maybeRegisterJobHandlers() {
        for (UpmScheduledJob job : this.jobs) {
            this.registerHandler(job);
        }
    }

    private void unregisterAllJobHandlers() {
        for (UpmScheduledJob job : this.jobs) {
            try {
                this.unregister(job);
            }
            catch (Exception e) {
                log.error("Error while unregistering job handler: " + UpmSchedulerImpl.getJobRunnerKey(job), (Throwable)e);
            }
        }
    }

    @Override
    public void triggerJob(Class<? extends UpmScheduledJob> jobClass, UpmScheduler.RunMode runMode) {
        for (UpmScheduledJob job : this.jobs) {
            if (!jobClass.isInstance(job)) continue;
            this.triggerJobInternal(job, runMode);
        }
    }

    @Override
    public void triggerRunnable(Runnable task, Duration delay, String name) {
        this.triggeredJobPhaser.register();
        Runnable wrappedTask = () -> {
            try {
                this.txTemplate.execute(() -> {
                    task.run();
                    return null;
                });
            }
            catch (Exception e) {
                log.error("Error in triggered job: " + name, (Throwable)e);
            }
            finally {
                this.triggeredJobPhaser.arriveAndDeregister();
            }
        };
        try {
            this.executor.schedule(wrappedTask, delay.getMillis(), TimeUnit.MILLISECONDS);
        }
        catch (Exception e) {
            log.error("Unable to schedule job: " + name, (Throwable)e);
        }
    }

    @Override
    public void waitForTriggeredJobs() {
        this.triggeredJobPhaser.register();
        this.triggeredJobPhaser.awaitAdvance(this.triggeredJobPhaser.arriveAndDeregister());
    }

    private void registerHandler(UpmScheduledJob job) {
        log.debug("Registering JobRunner: {}", (Object)UpmSchedulerImpl.getJobRunnerKey(job));
        this.pluginScheduler.registerJobRunner(UpmSchedulerImpl.getJobRunnerKey(job), (JobRunner)job);
    }

    private void initiallySchedule(UpmScheduledJob job) {
        JobDetails jobInfo = this.pluginScheduler.getJobDetails(UpmSchedulerImpl.getJobKey(job));
        if (jobInfo == null) {
            this.schedule(job);
        } else if (jobInfo.getSchedule().getIntervalScheduleInfo() == null || jobInfo.getSchedule().getIntervalScheduleInfo().getIntervalInMillis() != UpmSchedulerImpl.getJobInterval(job).getMillis()) {
            this.unschedule(job);
            this.schedule(job);
        }
        if (new DateTime().plusMinutes(1).isBefore((ReadableInstant)job.getStartTime())) {
            this.triggerRunnable(() -> {
                log.debug("Triggering ({}): {}", (Object)UpmScheduler.RunMode.TRIGGERED_BY_UPM_ENABLEMENT, (Object)job.getClass().getSimpleName());
                job.execute(UpmScheduler.RunMode.TRIGGERED_BY_UPM_ENABLEMENT);
            }, Duration.standardMinutes((long)UPM_SCHEDULER_INIT_DELAY_MINUTES), UpmSchedulerImpl.getJobKey(job).toString());
        }
    }

    private void unregister(UpmScheduledJob job) {
        log.debug("Unregistering JobRunner: {}", (Object)UpmSchedulerImpl.getJobRunnerKey(job));
        try {
            this.pluginScheduler.unregisterJobRunner(UpmSchedulerImpl.getJobRunnerKey(job));
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
    }

    private void schedule(UpmScheduledJob job) {
        log.debug("Scheduling: {}", (Object)UpmSchedulerImpl.getJobKey(job));
        Schedule schedule = Schedule.forInterval((long)UpmSchedulerImpl.getJobInterval(job).getMillis(), (Date)job.getStartTime().toDate());
        JobConfig jobConfig = JobConfig.forJobRunnerKey((JobRunnerKey)UpmSchedulerImpl.getJobRunnerKey(job)).withSchedule(schedule);
        try {
            this.pluginScheduler.scheduleJob(UpmSchedulerImpl.getJobKey(job), jobConfig);
        }
        catch (SchedulerServiceException e) {
            log.error("Failed to schedule {}: {}", (Object)UpmSchedulerImpl.getJobKey(job), (Object)e);
        }
    }

    private void unschedule(UpmScheduledJob job) {
        log.debug("Unscheduling: {}", (Object)UpmSchedulerImpl.getJobKey(job));
        this.pluginScheduler.unscheduleJob(UpmSchedulerImpl.getJobKey(job));
    }

    private void triggerJobInternal(UpmScheduledJob job, UpmScheduler.RunMode runMode) {
        this.triggerRunnable(() -> {
            log.debug("Triggering ({}): {}", (Object)runMode, (Object)job.getClass().getSimpleName());
            job.execute(runMode);
        }, Duration.ZERO, UpmSchedulerImpl.getJobKey(job).toString());
    }

    private void unregisterObsoleteJobs() {
        String[] oldJobNames;
        for (String ojc : oldJobNames = new String[]{"RemotePluginLicenseNotificationJob"}) {
            String jobKey = ojc + JOB_KEY_SUFFIX;
            this.pluginScheduler.unscheduleJob(JobId.of((String)jobKey));
        }
    }

    private void removeObsoleteSchedulerCompatJobsAndHandlers() {
        String[] jobNames;
        for (String ojc : jobNames = new String[]{"BundledUpdateCheckJob", "InstanceTopologyJob", "LocalPluginLicenseNotificationJob", "PluginRequestCheckJob", "PluginUpdateCheckJob", "RemotePluginLicenseNotificationJob"}) {
            this.pluginScheduler.unscheduleJob(JobId.of((String)(COMPAT_JOB_ID_PREFIX + ojc + JOB_KEY_SUFFIX)));
            this.pluginScheduler.unregisterJobRunner(JobRunnerKey.of((String)(COMPAT_JOB_RUNNER_KEY_PREFIX + ojc + OBSOLETE_JOB_HANDLER_KEY_SUFFIX)));
        }
    }

    private static Duration getJobInterval(UpmScheduledJob job) {
        return job.getInterval().getOrElse(DUMMY_INTERVAL_FOR_NON_REPEATING_JOB);
    }

    static JobId getJobKey(UpmScheduledJob job) {
        return JobId.of((String)(job.getClass().getSimpleName() + JOB_KEY_SUFFIX));
    }

    static JobRunnerKey getJobRunnerKey(UpmScheduledJob job) {
        return JobRunnerKey.of((String)(job.getClass().getSimpleName() + JOB_RUNNER_KEY_SUFFIX));
    }
}

