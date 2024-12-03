/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.confluence.event.events.cluster.ClusterEventWrapper
 *  com.atlassian.event.Event
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.migration.app.dto.AppContainerDetails
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
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.apache.commons.collections.CollectionUtils
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.execution;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.event.Event;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.migration.agent.entity.ExecutionStatus;
import com.atlassian.migration.agent.entity.Plan;
import com.atlassian.migration.agent.entity.PlanSchedulerVersion;
import com.atlassian.migration.agent.entity.Progress;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.entity.Task;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.ClusterInformationService;
import com.atlassian.migration.agent.service.ClusterLimits;
import com.atlassian.migration.agent.service.MigrationErrorCode;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.analytics.ErrorContainerType;
import com.atlassian.migration.agent.service.analytics.ErrorEvent;
import com.atlassian.migration.agent.service.catalogue.PlatformService;
import com.atlassian.migration.agent.service.catalogue.model.TransferResponseList;
import com.atlassian.migration.agent.service.event.ExecuteStepsEvent;
import com.atlassian.migration.agent.service.event.StepAllocation;
import com.atlassian.migration.agent.service.event.StepExecutorHeartbeatEvent;
import com.atlassian.migration.agent.service.event.StopPlanEvent;
import com.atlassian.migration.agent.service.execution.SchedulingAlgorithm;
import com.atlassian.migration.agent.service.execution.StepAllocations;
import com.atlassian.migration.agent.service.impl.DefaultPlanService;
import com.atlassian.migration.agent.service.impl.StepType;
import com.atlassian.migration.agent.service.log.MigrationLogService;
import com.atlassian.migration.agent.service.planning.StepPlanningEngine;
import com.atlassian.migration.agent.service.planning.StepPlanningEngines;
import com.atlassian.migration.agent.service.planning.TaskPlanningEngine;
import com.atlassian.migration.agent.service.stepexecutor.ProgressTracker;
import com.atlassian.migration.agent.service.stepexecutor.StepResult;
import com.atlassian.migration.agent.service.user.UserMappingsFileManager;
import com.atlassian.migration.agent.service.user.UsersToTombstoneFileManager;
import com.atlassian.migration.agent.store.PlanStore;
import com.atlassian.migration.agent.store.StepStore;
import com.atlassian.migration.agent.store.TaskStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.atlassian.migration.app.DefaultRegistrar;
import com.atlassian.migration.app.dto.AppContainerDetails;
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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public class PlanExecutionService
implements JobRunner {
    static final JobRunnerKey RUNNER_KEY = JobRunnerKey.of((String)"migration-plugin:node-status-checker");
    static final JobId JOB_ID = JobId.of((String)"migration-plugin:node-status-checker-job-id");
    static final Duration RESCHEDULE_INTERVAL = Duration.ofSeconds(90L);
    static final String CLUSTER_LOCK = "PlanExecutionServiceLock";
    private static final Logger log = ContextLoggerFactory.getLogger(PlanExecutionService.class);
    private final PluginTransactionTemplate ptx;
    private final TaskPlanningEngine taskPlanningEngine;
    private final StepPlanningEngines stepPlanningEngines;
    private final PlanStore planStore;
    private final TaskStore taskStore;
    private final StepStore stepStore;
    private final ProgressTracker progressTracker;
    private final PlatformService platformService;
    private final UserMappingsFileManager userMappingsFileManager;
    private final UsersToTombstoneFileManager usersToTombstoneFileManager;
    private final MigrationLogService migrationLogService;
    private final DefaultRegistrar cloudMigrationRegistrar;
    private final EventPublisher eventPublisher;
    private final ClusterInformationService clusterInformationService;
    private final SchedulerService schedulerService;
    private final ClusterLockService lockService;
    private final AnalyticsEventService analyticsEventService;
    private final AnalyticsEventBuilder analyticsEventBuilder;
    private final Clock clock;
    private final Supplier<String> idGenerator;
    private final ConcurrentMap<String, StepExecutorHeartbeatEvent> receivedHeartbeats;

    public PlanExecutionService(PluginTransactionTemplate ptx, TaskPlanningEngine taskPlanningEngine, List<StepPlanningEngine<?>> planningEngines, PlanStore planStore, TaskStore taskStore, PlatformService platformService, UserMappingsFileManager userMappingsFileManager, UsersToTombstoneFileManager usersToTombstoneFileManager, MigrationLogService migrationLogService, DefaultRegistrar cloudMigrationRegistrar, EventPublisher eventPublisher, ClusterInformationService clusterInformationService, SchedulerService schedulerService, StepStore stepStore, ProgressTracker progressTracker, ClusterLockService lockService, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder) {
        this(ptx, taskPlanningEngine, new StepPlanningEngines(planningEngines), planStore, taskStore, stepStore, progressTracker, platformService, userMappingsFileManager, usersToTombstoneFileManager, migrationLogService, cloudMigrationRegistrar, eventPublisher, clusterInformationService, schedulerService, lockService, analyticsEventService, analyticsEventBuilder, Clock.systemUTC(), () -> UUID.randomUUID().toString());
    }

    public PlanExecutionService(PluginTransactionTemplate ptx, TaskPlanningEngine taskPlanningEngine, StepPlanningEngines stepPlanningEngines, PlanStore planStore, TaskStore taskStore, StepStore stepStore, ProgressTracker progressTracker, PlatformService platformService, UserMappingsFileManager userMappingsFileManager, UsersToTombstoneFileManager usersToTombstoneFileManager, MigrationLogService migrationLogService, DefaultRegistrar cloudMigrationRegistrar, EventPublisher eventPublisher, ClusterInformationService clusterInformationService, SchedulerService schedulerService, ClusterLockService lockService, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, Clock clock, Supplier<String> idGenerator) {
        this.ptx = ptx;
        this.taskPlanningEngine = taskPlanningEngine;
        this.stepPlanningEngines = stepPlanningEngines;
        this.planStore = planStore;
        this.taskStore = taskStore;
        this.stepStore = stepStore;
        this.progressTracker = progressTracker;
        this.platformService = platformService;
        this.userMappingsFileManager = userMappingsFileManager;
        this.usersToTombstoneFileManager = usersToTombstoneFileManager;
        this.migrationLogService = migrationLogService;
        this.cloudMigrationRegistrar = cloudMigrationRegistrar;
        this.eventPublisher = eventPublisher;
        this.clusterInformationService = clusterInformationService;
        this.schedulerService = schedulerService;
        this.lockService = lockService;
        this.analyticsEventService = analyticsEventService;
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.clock = clock;
        this.idGenerator = idGenerator;
        this.receivedHeartbeats = new ConcurrentHashMap<String, StepExecutorHeartbeatEvent>();
    }

    @PostConstruct
    public void postConstruct() throws SchedulerServiceException {
        this.schedulerService.registerJobRunner(RUNNER_KEY, (JobRunner)this);
        log.debug("Successfully registered ClusterNodeStatusChecker job {}.", (Object)RUNNER_KEY);
        this.schedulerService.scheduleJob(JOB_ID, JobConfig.forJobRunnerKey((JobRunnerKey)RUNNER_KEY).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(Schedule.forInterval((long)RESCHEDULE_INTERVAL.toMillis(), (Date)new Date(System.currentTimeMillis() + 5000L))));
        log.debug("Successfully started ClusterNodeStatusChecker.");
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void cleanup() {
        this.schedulerService.unregisterJobRunner(RUNNER_KEY);
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void handleHeartbeatEvent(StepExecutorHeartbeatEvent heartbeatEvent) {
        this.receivedHeartbeats.put(heartbeatEvent.getNodeId(), heartbeatEvent);
    }

    @EventListener
    public void handleClusteredEvent(ClusterEventWrapper clusterEventWrapper) {
        Event wrappedEvent = clusterEventWrapper.getEvent();
        if (wrappedEvent instanceof StepExecutorHeartbeatEvent) {
            this.handleHeartbeatEvent((StepExecutorHeartbeatEvent)wrappedEvent);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void runPlan(String planId) {
        List steps;
        log.info("Starting execution of plan {}", (Object)planId);
        ClusterLock lock = this.acquireLock();
        try {
            steps = this.ptx.write(() -> {
                List<Task> newTasks = this.taskPlanningEngine.getFirstTasks(planId);
                this.progressTracker.updateDetailedStatusForPlan(newTasks);
                if (newTasks.isEmpty()) {
                    throw new IllegalArgumentException("A plan must have at least one task.");
                }
                newTasks.forEach(this::generateNextStep);
                return this.allocatePendingSteps(planId);
            });
        }
        finally {
            lock.unlock();
        }
        this.distributeToExecutors(steps);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean stopPlan(String planId) {
        boolean markedAsStopping;
        log.info("Stopping execution of plan {}", (Object)planId);
        ClusterLock lock = this.acquireLock();
        try {
            markedAsStopping = this.ptx.write(() -> {
                boolean stopping = this.tryMarkAsStopping(planId);
                if (stopping) {
                    log.info("Marked plan {} as stopping. Requesting all step executors to stop work.", (Object)planId);
                    Plan plan = this.planStore.getPlanAndLock(planId);
                    this.taskStore.stopInactiveTasks(planId);
                    this.stepStore.stopCreatedSteps(planId);
                    if (!this.taskStore.hasRunningTasks(planId)) {
                        plan.setProgress(plan.getProgress().copy().stopped());
                        this.planStore.updatePlan(plan);
                        this.platformService.updateMigrationStatusToMcs(plan);
                    }
                }
                return stopping;
            });
        }
        finally {
            lock.unlock();
        }
        if (markedAsStopping) {
            this.stopPlanOnExecutors(planId);
        }
        return markedAsStopping;
    }

    private boolean tryMarkAsStopping(String planId) {
        Plan plan = this.planStore.getPlanAndLock(planId);
        ExecutionStatus status = plan.getProgress().getStatus();
        if (!status.canGo(ExecutionStatus.STOPPING)) {
            return false;
        }
        plan.setProgress(plan.getProgress().copy().stopping());
        this.planStore.updatePlan(plan);
        this.platformService.updateMigrationStatusToMcs(plan);
        return true;
    }

    private void stopPlanOnExecutors(String planId) {
        this.eventPublisher.publish((Object)new StopPlanEvent(this, planId));
    }

    private ClusterLock acquireLock() {
        ClusterLock lock = this.lockService.getLockForName(CLUSTER_LOCK);
        lock.lock();
        return lock;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onStepCompleted(StepAllocation stepAllocation, @Nullable StepResult result, @Nullable Throwable throwable) {
        List steps = Collections.emptyList();
        ClusterLock lock = this.acquireLock();
        try {
            Step step = this.ptx.read(() -> this.stepStore.getStep(stepAllocation.getStepId()));
            log.info("Step {} completed execution {} with result={}", new Object[]{step.getId(), step.getNodeExecutionId(), result, throwable});
            if (!Objects.equals(step.getNodeExecutionId(), stepAllocation.getNodeExecutionId())) {
                log.warn("Step {} completed by zombie executor. Ignoring. Actual executionId={} Expected executionId={}", new Object[]{step.getId(), result, stepAllocation.getNodeExecutionId()});
                return;
            }
            steps = this.ptx.write(() -> {
                this.updateStatus(step, result, throwable);
                return this.allocateNewWork(step.getId());
            });
        }
        catch (Exception e) {
            try {
                log.error("Failed to handle step completion for step {}", (Object)stepAllocation.getStepId(), (Object)e);
                this.progressTracker.failTask(stepAllocation.getTaskId(), "Failed to complete step: " + e.getMessage());
            }
            catch (Exception ex) {
                log.error("Unable to mark step {} as failed. The step will be treated as hung and retried.", (Object)stepAllocation.getStepId(), (Object)e);
            }
        }
        finally {
            lock.unlock();
        }
        this.distributeToExecutors(steps);
    }

    private List<StepAllocation> allocateNewWork(String stepId) {
        Step step = this.stepStore.getStep(stepId);
        Task task = step.getTask();
        Plan plan = step.getPlan();
        if (plan.getProgress().getStatus().isCompleted()) {
            log.info("Plan {} has completed execution with status {}.", (Object)plan.getId(), (Object)plan.getProgress().getStatus());
            this.onPlanCompletion(plan);
        } else if (plan.getProgress().getStatus() != ExecutionStatus.STOPPING) {
            if (!task.getProgress().getStatus().isCompleted()) {
                if (this.generateNextStep(task, step.getId()).isPresent()) {
                    return this.allocatePendingSteps(plan.getId());
                }
                throw new IllegalStateException("Task generated no next step yet wasn't marked as completed.");
            }
            List<Task> nextTasks = this.taskPlanningEngine.getNextTasks(task);
            this.progressTracker.updateDetailedStatusForPlan(nextTasks);
            nextTasks.forEach(this::generateNextStep);
            return this.allocatePendingSteps(plan.getId());
        }
        return Collections.emptyList();
    }

    private void updateStatus(Step step, @Nullable StepResult result) {
        this.updateStatus(step, result, null);
    }

    private void updateStatus(Step step, @Nullable StepResult result, @Nullable Throwable throwable) {
        String stepId = step.getId();
        StepType stepType = StepType.valueOf(step.getType());
        if (throwable != null) {
            log.error("An error occurred while running step with id: {}", (Object)stepId, (Object)throwable);
            this.analyticsEventService.sendAnalyticsEvent(() -> this.analyticsEventBuilder.buildErrorOperationalEvent(new ErrorEvent.ErrorEventBuilder(MigrationErrorCode.UNHANDLED_ERROR, ErrorContainerType.MIGRATION_ERROR, step.getPlan().getMigrationId(), stepType).setReason(throwable.getMessage()).setCloudid(step.getPlan().getCloudSite().getCloudId()).build()));
            this.progressTracker.completed(step, StepResult.failed(String.format("An unexpected error occurred during step: %s. Error: %s", stepType.getDisplayName(), throwable.getMessage()), throwable));
        } else if (result == null) {
            log.error("Step {} failed to produce a result, yet did not raise a throwable.", (Object)stepId);
            this.progressTracker.completed(step, StepResult.failed(String.format("An unexpected error occurred during step: %s. The step failed to produce a result.", stepType.getDisplayName())));
        } else {
            this.progressTracker.completed(step, result);
        }
    }

    @VisibleForTesting
    void onPlanCompletion(Plan plan) {
        this.migrationLogService.uploadMigrationLogsZipFromClustersToMCS(plan.getCloudSite().getCloudId(), plan.getMigrationId(), plan.getId());
        if (plan.getProgress().getStatus().canTriggerAppMigration() && DefaultPlanService.getMigrateAppsTask(plan).isPresent()) {
            this.updateContainerStatusAndMigrateApps(plan);
        }
        if (plan.getProgress().getStatus() == ExecutionStatus.FAILED || plan.getProgress().getStatus() == ExecutionStatus.STOPPED || plan.getProgress().getStatus() == ExecutionStatus.INCOMPLETE) {
            this.uploadMigrationLogZipToMCS(plan.getCloudSite().getCloudId(), plan.getMigrationId(), plan.getId());
        }
        this.cleanupUserMappings(plan.getId());
        this.cleanupUsersToTombstone(plan.getId());
    }

    private void updateContainerStatusAndMigrateApps(Plan plan) {
        boolean containersUpdated = this.platformService.updateSpaceContainerStatuses(plan);
        if (containersUpdated) {
            log.info("All Space container for plan {} status is updated in MCS. Triggering app migration...", (Object)plan.getId());
            this.startAppMigration(plan);
        } else {
            String message = String.format("Space containers status were not updated completely in MCS for plan %s. App Migration will not begin", plan.getId());
            log.error(message);
            DefaultPlanService.getMigrateAppsTask(plan).ifPresent(task -> this.progressTracker.failTask(task.getId(), message));
        }
    }

    private void startAppMigration(Plan plan) {
        Set<AppContainerDetails> appContainerDetails = this.platformService.getAppContainers(plan.getCloudSite().getCloudId(), plan.getMigrationId());
        this.cloudMigrationRegistrar.startMigration(plan.getCloudSite().getCloudId(), plan.getMigrationId(), appContainerDetails);
    }

    private void cleanupUserMappings(String planId) {
        log.info("Cleaning up user mappings for plan {}", (Object)planId);
        this.userMappingsFileManager.cleanupUserMappingsFile(planId);
    }

    private void cleanupUsersToTombstone(String planId) {
        log.info("Cleaning up users to tombstone file for plan {}", (Object)planId);
        this.usersToTombstoneFileManager.cleanupUsersToTombstoneFile(planId);
    }

    private void uploadMigrationLogZipToMCS(String cloudId, String migrationId, String planId) {
        this.migrationLogService.uploadMigrationErrorLogZipToMCS(cloudId, migrationId);
    }

    private void distributeToExecutors(List<StepAllocation> steps) {
        if (CollectionUtils.isNotEmpty(steps)) {
            this.eventPublisher.publish((Object)new ExecuteStepsEvent(this, steps));
        }
    }

    @VisibleForTesting
    List<StepAllocation> allocatePendingSteps(String planId) {
        ArrayList<Step> unallocatedSteps = new ArrayList<Step>();
        for (StepType stepType : StepType.values()) {
            unallocatedSteps.addAll(this.stepStore.getCreatedStepsOfType(planId, stepType, this.clusterInformationService.getClusterLimits().getClusterConcurrencyLimit(stepType)));
        }
        return this.tryAllocateSteps(planId, unallocatedSteps);
    }

    @VisibleForTesting
    List<StepAllocation> tryAllocateSteps(String planId, List<Step> unallocatedSteps) {
        List<String> nodes = this.clusterInformationService.getAllNodeIds();
        log.info("Allocating steps for plan {}. There are currently {} step(s) pending execution, and {} available execution node(s): {}", new Object[]{planId, unallocatedSteps.size(), nodes.size(), nodes});
        ArrayList<StepAllocation> newlyAllocatedSteps = new ArrayList<StepAllocation>();
        if (!unallocatedSteps.isEmpty()) {
            StepAllocations currentAllocations = this.getStepAllocationsForNodes(planId, nodes);
            for (Step step : unallocatedSteps) {
                Optional<String> bestNode = this.findBestNode(currentAllocations, step, this.clusterInformationService.getClusterLimits());
                if (!bestNode.isPresent()) continue;
                StepAllocation stepAllocation = this.allocateStepToNode(step, bestNode.get());
                currentAllocations.addStepToNode(bestNode.get(), step);
                newlyAllocatedSteps.add(stepAllocation);
            }
        }
        return newlyAllocatedSteps;
    }

    Optional<String> findBestNode(StepAllocations currentAllocations, Step step, ClusterLimits clusterLimits) {
        StepType stepType = StepType.valueOf(step.getType());
        SchedulingAlgorithm schedulingAlgorithm = clusterLimits.getSchedulingAlgorithm(stepType);
        return schedulingAlgorithm.findBestNode(currentAllocations, clusterLimits, stepType);
    }

    private StepAllocation allocateStepToNode(Step step, String nodeId) {
        String executionId = this.idGenerator.get();
        log.info("Allocating step {} to node {}. ExecutionId={}", new Object[]{step.getId(), nodeId, executionId});
        step.setNodeId(nodeId);
        step.setNodeExecutionId(executionId);
        step.setNodeHeartbeat(this.clock.instant());
        this.stepStore.update(step);
        this.progressTracker.started(step, StepType.valueOf(step.getType()).getDisplayName());
        return new StepAllocation(step.getId(), step.getTask().getId(), nodeId, executionId);
    }

    private StepAllocations getStepAllocationsForNodes(String planId, List<String> nodes) {
        HashMap<String, StepAllocations.NodeStepAllocations> allocationsByNode = new HashMap<String, StepAllocations.NodeStepAllocations>();
        Map<String, List<Step>> currentAllocations = this.stepStore.getRunningStepsForPlan(planId).stream().collect(Collectors.groupingBy(Step::getNodeId));
        for (String node : nodes) {
            List<Step> currentAllocation = currentAllocations.get(node);
            allocationsByNode.put(node, new StepAllocations.NodeStepAllocations(node, currentAllocation));
        }
        return new StepAllocations(allocationsByNode);
    }

    private Optional<Step> generateNextStep(Task task) {
        return this.generateNextStep(task, null);
    }

    private Optional<Step> generateNextStep(Task task, @Nullable String predecessorStepId) {
        Optional<Step> nextStep = this.ptx.read(() -> this.stepStore.getStepsByTaskId(task.getId()).stream().filter(step -> !step.getProgress().getStatus().isCompleted()).findFirst());
        if (!nextStep.isPresent()) {
            if (predecessorStepId == null) {
                Step step = this.getStepPlanningEngine(task).createFirstStep(task);
                step.setIndex(0);
                step.setTask(task);
                this.setTransferId(step);
                this.ptx.write(() -> this.stepStore.addSteps((Collection<Step>)ImmutableList.of((Object)step)));
                nextStep = Optional.of(step);
            } else {
                Step predecessor = this.ptx.read(() -> this.stepStore.getStep(predecessorStepId));
                nextStep = this.getStepPlanningEngine(task).createNextStep(task, predecessor);
                if (nextStep.isPresent()) {
                    Step step = nextStep.get();
                    step.setIndex(predecessor.getIndex() + 1);
                    step.setTask(task);
                    this.setTransferId(step);
                    this.ptx.write(() -> this.stepStore.addSteps((Collection<Step>)ImmutableList.of((Object)step)));
                }
            }
        }
        return nextStep;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public JobRunnerResponse runJob(JobRunnerRequest req) {
        ArrayList<StepAllocation> steps = new ArrayList<StepAllocation>();
        ClusterLock lock = this.acquireLock();
        try {
            List runningPlanIds = this.ptx.read(() -> this.planStore.getPlanIdsInStatusForSchedulerVersion((List<ExecutionStatus>)ImmutableList.of((Object)((Object)ExecutionStatus.RUNNING), (Object)((Object)ExecutionStatus.STOPPING)), PlanSchedulerVersion.PLAN_EXECUTION_SERVICE));
            for (String planId : runningPlanIds) {
                log.info("Running plan execution watchdog for plan {}", (Object)planId);
                steps.addAll(this.ptx.write(() -> {
                    Plan plan = this.planStore.getPlanAndLock(planId);
                    this.resetHungSteps(plan, this.findHungSteps(planId));
                    return this.allocatePendingSteps(planId);
                }));
            }
        }
        finally {
            lock.unlock();
        }
        this.distributeToExecutors(steps);
        return JobRunnerResponse.success();
    }

    private List<Step> findHungSteps(String planId) {
        Instant currentTime = this.clock.instant();
        List<Step> hungSteps = this.stepStore.getHungStepsForPlan(planId, currentTime, RESCHEDULE_INTERVAL.toMillis());
        return hungSteps.stream().filter(step -> {
            StepExecutorHeartbeatEvent heartbeat = (StepExecutorHeartbeatEvent)((Object)((Object)this.receivedHeartbeats.get(step.getNodeId())));
            return heartbeat == null || !heartbeat.containsExecution(step.getNodeExecutionId()) || currentTime.toEpochMilli() - heartbeat.getHeartbeatTime() > RESCHEDULE_INTERVAL.toMillis();
        }).collect(Collectors.toList());
    }

    private void resetHungSteps(Plan plan, List<Step> hungSteps) {
        String planId = plan.getId();
        if (!hungSteps.isEmpty()) {
            log.warn("Found hung steps for plan {}: {}", (Object)planId, hungSteps.stream().map(step -> ImmutableMap.of((Object)"id", (Object)step.getId(), (Object)"nodeExecutionId", (Object)(step.getNodeExecutionId() == null ? "no execution Id set" : step.getNodeExecutionId()), (Object)"nodeHeartbeat", (Object)(step.getNodeHeartbeat() == null ? "no heartbeat set" : step.getNodeHeartbeat()))).collect(Collectors.toList()));
            for (Step step2 : hungSteps) {
                this.clearStepAllocation(step2);
                this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildStuckStepAnalyticsEvent(step2, Optional.empty()));
                if (plan.getProgress().getStatus() != ExecutionStatus.STOPPING) continue;
                this.updateStatus(step2, StepResult.stopped());
            }
        }
    }

    private void clearStepAllocation(Step step) {
        log.info("Cleared step allocation for step {}", (Object)step.getId());
        step.setProgress(Progress.created());
        step.setNodeHeartbeat(null);
        step.setNodeId(null);
        step.setNodeExecutionId(null);
        if (step.getType().equals(StepType.USERS_MIGRATION.name())) {
            step.setExecutionState(null);
        }
        this.stepStore.update(step);
        this.progressTracker.updateTransfer(step);
    }

    private StepPlanningEngine getStepPlanningEngine(Task task) {
        return this.stepPlanningEngines.of(task).orElseThrow(() -> new IllegalStateException("Could not find step planning engine for task of type: " + task.getClass().getCanonicalName()));
    }

    private void setTransferId(Step step) {
        Task task = step.getTask();
        StepType stepType = StepType.valueOf(step.getType());
        Optional<TransferResponseList> response = this.platformService.createTransfers(task.getPlan().getCloudSite().getCloudId(), task.getPlan().getMigrationId(), task.getContainerId(), Collections.singletonList(stepType.getOperationKey()));
        if (response.isPresent() && !response.get().getTransfers().isEmpty()) {
            this.ptx.write(() -> step.setTransferId(((TransferResponseList)response.get()).getTransfers().get(0).getTransferId()));
        }
    }
}

