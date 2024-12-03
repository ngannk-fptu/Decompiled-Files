/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.event.events.cluster.ClusterEventWrapper
 *  com.atlassian.event.Event
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.collect.ImmutableMap
 *  io.atlassian.util.concurrent.ThreadFactories
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  javax.inject.Inject
 *  javax.inject.Named
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.execution;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.event.Event;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.ClusterInformationService;
import com.atlassian.migration.agent.service.LoggingContextProvider;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.event.ExecuteStepsEvent;
import com.atlassian.migration.agent.service.event.StepAllocation;
import com.atlassian.migration.agent.service.event.StepExecutorHeartbeatEvent;
import com.atlassian.migration.agent.service.event.StopPlanEvent;
import com.atlassian.migration.agent.service.execution.AsyncStepExecutor;
import com.atlassian.migration.agent.service.execution.CancellableFuture;
import com.atlassian.migration.agent.service.execution.PlanExecutionService;
import com.atlassian.migration.agent.service.execution.StepExecutor;
import com.atlassian.migration.agent.service.impl.StepType;
import com.atlassian.migration.agent.service.stepexecutor.StepResult;
import com.atlassian.migration.agent.service.stepexecutor.attachment.AttachmentMigrationExecutor;
import com.atlassian.migration.agent.service.stepexecutor.globalentities.GlobalEntitiesExportExecutor;
import com.atlassian.migration.agent.service.stepexecutor.globalentities.GlobalEntitiesImportExecutor;
import com.atlassian.migration.agent.service.stepexecutor.globalentities.GlobalEntitiesUploadExecutor;
import com.atlassian.migration.agent.service.stepexecutor.space.SpaceExportExecutor;
import com.atlassian.migration.agent.service.stepexecutor.space.SpaceImportExecutor;
import com.atlassian.migration.agent.service.stepexecutor.space.SpaceUploadExecutor;
import com.atlassian.migration.agent.service.stepexecutor.space.SpaceUsersMigrationExecutor;
import com.atlassian.migration.agent.service.stepexecutor.user.UsersMigrationExecutor;
import com.atlassian.migration.agent.store.StepStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.google.common.collect.ImmutableMap;
import io.atlassian.util.concurrent.ThreadFactories;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Named
@ParametersAreNonnullByDefault
public class StepExecutionService {
    private static final Logger log = ContextLoggerFactory.getLogger(StepExecutionService.class);
    static final long HEARTBEAT_INTERVAL = 20000L;
    static final long CANCELLATION_TIMEOUT = 60000L;
    private static final String THEAD_NAME_PREFIX = "com.atlassian.migration-" + StepExecutionService.class.getSimpleName();
    private final PluginTransactionTemplate ptx;
    private final PlanExecutionService planExecutionService;
    private final StepStore stepStore;
    private final ImmutableMap<StepType, StepExecutor> stepRunners;
    private final ConcurrentMap<String, StepExecution> currentWork;
    private final ReentrantReadWriteLock workLock;
    private final Lock workInexclusiveLock;
    private final Lock workExclusiveLock;
    private final LoggingContextProvider loggingContextProvider;
    private final ExecutorService stepExecutorService;
    private final ClusterInformationService clusterInformationService;
    private final MigrationDarkFeaturesManager darkFeaturesManager;
    private final AnalyticsEventService analyticsEventService;
    private final AnalyticsEventBuilder analyticsEventBuilder;
    private final Timer heartbeatTimer;
    private final ExecutorService heartbeatRunner;
    private final EventPublisher eventPublisher;
    private final Clock clock;

    @Inject
    public StepExecutionService(PluginTransactionTemplate ptx, StepStore stepStore, LoggingContextProvider loggingContextProvider, UsersMigrationExecutor usersMigrationExecutor, GlobalEntitiesExportExecutor globalEntitiesExportExecutor, GlobalEntitiesUploadExecutor globalEntitiesUploadExecutor, GlobalEntitiesImportExecutor globalEntitiesImportExecutor, AttachmentMigrationExecutor attachmentMigrationExecutor, SpaceExportExecutor spaceExportExecutor, SpaceUsersMigrationExecutor spaceUsersMigrationExecutor, SpaceUploadExecutor spaceUploadExecutor, SpaceImportExecutor spaceImportExecutor, PlanExecutionService planExecutionService, EventPublisher eventPublisher, ClusterInformationService clusterInformationService, MigrationDarkFeaturesManager darkFeaturesManager, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, MigrationAgentConfiguration migrationAgentConfiguration) {
        this(ptx, stepStore, loggingContextProvider, usersMigrationExecutor, globalEntitiesExportExecutor, globalEntitiesUploadExecutor, globalEntitiesImportExecutor, attachmentMigrationExecutor, spaceExportExecutor, spaceUsersMigrationExecutor, spaceUploadExecutor, spaceImportExecutor, planExecutionService, eventPublisher, clusterInformationService, darkFeaturesManager, analyticsEventService, analyticsEventBuilder, Executors.newFixedThreadPool(migrationAgentConfiguration.getMaxStepExecutionThreads(), ThreadFactories.namedThreadFactory((String)THEAD_NAME_PREFIX)), new ReentrantReadWriteLock(), new Timer(), Executors.newSingleThreadExecutor(ThreadFactories.namedThreadFactory((String)(THEAD_NAME_PREFIX + "-heartbeatRunner"))), Clock.systemUTC());
    }

