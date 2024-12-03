/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
 *  com.atlassian.scheduler.status.JobDetails
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.guardrails;

import com.atlassian.migration.agent.entity.GuardrailsResponse;
import com.atlassian.migration.agent.entity.GuardrailsResponseGroup;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.newexport.DbType;
import com.atlassian.migration.agent.service.ClusterInformationService;
import com.atlassian.migration.agent.store.guardrails.AssessmentQuery;
import com.atlassian.migration.agent.store.guardrails.AssessmentStatus;
import com.atlassian.migration.agent.store.guardrails.GuardrailsResponseGroupStore;
import com.atlassian.migration.agent.store.guardrails.GuardrailsResponseStore;
import com.atlassian.migration.agent.store.guardrails.GuardrailsResponseType;
import com.atlassian.migration.agent.store.guardrails.InstanceAssessmentStatus;
import com.atlassian.migration.agent.store.guardrails.L1AssessmentResult;
import com.atlassian.migration.agent.store.jpa.impl.DialectResolver;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class InstanceAssessmentService
implements JobRunner {
    private final List<AssessmentQuery<?>> queryList;
    private static final Logger log = ContextLoggerFactory.getLogger(InstanceAssessmentService.class);
    private static final JobRunnerKey RUNNER_KEY = JobRunnerKey.of((String)"com.atlassian.confluence.migration.guardrails.InstanceAssessmentService");
    private static final String DEFAULT_NODE = "defaultClusterNode";
    private static final String SERVER_NODE = "serverNode";
    private final SchedulerService schedulerService;
    private final Map<String, AssessmentQuery<?>> queryMap;
    private final Map<DbType, List<String>> queriesToSkipPerDBMap;
    private final DialectResolver dialectResolver;
    private final GuardrailsResponseGroupStore guardRailsResponseGroupStore;
    private final GuardrailsResponseStore guardrailsResponseStore;
    private final ClusterInformationService clusterInformationService;
    private final PluginTransactionTemplate ptx;

    public InstanceAssessmentService(List<AssessmentQuery<?>> queryList, SchedulerService schedulerService, DialectResolver dialectResolver, GuardrailsResponseGroupStore guardRailsResponseGroupStore, GuardrailsResponseStore guardrailsResponseStore, ClusterInformationService clusterInformationService, PluginTransactionTemplate ptx) {
        this.queryList = queryList;
        this.schedulerService = schedulerService;
        this.dialectResolver = dialectResolver;
        this.guardRailsResponseGroupStore = guardRailsResponseGroupStore;
        this.guardrailsResponseStore = guardrailsResponseStore;
        this.clusterInformationService = clusterInformationService;
        this.ptx = ptx;
        this.queryMap = new HashMap();
        this.queriesToSkipPerDBMap = new HashMap<DbType, List<String>>();
        this.populateQueryMap();
        this.populateQueriesToSkipPerDBMap();
    }

    private void populateQueryMap() {
        this.queryList.forEach(query -> this.queryMap.put(query.getQueryId(), (AssessmentQuery<?>)query));
    }

    private void populateQueriesToSkipPerDBMap() {
    }

    public List<String> executeAllQueries() {
        return this.queryList.stream().map(this::executeQueryForL1).collect(Collectors.toList());
    }

    private String executeQueryForL1(AssessmentQuery<?> assessmentQuery) {
        L1AssessmentResult result = (L1AssessmentResult)assessmentQuery.execute();
        String queryId = assessmentQuery.getQueryId();
        return queryId + ":" + result.generateL1AssessmentData();
    }

    @PostConstruct
    public void initialize() {
        this.schedulerService.registerJobRunner(RUNNER_KEY, (JobRunner)this);
    }

    @PreDestroy
    public void cleanup() {
        this.schedulerService.unregisterJobRunner(RUNNER_KEY);
    }

    @Nullable
    public JobRunnerResponse runJob(@NotNull JobRunnerRequest request) {
        try {
            String jobId = request.getJobId().toString();
            String nodeId = this.getNodeId();
            GuardrailsResponseGroup responseGroup = new GuardrailsResponseGroup(jobId, nodeId);
            String responseGroupId = this.ptx.write(() -> this.guardRailsResponseGroupStore.createResponseGroup(responseGroup));
            log.info("Running job for jobId:{}", (Object)jobId);
            List queriesToSkip = this.queriesToSkipPerDBMap.getOrDefault((Object)this.dialectResolver.getDbType(), Collections.emptyList());
            this.queryList.forEach(query -> this.executeQueryAndSave((AssessmentQuery<?>)query, request.getJobId().toString(), responseGroupId, queriesToSkip));
            log.info("Running jobs completed for jobId:{}", (Object)request.getJobId());
            this.ptx.write(() -> this.guardRailsResponseGroupStore.updateResponseGroup(responseGroupId));
        }
        catch (Exception e) {
            log.error(String.format("Failed to run job - jobId:{} for nodeId:{} ", request.getJobId(), this.getNodeId()), (Throwable)e);
        }
        return JobRunnerResponse.success((String)"");
    }

    public void executeQueryAndSave(AssessmentQuery<?> query, String jobId, String responseGroupId, List<String> queriesToSkip) {
        Long start = System.currentTimeMillis();
        GuardrailsResponse guardrailsResponse = new GuardrailsResponse();
        guardrailsResponse.setResponseGroupId(responseGroupId);
        guardrailsResponse.setQueryId(query.getQueryId());
        guardrailsResponse.setGuardrailsResponseType(GuardrailsResponseType.L1);
        GuardrailsResponseGroup guardrailsResponseGroup = this.guardRailsResponseGroupStore.getResponseGroupByJobId(jobId);
        guardrailsResponse.setResponseGroup(guardrailsResponseGroup);
        if (queriesToSkip.contains(query.getQueryId())) {
            log.info("Skipping query with queryId:{}", (Object)query.getQueryId());
            guardrailsResponse.setQueryResponse("");
            guardrailsResponse.setSuccess(true);
            guardrailsResponse.setQueryStatus(InstanceAssessmentStatus.SKIPPED.toString());
        } else {
            L1AssessmentResult result = null;
            try {
                result = (L1AssessmentResult)query.execute();
                Long end = System.currentTimeMillis();
                log.info("Finished executing query with queryId:{} for jobID:{} in :{}ms", new Object[]{query.getQueryId(), jobId, end - start});
                guardrailsResponse.setQueryResponse(result.generateL1AssessmentData() != null ? result.generateL1AssessmentData() : "");
                guardrailsResponse.setSuccess(true);
            }
            catch (Exception e) {
                log.error("Error while executing query for queryID: " + query.getQueryId(), (Throwable)e);
                guardrailsResponse.setQueryResponse("");
                guardrailsResponse.setSuccess(false);
            }
            String queryStatus = InstanceAssessmentStatus.FAILED.toString();
            if (guardrailsResponse.isSuccess()) {
                queryStatus = InstanceAssessmentStatus.COMPLETE.toString();
            }
            guardrailsResponse.setQueryStatus(queryStatus);
        }
        this.ptx.write(() -> this.guardrailsResponseStore.createGuardrailsResponse(guardrailsResponse));
    }

    private String getNodeId() {
        return Optional.ofNullable(this.clusterInformationService.getCurrentNodeId()).orElse(DEFAULT_NODE);
    }

    public String executeQuery(String queryId) {
        return ((L1AssessmentResult)this.queryMap.get(queryId).execute()).generateL1AssessmentData();
    }

    public long getJobProgress(String jobId) {
        log.info("Job running for jobId:{}", (Object)jobId);
        int numberOfQueries = this.queryList.size();
        Long completedQueries = this.guardrailsResponseStore.getNumberOfQueries(jobId);
        log.info("Queries :{} is :{}", (Object)completedQueries, (Object)numberOfQueries);
        long progress = numberOfQueries == 0 ? 0L : completedQueries * 100L / (long)numberOfQueries;
        log.info("Progress for jobId:{} is :{}", (Object)jobId, (Object)progress);
        return progress;
    }

    public Set<JobDetails> getActiveJobs() {
        return this.schedulerService.getJobsByJobRunnerKey(RUNNER_KEY).stream().collect(Collectors.toSet());
    }

    public AssessmentStatus activeAssessment() {
        try {
            Set<JobDetails> activeJobs = this.getActiveJobs();
            if (!activeJobs.isEmpty()) {
                log.info("Job already running");
                JobDetails job = activeJobs.iterator().next();
                String jobId = job.getJobId().toString();
                return new AssessmentStatus(jobId, InstanceAssessmentStatus.IN_PROGRESS, String.valueOf(this.getJobProgress(jobId)));
            }
            return this.guardrailsResponseStore.getLatestResponseGroup().map(responseGroup -> new AssessmentStatus(responseGroup.getJobId(), InstanceAssessmentStatus.COMPLETE, String.valueOf(100L))).orElseGet(() -> new AssessmentStatus("", InstanceAssessmentStatus.NOT_STARTED, null));
        }
        catch (Exception e) {
            log.error("Failed to get active jobs with exception:{}", (Object)e.getMessage(), (Object)e);
            return new AssessmentStatus(null, InstanceAssessmentStatus.FAILED, null);
        }
    }

    public void scheduleJob() throws SchedulerServiceException {
        JobId jobId = this.schedulerService.scheduleJobWithGeneratedId(JobConfig.forJobRunnerKey((JobRunnerKey)RUNNER_KEY).withParameters(Collections.singletonMap(SERVER_NODE, this.getNodeId())).withSchedule(Schedule.runOnce(null)).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER));
        log.info("Job scheduled with id:{} for  nodeId:{}", (Object)jobId, (Object)this.getNodeId());
    }
}

