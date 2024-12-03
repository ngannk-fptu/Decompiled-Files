/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  io.atlassian.util.concurrent.ThreadFactories
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.PreDestroy
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.stepexecutor;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.migration.agent.dto.PlanDto;
import com.atlassian.migration.agent.dto.ProgressDto;
import com.atlassian.migration.agent.entity.AbstractSpaceTask;
import com.atlassian.migration.agent.entity.ConfluenceSpaceTask;
import com.atlassian.migration.agent.entity.ExecutionStatus;
import com.atlassian.migration.agent.entity.MapiTaskMapping;
import com.atlassian.migration.agent.entity.MigrationTag;
import com.atlassian.migration.agent.entity.Plan;
import com.atlassian.migration.agent.entity.Progress;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.entity.Task;
import com.atlassian.migration.agent.entity.TaskType;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.logging.LoggingContextBuilder;
import com.atlassian.migration.agent.mapi.entity.MapiTaskStatus;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.catalogue.PlatformService;
import com.atlassian.migration.agent.service.catalogue.TransferProgressRequest;
import com.atlassian.migration.agent.service.impl.MapiTaskMappingService;
import com.atlassian.migration.agent.service.impl.PlanConverter;
import com.atlassian.migration.agent.service.impl.StepSubType;
import com.atlassian.migration.agent.service.impl.StepType;
import com.atlassian.migration.agent.service.log.MigrationLogService;
import com.atlassian.migration.agent.service.planning.StepPlanningEngine;
import com.atlassian.migration.agent.service.planning.StepPlanningEngines;
import com.atlassian.migration.agent.service.planning.TaskPlanningEngine;
import com.atlassian.migration.agent.service.prc.model.CommandName;
import com.atlassian.migration.agent.service.stepexecutor.StepResult;
import com.atlassian.migration.agent.store.PlanStore;
import com.atlassian.migration.agent.store.StepProgressPropertiesStore;
import com.atlassian.migration.agent.store.StepStore;
import com.atlassian.migration.agent.store.TaskStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.google.common.collect.ImmutableList;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.PreDestroy;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public class ProgressTracker {
    private static final Logger log = ContextLoggerFactory.getLogger(ProgressTracker.class);
    private final PluginTransactionTemplate ptx;
    private final StepPlanningEngines planningEngines;
    private final StepStore stepStore;
    private final TaskStore taskStore;
    private final PlanStore planStore;
    private final StepProgressPropertiesStore stepProgressPropertiesStore;
    private final PlatformService platformService;
    private final TaskPlanningEngine taskPlanningEngine;
    private final AnalyticsEventService analyticsEventService;
    private final AnalyticsEventBuilder analyticsEventBuilder;
    private final PlanConverter planConverter;
    private final MigrationLogService migrationLogService;
    private final MapiTaskMappingService mapiTaskMappingService;
    private final ExecutorService executorService;
    private final Set<String> clubbedStepTypesForStandardisedEvents = new HashSet<String>(Arrays.asList(StepType.CONFLUENCE_EXPORT.name(), StepType.SPACE_USERS_MIGRATION.name(), StepType.GLOBAL_ENTITIES_EXPORT.name()));
    private static final Map<TaskType, String> translationMapToFetchDetailedStatusForPlan = new HashMap<TaskType, String>();

    public ProgressTracker(PluginTransactionTemplate ptx, List<StepPlanningEngine<?>> planningEngines, StepStore stepStore, TaskStore taskStore, PlanStore planStore, TaskPlanningEngine taskPlanningEngine, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, PlanConverter planConverter, MigrationLogService migrationLogService, MapiTaskMappingService mapiTaskMappingService, PlatformService platformService, StepProgressPropertiesStore stepProgressPropertiesStore) {
        this(ptx, planningEngines, stepStore, taskStore, planStore, taskPlanningEngine, analyticsEventService, analyticsEventBuilder, planConverter, migrationLogService, mapiTaskMappingService, platformService, ProgressTracker.newFixedThreadPool(4, ThreadFactories.namedThreadFactory((String)ProgressTracker.class.getName())), stepProgressPropertiesStore);
    }

    @VisibleForTesting
    ProgressTracker(PluginTransactionTemplate ptx, List<StepPlanningEngine<?>> planningEngines, StepStore stepStore, TaskStore taskStore, PlanStore planStore, TaskPlanningEngine taskPlanningEngine, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, PlanConverter planConverter, MigrationLogService migrationLogService, MapiTaskMappingService mapiTaskMappingService, PlatformService platformService, ExecutorService executorService, StepProgressPropertiesStore stepProgressPropertiesStore) {
        this.ptx = ptx;
        this.planningEngines = new StepPlanningEngines(planningEngines);
        this.stepStore = stepStore;
        this.taskStore = taskStore;
        this.planStore = planStore;
        this.taskPlanningEngine = taskPlanningEngine;
        this.analyticsEventService = analyticsEventService;
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.planConverter = planConverter;
        this.migrationLogService = migrationLogService;
        this.platformService = platformService;
        this.mapiTaskMappingService = mapiTaskMappingService;
        this.executorService = executorService;
        this.stepProgressPropertiesStore = stepProgressPropertiesStore;
    }

    @PreDestroy
    @VisibleForTesting
    void preDestroy() {
        this.executorService.shutdown();
    }

    private static ExecutorService newFixedThreadPool(int nThreads, ThreadFactory threadFactory) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(nThreads, nThreads, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(1000), threadFactory);
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    public void started(Step step, String message) {
        Objects.requireNonNull(step);
        Objects.requireNonNull(message);
        log.info("Step started: {}, message: {}", (Object)step.getId(), (Object)message);
        step.setProgress(step.getProgress().copy().started(message));
        this.stepStore.update(step);
        this.updateTransfer(step);
        Task task = step.getTask();
        StepPlanningEngine.PercentRange percentRange = this.getStepPercentRange(step);
        ExecutionStatus status = task.getProgress().getStatus();
        if (percentRange.isFirst() && status != ExecutionStatus.RUNNING) {
            task.setProgress(task.getProgress().copy().started(message));
            task.setProgress(task.getProgress().copy().progress(percentRange.from, message, step.getDetailedStatus()));
            this.updateContainer(task);
            this.saveTaskAnalyticsEvent(task, status);
        } else {
            task.setProgress(task.getProgress().copy().progress(percentRange.from, message, step.getDetailedStatus()));
        }
        this.taskStore.update(task);
    }

    public void progress(String stepId, int percent, @Nullable String message, @Nullable String detailedStatus) {
        this.progress(stepId, percent, message, detailedStatus, Collections.emptyMap());
    }

    public void progress(String stepId, int percent, @Nullable String message, @Nullable String detailedStatus, @Nonnull Map<String, Object> progressProperties) {
        Objects.requireNonNull(stepId);
        Step updatedStep = this.ptx.write(() -> {
            Step step = this.stepStore.getAndLock(stepId);
            step.setProgress(step.getProgress().copy().progress(percent, message, detailedStatus));
            this.storeStepProgressPropertiesSafely(stepId, progressProperties);
            LoggingContextBuilder.logCtx().withStep(step).execute(() -> {
                this.stepStore.update(step);
                this.updateTaskProgress(step, percent, message, detailedStatus);
            });
            return step;
        });
        this.updateTransferAsync(updatedStep);
    }

    private void storeStepProgressPropertiesSafely(String stepId, @NotNull Map<String, Object> progressProperties) {
        try {
            this.stepProgressPropertiesStore.storeStepProgressProperties(stepId, progressProperties);
        }
        catch (Exception e) {
            log.error("There was an error when storing progress properties for step: " + stepId, (Throwable)e);
        }
    }

    public void progressSubStep(String stepId, int percent, @Nullable String message, @Nullable String detailedStatus, @Nullable StepSubType subType, @Nonnull Map<String, Object> progressProperties) {
        Objects.requireNonNull(stepId);
        Step updatedStep = this.ptx.write(() -> {
            Step step = this.stepStore.getAndLock(stepId);
            step.setProgress(step.getProgress().copy().progress(percent, message, detailedStatus));
            this.storeStepProgressPropertiesSafely(stepId, progressProperties);
            if (subType != null) {
                step.setSubType(subType.name());
            }
            LoggingContextBuilder.logCtx().withStep(step).execute(() -> {
                this.stepStore.update(step);
                this.updateTaskProgress(step, percent, message, detailedStatus);
            });
            return step;
        });
        this.updateTransferAsync(updatedStep);
    }

    public void updateProgressPropertiesWithoutTransferUpdate(String stepId, Map<String, Object> progressProperties) {
        this.ptx.write(() -> {
            Step step = this.stepStore.getAndLock(stepId);
            this.storeStepProgressPropertiesSafely(stepId, progressProperties);
            LoggingContextBuilder.logCtx().withStep(step).execute(() -> this.stepStore.update(step));
        });
    }

    private void updateTaskProgress(Step step, int percent, @Nullable String message, @Nullable String detailedStatus) {
        StepPlanningEngine.PercentRange percentRange;
        try {
            percentRange = this.getStepPercentRange(step);
        }
        catch (IllegalStateException e) {
            log.error("Cannot get step range, skipping update of task", (Throwable)e);
            return;
        }
        Task task = step.getTask();
        int newPercent = percentRange.from + (percentRange.to - percentRange.from) * percent / 100;
        task.setProgress(task.getProgress().copy().progress(newPercent, message, detailedStatus));
        this.taskStore.update(task);
    }

    public void progressUpdateForSubStep(String stepId, int percent, @Nullable String message, String detailedStatus, @Nullable StepSubType nextSubStep, Map<String, Object> progressProperties) {
        StepPlanningEngine.PercentRange percentRange;
        Objects.requireNonNull(stepId);
        Step step = this.stepStore.getStep(stepId);
        try {
            percentRange = this.getSubStepPercentRange(step);
        }
        catch (IllegalStateException e) {
            log.error("Cannot get step range, skipping update of task", (Throwable)e);
            return;
        }
        int newStepPercent = percentRange.from + (percentRange.to - percentRange.from) * percent / 100;
        this.progressSubStep(stepId, newStepPercent, message, detailedStatus, nextSubStep, progressProperties);
    }

    public void completed(Step step, StepResult result) {
        Objects.requireNonNull(step);
        Objects.requireNonNull(result);
        if (result.isSuccess()) {
            this.done(step, result.getMessage(), result.getResult());
        } else if (result.isStopped()) {
            this.stopped(step);
        } else {
            this.failed(step, result.getMessage(), result.getException());
        }
        this.sendStandardisedStepCompletionEvents(step, step.getProgress().getStatus());
    }

    private StepPlanningEngine.PercentRange getStepPercentRange(Step step) {
        Task task = step.getTask();
        StepPlanningEngine<? extends Task> engine = this.planningEngines.of(task).orElseThrow(() -> new IllegalStateException("Unknown task type " + task.getClass()));
        Optional<StepPlanningEngine.PercentRange> maybeRange = engine.getStepPercentRange(step);
        return maybeRange.orElseThrow(() -> new IllegalStateException("Unknown step type " + step.getType()));
    }

    private StepPlanningEngine.PercentRange getSubStepPercentRange(Step step) {
        Task task = step.getTask();
        StepPlanningEngine<? extends Task> engine = this.planningEngines.of(task).orElseThrow(() -> new IllegalStateException("Unknown task type " + task.getClass()));
        Optional<StepPlanningEngine.PercentRange> maybeRange = engine.getSubStepPercentRange(step);
        return maybeRange.orElseThrow(() -> new IllegalStateException("Unknown step type " + step.getType()));
    }

    private void updatePlanWhenStepDone(Task task) {
        String planId = task.getPlan().getId();
        Plan plan = this.planStore.getPlanAndLock(planId);
        Progress planProgress = plan.getProgress();
        if (planProgress.getStatus().isCompleted()) {
            log.debug("Plan is completed, progress will not be updated further");
            return;
        }
        Progress newProgress = planProgress.copy().updatePercent(this.taskStore.calculatePlanPercent(planId));
        plan.setProgress(newProgress);
        if (this.taskPlanningEngine.hasReachedTerminalState(planId)) {
            this.setPlanAsComplete(plan);
            this.planStore.updatePlan(plan);
            this.setMapiTaskStatus(plan);
            this.savePlanAnalyticsEvents(planId, plan.getProgress().getStatus() == ExecutionStatus.DONE);
        } else {
            this.planStore.updatePlan(plan);
        }
        this.updateMigrationStatus(plan);
    }

    private void setMapiTaskStatus(Plan plan) {
        try {
            Optional<MapiTaskMapping> mapiTaskMapping = this.mapiTaskMappingService.getTaskMapping(plan.getId(), Optional.of(ImmutableList.of((Object)((Object)MapiTaskStatus.CHECKS_IN_PROGRESS), (Object)((Object)MapiTaskStatus.CHECKS_COMPLETED))), Optional.of(ImmutableList.of((Object)CommandName.MIGRATE.getName())));
            if (mapiTaskMapping.isPresent()) {
                this.mapiTaskMappingService.updateTaskMappingStatus(mapiTaskMapping.get(), MapiTaskStatus.MIGRATION_COMPLETED);
            }
        }
        catch (Exception e) {
            log.error("Error while setting mapi task status", (Throwable)e);
        }
    }

    private void setPlanAsComplete(Plan plan) {
        Progress newProgress;
        Optional<Task> failedTask = this.taskStore.findFirstTaskWithStatusForPlan(plan.getId(), ExecutionStatus.FAILED);
        boolean hasSuccessfulSpaceTask = this.taskStore.existTaskByStatusByTypeInPlan(plan.getId(), ExecutionStatus.DONE, ConfluenceSpaceTask.class);
        if (failedTask.isPresent() && !hasSuccessfulSpaceTask) {
            String message = failedTask.get().getProgress().getMessage();
            newProgress = plan.getProgress().copy().failed(message);
        } else {
            newProgress = plan.getProgress().getStatus() == ExecutionStatus.STOPPING ? plan.getProgress().copy().stopped() : (failedTask.isPresent() ? plan.getProgress().copy().incomplete(failedTask.get().getProgress().getMessage()) : plan.getProgress().copy().done());
        }
        plan.setProgress(newProgress);
    }

    public void failPlan(String planId, String message) {
        Plan updatedPlan = this.ptx.write(() -> {
            Plan plan = this.planStore.getPlanAndLock(planId);
            Progress newProgress = plan.getProgress().copy().failed(message);
            plan.setProgress(newProgress);
            this.planStore.updatePlan(plan);
            return plan;
        });
        this.updateMigrationStatus(updatedPlan);
        this.savePlanAnalyticsEvents(planId, false);
    }

    public void failTask(String taskId, String message) {
        this.ptx.write(() -> {
            Task task = this.taskStore.getTask(taskId);
            ExecutionStatus status = task.getProgress().getStatus();
            task.setProgress(task.getProgress().copy().failed(message));
            this.taskStore.update(task);
            this.stepStore.getStepsByTaskId(taskId).stream().filter(step -> !step.getProgress().getStatus().isCompleted()).forEach(step -> {
                step.setProgress(step.getProgress().copy().failed(message));
                this.stepStore.update((Step)step);
                this.updateTransfer((Step)step);
            });
            this.updateContainer(task);
            this.saveTaskAnalyticsEvent(task, status);
            this.updatePlanWhenStepDone(task);
        });
    }

    private void done(Step step, @Nullable String message, @Nullable String result) {
        StepPlanningEngine.PercentRange percentRange;
        log.debug("Step done, message: {}", (Object)message);
        try {
            percentRange = this.getStepPercentRange(step);
        }
        catch (IllegalStateException e) {
            this.failed(step, "Failed to get the step percent, just try to mark it fail", e);
            return;
        }
        step.setProgress(step.getProgress().copy().done(message, result));
        this.stepStore.update(step);
        this.updateTransfer(step);
        Task task = step.getTask();
        ExecutionStatus status = task.getProgress().getStatus();
        if (percentRange.isLast()) {
            task.setProgress(task.getProgress().copy().done(message, null));
            this.saveTaskAnalyticsEvent(task, status);
        } else if (step.getPlan().getProgress().getStatus() == ExecutionStatus.STOPPING) {
            task.setProgress(task.getProgress().copy().stopped());
            this.saveTaskAnalyticsEvent(task, status);
        } else {
            task.setProgress(task.getProgress().copy().progress(percentRange.to, message, step.getDetailedStatus()));
        }
        this.taskStore.update(task);
        this.updateContainer(task);
        this.updatePlanWhenStepDone(task);
    }

    public void updateDetailedStatusForPlan(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return;
        }
        Task currentTask = tasks.get(0);
        Plan plan = currentTask.getPlan();
        plan.setProgress(plan.getProgress().copy().updateDetailedStatus(translationMapToFetchDetailedStatusForPlan.get((Object)currentTask.getType())));
    }

    private void stopped(Step step) {
        log.debug("Step stopped");
        step.setProgress(step.getProgress().copy().stopped());
        this.stepStore.update(step);
        this.updateTransfer(step);
        Task task = step.getTask();
        ExecutionStatus status = task.getProgress().getStatus();
        task.setProgress(task.getProgress().copy().stopped());
        this.taskStore.update(task);
        this.updateContainer(task);
        this.saveTaskAnalyticsEvent(task, status);
        this.updatePlanWhenStepDone(task);
    }

    private void failed(Step step, String message, @Nullable Throwable e) {
        log.error("Step failed, message: {}", (Object)message, (Object)e);
        step.setProgress(step.getProgress().copy().failed(message));
        this.stepStore.update(step);
        this.updateTransfer(step);
        Task task = step.getTask();
        ExecutionStatus status = task.getProgress().getStatus();
        task.setProgress(task.getProgress().copy().failed(message));
        this.taskStore.update(task);
        this.updateContainer(task);
        this.saveTaskAnalyticsEvent(task, status);
        this.updatePlanWhenStepDone(task);
        this.saveToLogFile(step, message, e);
    }

    void savePlanAnalyticsEvents(String planId, boolean success) {
        try {
            this.ptx.write(() -> {
                Plan plan = this.planStore.getPlanAndLock(planId);
                ExecutionStatus status = plan.getProgress().getStatus();
                PlanDto planDto = this.planConverter.entityToDto(plan, true);
                this.analyticsEventService.sendAnalyticsEvents(() -> ImmutableList.of((Object)this.analyticsEventBuilder.buildUpdatedPlanStatusAnalyticEvent(planDto, ProgressDto.convertStatus(status, null)), (Object)this.analyticsEventBuilder.buildCompletedPlanAnalyticsEvent(planDto), (Object)this.analyticsEventBuilder.buildMetricEventForConfluenceMigration(success), (Object)this.analyticsEventBuilder.buildPlatformPlanCompletionOperationalEvent(planDto, plan.getMigrationScopeId(), status), (Object)this.analyticsEventBuilder.buildPlatformPlanCompletionMetricEvent(status, planDto.getMigrationTag(), planDto.getMigrationCreator())));
            });
        }
        catch (Exception e) {
            log.error("Failed to save analytics", (Throwable)e);
        }
    }

    private void sendStandardisedStepCompletionEvents(Step currentStep, ExecutionStatus status) {
        String currentStepType = currentStep.getType();
        if (this.clubbedStepTypesForStandardisedEvents.contains(currentStepType) && status == ExecutionStatus.DONE) {
            return;
        }
        MigrationTag migrationTag = currentStep.getPlan().getMigrationTag();
        this.analyticsEventService.sendAnalyticsEventsAsync(() -> ImmutableList.of((Object)this.analyticsEventBuilder.buildPlatformStepCompletionOperationalEvent(currentStep, status), (Object)this.analyticsEventBuilder.buildPlatformStepCompletionMetricEvent(currentStepType, status, migrationTag), (Object)this.analyticsEventBuilder.buildPlatformStepCompletionExtendedMetricEvent(currentStepType, status, migrationTag)));
    }

    void saveTaskAnalyticsEvent(Task task, ExecutionStatus status) {
        boolean abstractSpaceTask = task instanceof AbstractSpaceTask;
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildUpdatedTaskStatusAnalyticEvent(task.getPlan().getId(), task.getId(), task.getAnalyticsEventType(), ProgressDto.convertStatus(status, null), ProgressDto.convertStatus(task.getProgress().getStatus(), null), abstractSpaceTask ? ((AbstractSpaceTask)task).getSpaceKey() : null));
    }

    private void saveToLogFile(Step step, String reason, @Nullable Throwable exception) {
        this.migrationLogService.saveToLogFile(step, reason, exception);
    }

    private void updateMigrationStatus(Plan plan) {
        if (plan.getProgress().getStatus().isCompleted()) {
            this.platformService.updateMigrationStatusToMcs(plan);
        }
    }

    @VisibleForTesting
    void updateContainer(Task task) {
        try {
            Plan plan = this.ptx.read(() -> this.taskStore.getTask(task.getId()).getPlan());
            boolean isGlobalEntitiesTaskPresent = plan.getGlobalEntitiesTaskOfPlan().isPresent();
            ExecutionStatus status = task.getProgress().getStatus();
            if (isGlobalEntitiesTaskPresent && (status.equals((Object)ExecutionStatus.DONE) && task.getType().equals((Object)TaskType.USERS) || !ExecutionStatus.COMPLETE_STATUSES.contains((Object)status) && task.getType().equals((Object)TaskType.GLOBAL_ENTITIES))) {
                return;
            }
            if (task.getContainerId() != null && !task.getContainerId().isEmpty()) {
                this.platformService.updateContainersStatus(plan.getCloudSite().getCloudId(), plan.getMigrationId(), task.getContainerId(), status.getContainerStatus(), task.getProgress().getMessage());
            }
        }
        catch (Exception e) {
            log.error("Failed to update container status for task {}", (Object)task.getId(), (Object)e);
        }
    }

    public void updateTransfer(Step step) {
        try {
            Plan plan = step.getPlan();
            Progress progress = step.getProgress();
            if (step.getTransferId() != null && !step.getTransferId().isEmpty()) {
                this.platformService.updateTransferStatus(plan.getCloudSite().getCloudId(), plan.getMigrationId(), step.getTransferId(), progress.getStatus().getTransferStatus(), progress.getMessage());
                Map<String, Object> stepProgressProperties = this.stepProgressPropertiesStore.getStepProgressProperties(step.getId());
                this.platformService.updateTransferProgress(plan.getCloudSite().getCloudId(), plan.getMigrationId(), step.getTransferId(), new TransferProgressRequest(progress.getPercent(), progress.getMessage(), stepProgressProperties));
            }
        }
        catch (Exception e) {
            log.debug("Failed to update transfer status for step {}", (Object)step.getId(), (Object)e);
        }
    }

    @VisibleForTesting
    void updateTransferAsync(Step step) {
        try {
            this.executorService.submit(() -> this.updateTransfer(step));
        }
        catch (Exception e) {
            log.debug("Couldn't submit update transfer progress task for step {}", (Object)step.getId(), (Object)e);
        }
    }

    static {
        translationMapToFetchDetailedStatusForPlan.put(TaskType.SPACE, "Migrating spaces");
        translationMapToFetchDetailedStatusForPlan.put(TaskType.USERS, "Migrating users and groups");
        translationMapToFetchDetailedStatusForPlan.put(TaskType.GLOBAL_ENTITIES, "Migrating global templates");
        translationMapToFetchDetailedStatusForPlan.put(TaskType.ATTACHMENTS, "Migrating attachments");
        translationMapToFetchDetailedStatusForPlan.put(TaskType.APPS, "Migrating apps");
    }
}