    public StepExecutionService(PluginTransactionTemplate ptx, StepStore stepStore, LoggingContextProvider loggingContextProvider, UsersMigrationExecutor usersMigrationExecutor, GlobalEntitiesExportExecutor globalEntitiesExportExecutor, GlobalEntitiesUploadExecutor globalEntitiesUploadExecutor, GlobalEntitiesImportExecutor globalEntitiesImportExecutor, AttachmentMigrationExecutor attachmentMigrationExecutor, SpaceExportExecutor spaceExportExecutor, SpaceUsersMigrationExecutor spaceUsersMigrationExecutor, SpaceUploadExecutor spaceUploadExecutor, SpaceImportExecutor spaceImportExecutor, PlanExecutionService planExecutionService, EventPublisher eventPublisher, ClusterInformationService clusterInformationService, MigrationDarkFeaturesManager darkFeaturesManager, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, ExecutorService stepExecutorService, ReentrantReadWriteLock workLock, Timer heartbeatTimer, ExecutorService heartbeatRunner, Clock clock) {
        this.ptx = ptx;
        this.stepStore = stepStore;
        this.loggingContextProvider = loggingContextProvider;
        this.clusterInformationService = clusterInformationService;
        this.darkFeaturesManager = darkFeaturesManager;
        this.currentWork = new ConcurrentHashMap<String, StepExecution>();
        this.workLock = workLock;
        this.workInexclusiveLock = this.workLock.readLock();
        this.workExclusiveLock = this.workLock.writeLock();
        this.planExecutionService = planExecutionService;
        this.eventPublisher = eventPublisher;
        this.analyticsEventService = analyticsEventService;
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.stepRunners = ImmutableMap.builder().put((Object)usersMigrationExecutor.getStepType(), (Object)usersMigrationExecutor).put((Object)globalEntitiesExportExecutor.getStepType(), (Object)globalEntitiesExportExecutor).put((Object)globalEntitiesUploadExecutor.getStepType(), (Object)globalEntitiesUploadExecutor).put((Object)globalEntitiesImportExecutor.getStepType(), (Object)globalEntitiesImportExecutor).put((Object)attachmentMigrationExecutor.getStepType(), (Object)attachmentMigrationExecutor).put((Object)spaceExportExecutor.getStepType(), (Object)spaceExportExecutor).put((Object)spaceUsersMigrationExecutor.getStepType(), (Object)spaceUsersMigrationExecutor).put((Object)spaceUploadExecutor.getStepType(), (Object)spaceUploadExecutor).put((Object)spaceImportExecutor.getStepType(), (Object)spaceImportExecutor).build();
        this.stepExecutorService = stepExecutorService;
        this.heartbeatTimer = heartbeatTimer;
        this.heartbeatRunner = heartbeatRunner;
        this.clock = clock;
    }

