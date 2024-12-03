/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  io.atlassian.util.concurrent.ThreadFactories
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  liquibase.util.StringUtil
 *  lombok.Generated
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.stepexecutor.space;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.entity.ConfluenceSpaceTask;
import com.atlassian.migration.agent.entity.ImportType;
import com.atlassian.migration.agent.entity.Plan;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.ClusterLimits;
import com.atlassian.migration.agent.service.ConfluenceImportExportTaskStatus;
import com.atlassian.migration.agent.service.MigrationErrorCode;
import com.atlassian.migration.agent.service.SpaceImportContextDto;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.analytics.ErrorEvent;
import com.atlassian.migration.agent.service.catalogue.model.MigrationCatalogueStorageFile;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.confluence.ConfluenceCloudService;
import com.atlassian.migration.agent.service.confluence.exception.ConfluenceCloudServiceException;
import com.atlassian.migration.agent.service.execution.AsyncStepExecutor;
import com.atlassian.migration.agent.service.execution.CancellableFuture;
import com.atlassian.migration.agent.service.execution.SpaceBoundStepExecutor;
import com.atlassian.migration.agent.service.execution.UncheckedInterruptedException;
import com.atlassian.migration.agent.service.impl.StepType;
import com.atlassian.migration.agent.service.stepexecutor.ImportExecutor;
import com.atlassian.migration.agent.service.stepexecutor.ProgressTracker;
import com.atlassian.migration.agent.service.stepexecutor.StepExecutionException;
import com.atlassian.migration.agent.service.stepexecutor.StepResult;
import com.atlassian.migration.agent.service.stepexecutor.space.helper.SpaceImportConfigFileException;
import com.atlassian.migration.agent.service.stepexecutor.space.helper.SpaceImportConfigFileManager;
import com.atlassian.migration.agent.service.util.StopConditionCheckingUtil;
import com.atlassian.migration.agent.store.StepStore;
import com.atlassian.migration.agent.store.impl.MigratedSpaceStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import io.atlassian.util.concurrent.ThreadFactories;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import liquibase.util.StringUtil;
import lombok.Generated;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public class SpaceImportExecutor
extends ImportExecutor
implements SpaceBoundStepExecutor,
AsyncStepExecutor {
    private static final StepType IMPORT_STEP_TYPE = StepType.CONFLUENCE_IMPORT;
    private static final Pattern SPACE_EXISTS_PATTERN = Pattern.compile("A space with key (.+) already exists\\.");
    private static final Pattern PERSONAL_SPACE_EXISTS_PATTERN = Pattern.compile("A personal space already exists for user (.+)\\.");
    private static final Pattern PARALLEL_SPACE_MIGRATION_PATTERN = Pattern.compile("Migrating the same space has been skipped");
    private static final Pattern TEAM_CALENDAR_IMPORT_FAILURE_PATTERN = Pattern.compile("Error during import while importing Team Calendars table");
    private static final Pattern TEAM_CALENDAR_ENTITY_FAILURE_PATTERN = Pattern.compile("Error processing file for entity TC_");
    private static final Pattern TEAM_CALENDAR_NO_REASSIGNER_PATTERN = Pattern.compile("Error when creating Team Calendar ID Reassigners");
    private static final Pattern TEAM_CALENDAR_REASSIGNER_FAILURE_PATTERN = Pattern.compile("No transformer chain found for entity TC_");
    public static final int MAX_CC_BULK_PROGRESS_RESPONSE_SIZE = 50;
    static String SKIP_STEP_SPACE_EXISTS = "We can't migrate the space %s as it has already been imported by another ongoing or completed migration.";
    static String TEAM_CALENDAR_MIGRATION_FAILED = "%s space failed to migrate because the associated Team Calendars couldn\u2019t be migrated.";
    static String SKIP_STEP_SPACE_MIGRATING = "We can't migrate space %s because another migration is currently importing it.";
    @VisibleForTesting
    static final Duration MAX_DURATION_UNPROCESSED_STATUS_ALLOWED = Duration.ofMinutes(5L);
    private static final Duration DEFAULT_POLLING_PERIOD = Duration.ofSeconds(5L);
    private static final Logger log = ContextLoggerFactory.getLogger(SpaceImportExecutor.class);
    private final ConfluenceCloudService confluenceCloudService;
    private final SpaceManager spaceManager;
    private final MigratedSpaceStore migratedSpaceStore;
    private final MigrationDarkFeaturesManager darkFeaturesManager;
    private final ConcurrentMap<String, SpaceImportJob> activeImports = new ConcurrentHashMap<String, SpaceImportJob>();
    private final ScheduledExecutorService progressPoller;
    private final ExecutorService importRequestExecutor;
    private final CloudSiteService cloudSiteService;
    private final Duration pollingPeriod;
    private final SpaceImportConfigFileManager spaceImportConfigFileManager;
    private final ClusterLimits clusterLimits;

    public SpaceImportExecutor(ProgressTracker progressTracker, StepStore stepStore, PluginTransactionTemplate ptx, SpaceManager spaceManager, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, MigratedSpaceStore migratedSpaceStore, MigrationDarkFeaturesManager darkFeaturesManager, MigrationAgentConfiguration migrationAgentConfiguration, ConfluenceCloudService confluenceCloudService, CloudSiteService cloudSiteService, SpaceImportConfigFileManager spaceImportConfigFileManager, ClusterLimits clusterLimits) {
        super(progressTracker, stepStore, ptx, analyticsEventService, analyticsEventBuilder, migrationAgentConfiguration);
        this.confluenceCloudService = confluenceCloudService;
        this.cloudSiteService = cloudSiteService;
        this.spaceManager = spaceManager;
        this.migratedSpaceStore = migratedSpaceStore;
        this.darkFeaturesManager = darkFeaturesManager;
        this.pollingPeriod = DEFAULT_POLLING_PERIOD;
        this.progressPoller = Executors.newSingleThreadScheduledExecutor(ThreadFactories.namedThreadFactory((String)this.getClass().getName()));
        this.importRequestExecutor = new ThreadPoolExecutor(0, migrationAgentConfiguration.getMaxConcurrentSpaceImportRequests(), 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
        this.spaceImportConfigFileManager = spaceImportConfigFileManager;
        this.clusterLimits = clusterLimits;
    }

    @VisibleForTesting
    SpaceImportExecutor(ProgressTracker progressTracker, StepStore stepStore, ConfluenceCloudService confluenceCloudService, CloudSiteService cloudSiteService, PluginTransactionTemplate ptx, SpaceManager spaceManager, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, MigratedSpaceStore migratedSpaceStore, MigrationDarkFeaturesManager darkFeaturesManager, Supplier<Instant> instantSupplier, MigrationAgentConfiguration migrationAgentConfiguration, ScheduledExecutorService progressPoller, ExecutorService importRequestExecutor, Duration pollingPeriod, SpaceImportConfigFileManager spaceImportConfigFileManager, ClusterLimits clusterLimits) {
        super(progressTracker, stepStore, ptx, analyticsEventService, analyticsEventBuilder, instantSupplier, migrationAgentConfiguration);
        this.confluenceCloudService = confluenceCloudService;
        this.cloudSiteService = cloudSiteService;
        this.spaceManager = spaceManager;
        this.migratedSpaceStore = migratedSpaceStore;
        this.darkFeaturesManager = darkFeaturesManager;
        this.progressPoller = progressPoller;
        this.importRequestExecutor = importRequestExecutor;
        this.pollingPeriod = pollingPeriod;
        this.spaceImportConfigFileManager = spaceImportConfigFileManager;
        this.clusterLimits = clusterLimits;
    }

    @PostConstruct
    public void initialize() {
        this.startCloudPoller();
    }

    @PreDestroy
    public void cleanup() {
        this.stopCloudPoller();
        this.importRequestExecutor.shutdownNow();
    }

    @VisibleForTesting
    void startCloudPoller() {
        this.progressPoller.scheduleWithFixedDelay(this::checkProgress, this.pollingPeriod.toMillis(), this.pollingPeriod.toMillis(), TimeUnit.MILLISECONDS);
    }

    @VisibleForTesting
    void stopCloudPoller() {
        this.progressPoller.shutdown();
    }

    @Override
    protected String initiateImport(String stepId) {
        Step step = this.ptx.read(() -> this.stepStore.getStep(stepId));
        String spaceKey = ((ConfluenceSpaceTask)step.getTask()).getSpaceKey();
        if (Objects.isNull(this.spaceManager.getSpace(spaceKey))) {
            log.info("Skipping initiating confluence space import as space cannot be found in server");
            return "SKIPPED";
        }
        CloudSite cloudSite = step.getPlan().getCloudSite();
        String cloudId = cloudSite.getCloudId();
        String containerToken = cloudSite.getContainerToken();
        String migrationId = step.getPlan().getMigrationId();
        log.info("Initiate confluence space import with stepId: {}", (Object)stepId);
        long freeHeapSizeAtStart = Runtime.getRuntime().freeMemory();
        try {
            this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildSpaceImportStartEvent(step, ((Instant)this.instantSupplier.get()).toEpochMilli()));
            ConfluenceImportExportTaskStatus response = this.confluenceCloudService.initiateConfluenceSpaceImport(cloudId, containerToken, this.createSpaceImportContext(step));
            log.info("Initiated import task. Response: {}", (Object)response);
            String string = response.getId();
            return string;
        }
        catch (UncheckedInterruptedException uncheckedInterruptedException) {
            throw new UncheckedInterruptedException(uncheckedInterruptedException);
        }
        catch (Exception exception) {
            this.checkIfGoodEventAndLoggingErrorAnalyticEvents(migrationId, cloudId, exception.getMessage(), spaceKey, ImportType.CSV, Optional.empty(), Optional.empty());
            this.saveCompletedStepAnalyticsEvent(false, ImportType.CSV, step, spaceKey);
            throw new StepExecutionException(MigrationErrorCode.SPACE_IMPORT_INITIATE_FAILED, IMPORT_STEP_TYPE, migrationId, exception.getMessage(), exception);
        }
        finally {
            this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildStepLevelHeapSizeAnalyticsEvent(step, freeHeapSizeAtStart, this.clusterLimits.getClusterConcurrencyLimit(this.getStepType()), this.clusterLimits.getConcurrencyPerNodeLimit(this.getStepType())));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Optional<StepResult> doProgressCheck(Step step, String importTaskId) {
        ConfluenceImportExportTaskStatus response;
        StopConditionCheckingUtil.throwIfStopConditionWasReached();
        String stepId = step.getId();
        String migrationId = step.getPlan().getMigrationId();
        if (this.stepIsInCompleteStatus(step)) {
            log.warn("Step {} already in complete status, no need to do progress check.", (Object)step.getId());
            return this.stepResultForCompletedStep(step);
        }
        String spaceKey = ((ConfluenceSpaceTask)step.getTask()).getSpaceKey();
        CloudSite cloudSite = step.getPlan().getCloudSite();
        String cloudId = cloudSite.getCloudId();
        boolean success = false;
        ImportType importType = ImportType.CSV;
        try {
            response = this.confluenceCloudService.getConfluenceSpaceImportProgress(cloudId, cloudSite.getContainerToken(), importTaskId);
        }
        catch (UncheckedInterruptedException uncheckedInterruptedException) {
            throw new UncheckedInterruptedException(uncheckedInterruptedException);
        }
        catch (ConfluenceCloudServiceException exception) {
            this.checkIfGoodEventAndLoggingErrorAnalyticEvents(migrationId, cloudId, exception.getMessage(), spaceKey, importType, Optional.of(importTaskId), Optional.empty());
            this.saveCompletedStepAnalyticsEvent(success, importType, step, spaceKey);
            return Optional.of(SpaceImportExecutor.getStepResultForFailure(exception.getMessage()));
        }
        log.debug("Got progress for task {}: {}", (Object)importTaskId, (Object)response);
        if (!response.isComplete()) {
            this.progressTracker.progress(stepId, response.getPercentageComplete(), StepType.CONFLUENCE_IMPORT.getDisplayName(), StepType.CONFLUENCE_IMPORT.getDetailedStatus());
            return Optional.empty();
        }
        try {
            if (!response.isSuccessful()) {
                Optional<StepResult> parallelMigrations = this.handleParallelSpaceImports(response.getMessage(), spaceKey);
                if (parallelMigrations.isPresent()) {
                    Optional<StepResult> optional = parallelMigrations;
                    return optional;
                }
                success = this.checkIfGoodEventAndLoggingErrorAnalyticEvents(migrationId, cloudId, response.getMessage(), spaceKey, importType, Optional.of(importTaskId), Optional.ofNullable(response.getStatusCode()));
                Optional<StepResult> optional = Optional.of(SpaceImportExecutor.getStepResultForFailure(response.getMessage()));
                return optional;
            }
            this.analyticsEventService.sendAnalyticsEvents(() -> ImmutableList.of((Object)this.analyticsEventBuilder.buildSuccessfulSpaceMigration(), (Object)this.analyticsEventBuilder.buildImportStepCounterEvent(importType, true)));
            if (this.darkFeaturesManager.spaceFiltersEnabled()) {
                this.migratedSpaceStore.addSpace(this.spaceManager.getSpace(spaceKey), cloudSite.getCloudId());
            }
            success = true;
            Optional<StepResult> optional = Optional.of(StepResult.succeeded("Migration complete"));
            return optional;
        }
        finally {
            this.saveCompletedStepAnalyticsEvent(success, importType, step, spaceKey);
        }
    }

    private Optional<StepResult> stepResultForCompletedStep(Step step) {
        switch (step.getProgress().getStatus()) {
            case FAILED: {
                return Optional.of(SpaceImportExecutor.getStepResultForFailure("Space import step failed"));
            }
            case STOPPED: {
                return Optional.of(StepResult.stopped());
            }
            case DONE: {
                return Optional.of(StepResult.succeeded("Space import step succeeded"));
            }
        }
        return Optional.empty();
    }

    private boolean checkIfGoodEventAndLoggingErrorAnalyticEvents(String migrationId, String cloudId, String failureReason, String spaceKey, ImportType importType, Optional<String> importTaskId, Optional<Integer> statusCode) {
        ErrorEvent errorEvent = new ErrorEvent.ErrorEventBuilder(MigrationErrorCode.SPACE_IMPORT_FAILED, MigrationErrorCode.SPACE_IMPORT_FAILED.getContainerType(), migrationId, IMPORT_STEP_TYPE).setCloudid(cloudId).setReason(failureReason).setSpaceKey(spaceKey).build();
        this.analyticsEventService.sendAnalyticsEvents(() -> ImmutableList.of((Object)this.analyticsEventBuilder.buildFailedSpaceMigration(IMPORT_STEP_TYPE, MigrationErrorCode.SPACE_IMPORT_FAILED), (Object)this.analyticsEventBuilder.buildImportStepCounterEvent(importType, false), (Object)this.analyticsEventBuilder.buildErrorOperationalEventForSpaceImport(errorEvent, importType, importTaskId, statusCode)));
        return MigrationErrorCode.SPACE_IMPORT_FAILED.shouldBeTreatedAsGoodEventInReliabilitySlo();
    }

    private void saveCompletedStepAnalyticsEvent(boolean success, ImportType importType, Step step, String spaceKey) {
        ImmutableMap additionalAttributes = ImmutableMap.of((Object)"stepSuccessful", (Object)String.valueOf(success), (Object)"importType", (Object)importType.name());
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.lambda$saveCompletedStepAnalyticsEvent$6(step, (Map)additionalAttributes));
        this.saveStepTimerEvent(step, spaceKey, success);
    }

    private void saveStepTimerEvent(Step step, String spaceKey, boolean success) {
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildSpaceImportTimerEvent(success, this.getStepTime(step), spaceKey, step));
    }

    @VisibleForTesting
    static StepResult getStepResultForFailure(@Nullable String message) {
        if (message == null) {
            return StepResult.failed("Cloud import failed with no message");
        }
        return StepResult.failed(String.format("Import to cloud failed. Message: %s", message));
    }

    public static StepResult checkParallelMigrations(@Nullable String message, String spaceKey) {
        Matcher spaceExistsMatcher = SPACE_EXISTS_PATTERN.matcher(message);
        Matcher personalSpaceExistsMatcher = PERSONAL_SPACE_EXISTS_PATTERN.matcher(message);
        Matcher parallelMigrationMatcher = PARALLEL_SPACE_MIGRATION_PATTERN.matcher(message);
        if (spaceExistsMatcher.find() || personalSpaceExistsMatcher.find()) {
            String skippedSpaceReason = String.format(SKIP_STEP_SPACE_EXISTS, spaceKey);
            return StepResult.succeeded(skippedSpaceReason, "SKIPPED");
        }
        if (parallelMigrationMatcher.find()) {
            String skippedSpaceReason = String.format(SKIP_STEP_SPACE_MIGRATING, spaceKey);
            return StepResult.succeeded(skippedSpaceReason, "SKIPPED");
        }
        return null;
    }

    private SpaceImportContextDto createSpaceImportContext(Step step) {
        Plan plan = step.getPlan();
        String migrationId = step.getPlan().getMigrationId();
        String stepConfig = step.getConfig();
        if (StringUtils.isBlank((String)stepConfig)) {
            throw new StepExecutionException(MigrationErrorCode.SPACE_IMPORT_MISSING_CONFIG, IMPORT_STEP_TYPE, migrationId, "Received a blank stepConfig for upload file or files. Process cannot proceed.");
        }
        if (!(step.getTask() instanceof ConfluenceSpaceTask)) {
            throw new StepExecutionException(MigrationErrorCode.SPACE_IMPORT_TASK_NOT_CONFLUENCE, IMPORT_STEP_TYPE, migrationId, "Cannot proceed because task is not an instance of ConfluenceSpaceTask");
        }
        ConfluenceSpaceTask spaceTask = (ConfluenceSpaceTask)step.getTask();
        Space space = Optional.ofNullable(this.spaceManager.getSpace(spaceTask.getSpaceKey())).orElseThrow(() -> new StepExecutionException(MigrationErrorCode.SPACE_IMPORT_MISSING_SPACE_KEY, IMPORT_STEP_TYPE, migrationId, "Cannot proceed because can't find any space with spaceKey: " + spaceTask.getSpaceKey()));
        String spaceId = String.valueOf(space.getId());
        try {
            List<MigrationCatalogueStorageFile> files = this.spaceImportConfigFileManager.getSpaceImportStepConfigFromFile(migrationId, spaceId);
            SpaceImportContextDto spaceImportContextDto = new SpaceImportContextDto(plan.getId(), spaceTask.getId(), null, space.getId(), space.getKey(), plan.getMigrationScopeId(), plan.getMigrationId(), files);
            return spaceImportContextDto;
        }
        catch (SpaceImportConfigFileException e) {
            throw new StepExecutionException(MigrationErrorCode.SPACE_IMPORT_UNFORMATTED_JSON, IMPORT_STEP_TYPE, migrationId, "Unexpected json format for step config.", e);
        }
        finally {
            this.spaceImportConfigFileManager.cleanupSpaceImportStepConfigFile(migrationId, spaceId);
        }
    }

    @Override
    public StepType getStepType() {
        return StepType.CONFLUENCE_IMPORT;
    }

    @Override
    public StepResult runStep(String stepId) {
        Step step = this.ptx.read(() -> this.stepStore.getStep(stepId));
        String spaceKey = ((ConfluenceSpaceTask)step.getTask()).getSpaceKey();
        return this.wrapStepResultSupplier(this.analyticsEventBuilder, this.analyticsEventService, step, spaceKey, this.spaceManager, () -> this.runSpaceImport(step));
    }

    private StepResult runSpaceImport(Step step) {
        String stepId = step.getId();
        try {
            Optional<StepResult> result;
            String importTaskId = this.startSpaceImport(step);
            while (!(result = this.doProgressCheck(step, importTaskId)).isPresent()) {
                Thread.sleep(this.pollingPeriod.toMillis());
            }
            return result.get();
        }
        catch (UncheckedInterruptedException | InterruptedException e) {
            log.info("Space import was stopped. StepId={}", (Object)stepId);
            return StepResult.stopped();
        }
        catch (Exception e) {
            log.error("An error occurred while running step with id: {}", (Object)stepId, (Object)e);
            return StepResult.failed(String.format("An unexpected error occurred during step: %s. Error: %s", this.getStepType(), e.getMessage()), e);
        }
    }

    @Override
    public CancellableFuture<StepResult> runStepAsync(String stepId) {
        CancellableFuture<StepResult> future = new CancellableFuture<StepResult>();
        try {
            CompletableFuture.runAsync(() -> {
                if (!future.isCancelled()) {
                    Step step = this.ptx.read(() -> this.stepStore.getStep(stepId));
                    String spaceKey = ((ConfluenceSpaceTask)step.getTask()).getSpaceKey();
                    Space space = this.spaceManager.getSpace(spaceKey);
                    if (Objects.isNull(space)) {
                        log.info("Skipping space import as space cannot be found in server");
                        this.handleSkippedSpaceImportStep(step, spaceKey, future);
                    } else {
                        String importJobId = this.startSpaceImport(step);
                        Plan plan = step.getPlan();
                        SpaceImportJob importJob = new SpaceImportJob(stepId, plan.getCloudSite().getCloudId(), importJobId, space.getId(), spaceKey, plan.getMigrationId(), future, (Instant)this.instantSupplier.get());
                        this.activeImports.put(stepId, importJob);
                    }
                } else {
                    this.handleEarlyTermination(stepId, future, StepResult.stopped());
                }
            }, this.importRequestExecutor).whenComplete((unused, throwable) -> {
                if (throwable != null) {
                    log.error("Failed to initiate space import for step {}", (Object)stepId, throwable);
                    this.handleEarlyTermination(stepId, future, SpaceImportExecutor.getStepResultForFailure(throwable.getMessage()));
                }
            });
        }
        catch (Throwable t) {
            future.completeExceptionally(t);
        }
        return future;
    }

    private String startSpaceImport(Step step) {
        String importTaskId;
        String executionState = step.getExecutionState();
        if (StringUtil.isNotEmpty((String)executionState)) {
            importTaskId = executionState;
        } else {
            importTaskId = this.initiateImport(step.getId());
            this.ptx.write(() -> {
                step.setExecutionState(importTaskId);
                this.stepStore.update(step);
            });
        }
        return importTaskId;
    }

    private void checkProgress() {
        try {
            if (!this.activeImports.isEmpty()) {
                this.handleCancellations();
                Map<String, List<SpaceImportJob>> importsByCloudId = this.activeImports.values().stream().collect(Collectors.groupingBy(SpaceImportJob::getCloudId));
                for (Map.Entry<String, List<SpaceImportJob>> importsForCloud : importsByCloudId.entrySet()) {
                    this.updateProgressForCloud(importsForCloud.getKey(), importsForCloud.getValue());
                }
            }
        }
        catch (Throwable t) {
            log.error("Failed to check import progress.", t);
        }
    }

    private void updateProgressForCloud(String cloudId, List<SpaceImportJob> importJobs) {
        try {
            CloudSite cloudSite = this.cloudSiteService.getByCloudId(cloudId).orElseThrow(() -> new IllegalStateException("Cloud site no longer exists."));
            List partitionedImportList = Lists.partition(importJobs, (int)50);
            HashMap<String, ConfluenceImportExportTaskStatus> statusResponseList = new HashMap<String, ConfluenceImportExportTaskStatus>();
            for (List spaceImportList : partitionedImportList) {
                List<String> importTaskIds = spaceImportList.stream().map(SpaceImportJob::getCloudImportTaskId).collect(Collectors.toList());
                statusResponseList.putAll(this.confluenceCloudService.getBulkConfluenceSpaceImportProgress(cloudSite.getCloudId(), cloudSite.getContainerToken(), importTaskIds).getStatuses());
            }
            for (SpaceImportJob importJob : importJobs) {
                this.updateProgressForJob(importJob, statusResponseList);
            }
        }
        catch (Exception e) {
            List spaces = importJobs.stream().map(job -> String.format("%s=%s", ((SpaceImportJob)job).getSpaceKey(), ((SpaceImportJob)job).getSpaceId())).collect(Collectors.toList());
            log.error("Failed to update space import step progress for spaces {}", spaces, (Object)e);
            importJobs.forEach(job -> this.handleJobError((SpaceImportJob)job, e));
        }
    }

    private void updateProgressForJob(SpaceImportJob importJob, Map<String, ConfluenceImportExportTaskStatus> statusList) {
        try {
            Optional<ConfluenceImportExportTaskStatus> status = Optional.ofNullable(statusList.get(importJob.getCloudImportTaskId()));
            if (status.isPresent()) {
                this.updateProgressForStep(importJob, status.get()).ifPresent(stepResult -> this.handleJobCompletion(importJob, (StepResult)stepResult, Optional.ofNullable(((ConfluenceImportExportTaskStatus)status.get()).getStatusCode())));
            } else if (Duration.between(importJob.getStartTime(), (Temporal)this.instantSupplier.get()).toMillis() > MAX_DURATION_UNPROCESSED_STATUS_ALLOWED.toMillis()) {
                this.handleJobError(importJob, "Starting the space import in Confluence Cloud timed out.");
            }
        }
        catch (Exception e) {
            log.error("Failed to update space import step progress for space {} '{}'", new Object[]{importJob.getSpaceId(), importJob.getSpaceKey(), e});
            this.handleJobError(importJob, e);
        }
    }

    private void handleCancellations() {
        List cancellations = this.activeImports.values().stream().filter(job -> job.getFutureResult().isCancelled()).collect(Collectors.toList());
        for (SpaceImportJob cancelledJob : cancellations) {
            this.handleJobCompletion(cancelledJob, StepResult.stopped(), Optional.empty());
        }
    }

    private void handleSkippedSpaceImportStep(Step step, String spaceKey, CompletableFuture<StepResult> futureResult) {
        String spaceNotFoundReason = String.format("Space %s is deleted or doesn't exist on your instance and therefore can't be migrated.", spaceKey);
        StepResult result = StepResult.succeeded(spaceNotFoundReason, "SKIPPED");
        try {
            this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildStepSkipAnalyticEvent(step, spaceNotFoundReason));
        }
        catch (Exception e) {
            log.error("Failed to send analytics events for skipped import step.");
        }
        futureResult.complete(result);
        this.activeImports.remove(step.getId());
    }

    private void handleEarlyTermination(String stepId, CompletableFuture<StepResult> futureResult, StepResult result) {
        try {
            Step step = this.ptx.read(() -> this.stepStore.getStep(stepId));
            String spaceKey = ((ConfluenceSpaceTask)step.getTask()).getSpaceKey();
            boolean success = result.isSuccess() || this.checkIfGoodEventAndLoggingErrorAnalyticEvents(step.getPlan().getMigrationId(), step.getPlan().getCloudSite().getCloudId(), result.getMessage(), spaceKey, ImportType.CSV, Optional.empty(), Optional.empty());
            this.saveCompletedStepAnalyticsEvent(success, ImportType.CSV, this.stepStore.getStep(stepId), spaceKey);
        }
        catch (Exception e) {
            log.error("Failed to send analytics events for import step termination.");
        }
        futureResult.complete(result);
        this.activeImports.remove(stepId);
    }

    private void handleJobCompletion(SpaceImportJob importJob, StepResult result, Optional<Integer> statusCode) {
        try {
            boolean success = result.isSuccess() || this.checkIfGoodEventAndLoggingErrorAnalyticEvents(importJob.getMigrationId(), importJob.getCloudId(), result.getMessage(), importJob.getSpaceKey(), ImportType.CSV, Optional.of(importJob.getCloudImportTaskId()), statusCode);
            this.saveCompletedStepAnalyticsEvent(success, ImportType.CSV, this.stepStore.getStep(importJob.getStepId()), importJob.getSpaceKey());
        }
        catch (Exception e) {
            log.error("Failed to send analytics events for import step completion of space: {}={}", (Object)importJob.getSpaceKey(), (Object)importJob.getSpaceId());
        }
        importJob.futureResult.complete(result);
        this.activeImports.remove(importJob.getStepId());
    }

    private void handleJobError(SpaceImportJob importJob, Exception e) {
        this.handleJobError(importJob, e.getMessage());
    }

    private void handleJobError(SpaceImportJob importJob, String errorMessage) {
        this.handleJobCompletion(importJob, SpaceImportExecutor.getStepResultForFailure(errorMessage), Optional.empty());
    }

    private Optional<StepResult> handleParallelSpaceImports(String message, String spaceKey) {
        StepResult parallelMigrations;
        if (StringUtils.isNotEmpty((String)message) && (parallelMigrations = SpaceImportExecutor.checkParallelMigrations(message, spaceKey)) != null) {
            this.analyticsEventService.sendAnalyticsEvents(() -> ImmutableList.of((Object)this.analyticsEventBuilder.buildSuccessfulSpaceMigration(), (Object)this.analyticsEventBuilder.buildImportStepCounterEvent(ImportType.CSV, true)));
            return Optional.of(parallelMigrations);
        }
        return Optional.empty();
    }

    public static Optional<StepResult> handleTeamCalendarMigrationFailure(String message, String spaceKey) {
        if (StringUtils.isNotEmpty((String)message)) {
            Matcher teamCalendarMigrationImportMatcher = TEAM_CALENDAR_IMPORT_FAILURE_PATTERN.matcher(message);
            Matcher teamCalendarMigrationEntityMatcher = TEAM_CALENDAR_ENTITY_FAILURE_PATTERN.matcher(message);
            Matcher teamCalendarMigrationNoReassignerMatcher = TEAM_CALENDAR_NO_REASSIGNER_PATTERN.matcher(message);
            Matcher teamCalendarMigrationReassignerErrorMatcher = TEAM_CALENDAR_REASSIGNER_FAILURE_PATTERN.matcher(message);
            if (teamCalendarMigrationImportMatcher.find() || teamCalendarMigrationEntityMatcher.find() || teamCalendarMigrationNoReassignerMatcher.find() || teamCalendarMigrationReassignerErrorMatcher.find()) {
                String teamCalendarFailureReason = String.format(TEAM_CALENDAR_MIGRATION_FAILED, spaceKey);
                return Optional.of(StepResult.failed(teamCalendarFailureReason));
            }
            return Optional.empty();
        }
        return Optional.empty();
    }

    private Optional<StepResult> updateProgressForStep(SpaceImportJob importJob, ConfluenceImportExportTaskStatus status) {
        Space space;
        if (!status.isComplete()) {
            this.progressTracker.progress(importJob.getStepId(), status.getPercentageComplete(), StepType.CONFLUENCE_IMPORT.getDisplayName(), StepType.CONFLUENCE_IMPORT.getDetailedStatus());
            return Optional.empty();
        }
        if (!status.isSuccessful()) {
            Optional<StepResult> parallelMigrations = this.handleParallelSpaceImports(status.getMessage(), importJob.getSpaceKey());
            if (parallelMigrations.isPresent()) {
                return parallelMigrations;
            }
            Optional<StepResult> teamCalendarsMigration = SpaceImportExecutor.handleTeamCalendarMigrationFailure(status.getMessage(), importJob.getSpaceKey());
            if (teamCalendarsMigration.isPresent()) {
                return teamCalendarsMigration;
            }
            return Optional.of(SpaceImportExecutor.getStepResultForFailure(status.getMessage()));
        }
        this.analyticsEventService.sendAnalyticsEvents(() -> ImmutableList.of((Object)this.analyticsEventBuilder.buildSuccessfulSpaceMigration(), (Object)this.analyticsEventBuilder.buildImportStepCounterEvent(ImportType.CSV, true)));
        if (this.darkFeaturesManager.spaceFiltersEnabled() && (space = this.spaceManager.getSpace(importJob.getSpaceId().longValue())) != null) {
            this.migratedSpaceStore.addSpace(space, importJob.getCloudId());
        }
        return Optional.of(StepResult.succeeded("Migration complete"));
    }

    private /* synthetic */ EventDto lambda$saveCompletedStepAnalyticsEvent$6(Step step, Map additionalAttributes) {
        return this.analyticsEventBuilder.buildCompletedStepAnalyticsEvent(step, () -> additionalAttributes);
    }

    private static final class SpaceImportJob {
        private final String stepId;
        private final String cloudId;
        private final String cloudImportTaskId;
        private final Long spaceId;
        private final String spaceKey;
        private final String migrationId;
        private final CompletableFuture<StepResult> futureResult;
        private final Instant startTime;

        private String getMigrationId() {
            return this.migrationId;
        }

        private String getSpaceKey() {
            return this.spaceKey;
        }

        private Long getSpaceId() {
            return this.spaceId;
        }

        @Generated
        public SpaceImportJob(String stepId, String cloudId, String cloudImportTaskId, Long spaceId, String spaceKey, String migrationId, CompletableFuture<StepResult> futureResult, Instant startTime) {
            this.stepId = stepId;
            this.cloudId = cloudId;
            this.cloudImportTaskId = cloudImportTaskId;
            this.spaceId = spaceId;
            this.spaceKey = spaceKey;
            this.migrationId = migrationId;
            this.futureResult = futureResult;
            this.startTime = startTime;
        }

        @Generated
        public String getStepId() {
            return this.stepId;
        }

        @Generated
        public String getCloudId() {
            return this.cloudId;
        }

        @Generated
        public String getCloudImportTaskId() {
            return this.cloudImportTaskId;
        }

        @Generated
        public CompletableFuture<StepResult> getFutureResult() {
            return this.futureResult;
        }

        @Generated
        public Instant getStartTime() {
            return this.startTime;
        }

        @Generated
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof SpaceImportJob)) {
                return false;
            }
            SpaceImportJob other = (SpaceImportJob)o;
            String this$stepId = this.getStepId();
            String other$stepId = other.getStepId();
            if (this$stepId == null ? other$stepId != null : !this$stepId.equals(other$stepId)) {
                return false;
            }
            String this$cloudId = this.getCloudId();
            String other$cloudId = other.getCloudId();
            if (this$cloudId == null ? other$cloudId != null : !this$cloudId.equals(other$cloudId)) {
                return false;
            }
            String this$cloudImportTaskId = this.getCloudImportTaskId();
            String other$cloudImportTaskId = other.getCloudImportTaskId();
            if (this$cloudImportTaskId == null ? other$cloudImportTaskId != null : !this$cloudImportTaskId.equals(other$cloudImportTaskId)) {
                return false;
            }
            Long this$spaceId = this.getSpaceId();
            Long other$spaceId = other.getSpaceId();
            if (this$spaceId == null ? other$spaceId != null : !((Object)this$spaceId).equals(other$spaceId)) {
                return false;
            }
            String this$spaceKey = this.getSpaceKey();
            String other$spaceKey = other.getSpaceKey();
            if (this$spaceKey == null ? other$spaceKey != null : !this$spaceKey.equals(other$spaceKey)) {
                return false;
            }
            String this$migrationId = this.getMigrationId();
            String other$migrationId = other.getMigrationId();
            if (this$migrationId == null ? other$migrationId != null : !this$migrationId.equals(other$migrationId)) {
                return false;
            }
            CompletableFuture<StepResult> this$futureResult = this.getFutureResult();
            CompletableFuture<StepResult> other$futureResult = other.getFutureResult();
            if (this$futureResult == null ? other$futureResult != null : !this$futureResult.equals(other$futureResult)) {
                return false;
            }
            Instant this$startTime = this.getStartTime();
            Instant other$startTime = other.getStartTime();
            return !(this$startTime == null ? other$startTime != null : !((Object)this$startTime).equals(other$startTime));
        }

        @Generated
        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            String $stepId = this.getStepId();
            result = result * 59 + ($stepId == null ? 43 : $stepId.hashCode());
            String $cloudId = this.getCloudId();
            result = result * 59 + ($cloudId == null ? 43 : $cloudId.hashCode());
            String $cloudImportTaskId = this.getCloudImportTaskId();
            result = result * 59 + ($cloudImportTaskId == null ? 43 : $cloudImportTaskId.hashCode());
            Long $spaceId = this.getSpaceId();
            result = result * 59 + ($spaceId == null ? 43 : ((Object)$spaceId).hashCode());
            String $spaceKey = this.getSpaceKey();
            result = result * 59 + ($spaceKey == null ? 43 : $spaceKey.hashCode());
            String $migrationId = this.getMigrationId();
            result = result * 59 + ($migrationId == null ? 43 : $migrationId.hashCode());
            CompletableFuture<StepResult> $futureResult = this.getFutureResult();
            result = result * 59 + ($futureResult == null ? 43 : $futureResult.hashCode());
            Instant $startTime = this.getStartTime();
            result = result * 59 + ($startTime == null ? 43 : ((Object)$startTime).hashCode());
            return result;
        }

        @Generated
        public String toString() {
            return "SpaceImportExecutor.SpaceImportJob(stepId=" + this.getStepId() + ", cloudId=" + this.getCloudId() + ", cloudImportTaskId=" + this.getCloudImportTaskId() + ", spaceId=" + this.getSpaceId() + ", spaceKey=" + this.getSpaceKey() + ", migrationId=" + this.getMigrationId() + ", futureResult=" + this.getFutureResult() + ", startTime=" + this.getStartTime() + ")";
        }
    }
}

