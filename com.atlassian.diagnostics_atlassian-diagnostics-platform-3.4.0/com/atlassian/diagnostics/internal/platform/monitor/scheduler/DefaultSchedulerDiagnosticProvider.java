/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.SchedulerHistoryService
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.caesium.impl.CaesiumSchedulerService
 *  com.atlassian.scheduler.caesium.spi.CaesiumSchedulerConfiguration
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.core.RunningJob
 *  com.atlassian.scheduler.core.SchedulerServiceController
 *  com.atlassian.scheduler.status.JobDetails
 *  com.atlassian.scheduler.status.RunDetails
 */
package com.atlassian.diagnostics.internal.platform.monitor.scheduler;

import com.atlassian.diagnostics.internal.platform.monitor.scheduler.RunningJobDiagnostic;
import com.atlassian.diagnostics.internal.platform.monitor.scheduler.ScheduledJobDiagnostic;
import com.atlassian.diagnostics.internal.platform.monitor.scheduler.SchedulerDiagnostic;
import com.atlassian.diagnostics.internal.platform.monitor.scheduler.SchedulerDiagnosticProvider;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.SchedulerHistoryService;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.caesium.impl.CaesiumSchedulerService;
import com.atlassian.scheduler.caesium.spi.CaesiumSchedulerConfiguration;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.core.RunningJob;
import com.atlassian.scheduler.core.SchedulerServiceController;
import com.atlassian.scheduler.status.JobDetails;
import com.atlassian.scheduler.status.RunDetails;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultSchedulerDiagnosticProvider
implements SchedulerDiagnosticProvider {
    private final CaesiumSchedulerConfiguration caesiumSchedulerConfiguration;
    private final SchedulerHistoryService schedulerHistoryService;
    private final SchedulerServiceController schedulerServiceController;
    private final SchedulerService schedulerService;
    private Optional<CaesiumSchedulerService> caesiumSchedulerService = Optional.empty();

    public DefaultSchedulerDiagnosticProvider(CaesiumSchedulerConfiguration caesiumSchedulerConfiguration, SchedulerHistoryService schedulerHistoryService, SchedulerServiceController schedulerServiceController, SchedulerService schedulerService) {
        this.caesiumSchedulerConfiguration = caesiumSchedulerConfiguration;
        this.schedulerHistoryService = schedulerHistoryService;
        this.schedulerServiceController = schedulerServiceController;
        this.schedulerService = schedulerService;
        if (schedulerService instanceof CaesiumSchedulerService) {
            this.caesiumSchedulerService = Optional.of((CaesiumSchedulerService)schedulerService);
        } else if (schedulerServiceController instanceof CaesiumSchedulerService) {
            this.caesiumSchedulerService = Optional.of((CaesiumSchedulerService)schedulerServiceController);
        }
    }

    @Override
    public SchedulerDiagnostic getDiagnostic() {
        int workerThreadCount = this.caesiumSchedulerConfiguration.workerThreadCount();
        return new SchedulerDiagnostic(workerThreadCount, this.getRunningJobs(), this.getScheduledJobs());
    }

    private List<RunningJobDiagnostic> getRunningJobs() {
        return this.schedulerServiceController.getLocallyRunningJobs().stream().map(runningJob -> {
            Optional<JobRunner> jobRunner = this.getJobRunner(runningJob.getJobConfig().getJobRunnerKey());
            return new RunningJobDiagnostic((RunningJob)runningJob, jobRunner);
        }).collect(Collectors.toList());
    }

    private List<ScheduledJobDiagnostic> getScheduledJobs() {
        return this.schedulerService.getJobRunnerKeysForAllScheduledJobs().stream().flatMap(key -> Stream.of(this.schedulerService.getJobsByJobRunnerKey(key))).flatMap(jobsForRunnerKey -> jobsForRunnerKey.stream().map(jobDetails -> new ScheduledJobDiagnostic((JobDetails)jobDetails, this.getRunDetails((JobDetails)jobDetails), this.getJobRunner(jobDetails.getJobRunnerKey())))).collect(Collectors.toList());
    }

    private Optional<RunDetails> getRunDetails(JobDetails jobDetails) {
        return Optional.ofNullable(this.schedulerHistoryService.getLastRunForJob(jobDetails.getJobId()));
    }

    private Optional<JobRunner> getJobRunner(JobRunnerKey jobRunnerKey) {
        return this.caesiumSchedulerService.map(service -> service.getJobRunner(jobRunnerKey));
    }
}

