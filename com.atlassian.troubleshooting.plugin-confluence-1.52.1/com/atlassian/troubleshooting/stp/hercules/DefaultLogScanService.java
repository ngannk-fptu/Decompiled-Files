/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.hercules;

import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.troubleshooting.api.ClusterService;
import com.atlassian.troubleshooting.stp.hercules.LogScanFactory;
import com.atlassian.troubleshooting.stp.hercules.LogScanMonitor;
import com.atlassian.troubleshooting.stp.hercules.LogScanReportRunner;
import com.atlassian.troubleshooting.stp.hercules.LogScanReportSettings;
import com.atlassian.troubleshooting.stp.hercules.LogScanResult;
import com.atlassian.troubleshooting.stp.hercules.LogScanService;
import com.atlassian.troubleshooting.stp.hercules.cache.LogScanCacheSupplier;
import com.atlassian.troubleshooting.stp.scheduler.ScheduleFactory;
import com.atlassian.troubleshooting.stp.scheduler.SchedulerServiceProvider;
import com.atlassian.troubleshooting.stp.scheduler.TaskSettingsStore;
import com.atlassian.troubleshooting.stp.task.DefaultTaskMonitor;
import com.atlassian.troubleshooting.stp.task.MonitoredTaskExecutor;
import com.atlassian.troubleshooting.stp.task.MonitoredTaskExecutorFactory;
import com.atlassian.troubleshooting.stp.task.TaskMonitor;
import com.atlassian.troubleshooting.stp.task.TaskType;
import java.io.File;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;

