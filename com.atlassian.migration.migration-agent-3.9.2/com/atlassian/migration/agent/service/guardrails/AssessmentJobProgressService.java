/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.status.JobDetails
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.guardrails;

import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.entity.GuardrailsResponseGroup;
import com.atlassian.migration.agent.entity.InstanceAnalysisControl;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.guardrails.InstanceAnalysisControlService;
import com.atlassian.migration.agent.service.guardrails.InstanceAnalysisControlTypes;
import com.atlassian.migration.agent.service.guardrails.InstanceAssessmentService;
import com.atlassian.migration.agent.service.guardrails.usage.AccessLogProcessingJobRunner;
import com.atlassian.migration.agent.store.guardrails.AssessmentStatus;
import com.atlassian.migration.agent.store.guardrails.GuardrailsResponseGroupStore;
import com.atlassian.migration.agent.store.guardrails.InstanceAnalysisControlStore;
import com.atlassian.migration.agent.store.guardrails.InstanceAssessmentStatus;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.status.JobDetails;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;

public class AssessmentJobProgressService {
    private static final Logger log = ContextLoggerFactory.getLogger(AssessmentJobProgressService.class);
    private final InstanceAnalysisControlStore instanceAnalysisControlStore;
    private final InstanceAnalysisControlService instanceAnalysisControlService;
    private final MigrationDarkFeaturesManager featuresManager;
    private static final String SERVER_NODE = "serverNode";
    private static final int TIME_IN_MINUTES = 60;
    private final InstanceAssessmentService instanceAssessmentService;
    private final GuardrailsResponseGroupStore guardrailsResponseGroupStore;
    private final AccessLogProcessingJobRunner accessLogProcessingJobRunner;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    public AssessmentJobProgressService(InstanceAnalysisControlStore instanceAnalysisControlStore, InstanceAnalysisControlService instanceAnalysisControlService, MigrationDarkFeaturesManager featuresManager, GuardrailsResponseGroupStore guardrailsResponseGroupStore, InstanceAssessmentService instanceAssessmentService, AccessLogProcessingJobRunner accessLogProcessingJobRunner) {
        this.instanceAnalysisControlStore = instanceAnalysisControlStore;
        this.instanceAnalysisControlService = instanceAnalysisControlService;
        this.featuresManager = featuresManager;
        this.instanceAssessmentService = instanceAssessmentService;
        this.guardrailsResponseGroupStore = guardrailsResponseGroupStore;
        this.accessLogProcessingJobRunner = accessLogProcessingJobRunner;
    }

    public AssessmentStatus processJobProgress() {
        Optional<InstanceAnalysisControl> instanceAnalysisControl;
        if (this.featuresManager.isBrowserMetricsEnabled() && (instanceAnalysisControl = this.instanceAnalysisControlService.findInstanceAnalysisControl(InstanceAnalysisControlTypes.BROWSER_METRICS.name())).isPresent()) {
            return this.getCompleteAnalysisProgress(instanceAnalysisControl.get());
        }
        Optional<GuardrailsResponseGroup> guardrailsResponseGroup = this.guardrailsResponseGroupStore.findLastJobId();
        if (!guardrailsResponseGroup.isPresent()) {
            return new AssessmentStatus("", InstanceAssessmentStatus.NOT_STARTED, null);
        }
        String jobId = guardrailsResponseGroup.get().getJobId();
        try {
            Set<JobDetails> activeJobsDetails = this.instanceAssessmentService.getActiveJobs();
            Set activeJobs = activeJobsDetails.stream().map(JobDetails::getJobId).map(Object::toString).collect(Collectors.toSet());
            log.info("Current jobId is:{} and number of running jobs are :{}", (Object)jobId, (Object)activeJobs.size());
            if (activeJobs.contains(jobId)) {
                long progress = this.instanceAssessmentService.getJobProgress(jobId);
                return new AssessmentStatus(jobId, InstanceAssessmentStatus.IN_PROGRESS, String.valueOf(progress) + "% COMPLETE");
            }
            log.info("Job completed for jobId:{}", (Object)jobId);
            return new AssessmentStatus(this.formatter.format(guardrailsResponseGroup.get().getEndTimestamp()), InstanceAssessmentStatus.COMPLETE, "100% COMPLETE");
        }
        catch (Exception e) {
            log.error("Failed to get progress for jobId:{} with exception:{}", (Object)jobId, (Object)e);
            return new AssessmentStatus("", InstanceAssessmentStatus.FAILED, null);
        }
    }

    private AssessmentStatus getCompleteAnalysisProgress(InstanceAnalysisControl instanceAnalysisControl) {
        if (this.instanceAnalysisControlStore.isFinished(instanceAnalysisControl.getEndTimestamp())) {
            if (!this.accessLogProcessingJobRunner.isFinished()) {
                return new AssessmentStatus("", InstanceAssessmentStatus.IN_PROGRESS, "0 h 1 m remaining");
            }
            Long endTimestamp = instanceAnalysisControl.getEndTimestamp();
            Timestamp tms = new Timestamp(endTimestamp);
            Date completedDate = new Date(tms.getTime());
            return new AssessmentStatus(this.formatter.format(completedDate), InstanceAssessmentStatus.COMPLETE, null);
        }
        Duration remainingDuration = this.instanceAnalysisControlService.calculateRemainingDuration(instanceAnalysisControl);
        if (remainingDuration.isZero()) {
            this.stopAssessmentCollection();
        }
        return new AssessmentStatus("", InstanceAssessmentStatus.IN_PROGRESS, remainingDuration.toHours() + " h " + remainingDuration.toMinutes() % 60L + " m remaining");
    }

    private void stopAssessmentCollection() {
        log.info("Stopping Assessment Collection.");
        if (this.instanceAnalysisControlService.findInstanceAnalysisControl(InstanceAnalysisControlTypes.BROWSER_METRICS.name()).isPresent()) {
            this.instanceAnalysisControlService.finishAssessmentCollection();
            this.accessLogProcessingJobRunner.cleanup();
        }
    }

    public AssessmentStatus scheduleInstanceAssessment() throws SchedulerServiceException {
        Set<JobDetails> activeJobs;
        Optional<InstanceAnalysisControl> currentInstanceAnalysis;
        if (this.featuresManager.isBrowserMetricsEnabled()) {
            this.accessLogProcessingJobRunner.startAssessment();
        }
        if ((currentInstanceAnalysis = this.instanceAnalysisControlService.findInstanceAnalysisControl(InstanceAnalysisControlTypes.BROWSER_METRICS.name())).isPresent() && !this.instanceAnalysisControlStore.isFinished(currentInstanceAnalysis.get().getEndTimestamp())) {
            return new AssessmentStatus("", InstanceAssessmentStatus.IN_PROGRESS, this.processJobProgress().getProgress());
        }
        if (this.featuresManager.isBrowserMetricsEnabled()) {
            this.instanceAnalysisControlService.startAssessmentCollection();
        }
        if (!(activeJobs = this.instanceAssessmentService.getActiveJobs()).isEmpty()) {
            JobDetails job = activeJobs.iterator().next();
            String jobId = job.getJobId().toString();
            Map map = job.getParameters();
            String nodeId = (String)map.get(SERVER_NODE);
            log.info("Job already running for nodeId: {} and jobId:{}", (Object)nodeId, (Object)jobId);
            return new AssessmentStatus("", InstanceAssessmentStatus.IN_PROGRESS, this.processJobProgress().getProgress());
        }
        log.info("There are no active jobs");
        this.instanceAssessmentService.scheduleJob();
        return new AssessmentStatus("", InstanceAssessmentStatus.NEW, null);
    }
}

