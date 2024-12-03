/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.guardrails.usage;

import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.ClusterInformationService;
import com.atlassian.migration.agent.service.guardrails.logs.TomcatAccessLogParser;
import com.atlassian.migration.agent.service.guardrails.logs.TomcatAccessLogsFinder;
import com.atlassian.migration.agent.service.guardrails.logs.UsageMetricsNodeData;
import com.atlassian.migration.agent.service.guardrails.usage.DailyUsageDetails;
import com.atlassian.migration.agent.service.guardrails.usage.DailyUsageMetricsStore;
import com.atlassian.migration.agent.service.guardrails.usage.DailyUsageSummary;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class AccessLogProcessingJobRunner {
    private static final Logger log = ContextLoggerFactory.getLogger(AccessLogProcessingJobRunner.class);
    protected static final String DEFAULT_NODE = "defaultClusterNode";
    private static final JobRunnerKey RUNNER_KEY = JobRunnerKey.of((String)"com.atlassian.confluence.migration.guardrails.AccessLogProcessingJobRunner");
    private static final JobId JOB_ID = JobId.of((String)"com.atlassian.confluence.migration.guardrails.AccessLogProcessingJobRunner.JobId");
    private static final JobRunnerKey MERGE_RUNNER_KEY = JobRunnerKey.of((String)"com.atlassian.confluence.migration.guardrails.AccessLogProcessingJobRunner-merge");
    private static final JobId MERGE_JOB_ID = JobId.of((String)"com.atlassian.confluence.migration.guardrails.DailyUsageJobRunner.AccessLogProcessingJobRunner-merge");
    public static final int DAYS_PRODUCT_ANALYTICS_COLLECTION = 14;
    private static final Duration SCHEDULER_DELAY = Duration.ofSeconds(10L);
    private final SchedulerService schedulerService;
    private final ClusterInformationService clusterInformationService;
    private final TomcatAccessLogsFinder tomcatAccessLogsFinder;
    private final TomcatAccessLogParser tomcatAccessLogsParser;
    private final DailyUsageMetricsStore dailyUsageMetricsStore;
    private final MigrationDarkFeaturesManager features;

    public AccessLogProcessingJobRunner(SchedulerService schedulerService, ClusterInformationService clusterInformationService, TomcatAccessLogsFinder tomcatAccessLogsFinder, TomcatAccessLogParser tomcatAccessLogsParser, DailyUsageMetricsStore dailyUsageMetricsStore, MigrationDarkFeaturesManager features) {
        this.schedulerService = schedulerService;
        this.clusterInformationService = clusterInformationService;
        this.tomcatAccessLogsFinder = tomcatAccessLogsFinder;
        this.tomcatAccessLogsParser = tomcatAccessLogsParser;
        this.dailyUsageMetricsStore = dailyUsageMetricsStore;
        this.features = features;
    }

    public void startAssessment() throws SchedulerServiceException {
        this.schedulerService.registerJobRunner(RUNNER_KEY, this::runParseJob);
        this.schedulerService.scheduleJob(JOB_ID, JobConfig.forJobRunnerKey((JobRunnerKey)RUNNER_KEY).withRunMode(RunMode.RUN_LOCALLY).withSchedule(Schedule.runOnce((Date)Date.from(Instant.now().plus(SCHEDULER_DELAY)))));
        log.info("Successfully registered access log processing job {}.", (Object)RUNNER_KEY);
    }

    public int getProgress() {
        if (this.isFinished()) {
            return 100;
        }
        LocalDate today = LocalDate.now();
        return this.dailyUsageMetricsStore.getProgress(today.minusDays(14L), today.minusDays(1L));
    }

    public boolean isFinished() {
        return this.schedulerService.getJobsByJobRunnerKey(RUNNER_KEY).isEmpty() && this.schedulerService.getJobsByJobRunnerKey(MERGE_RUNNER_KEY).isEmpty();
    }

    public void cleanup() {
        this.schedulerService.unregisterJobRunner(RUNNER_KEY);
        this.schedulerService.unregisterJobRunner(MERGE_RUNNER_KEY);
        this.schedulerService.unscheduleJob(JOB_ID);
        this.schedulerService.unscheduleJob(MERGE_JOB_ID);
    }

    public JobRunnerResponse runParseJob(@NotNull JobRunnerRequest request) {
        if (!this.features.isBrowserMetricsEnabled()) {
            this.cleanup();
            return JobRunnerResponse.failed((String)"Collecting daily usage is disabled");
        }
        try {
            LocalDate today = request.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate yesterday = today.minusDays(1L);
            LocalDate startDate = today.minusDays(14L);
            Map<LocalDate, File> files = this.tomcatAccessLogsFinder.listAccessLogFilesFromDaysAgo(startDate);
            if (files != null) {
                Set collected = this.dailyUsageMetricsStore.listSummaries(startDate, yesterday).stream().map(DailyUsageSummary::getDate).collect(Collectors.toSet());
                LocalDate max = files.keySet().stream().max(Comparator.naturalOrder()).orElse(yesterday);
                LocalDate date = startDate;
                while (!date.isAfter(max)) {
                    if (!collected.contains(date)) {
                        this.processFile(date, files.get(date));
                    }
                    date = date.plusDays(1L);
                }
            }
            this.scheduleMergeJob();
        }
        catch (Exception e) {
            log.error("Failed to run job - jobId:{} for nodeId:{} ", new Object[]{request.getJobId(), this.getNodeId(), e});
            return JobRunnerResponse.failed((Throwable)e);
        }
        return JobRunnerResponse.success();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processFile(LocalDate localDate, File file) throws IOException {
        String nodeId = this.getNodeId();
        if (this.dailyUsageMetricsStore.hasData(nodeId, localDate)) {
            return;
        }
        UsageMetricsNodeData.UsageMetricsNodeDataBuilder nodeBuilder = UsageMetricsNodeData.builder().id(nodeId).nodeStatus(UsageMetricsNodeData.NodeStatus.AVAILABLE);
        DailyUsageDetails.DailyUsageDetailsBuilder builder = DailyUsageDetails.createBuilder(localDate);
        try {
            if (file == null) {
                nodeBuilder.dataCollectionStatus(UsageMetricsNodeData.DataCollectionStatus.MISSING_FILE);
            } else {
                this.tomcatAccessLogsParser.processLines(file, entry -> builder.addLogEntry(entry.getDate(), this.hashUserName(entry.getUser()), entry.getPageType()));
                nodeBuilder.dataCollectionStatus(UsageMetricsNodeData.DataCollectionStatus.COMPLETE);
            }
        }
        catch (Throwable e) {
            log.error("Parsing access logs failed for nodeId: {}", (Object)nodeId);
            nodeBuilder.dataCollectionStatus(UsageMetricsNodeData.DataCollectionStatus.FAILED);
        }
        finally {
            this.dailyUsageMetricsStore.storePartial(builder.nodes((List<UsageMetricsNodeData>)ImmutableList.of((Object)nodeBuilder.build())).build());
        }
    }

    private String hashUserName(String name) {
        return name == null ? null : DigestUtils.sha256Hex((String)name);
    }

    public void scheduleMergeJob() throws SchedulerServiceException {
        this.schedulerService.registerJobRunner(MERGE_RUNNER_KEY, this::runMergeJob);
        this.schedulerService.scheduleJob(MERGE_JOB_ID, JobConfig.forJobRunnerKey((JobRunnerKey)MERGE_RUNNER_KEY).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(Schedule.runOnce((Date)Date.from(Instant.now().plus(SCHEDULER_DELAY)))));
        log.info("Successfully registered daily usage processing job {}.", (Object)RUNNER_KEY);
    }

    public JobRunnerResponse runMergeJob(@NotNull JobRunnerRequest request) {
        if (!this.features.isBrowserMetricsEnabled()) {
            this.cleanup();
            return JobRunnerResponse.failed((String)"Collecting daily usage is disabled");
        }
        try {
            Instant now = request.getStartTime().toInstant();
            LocalDate today = now.atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate min = today.minusDays(14L);
            LocalDate max = this.lastCompleted(now, min, today);
            if (max == null) {
                return JobRunnerResponse.success((String)"Skipping, yesterday not completed");
            }
            this.dailyUsageMetricsStore.combine(min, max, this.getRequiredNodes());
        }
        catch (Exception e) {
            log.error("Failed to run job - jobId:{} for nodeId:{} ", new Object[]{request.getJobId(), this.getNodeId(), e});
            return JobRunnerResponse.failed((Throwable)e);
        }
        return JobRunnerResponse.success();
    }

    private List<String> getRequiredNodes() {
        return this.clusterInformationService.isClustered() ? this.clusterInformationService.getAllNodeIds() : ImmutableList.of();
    }

    private LocalDate lastCompleted(Instant now, LocalDate min, LocalDate today) {
        LocalDate date = today.minusDays(1L);
        while (!date.isBefore(min)) {
            if (this.isCompleted(now, date)) {
                return date;
            }
            date = date.minusDays(1L);
        }
        return null;
    }

    private boolean isCompleted(Instant now, LocalDate date) {
        Optional<Instant> lastModified = this.dailyUsageMetricsStore.lastModified(this.getNodeId(), date);
        if (lastModified.isPresent() && this.clusterInformationService.isClustered()) {
            return now.isAfter(lastModified.get().plus(Duration.ofDays(1L))) || this.clusterInformationService.getAllNodeIds().stream().allMatch(nodeId -> this.dailyUsageMetricsStore.lastModified((String)nodeId, date).isPresent());
        }
        return lastModified.isPresent();
    }

    private String getNodeId() {
        return Optional.ofNullable(this.clusterInformationService.getCurrentNodeId()).orElse(DEFAULT_NODE);
    }
}

