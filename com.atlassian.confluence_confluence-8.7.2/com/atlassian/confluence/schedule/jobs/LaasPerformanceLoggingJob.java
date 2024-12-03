/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.logging.LoggingContext
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.schedule.jobs;

import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.util.logging.LoggingContext;
import com.atlassian.confluence.util.profiling.ConfluenceMonitoringControl;
import com.atlassian.confluence.util.profiling.TimerSnapshot;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class LaasPerformanceLoggingJob
implements JobRunner {
    public static final String LAAS_PERFORMANCE_LOGGING_FEATURE_NAME = "confluence.performance.laas.logging";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(LaasPerformanceLoggingJob.class);
    private final DarkFeaturesManager featuresManager;
    private final ConfluenceMonitoringControl control;

    public LaasPerformanceLoggingJob(DarkFeaturesManager featuresManager, ConfluenceMonitoringControl control) {
        this.featuresManager = Objects.requireNonNull(featuresManager);
        this.control = Objects.requireNonNull(control);
    }

    public @Nullable JobRunnerResponse runJob(JobRunnerRequest request) {
        if (!this.featuresManager.getSiteDarkFeatures().isFeatureEnabled(LAAS_PERFORMANCE_LOGGING_FEATURE_NAME)) {
            return JobRunnerResponse.aborted((String)"LaaS performance logging is turned off");
        }
        List<TimerSnapshot> timerSnapshotList = this.control.snapshotTimers();
        this.control.clear();
        for (TimerSnapshot snapshot : timerSnapshotList) {
            if (snapshot.getInvocationCount() <= 0L) continue;
            TimerSnapshotModel model = new TimerSnapshotModel(snapshot);
            LoggingContext.executeWithContext((String)"stats", writer -> objectMapper.writeValue(writer, (Object)model), () -> log.info("Performance metrics"));
        }
        return JobRunnerResponse.success((String)"Performance data is flushed to LaaS");
    }

    static class TimerSnapshotModel {
        private final String name;
        private final long counter;
        private final double wallMean;
        private final long wallMin;
        private final long wallMax;
        private final double cpuMean;
        private final long cpuMin;
        private final long cpuMax;
        private final long cpuTotal;

        public TimerSnapshotModel(TimerSnapshot sample) {
            this.name = sample.getName();
            this.counter = sample.getInvocationCount();
            this.wallMean = sample.getInvocationCount() == 0L ? 0.0 : (double)sample.getElapsedTotalTime(TimeUnit.MILLISECONDS) / (double)sample.getInvocationCount();
            this.wallMin = sample.getElapsedMinTime(TimeUnit.MILLISECONDS);
            this.wallMax = sample.getElapsedMaxTime(TimeUnit.MILLISECONDS);
            this.cpuMean = sample.getInvocationCount() == 0L ? 0.0 : (double)sample.getCpuTotalTime(TimeUnit.MILLISECONDS) / (double)sample.getInvocationCount();
            this.cpuMin = sample.getCpuMinTime(TimeUnit.MILLISECONDS);
            this.cpuMax = sample.getCpuMaxTime(TimeUnit.MILLISECONDS);
            this.cpuTotal = sample.getCpuTotalTime(TimeUnit.MILLISECONDS);
        }

        public String getName() {
            return this.name;
        }

        public long getCounter() {
            return this.counter;
        }

        public double getWallMean() {
            return this.wallMean;
        }

        public long getWallMin() {
            return this.wallMin;
        }

        public long getWallMax() {
            return this.wallMax;
        }

        public double getCpuMean() {
            return this.cpuMean;
        }

        public long getCpuMin() {
            return this.cpuMin;
        }

        public long getCpuMax() {
            return this.cpuMax;
        }

        public long getCpuTotal() {
            return this.cpuTotal;
        }
    }
}