@ParametersAreNonnullByDefault
public class DefaultLogScanService
implements LogScanService,
LifecycleAware,
DisposableBean {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultLogScanService.class);
    private static final String TASK_ID_HERCULES = "HerculesScheduledScanTask";
    private static final JobId JOB_ID = JobId.of((String)"HerculesScheduledScanTask");
    private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)"HerculesScheduledScanTask");
    private static final String PROP_RECIPIENTS = "recipients";
    private final LogScanFactory logScanFactory;
    private final LogScanCacheSupplier cacheSupplier;
    private final MonitoredTaskExecutor<LogScanResult, LogScanMonitor> logScanExecutor;
    private final MonitoredTaskExecutor<Void, DefaultTaskMonitor<Void>> logScanReportExecutor;
    private final SchedulerServiceProvider schedulerServiceProvider;
    private final TaskSettingsStore taskSettingsStore;
    private final ClusterService clusterService;
    private SchedulerService schedulerService;

    @Autowired
    public DefaultLogScanService(LogScanFactory logScanFactory, SchedulerServiceProvider schedulerServiceProvider, LogScanCacheSupplier cacheSupplier, MonitoredTaskExecutorFactory taskExecutorFactory, ScheduleFactory scheduleFactory, ClusterService clusterService, PluginSettingsFactory pluginSettingsFactory) {
        this.logScanFactory = Objects.requireNonNull(logScanFactory);
        this.cacheSupplier = Objects.requireNonNull(cacheSupplier);
        this.schedulerServiceProvider = Objects.requireNonNull(schedulerServiceProvider);
        this.clusterService = Objects.requireNonNull(clusterService);
        this.logScanExecutor = taskExecutorFactory.create(TaskType.HERCULES, 1);
        this.logScanReportExecutor = taskExecutorFactory.create(TaskType.HERCULES_REPORT, 1);
        this.taskSettingsStore = new TaskSettingsStore(TASK_ID_HERCULES, pluginSettingsFactory.createGlobalSettings(), scheduleFactory);
    }

    public void destroy() {
        this.logScanExecutor.shutdown();
        this.schedulerService.unregisterJobRunner(JobRunnerKey.of((String)TASK_ID_HERCULES));
        this.clearScanResultCache();
    }

    @Override
    public LogScanMonitor getMonitor(String taskId) {
        return this.logScanExecutor.getMonitor(taskId);
    }

    @Override
    public synchronized void cancelScan(String taskId) {
        LogScanMonitor monitor = this.getMonitor(taskId);
        if (monitor != null && !monitor.isDone() && !monitor.isCancelled()) {
            monitor.cancel(true);
            this.clearScanResultCache();
            LOG.debug("Cancelled scan task [{}]", (Object)taskId);
        }
    }

    @Override
    public LogScanMonitor getLatestScan() {
        return this.getCurrentNodeInProgressMonitors().stream().max(Comparator.comparing(o -> new Date(o.getCreatedTimestamp()))).orElse(null);
    }

    @Override
    @Nonnull
    public LogScanReportSettings getReportSettings() {
        return new LogScanReportSettings.Builder().enabled(this.taskSettingsStore.isEnabled()).recipients(this.taskSettingsStore.getStringProperty(PROP_RECIPIENTS)).schedule(this.taskSettingsStore.getSchedule()).build();
    }

    @Override
    public void setReportSettings(LogScanReportSettings settings) {
        Schedule schedule = settings.getSchedule();
        this.taskSettingsStore.setEnabled(settings.isEnabled());
        this.taskSettingsStore.setProperty(PROP_RECIPIENTS, settings.getRecipients());
        this.taskSettingsStore.setSchedule(schedule);
        if (settings.isEnabled()) {
            this.scheduleLogScanJob(settings.getSchedule());
        } else {
            this.schedulerService.unscheduleJob(JOB_ID);
        }
    }

    public void onStart() {
        this.schedulerService = this.schedulerServiceProvider.getSchedulerService();
        this.schedulerService.registerJobRunner(JOB_RUNNER_KEY, (JobRunner)new LogScanReportRunner(this));
        LogScanReportSettings settings = this.getReportSettings();
        if (settings.isEnabled()) {
            this.scheduleLogScanJob(settings.getSchedule());
        }
    }

    public void onStop() {
    }

    @Override
    @Nonnull
    public synchronized LogScanMonitor scan(File logFile) {
        Objects.requireNonNull(logFile);
        Optional<LogScanMonitor> currentlyRunning = this.getCurrentNodeInProgressMonitors().stream().filter(monitor -> monitor.getLogFilePath().equals(logFile.getPath())).findFirst();
        if (currentlyRunning.isPresent()) {
            LOG.info("There is already a scan in progress for [{}], we will not create a new one", (Object)logFile);
            return currentlyRunning.get();
        }
        LogScanMonitor monitor2 = this.logScanExecutor.submit(this.logScanFactory.createLogScanTask(logFile));
        this.cacheSupplier.getCache().set(monitor2);
        LOG.debug("Created a scan task for log file [{}]", (Object)logFile);
        return monitor2;
    }

    @Override
    @Nonnull
    public TaskMonitor<Void> sendLogScanReport() {
        return this.logScanReportExecutor.submit(this.logScanFactory.createLogScanReportTask(this.getReportSettings()));
    }

    @Override
    @Nullable
    public LogScanMonitor getLastScan() {
        return this.cacheSupplier.getCache().get();
    }

    @Override
    public void clearScanResultCache() {
        this.cacheSupplier.getCache().destroy();
    }

    private List<LogScanMonitor> getCurrentNodeInProgressMonitors() {
        return this.logScanExecutor.getMonitors().stream().filter(monitor -> !monitor.isDone() && !monitor.isCancelled() && monitor.getNodeId().equals(this.clusterService.getCurrentNodeId())).collect(Collectors.toList());
    }

    private void scheduleLogScanJob(Schedule schedule) {
        try {
            this.schedulerService.scheduleJob(JOB_ID, JobConfig.forJobRunnerKey((JobRunnerKey)JOB_RUNNER_KEY).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(schedule));
        }
        catch (SchedulerServiceException e) {
            LOG.error("Failed to schedule log scan job", (Throwable)e);
        }
    }
}

