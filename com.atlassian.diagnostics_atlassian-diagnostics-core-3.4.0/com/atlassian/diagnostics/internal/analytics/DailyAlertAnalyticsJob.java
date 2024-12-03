/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.AlertCriteria
 *  com.atlassian.diagnostics.CallbackResult
 *  com.atlassian.diagnostics.PageRequest
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.events.PluginFrameworkStartedEvent
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
 *  javax.annotation.Nonnull
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal.analytics;

import com.atlassian.diagnostics.AlertCriteria;
import com.atlassian.diagnostics.CallbackResult;
import com.atlassian.diagnostics.PageRequest;
import com.atlassian.diagnostics.internal.InternalMonitoringService;
import com.atlassian.diagnostics.internal.analytics.AnalyticsUtils;
import com.atlassian.diagnostics.internal.analytics.DailyAlertCountAnalyticsEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.events.PluginFrameworkStartedEvent;
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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DailyAlertAnalyticsJob {
    private static final Logger log = LoggerFactory.getLogger(DailyAlertAnalyticsJob.class);
    private static final JobId JOB_ID = JobId.of((String)DailyAlertAnalyticsJob.class.getName());
    private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)DailyAlertAnalyticsJob.class.getSimpleName());
    private final EventPublisher eventPublisher;
    private final InternalMonitoringService monitoringService;
    private final SchedulerService schedulerService;

    public DailyAlertAnalyticsJob(EventPublisher eventPublisher, InternalMonitoringService monitoringService, SchedulerService schedulerService) {
        this.eventPublisher = eventPublisher;
        this.monitoringService = monitoringService;
        this.schedulerService = schedulerService;
    }

    @PreDestroy
    public void destroy() {
        this.schedulerService.unregisterJobRunner(JOB_RUNNER_KEY);
    }

    @EventListener
    public void onFrameworkStarted(PluginFrameworkStartedEvent event) {
        this.schedulerService.registerJobRunner(JOB_RUNNER_KEY, (JobRunner)new AlertAnalyticsJobRunner());
        try {
            this.schedulerService.scheduleJob(JOB_ID, JobConfig.forJobRunnerKey((JobRunnerKey)JOB_RUNNER_KEY).withSchedule(Schedule.forCronExpression((String)"0 19 * * * ?")).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER));
        }
        catch (SchedulerServiceException e) {
            log.warn("Failed to schedule daily alert analytics publishing job", (Throwable)e);
        }
    }

    private class AlertAnalyticsJobRunner
    implements JobRunner {
        private static final long MILLIS_IN_DAY = 86400000L;

        private AlertAnalyticsJobRunner() {
        }

        public JobRunnerResponse runJob(@Nonnull JobRunnerRequest request) {
            long epochDay = Instant.now().minus(1L, ChronoUnit.DAYS).toEpochMilli() / 86400000L;
            Instant since = Instant.ofEpochMilli(epochDay * 86400000L);
            Instant until = since.plus(1L, ChronoUnit.DAYS);
            HashMap nodeHashCache = new HashMap();
            DailyAlertAnalyticsJob.this.monitoringService.internalStreamAlertCounts(AlertCriteria.builder().since(since).until(until).build(), stat -> {
                Set<String> nodeUuids = stat.getCountsByNodeName().keySet().stream().map(nodeName -> nodeHashCache.computeIfAbsent(nodeName, AnalyticsUtils::toUuidFormat)).collect(Collectors.toSet());
                DailyAlertAnalyticsJob.this.eventPublisher.publish((Object)new DailyAlertCountAnalyticsEvent(epochDay, stat.getIssue(), stat.getPlugin(), nodeUuids, stat.getTotalCount()));
                return CallbackResult.CONTINUE;
            }, PageRequest.ofSize((int)250));
            return JobRunnerResponse.success();
        }
    }
}

