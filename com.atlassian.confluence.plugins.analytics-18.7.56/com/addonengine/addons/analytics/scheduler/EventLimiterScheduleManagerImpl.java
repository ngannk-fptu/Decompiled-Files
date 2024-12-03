/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.TuplesKt
 *  kotlin.collections.MapsKt
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.addonengine.addons.analytics.scheduler;

import com.addonengine.addons.analytics.scheduler.EventLimiterJobRunner;
import com.addonengine.addons.analytics.scheduler.EventLimiterScheduleManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import java.util.Map;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u00012\u00020\u00022\u00020\u0003B\u0019\b\u0007\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\b\u0010\u0016\u001a\u00020\u0017H\u0016J\b\u0010\u0018\u001a\u00020\u0017H\u0016R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\f\u001a\n \u000e*\u0004\u0018\u00010\r0\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000f\u001a\n \u000e*\u0004\u0018\u00010\u00100\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\n0\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0014\u001a\n \u000e*\u0004\u0018\u00010\u00150\u0015X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2={"Lcom/addonengine/addons/analytics/scheduler/EventLimiterScheduleManagerImpl;", "Lcom/addonengine/addons/analytics/scheduler/EventLimiterScheduleManager;", "Lorg/springframework/beans/factory/InitializingBean;", "Lorg/springframework/beans/factory/DisposableBean;", "schedulerService", "Lcom/atlassian/scheduler/SchedulerService;", "jobRunner", "Lcom/addonengine/addons/analytics/scheduler/EventLimiterJobRunner;", "(Lcom/atlassian/scheduler/SchedulerService;Lcom/addonengine/addons/analytics/scheduler/EventLimiterJobRunner;)V", "batchSize", "", "frequency", "jobConfig", "Lcom/atlassian/scheduler/config/JobConfig;", "kotlin.jvm.PlatformType", "jobId", "Lcom/atlassian/scheduler/config/JobId;", "jobParameters", "", "", "log", "Lorg/slf4j/Logger;", "afterPropertiesSet", "", "destroy", "analytics"})
public final class EventLimiterScheduleManagerImpl
implements EventLimiterScheduleManager,
InitializingBean,
DisposableBean {
    @NotNull
    private final SchedulerService schedulerService;
    @NotNull
    private final EventLimiterJobRunner jobRunner;
    private final Logger log;
    private final long frequency;
    private final long batchSize;
    @NotNull
    private final Map<String, Long> jobParameters;
    private final JobId jobId;
    private final JobConfig jobConfig;

    @Autowired
    public EventLimiterScheduleManagerImpl(@ComponentImport @NotNull SchedulerService schedulerService, @NotNull EventLimiterJobRunner jobRunner) {
        Intrinsics.checkNotNullParameter((Object)schedulerService, (String)"schedulerService");
        Intrinsics.checkNotNullParameter((Object)jobRunner, (String)"jobRunner");
        this.schedulerService = schedulerService;
        this.jobRunner = jobRunner;
        this.log = LoggerFactory.getLogger(this.getClass());
        String string = System.getProperty("addonengine.analytics.eventLimiter.frequency");
        if (string == null) {
            string = "60";
        }
        this.frequency = Long.parseLong(string) * (long)1000;
        String string2 = System.getProperty("addonengine.analytics.eventLimiter.batchSize");
        if (string2 == null) {
            string2 = "10000";
        }
        this.batchSize = Long.parseLong(string2);
        this.jobParameters = MapsKt.mapOf((Pair)TuplesKt.to((Object)"BATCH_SIZE", (Object)this.batchSize));
        this.jobId = JobId.of((String)"AnalyticsForConfluence.EventLimiter");
        this.jobConfig = JobConfig.forJobRunnerKey((JobRunnerKey)this.jobRunner.getJobRunnerKey()).withSchedule(Schedule.forInterval((long)this.frequency, null)).withParameters(this.jobParameters).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER);
    }

    public void afterPropertiesSet() {
        this.log.warn("Initializing Event Limiter Schedule with frequency: " + this.frequency + " milliseconds");
        this.schedulerService.registerJobRunner(this.jobRunner.getJobRunnerKey(), (JobRunner)this.jobRunner);
        this.schedulerService.scheduleJob(this.jobId, this.jobConfig);
    }

    public void destroy() {
        this.log.warn("Destroying Event Limiter Schedule");
        this.schedulerService.unscheduleJob(this.jobId);
        this.schedulerService.unregisterJobRunner(this.jobRunner.getJobRunnerKey());
    }
}