    @PostConstruct
    public void initialise() {
        this.heartbeatTimer.scheduleAtFixedRate((TimerTask)new HeartbeatTask(), 20000L, 20000L);
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void preDestroy() {
        this.eventPublisher.unregister((Object)this);
        this.stepExecutorService.shutdown();
        this.heartbeatRunner.shutdown();
        this.heartbeatTimer.cancel();
    }

    @EventListener
    public void handleExecuteStepsEvent(ExecuteStepsEvent event) {
        try {
            String thisNodeId = this.clusterInformationService.getCurrentNodeId();
            List<StepAllocation> steps = event.getStepAllocations().stream().filter(step -> step.getNodeId().equals(thisNodeId)).collect(Collectors.toList());
            if (!steps.isEmpty()) {
                this.runStepBatch(steps, thisNodeId);
            }
        }
        catch (Exception e) {
            log.error("Failed to handle ExecuteTasksEvent {}", (Object)event, (Object)e);
            throw e;
        }
    }

    @EventListener
    public void handleStopPlanEvent(StopPlanEvent event) {
        try {
            this.stopStepsForPlan(event.getPlanId());
        }
        catch (Exception e) {
            log.error("Failed to handle StopPlanEvent {}", (Object)event, (Object)e);
            throw e;
        }
    }

    @EventListener
    public void handleClusteredEvent(ClusterEventWrapper clusterEventWrapper) {
        Event wrappedEvent = clusterEventWrapper.getEvent();
        if (wrappedEvent instanceof ExecuteStepsEvent) {
            this.handleExecuteStepsEvent((ExecuteStepsEvent)wrappedEvent);
        } else if (wrappedEvent instanceof StopPlanEvent) {
            this.handleStopPlanEvent((StopPlanEvent)wrappedEvent);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void runStepBatch(List<StepAllocation> stepBatch, String nodeId) {
        log.info("Running steps: {} on node: {}", stepBatch, (Object)nodeId);
        for (StepAllocation stepAllocation : stepBatch) {
            String stepId = stepAllocation.getStepId();
            this.workInexclusiveLock.lock();
            try {
                Step step = this.ptx.read(() -> this.stepStore.getStep(stepAllocation.getStepId()));
                this.currentWork.compute(stepId, (unused, oldValue) -> {
                    if (oldValue != null) {
                        if (((StepExecution)oldValue).getStepAllocation().getNodeExecutionId().equals(stepAllocation.getNodeExecutionId())) {
                            return oldValue;
                        }
                        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildStuckStepAnalyticsEvent(step, Optional.of(((StepExecution)oldValue).getStepAllocation())));
                        log.warn("Step allocator marked the currently running instance of step {} as hung. Stopping the currently running instance of this step (execution {}).", (Object)step.getId(), (Object)((StepExecution)oldValue).stepAllocation.getNodeExecutionId());
                        oldValue.setMarkedAsHung();
                        oldValue.stop();
                    }
                    StepExecution stepExecution = this.getNewStepExecution(stepAllocation, step);
                    stepExecution.start();
                    return stepExecution;
                });
            }
            catch (Throwable e) {
                log.error("Failed to execute step: {}", (Object)stepId, (Object)e);
                this.completeStep(stepAllocation, StepResult.failed("Failed to start step.", e), e);
            }
            finally {
                this.workInexclusiveLock.unlock();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stopStepsForPlan(String planId) {
        try {
            this.workExclusiveLock.lock();
            List stepIdsForPlan = this.ptx.read(() -> this.stepStore.getStepIdsForPlan(planId));
            for (String stepId : stepIdsForPlan) {
                StepExecution execution = (StepExecution)this.currentWork.get(stepId);
                if (execution == null) continue;
                execution.stop();
            }
        }
        finally {
            this.workExclusiveLock.unlock();
        }
    }

    void completeStep(StepAllocation stepAllocation, StepResult result) {
        this.completeStep(stepAllocation, result, null);
    }

    void completeStep(StepAllocation stepAllocation, Throwable throwable) {
        this.completeStep(stepAllocation, null, throwable);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @VisibleForTesting
    void completeStep(StepAllocation stepAllocation, @Nullable StepResult result, @Nullable Throwable throwable) {
        this.workInexclusiveLock.lock();
        try {
            StepExecution execution = (StepExecution)this.currentWork.get(stepAllocation.getStepId());
            if (execution != null && !execution.isMarkedAsHung().booleanValue()) {
                this.currentWork.remove(stepAllocation.getStepId());
            }
            this.planExecutionService.onStepCompleted(stepAllocation, result, throwable);
        }
        finally {
            this.workInexclusiveLock.unlock();
        }
    }

    @VisibleForTesting
    @NotNull
    StepExecution getNewStepExecution(StepAllocation stepAllocation, Step step) {
        return new StepExecution(stepAllocation, StepType.valueOf(step.getType()));
    }

    private void writeDatabaseHeartbeat() {
        try {
            Set<String> executionIds = this.getCurrentExecutionIds();
            if (!executionIds.isEmpty()) {
                Instant heartBeatValue = this.clock.instant();
                this.ptx.write(() -> this.stepStore.setNodeHeartbeat(executionIds, heartBeatValue));
                Instant now = this.clock.instant();
                log.info("Wrote heart beat for active step executions: {}. Heart beat was [{}]. Finished at [{}]. Duration: [{}ms]", new Object[]{executionIds, heartBeatValue, now, Duration.between(heartBeatValue, now).toMillis()});
            }
        }
        catch (Exception e) {
            log.error("Failed to write database heartbeat.", (Throwable)e);
        }
    }

    private Set<String> getCurrentExecutionIds() {
        return this.currentWork.values().stream().map(exec -> ((StepExecution)exec).stepAllocation.getNodeExecutionId()).collect(Collectors.toSet());
    }

    private void sendHeartbeatEvent(Set<String> executionIds, Instant heartbeatTime) {
        String nodeId = this.clusterInformationService.getCurrentNodeId();
        this.eventPublisher.publish((Object)new StepExecutorHeartbeatEvent(this, nodeId, heartbeatTime.toEpochMilli(), executionIds));
    }

    @VisibleForTesting
    final class StepExecution {
        private final StepAllocation stepAllocation;
        private final StepType stepType;
        private volatile Future<StepResult> futureStepResult;
        private final AtomicBoolean hasStarted;
        private final AtomicBoolean scheduledToStart;
        private final AtomicBoolean markedAsHung;
        private final AtomicBoolean isCancellable;

        @VisibleForTesting
        StepExecution(StepAllocation stepAllocation, StepType stepType) {
            this.stepAllocation = stepAllocation;
            this.stepType = stepType;
            this.hasStarted = new AtomicBoolean(false);
            this.scheduledToStart = new AtomicBoolean(false);
            this.markedAsHung = new AtomicBoolean(false);
            this.isCancellable = new AtomicBoolean(true);
        }

        Boolean isMarkedAsHung() {
            return this.markedAsHung.get();
        }

        @VisibleForTesting
        void setMarkedAsHung() {
            this.markedAsHung.compareAndSet(false, true);
        }

        @VisibleForTesting
        AtomicBoolean isScheduledToStart() {
            return this.scheduledToStart;
        }

        @VisibleForTesting
        AtomicBoolean hasStarted() {
            return this.hasStarted;
        }

        @VisibleForTesting
        void start() {
            if (this.scheduledToStart.compareAndSet(false, true)) {
                String stepId = this.stepAllocation.getStepId();
                StepExecutor stepExecutor = (StepExecutor)StepExecutionService.this.stepRunners.get((Object)this.stepType);
                this.futureStepResult = stepExecutor instanceof AsyncStepExecutor && StepExecutionService.this.darkFeaturesManager.isUnlimitedSpaceImportConcurrencyEnabled() ? this.runOnAsyncExecutor((AsyncStepExecutor)stepExecutor, stepId) : this.runOnExecutor(stepExecutor, stepId);
            }
        }

        private Future<StepResult> runOnAsyncExecutor(AsyncStepExecutor stepExecutor, String stepId) {
            try {
                return StepExecutionService.this.loggingContextProvider.forStep(stepId).execute(() -> {
                    if (this.hasStarted.compareAndSet(false, true)) {
                        CancellableFuture<StepResult> future = stepExecutor.runStepAsync(stepId);
                        future.whenCompleteOrCancelledAsync((stepResult, throwable) -> {
                            if (stepResult == null && throwable instanceof CancellationException) {
                                StepExecutionService.this.completeStep(this.stepAllocation, StepResult.stopped(), null);
                            } else {
                                StepExecutionService.this.completeStep(this.stepAllocation, (StepResult)stepResult, (Throwable)throwable);
                            }
                        }, 60000L, TimeUnit.MILLISECONDS, StepExecutionService.this.stepExecutorService);
                        return future;
                    }
                    return CompletableFuture.completedFuture(StepResult.stopped());
                });
            }
            catch (Throwable t) {
                StepExecutionService.this.completeStep(this.stepAllocation, t);
                return CompletableFuture.completedFuture(null);
            }
        }

        private Future<StepResult> runOnExecutor(StepExecutor stepExecutor, String stepId) {
            return StepExecutionService.this.stepExecutorService.submit(() -> {
                try {
                    return StepExecutionService.this.loggingContextProvider.forStep(stepId).execute(() -> {
                        if (this.hasStarted.compareAndSet(false, true)) {
                            StepResult stepResult = stepExecutor.runStep(stepId);
                            this.isCancellable.set(false);
                            if (Thread.currentThread().isInterrupted()) {
                                log.debug("Step {} was interrupted.", (Object)this.stepAllocation.getStepId());
                            }
                            StepExecutionService.this.completeStep(this.stepAllocation, stepResult);
                            return stepResult;
                        }
                        return StepResult.stopped();
                    });
                }
                catch (Throwable t) {
                    StepExecutionService.this.completeStep(this.stepAllocation, t);
                    return null;
                }
            });
        }

        private StepAllocation getStepAllocation() {
            return this.stepAllocation;
        }

        @VisibleForTesting
        void stop() {
            if (this.futureStepResult != null && this.isCancellable.compareAndSet(true, false)) {
                this.futureStepResult.cancel(true);
            }
            if (this.hasStarted.compareAndSet(false, true)) {
                StepExecutionService.this.completeStep(this.stepAllocation, StepResult.stopped());
            }
        }
    }

    @VisibleForTesting
    final class HeartbeatTask
    extends TimerTask {
        HeartbeatTask() {
        }

        @Override
        public void run() {
            try {
                StepExecutionService.this.sendHeartbeatEvent(StepExecutionService.this.getCurrentExecutionIds(), StepExecutionService.this.clock.instant());
                StepExecutionService.this.heartbeatRunner.submit(() -> StepExecutionService.this.writeDatabaseHeartbeat());
            }
            catch (Throwable t) {
                log.error("Failed to heartbeat active tasks.", t);
            }
        }
    }
}

