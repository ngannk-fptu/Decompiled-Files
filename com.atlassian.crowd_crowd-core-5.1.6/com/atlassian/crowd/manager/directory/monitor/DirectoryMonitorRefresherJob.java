/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.SynchronisableDirectory
 *  com.atlassian.crowd.directory.loader.DirectoryInstanceLoader
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.spi.DcLicenseChecker
 *  com.atlassian.crowd.exception.DirectoryInstantiationException
 *  com.atlassian.crowd.manager.directory.DirectoryManager
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
 *  com.atlassian.scheduler.config.Schedule$Type
 *  com.atlassian.scheduler.status.JobDetails
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.manager.directory.monitor;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.crowd.directory.DbCachingDirectoryPoller;
import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.SynchronisableDirectory;
import com.atlassian.crowd.directory.loader.DirectoryInstanceLoader;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.spi.DcLicenseChecker;
import com.atlassian.crowd.exception.DirectoryInstantiationException;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.manager.directory.monitor.poller.DirectoryPollerJobRunner;
import com.atlassian.crowd.manager.directory.monitor.poller.DirectoryPollerManager;
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
import com.atlassian.scheduler.status.JobDetails;
import com.google.common.collect.ImmutableMap;
import java.time.Clock;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryMonitorRefresherJob
implements JobRunner {
    public static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)(DirectoryMonitorRefresherJob.class.getName() + "-runner"));
    static final String LOCK_NAME = DirectoryMonitorRefresherJob.class.getName() + "-lock";
    static final long DEFAULT_POLLING_DELAY = 5000L;
    static final String POLLER_JOBID_PREFIX = DirectoryPollerManager.class.getName() + ".";
    private static final Logger log = LoggerFactory.getLogger(DirectoryMonitorRefresherJob.class);
    private final SchedulerService schedulerService;
    private final DirectoryInstanceLoader directoryInstanceLoader;
    private final DirectoryManager directoryManager;
    private final ClusterLockService clusterLockService;
    private final Clock clock;
    private final DcLicenseChecker licenseChecker;

    public DirectoryMonitorRefresherJob(SchedulerService schedulerService, DirectoryInstanceLoader directoryInstanceLoader, DirectoryManager directoryManager, ClusterLockService clusterLockService, Clock clock, DcLicenseChecker licenseChecker) {
        this.schedulerService = schedulerService;
        this.directoryInstanceLoader = directoryInstanceLoader;
        this.directoryManager = directoryManager;
        this.clusterLockService = clusterLockService;
        this.clock = clock;
        this.licenseChecker = licenseChecker;
    }

    @PostConstruct
    public void registerJobRunner() {
        this.schedulerService.registerJobRunner(JOB_RUNNER_KEY, (JobRunner)this);
    }

    @PreDestroy
    public void unregisterJobRunner() {
        this.schedulerService.unregisterJobRunner(JOB_RUNNER_KEY);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        ClusterLock clusterLock = this.clusterLockService.getLockForName(LOCK_NAME);
        if (clusterLock.tryLock()) {
            try {
                log.debug("Refreshing directory monitors");
                Map<JobId, RemoteDirectory> synchronisableDirectories = this.directoryManager.findAllDirectories().stream().filter(Directory::isActive).flatMap(directory -> {
                    try {
                        return Stream.of(this.directoryInstanceLoader.getDirectory(directory));
                    }
                    catch (DirectoryInstantiationException e) {
                        log.warn("Unable to instantiate directory {} when updating synchronisation schedules", (Object)directory.getId());
                        return Stream.empty();
                    }
                }).filter(directory -> directory instanceof SynchronisableDirectory).collect(Collectors.toMap(dir -> this.getJobId(dir.getDirectoryId()), Function.identity()));
                log.debug("Found {} synchronisable directories", (Object)synchronisableDirectories.size());
                List<JobDetails> scheduledJobs = this.schedulerService.getJobsByJobRunnerKey(DirectoryPollerJobRunner.JOB_RUNNER_KEY).stream().filter(jobDetails -> jobDetails.getJobId().toString().startsWith(POLLER_JOBID_PREFIX)).collect(Collectors.toList());
                this.updateExistingJobs(synchronisableDirectories, scheduledJobs);
                this.addNewJobs(synchronisableDirectories, scheduledJobs);
            }
            finally {
                clusterLock.unlock();
            }
        } else {
            log.debug("Lock {} is already held, skipping", (Object)LOCK_NAME);
        }
        return JobRunnerResponse.success();
    }

    private void addNewJobs(Map<JobId, RemoteDirectory> expectedJobs, List<JobDetails> scheduledPollingJobs) {
        Set scheduledJobIds = scheduledPollingJobs.stream().map(JobDetails::getJobId).collect(Collectors.toSet());
        expectedJobs.entrySet().stream().filter(job -> !scheduledJobIds.contains(job.getKey())).forEach(missingSyncEntry -> this.schedulePollingJob((RemoteDirectory)missingSyncEntry.getValue()));
    }

    private void updateExistingJobs(Map<JobId, RemoteDirectory> expectedJobs, List<JobDetails> scheduledPollingJobs) {
        scheduledPollingJobs.forEach(scheduledJob -> {
            JobId jobId = scheduledJob.getJobId();
            RemoteDirectory synchronisableDirectory = (RemoteDirectory)expectedJobs.get(jobId);
            if (synchronisableDirectory != null) {
                if (this.shouldReschedule((JobDetails)scheduledJob, synchronisableDirectory)) {
                    log.debug("Synchronisation period differs for directory {} - will reschedule", (Object)synchronisableDirectory.getDirectoryId());
                    this.schedulePollingJob(synchronisableDirectory);
                }
            } else {
                log.debug("Unscheduling polling job {}, as the directory isn't synchronisable", (Object)jobId);
                this.schedulerService.unscheduleJob(jobId);
            }
        });
    }

    private boolean shouldReschedule(JobDetails scheduledJob, RemoteDirectory remoteDirectory) {
        return !this.normalizeScheduleForComparison(this.prepareScheduleForDirectory(remoteDirectory)).equals((Object)this.normalizeScheduleForComparison(scheduledJob.getSchedule()));
    }

    private Schedule normalizeScheduleForComparison(Schedule schedule) {
        if (schedule.getIntervalScheduleInfo() != null) {
            return Schedule.forInterval((long)schedule.getIntervalScheduleInfo().getIntervalInMillis(), null);
        }
        return schedule;
    }

    private void schedulePollingJob(RemoteDirectory directory) {
        JobConfig jobConfig = this.createPollingJobConfig(directory);
        JobId jobId = this.getJobId(directory.getDirectoryId());
        log.debug("Scheduling polling job {}, with schedule {}", (Object)jobId, (Object)jobConfig.getSchedule());
        try {
            this.schedulerService.scheduleJob(jobId, jobConfig);
        }
        catch (SchedulerServiceException e) {
            log.error("Failed to schedule directory polling job {}", (Object)jobId);
        }
    }

    private JobId getJobId(long directoryId) {
        return JobId.of((String)(POLLER_JOBID_PREFIX + directoryId));
    }

    private JobConfig createPollingJobConfig(RemoteDirectory directory) {
        long directoryId = directory.getDirectoryId();
        return JobConfig.forJobRunnerKey((JobRunnerKey)DirectoryPollerJobRunner.JOB_RUNNER_KEY).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(this.prepareScheduleForDirectory(directory)).withParameters((Map)ImmutableMap.builder().put((Object)"DIRECTORY_ID", (Object)directoryId).build());
    }

    private Schedule prepareScheduleForDirectory(RemoteDirectory remoteDirectory) {
        if (Schedule.Type.INTERVAL.equals((Object)this.getScheduleType(remoteDirectory)) || !this.licenseChecker.isDcLicense()) {
            return Schedule.forInterval((long)this.getInterval(remoteDirectory).toMillis(), (Date)new Date(this.getFirstRunTime()));
        }
        String value = Objects.requireNonNull(remoteDirectory.getValue("directory.cache.synchronise.cron"));
        return Schedule.forCronExpression((String)value);
    }

    private Duration getInterval(RemoteDirectory remoteDirectory) {
        return DbCachingDirectoryPoller.getPollingInterval(remoteDirectory);
    }

    private long getFirstRunTime() {
        return this.clock.millis() + Long.getLong("crowd.polling.startdelay", 5000L);
    }

    private Schedule.Type getScheduleType(RemoteDirectory remoteDirectory) {
        String synchronizationOption = remoteDirectory.getValue("directory.cache.synchronise.type");
        if ("cronExpression".equals(synchronizationOption)) {
            return Schedule.Type.CRON_EXPRESSION;
        }
        return Schedule.Type.INTERVAL;
    }
}

