/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.lang3.ObjectUtils
 *  org.codehaus.jackson.type.TypeReference
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.stepexecutor.globalentities;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.entity.MigrateGlobalEntitiesTask;
import com.atlassian.migration.agent.entity.Plan;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.entity.Task;
import com.atlassian.migration.agent.json.JsonSerializingException;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.ConfluenceImportExportTaskStatus;
import com.atlassian.migration.agent.service.GlobalEntitiesImportContextDto;
import com.atlassian.migration.agent.service.MigrationErrorCode;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.analytics.ErrorEvent;
import com.atlassian.migration.agent.service.catalogue.model.MigrationCatalogueStorageFile;
import com.atlassian.migration.agent.service.confluence.ConfluenceCloudService;
import com.atlassian.migration.agent.service.confluence.exception.ConfluenceCloudServiceException;
import com.atlassian.migration.agent.service.execution.UncheckedInterruptedException;
import com.atlassian.migration.agent.service.impl.StepType;
import com.atlassian.migration.agent.service.stepexecutor.ImportExecutor;
import com.atlassian.migration.agent.service.stepexecutor.ProgressTracker;
import com.atlassian.migration.agent.service.stepexecutor.StepExecutionException;
import com.atlassian.migration.agent.service.stepexecutor.StepResult;
import com.atlassian.migration.agent.service.util.StopConditionCheckingUtil;
import com.atlassian.migration.agent.store.StepStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.lang3.ObjectUtils;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public class GlobalEntitiesImportExecutor
extends ImportExecutor {
    private static final Logger log = ContextLoggerFactory.getLogger(GlobalEntitiesImportExecutor.class);
    private final ConfluenceCloudService confluenceCloudService;

    public GlobalEntitiesImportExecutor(ProgressTracker progressTracker, StepStore stepStore, ConfluenceCloudService confluenceCloudService, PluginTransactionTemplate ptx, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, MigrationAgentConfiguration migrationAgentConfiguration) {
        super(progressTracker, stepStore, ptx, analyticsEventService, analyticsEventBuilder, migrationAgentConfiguration);
        this.confluenceCloudService = confluenceCloudService;
    }

    @VisibleForTesting
    protected GlobalEntitiesImportExecutor(ProgressTracker progressTracker, StepStore stepStore, ConfluenceCloudService confluenceCloudService, PluginTransactionTemplate ptx, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, Supplier<Instant> instantSupplier, MigrationAgentConfiguration migrationAgentConfiguration) {
        super(progressTracker, stepStore, ptx, analyticsEventService, analyticsEventBuilder, instantSupplier, migrationAgentConfiguration);
        this.confluenceCloudService = confluenceCloudService;
    }

    @Override
    public StepType getStepType() {
        return StepType.GLOBAL_ENTITIES_IMPORT;
    }

    @Override
    protected String initiateImport(String stepId) {
        log.info("Initiate global templates import with stepId: {}", (Object)stepId);
        Step step = this.ptx.read(() -> this.stepStore.getStep(stepId));
        GlobalEntitiesImportContextDto ctx = this.createImportContext(step, step.getPlan());
        CloudSite cloudSite = step.getPlan().getCloudSite();
        String cloudId = cloudSite.getCloudId();
        String containerToken = cloudSite.getContainerToken();
        String migrationId = step.getPlan().getMigrationId();
        try {
            this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildGlobalEntitiesExportImportStartEvent(step, ((Instant)this.instantSupplier.get()).toEpochMilli()));
            ConfluenceImportExportTaskStatus response = this.confluenceCloudService.initiateGlobalEntitiesImport(cloudId, containerToken, ctx);
            log.info("Initiated global templates import task. Response: {}", (Object)response);
            return response.getId();
        }
        catch (UncheckedInterruptedException uncheckedInterruptedException) {
            throw new UncheckedInterruptedException(uncheckedInterruptedException);
        }
        catch (Exception exception) {
            this.checkIfGoodEventAndLoggingErrorAnalyticEvents(migrationId, cloudId, exception.getMessage(), Optional.empty());
            this.saveCompletedStepAnalyticsEvent(false, step, ctx);
            throw new StepExecutionException(MigrationErrorCode.GLOBAL_ENTITIES_IMPORT_INITIATE_FAILED, this.getStepType(), migrationId, exception.getMessage(), exception);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Optional<StepResult> doProgressCheck(Step step, String taskId) {
        ConfluenceImportExportTaskStatus response;
        StopConditionCheckingUtil.throwIfStopConditionWasReached();
        String stepId = step.getId();
        if (this.stepIsInCompleteStatus(step)) {
            log.warn("Step {} already in complete status, no need to do progress check.", (Object)stepId);
            return Optional.empty();
        }
        Plan plan = step.getPlan();
        GlobalEntitiesImportContextDto ctx = this.createImportContext(step, plan);
        String migrationId = plan.getMigrationId();
        CloudSite cloudSite = plan.getCloudSite();
        String cloudId = cloudSite.getCloudId();
        boolean success = false;
        try {
            response = this.confluenceCloudService.getGlobalEntitiesImportProgress(cloudId, cloudSite.getContainerToken(), taskId);
        }
        catch (UncheckedInterruptedException uncheckedInterruptedException) {
            throw new UncheckedInterruptedException(uncheckedInterruptedException);
        }
        catch (ConfluenceCloudServiceException exception) {
            this.checkIfGoodEventAndLoggingErrorAnalyticEvents(migrationId, cloudId, exception.getMessage(), Optional.of(taskId));
            this.saveCompletedStepAnalyticsEvent(success, step, ctx);
            return Optional.of(GlobalEntitiesImportExecutor.getStepResultForFailure(exception.getMessage()));
        }
        log.debug("Got progress for task {}: {}", (Object)taskId, (Object)response);
        if (!response.isComplete()) {
            this.progressTracker.progress(stepId, response.getPercentageComplete(), StepType.GLOBAL_ENTITIES_IMPORT.getDisplayName(), StepType.GLOBAL_ENTITIES_IMPORT.getDetailedStatus());
            return Optional.empty();
        }
        try {
            if (!response.isSuccessful()) {
                success = this.checkIfGoodEventAndLoggingErrorAnalyticEvents(migrationId, cloudId, response.getMessage(), Optional.of(taskId));
                Optional<StepResult> optional = Optional.of(GlobalEntitiesImportExecutor.getStepResultForFailure(response.getMessage()));
                return optional;
            }
            this.analyticsEventService.sendAnalyticsEvents(() -> ImmutableList.of((Object)this.analyticsEventBuilder.buildSuccessfulGlobalEntitiesMigration(), (Object)this.analyticsEventBuilder.buildGlobalEntitiesImportStepCounterEvent(true)));
            success = true;
            Optional<StepResult> optional = Optional.of(StepResult.succeeded("Global templates migration complete"));
            return optional;
        }
        finally {
            this.saveCompletedStepAnalyticsEvent(success, step, ctx);
        }
    }

    private GlobalEntitiesImportContextDto createImportContext(Step step, Plan plan) {
        String migrationId = plan.getMigrationId();
        String stepConfig = step.getConfig();
        Task task = step.getTask();
        if (!(task instanceof MigrateGlobalEntitiesTask)) {
            throw new StepExecutionException(MigrationErrorCode.GLOBAL_ENTITIES_IMPORT_INCORRECT_TASK_TYPE, this.getStepType(), migrationId, "Cannot proceed because task is not an instance of MigrateGlobalEntitiesTask");
        }
        try {
            List files = (List)Jsons.readValue(stepConfig, (TypeReference)new TypeReference<List<MigrationCatalogueStorageFile>>(){});
            return new GlobalEntitiesImportContextDto(plan.getId(), task.getId(), plan.getMigrationScopeId(), plan.getMigrationId(), files);
        }
        catch (JsonSerializingException e) {
            throw new StepExecutionException(MigrationErrorCode.GLOBAL_ENTITIES_IMPORT_UNFORMATTED_JSON, this.getStepType(), migrationId, "Unexpected json format for stepConfig.", e);
        }
    }

    private boolean checkIfGoodEventAndLoggingErrorAnalyticEvents(String migrationId, String cloudId, String failureReason, Optional<String> importTaskId) {
        ErrorEvent errorEvent = new ErrorEvent.ErrorEventBuilder(MigrationErrorCode.GLOBAL_ENTITIES_IMPORT_FAILED, MigrationErrorCode.GLOBAL_ENTITIES_IMPORT_FAILED.getContainerType(), migrationId, this.getStepType()).setCloudid(cloudId).setReason(failureReason).build();
        ImmutableList events = ImmutableList.of((Object)this.analyticsEventBuilder.buildFailedGlobalEntitiesMigration(this.getStepType(), MigrationErrorCode.GLOBAL_ENTITIES_IMPORT_FAILED), (Object)this.analyticsEventBuilder.buildGlobalEntitiesImportStepCounterEvent(false), (Object)this.analyticsEventBuilder.buildErrorOperationalEventWithImportTaskId(errorEvent, importTaskId));
        this.analyticsEventService.sendAnalyticsEvents(() -> GlobalEntitiesImportExecutor.lambda$checkIfGoodEventAndLoggingErrorAnalyticEvents$3((List)events));
        return MigrationErrorCode.GLOBAL_ENTITIES_IMPORT_FAILED.shouldBeTreatedAsGoodEventInReliabilitySlo();
    }

    @VisibleForTesting
    static StepResult getStepResultForFailure(@Nullable String message) {
        if (message == null) {
            return StepResult.failed("Cloud import failed for global templates with no message");
        }
        return StepResult.failed(String.format("Import to cloud failed for global templates. Message: %s", message));
    }

    private void saveCompletedStepAnalyticsEvent(boolean success, Step step, GlobalEntitiesImportContextDto importContext) {
        ImmutableMap additionalAttributes = ImmutableMap.of((Object)"stepSuccessful", (Object)String.valueOf(success));
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.lambda$saveCompletedStepAnalyticsEvent$5(step, (Map)additionalAttributes));
        this.saveStepTimerEvent(step.getId(), importContext, success);
    }

    private void saveStepTimerEvent(String stepId, GlobalEntitiesImportContextDto importContext, boolean success) {
        this.analyticsEventService.saveAnalyticsEventAsync(() -> {
            Step step = this.ptx.read(() -> this.stepStore.getStep(stepId));
            return this.analyticsEventBuilder.buildGlobalEntitiesImportStepTimerEvent(success, this.getStepTime(step), importContext.getPlanId(), step.getPlan().getCloudSite().getCloudId(), importContext.getTaskId());
        });
    }

    @Override
    public StepResult runStep(String stepId) {
        Step step = this.ptx.read(() -> this.stepStore.getStep(stepId));
        String executionState = step.getExecutionState();
        try {
            Optional<StepResult> result;
            String importTaskId;
            if (ObjectUtils.isNotEmpty((Object)executionState)) {
                importTaskId = executionState;
            } else {
                importTaskId = this.initiateImport(stepId);
                this.ptx.write(() -> step.setExecutionState(importTaskId));
            }
            while (!(result = this.doProgressCheck(step, importTaskId)).isPresent()) {
                Thread.sleep(POLLING_PERIOD.toMillis());
            }
            return result.get();
        }
        catch (UncheckedInterruptedException | InterruptedException e) {
            log.info("Global entities import was stopped. StepId={}", (Object)stepId);
            return StepResult.stopped();
        }
        catch (Exception e) {
            log.error("An error occurred while running step with id: {}", (Object)stepId, (Object)e);
            return StepResult.failed(String.format("An unexpected error occurred during step: %s. Error: %s", this.getStepType(), e.getMessage()), e);
        }
    }

    private /* synthetic */ EventDto lambda$saveCompletedStepAnalyticsEvent$5(Step step, Map additionalAttributes) {
        return this.analyticsEventBuilder.buildCompletedStepAnalyticsEvent(step, () -> additionalAttributes);
    }

    private static /* synthetic */ Collection lambda$checkIfGoodEventAndLoggingErrorAnalyticEvents$3(List events) {
        return events;
    }
}

