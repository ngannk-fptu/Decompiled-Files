/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.analytics.events.EventDto
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.stepexecutor.user;

import com.atlassian.cmpt.analytics.events.EventDto;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.dto.MigrationDetailsDto;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.entity.ExecutionStatus;
import com.atlassian.migration.agent.entity.GlobalEntityType;
import com.atlassian.migration.agent.entity.MigrateUsersTask;
import com.atlassian.migration.agent.entity.MigrationTag;
import com.atlassian.migration.agent.entity.Plan;
import com.atlassian.migration.agent.entity.Progress;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.MigrationErrorCode;
import com.atlassian.migration.agent.service.UserMappingsManager;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.analytics.ErrorEvent;
import com.atlassian.migration.agent.service.catalogue.EnterpriseGatekeeperClient;
import com.atlassian.migration.agent.service.email.GlobalEmailFixesConfigService;
import com.atlassian.migration.agent.service.execution.StepExecutor;
import com.atlassian.migration.agent.service.execution.UncheckedInterruptedException;
import com.atlassian.migration.agent.service.impl.StepSubType;
import com.atlassian.migration.agent.service.impl.StepType;
import com.atlassian.migration.agent.service.stepexecutor.ProgressTracker;
import com.atlassian.migration.agent.service.stepexecutor.StepExecutionException;
import com.atlassian.migration.agent.service.stepexecutor.StepResult;
import com.atlassian.migration.agent.service.stepexecutor.TombstoneMappingsPublisher;
import com.atlassian.migration.agent.service.stepexecutor.TombstoneMappingsPublisherException;
import com.atlassian.migration.agent.service.stepexecutor.UsersGroupMigrationRequestData;
import com.atlassian.migration.agent.service.stepexecutor.space.TombstoneUser;
import com.atlassian.migration.agent.service.user.RetryingUsersMigrationService;
import com.atlassian.migration.agent.service.user.TombstoneFileParameters;
import com.atlassian.migration.agent.service.user.UserMappingsFileManager;
import com.atlassian.migration.agent.service.user.UsersMigrationException;
import com.atlassian.migration.agent.service.user.UsersMigrationRequestBuilder;
import com.atlassian.migration.agent.service.user.UsersMigrationService;
import com.atlassian.migration.agent.service.user.UsersMigrationStatusResponse;
import com.atlassian.migration.agent.service.user.UsersToTombstoneFileManager;
import com.atlassian.migration.agent.service.user.request.v2.UsersMigrationV2FilePayload;
import com.atlassian.migration.agent.service.user.request.v2.UsersMigrationV2Request;
import com.atlassian.migration.agent.service.util.StopConditionCheckingUtil;
import com.atlassian.migration.agent.store.StepProgressPropertiesStore;
import com.atlassian.migration.agent.store.StepStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public class UsersMigrationExecutor
implements StepExecutor {
    private static final Logger log = ContextLoggerFactory.getLogger(UsersMigrationExecutor.class);
    private static final Duration POLLING_PERIOD = Duration.ofSeconds(5L);
    private static final int CONCURRENCY_LEVEL = 1;
    private static final String TOMBSTONE_USERS_MIGRATED = "tombstoneUsersMigrated";
    public static final String USERS_MIGRATION_ACTION = "usersAndGroupsMigrated";
    public static final String USERS_MIGRATION_JOB_SUBMITTED_ACTION = "usersAndGroupsMigrationJobSubmitted";
    public static final String USERS_MIGRATION_V2_REQUEST_BUILT_ACTION = "usersAndGroupsMigrationV2RequestBuilt";
    public static final String TOTAL_USERS_COUNT_PROGRESS_PROPERTY = "totalUsersCount";
    public static final String MIGRATED_USERS_COUNT_PROGRESS_PROPERTY = "migratedUsersCount";
    public static final String TOTAL_GROUPS_COUNT_PROGRESS_PROPERTY = "totalGroupsCount";
    public static final String MIGRATED_GROUPS_COUNT_PROGRESS_PROERPTY = "migratedGroupsCount";
    private final UsersMigrationService usersMigrationService;
    private final ProgressTracker progressTracker;
    private final StepStore stepStore;
    private final StepProgressPropertiesStore stepProgressPropertiesStore;
    private final UsersMigrationRequestBuilder usersMigrationRequestBuilder;
    private final AnalyticsEventService analyticsEventService;
    private final AnalyticsEventBuilder analyticsEventBuilder;
    private final UserMappingsFileManager userMappingsFileManager;
    private final EnterpriseGatekeeperClient enterpriseGatekeeperClient;
    private final Supplier<Instant> instantSupplier;
    private final PluginTransactionTemplate ptx;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;
    private final GlobalEmailFixesConfigService globalEmailFixesConfigService;
    private final UsersToTombstoneFileManager usersToTombstoneFileManager;
    private final TombstoneMappingsPublisher tombstoneMappingsPublisher;

    public UsersMigrationExecutor(RetryingUsersMigrationService usersMigrationService, ProgressTracker progressTracker, UsersMigrationRequestBuilder usersMigrationRequestBuilder, StepStore stepStore, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, UserMappingsFileManager userMappingsFileManager, EnterpriseGatekeeperClient enterpriseGatekeeperClient, PluginTransactionTemplate ptx, MigrationDarkFeaturesManager migrationDarkFeaturesManager, GlobalEmailFixesConfigService globalEmailFixesConfigService, UsersToTombstoneFileManager usersToTombstoneFileManager, TombstoneMappingsPublisher tombstoneMappingsPublisher, StepProgressPropertiesStore stepProgressPropertiesStore) {
        this(usersMigrationService, progressTracker, usersMigrationRequestBuilder, stepStore, analyticsEventService, analyticsEventBuilder, userMappingsFileManager, enterpriseGatekeeperClient, ptx, Instant::now, migrationDarkFeaturesManager, globalEmailFixesConfigService, usersToTombstoneFileManager, tombstoneMappingsPublisher, stepProgressPropertiesStore);
    }

    @VisibleForTesting
    UsersMigrationExecutor(RetryingUsersMigrationService usersMigrationService, ProgressTracker progressTracker, UsersMigrationRequestBuilder usersMigrationRequestBuilder, StepStore stepStore, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, UserMappingsFileManager userMappingsFileManager, EnterpriseGatekeeperClient enterpriseGatekeeperClient, PluginTransactionTemplate ptx, Supplier<Instant> instantSupplier, MigrationDarkFeaturesManager migrationDarkFeaturesManager, GlobalEmailFixesConfigService globalEmailFixesConfigService, UsersToTombstoneFileManager usersToTombstoneFileManager, TombstoneMappingsPublisher tombstoneMappingsPublisher, StepProgressPropertiesStore stepProgressPropertiesStore) {
        this.usersMigrationService = usersMigrationService;
        this.progressTracker = progressTracker;
        this.stepStore = stepStore;
        this.usersMigrationRequestBuilder = usersMigrationRequestBuilder;
        this.analyticsEventService = analyticsEventService;
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.userMappingsFileManager = userMappingsFileManager;
        this.enterpriseGatekeeperClient = enterpriseGatekeeperClient;
        this.instantSupplier = instantSupplier;
        this.ptx = ptx;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
        this.globalEmailFixesConfigService = globalEmailFixesConfigService;
        this.usersToTombstoneFileManager = usersToTombstoneFileManager;
        this.tombstoneMappingsPublisher = tombstoneMappingsPublisher;
        this.stepProgressPropertiesStore = stepProgressPropertiesStore;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @VisibleForTesting
    UsersMigrationExecutorJobMetadata startUsersMigrationAndBuildJobMetadata(String stepId) {
        Step jobStep = this.stepStore.getStep(stepId);
        Plan plan = jobStep.getPlan();
        MigrateUsersTask usersTask = plan.getUserTaskOfPlan().orElseThrow(() -> new IllegalStateException(String.format("UsersMigrationExecutor should not be invoked for a plan with no user migration task. StepId: %s", stepId)));
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildUserMigrationStartEvent(jobStep, this.instantSupplier.get().toEpochMilli(), this.getMigrationAttributes(plan.getCloudSite().getCloudId())));
        boolean success = false;
        Instant startTime = this.instantSupplier.get();
        long freeHeapSizeAtStart = Runtime.getRuntime().freeMemory();
        try {
            List<Step> steps = this.stepStore.stepsCurrentlyRunning(StepType.USERS_MIGRATION.name());
            boolean multipleStepsRunning = this.multipleStepsInUsersMigration(steps, stepId);
            if (multipleStepsRunning) {
                success = MigrationErrorCode.MULTIPLE_USERS_MIGRATIONS_STEP_RUNNING.shouldBeTreatedAsGoodEventInReliabilitySlo();
                EventDto counterMetric = this.analyticsEventBuilder.buildFailedUserMigration((List<Integer>)ImmutableList.of((Object)MigrationErrorCode.MULTIPLE_USERS_MIGRATIONS_STEP_RUNNING.getCode()), success);
                this.analyticsEventService.saveAnalyticsEventAsync(() -> counterMetric);
                throw new StepExecutionException(MigrationErrorCode.USER_MIGRATION_ERROR, StepType.USERS_MIGRATION, plan.getMigrationId(), "Another plan is currently running users migration.");
            }
            String migrationScopeId = plan.getMigrationScopeId();
            String planId = plan.getId();
            MigrationTag migrationTag = plan.getMigrationTag();
            String containerToken = plan.getCloudSite().getContainerToken();
            Set<String> spaceKeys = plan.getSpaceKeysBasedOnUserTaskInPlan();
            this.progressTracker.progressUpdateForSubStep(stepId, 0, StepSubType.valueOf(jobStep.getSubType()).getDisplayName(), StepSubType.USERS_EXPORT.getDetailedStatus(), StepSubType.USERS_EXPORT, Collections.emptyMap());
            Optional<GlobalEntityType> globalEntityType = plan.getGlobalEntityTaskOfPlan();
            this.failAllUserGroupsMigrationIfDisableScopedGroupsFFIsOn(plan, stepId, migrationTag, !spaceKeys.isEmpty());
            UsersGroupMigrationRequestData v2RequestData = this.buildUserMigrationRequestV2(migrationScopeId, plan.getMigrationId(), planId, plan.getCloudSite().getCloudId(), stepId, spaceKeys, migrationTag, globalEntityType);
            UsersMigrationV2FilePayload filePayload = v2RequestData.getFilePayload();
            UsersMigrationV2Request v2Request = v2RequestData.getUsersMigrationV2Request();
            this.progressTracker.progressUpdateForSubStep(stepId, 100, StepSubType.USERS_IMPORT.getDisplayName(), StepSubType.USERS_IMPORT.getDetailedStatus(), StepSubType.USERS_IMPORT, UsersMigrationExecutor.getUserUploadProgressProperties(filePayload));
            String taskId = this.initiateUsersAndGroupsImportV2(containerToken, filePayload, v2Request, plan);
            UsersMigrationExecutorJobMetadata usersMigrationExecutorJobMetadata = new UsersMigrationExecutorJobMetadata(stepId, taskId, new UsersGroupsRequestMetadata(filePayload.getUsers().size(), filePayload.getGroups().size(), usersTask.isScoped()));
            return usersMigrationExecutorJobMetadata;
        }
        finally {
            long timeTaken = this.instantSupplier.get().toEpochMilli() - startTime.toEpochMilli();
            EventDto timerEvent = this.analyticsEventBuilder.buildUserStepTimerEvent(success, timeTaken, USERS_MIGRATION_JOB_SUBMITTED_ACTION, jobStep.getPlan().getId(), usersTask.isScoped());
            this.analyticsEventService.saveAnalyticsEvents(() -> ImmutableList.of((Object)timerEvent, (Object)this.analyticsEventBuilder.buildStepLevelHeapSizeAnalyticsEvent(jobStep, freeHeapSizeAtStart, 1, 1)));
        }
    }

    @VisibleForTesting
    String initiateUsersAndGroupsImportV2(String containerToken, UsersMigrationV2FilePayload filePayload, UsersMigrationV2Request request, Plan plan) {
        try {
            String importTaskId = this.usersMigrationService.initiateUsersAndGroupsMigrationV2(containerToken, request);
            log.info("Initiated users and groups import task for {} users and {} groups. taskId: {}, migrationScopeId: {}", new Object[]{filePayload.getUsers().size(), filePayload.getGroups().size(), importTaskId, request.getMigrationScopeId()});
            return importTaskId;
        }
        catch (Exception e) {
            if (StopConditionCheckingUtil.isStoppingExceptionInCausalChain(e)) {
                throw new UncheckedInterruptedException(e);
            }
            this.sendErrorOperationalEvent(plan.getMigrationId(), plan.getCloudSite().getCloudId(), Optional.ofNullable(e.getMessage()), Optional.empty());
            throw new UsersMigrationException("Failed to initiate users and groups import task for migration " + plan.getMigrationId(), e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @VisibleForTesting
    Optional<StepResult> doProgressCheck(UsersMigrationExecutorJobMetadata jobMetadata) {
        UsersMigrationStatusResponse response;
        String stepId = jobMetadata.stepId;
        String taskId = jobMetadata.taskId;
        UsersGroupsRequestMetadata requestMetadata = jobMetadata.requestMetadata;
        Step step = this.stepStore.getStep(stepId);
        Plan plan = step.getPlan();
        CloudSite cloudSite = plan.getCloudSite();
        String containerToken = cloudSite.getContainerToken();
        String cloudId = cloudSite.getCloudId();
        String planId = plan.getId();
        String migrationScopeId = plan.getMigrationScopeId();
        ExecutionStatus planStatus = plan.getProgress().getStatus();
        String migrationId = plan.getMigrationId();
        if (planStatus == ExecutionStatus.STOPPING || planStatus == ExecutionStatus.STOPPED) {
            this.handlePlanStop(containerToken, taskId, step, plan, requestMetadata);
            return Optional.of(StepResult.stopped());
        }
        try {
            response = this.usersMigrationService.getUsersAndGroupsMigrationProgress(containerToken, taskId);
            log.debug("Got progress for task {}: {}  for migrationId: {}", new Object[]{taskId, response, migrationId});
        }
        catch (Exception e) {
            if (StopConditionCheckingUtil.isStoppingExceptionInCausalChain(e)) {
                throw new UncheckedInterruptedException(e);
            }
            this.sendErrorOperationalEvent(migrationId, cloudId, Optional.ofNullable(e.getMessage()), Optional.of(taskId));
            throw new StepExecutionException(MigrationErrorCode.USER_MIGRATION_ERROR, StepType.USERS_MIGRATION, plan.getMigrationId(), "Failed to get users and groups migration progress");
        }
        Map<String, Object> userImportProgressProperties = this.buildUserImportProgressProperties(step, response);
        if (!response.isComplete()) {
            log.debug("Users and groups migration to cloud is still going. Updated progress for task {} to {} for migrationId: {}.", new Object[]{taskId, response.getProgressPercentage(), migrationId});
            this.progressTracker.progressUpdateForSubStep(stepId, response.getProgressPercentage(), step.getDisplayName(), StepSubType.USERS_IMPORT.getDetailedStatus(), StepSubType.USERS_IMPORT, userImportProgressProperties);
            return Optional.empty();
        }
        boolean success = false;
        try {
            if (!response.isSuccessful()) {
                success = MigrationErrorCode.USER_MIGRATION_ERROR.shouldBeTreatedAsGoodEventInReliabilitySlo();
                ErrorEvent errorEvent = new ErrorEvent.ErrorEventBuilder(MigrationErrorCode.USER_MIGRATION_ERROR, MigrationErrorCode.USER_MIGRATION_ERROR.getContainerType(), plan.getMigrationId(), StepType.USERS_MIGRATION).setCloudid(cloudId).setReason(response.getErrorMessages().toString()).build();
                ImmutableList metrics = ImmutableList.of((Object)this.analyticsEventBuilder.buildFailedUserMigration(response.getErrorCodes(), success), (Object)this.analyticsEventBuilder.buildMigrationStepMetrics(StepType.USERS_MIGRATION, success), (Object)this.analyticsEventBuilder.buildMigrationErrorMetric(MigrationErrorCode.USER_MIGRATION_ERROR), (Object)this.analyticsEventBuilder.buildErrorOperationalEventWithImportTaskId(errorEvent, Optional.of(taskId)));
                this.analyticsEventService.sendAnalyticsEvents(() -> UsersMigrationExecutor.lambda$doProgressCheck$4((List)metrics));
                log.error("Users and groups migration failed for task: {} with errors: {} for migrationId: {}", new Object[]{taskId, response.getErrors(), migrationId});
                throw new StepExecutionException(MigrationErrorCode.USER_MIGRATION_ERROR, StepType.USERS_MIGRATION, plan.getMigrationId(), response.getFirstErrorMessage().orElse("Unknown error"));
            }
            Map<String, String> mappings = this.getUserMappingsAndSaveToFile(cloudId, planId, migrationScopeId);
            if (this.migrationDarkFeaturesManager.shouldHandleGlobalEmailFixes() && !this.migrationDarkFeaturesManager.isGlobalEmailFixesSendTombstonesFeatureDisabled()) {
                List<TombstoneUser> usersToTombstone = this.usersToTombstoneFileManager.getUsersToTombstoneFromFile(planId).stream().filter(migrationUser -> !mappings.containsKey(migrationUser.getUserKey())).map(TombstoneUser::fromMigrationUser).collect(Collectors.toList());
                List<TombstoneUser> tombstoneUsersWithAaids = this.createAndPublishTombstoneMappings(step, usersToTombstone, requestMetadata.scoped);
                HashMap<String, String> mergedMappings = new HashMap<String, String>(mappings);
                tombstoneUsersWithAaids.forEach(tombstoneUser -> mergedMappings.put(tombstoneUser.getUserKey(), tombstoneUser.getAaid()));
                this.saveUserMappingsToFile(planId, mergedMappings);
            }
            this.progressTracker.updateProgressPropertiesWithoutTransferUpdate(stepId, userImportProgressProperties);
            this.analyticsEventService.saveAnalyticsEventAsync(this.analyticsEventBuilder::buildSuccessfulUserMigration);
            success = true;
            Optional<StepResult> optional = Optional.of(StepResult.succeeded("Users and groups migration complete"));
            return optional;
        }
        finally {
            this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildCompletedStepAnalyticsEvent(step));
            this.saveTimerEvent(USERS_MIGRATION_ACTION, success, this.getStepTime(step), planId, requestMetadata);
        }
    }

    private Map<String, Object> buildUserImportProgressProperties(Step step, UsersMigrationStatusResponse response) {
        int totalUsers = response.getTotalUsersCount();
        int totalGroups = response.getTotalGroupsCount();
        Map previousProgressProperties = this.ptx.read(() -> this.stepProgressPropertiesStore.getStepProgressProperties(step.getId()));
        if (response.getTotalUsersCount() == 0) {
            totalUsers = previousProgressProperties.getOrDefault(TOTAL_USERS_COUNT_PROGRESS_PROPERTY, 0);
            totalGroups = previousProgressProperties.getOrDefault(TOTAL_GROUPS_COUNT_PROGRESS_PROPERTY, 0);
        }
        return ImmutableMap.builder().put((Object)TOTAL_USERS_COUNT_PROGRESS_PROPERTY, (Object)totalUsers).put((Object)MIGRATED_USERS_COUNT_PROGRESS_PROPERTY, (Object)response.getMigratedUsersCount()).put((Object)TOTAL_GROUPS_COUNT_PROGRESS_PROPERTY, (Object)totalGroups).put((Object)MIGRATED_GROUPS_COUNT_PROGRESS_PROERPTY, (Object)response.getMigratedGroupsCount()).build();
    }

    private static Map<String, Object> getUserUploadProgressProperties(UsersMigrationV2FilePayload filePayload) {
        return ImmutableMap.builder().put((Object)TOTAL_USERS_COUNT_PROGRESS_PROPERTY, (Object)filePayload.getUsers().size()).put((Object)MIGRATED_USERS_COUNT_PROGRESS_PROPERTY, (Object)0).put((Object)TOTAL_GROUPS_COUNT_PROGRESS_PROPERTY, (Object)filePayload.getGroups().size()).put((Object)MIGRATED_GROUPS_COUNT_PROGRESS_PROERPTY, (Object)0).build();
    }

    private List<TombstoneUser> createAndPublishTombstoneMappings(Step step, List<TombstoneUser> tombstoneUsers, boolean scoped) {
        Instant startTime = this.instantSupplier.get();
        try {
            List<TombstoneUser> tombstoneUsersWithAaids = this.tombstoneMappingsPublisher.createAndPublishTombstoneMappings(step, tombstoneUsers);
            this.saveTimerEvent(TOMBSTONE_USERS_MIGRATED, true, this.instantSupplier.get().toEpochMilli() - startTime.toEpochMilli(), step.getPlan().getId(), scoped, tombstoneUsers.size());
            return tombstoneUsersWithAaids;
        }
        catch (TombstoneMappingsPublisherException e) {
            this.saveTimerEvent(TOMBSTONE_USERS_MIGRATED, false, this.instantSupplier.get().toEpochMilli() - startTime.toEpochMilli(), step.getPlan().getId(), scoped, tombstoneUsers.size());
            this.sendErrorOperationalEvent(step.getPlan().getMigrationId(), step.getPlan().getCloudSite().getCloudId(), Optional.ofNullable(e.getMessage()), Optional.empty());
            throw new StepExecutionException(MigrationErrorCode.CREATE_AND_PUBLISHING_TOMBSTONE_MAPPINGS, StepType.USERS_MIGRATION, step.getPlan().getMigrationId(), e.getMessage(), e);
        }
    }

    private Map<String, String> getUserMappingsAndSaveToFile(String cloudId, String planId, String migrationScopeId) {
        try {
            UserMappingsManager userMappingsManager = new UserMappingsManager(this.migrationDarkFeaturesManager, this.enterpriseGatekeeperClient, cloudId, migrationScopeId);
            Map<String, String> mappings = userMappingsManager.getMappings();
            this.userMappingsFileManager.saveToFile(planId, mappings);
            return mappings;
        }
        catch (RuntimeException e) {
            log.error("Error getUserMappingsAndSaveToFile: {}", (Object)e.getMessage(), (Object)e);
            return Collections.emptyMap();
        }
    }

    private void saveUserMappingsToFile(String planId, Map<String, String> mappings) {
        try {
            this.userMappingsFileManager.saveToFile(planId, mappings);
        }
        catch (RuntimeException e) {
            log.error("Error saveUserMappingsToFile: {}", (Object)e.getMessage(), (Object)e);
        }
    }

    @VisibleForTesting
    UsersGroupMigrationRequestData buildUserMigrationRequestV2(String migrationScopeId, String migrationId, String planId, String cloudId, String stepId, Set<String> spaceKeys, MigrationTag migrationTag, Optional<GlobalEntityType> globalEntityType) {
        Instant startTime = this.instantSupplier.get();
        boolean scoped = !spaceKeys.isEmpty();
        try {
            UsersMigrationV2FilePayload filePayload = this.usersMigrationRequestBuilder.createUsersMigrationRequestV2FilePayload(spaceKeys, cloudId, TombstoneFileParameters.withFile(planId), globalEntityType);
            this.progressTracker.progressUpdateForSubStep(stepId, 100, StepSubType.USERS_UPLOAD.getDisplayName(), StepSubType.USERS_UPLOAD.getDetailedStatus(), StepSubType.USERS_UPLOAD, Collections.emptyMap());
            UsersMigrationV2Request requestPayload = this.usersMigrationRequestBuilder.createUsersMigrationRequestV2(migrationScopeId, migrationId, planId, cloudId, filePayload);
            long timeTaken = this.instantSupplier.get().toEpochMilli() - startTime.toEpochMilli();
            this.saveTimerEvent(USERS_MIGRATION_V2_REQUEST_BUILT_ACTION, true, timeTaken, planId, scoped, filePayload.getUsers().size(), filePayload.getGroups().size());
            this.sendUserExportStepCompletionPlatformEvents(migrationId, stepId, migrationScopeId, planId, cloudId, ExecutionStatus.DONE, migrationTag);
            return new UsersGroupMigrationRequestData(filePayload, requestPayload);
        }
        catch (Exception e) {
            log.error("Error building users migration request for migrationId:{} : {}", new Object[]{migrationId, e.getMessage(), e});
            if (StopConditionCheckingUtil.isStoppingExceptionInCausalChain(e)) {
                throw new UncheckedInterruptedException(e);
            }
            this.saveTimerEvent(USERS_MIGRATION_V2_REQUEST_BUILT_ACTION, false, this.instantSupplier.get().toEpochMilli() - startTime.toEpochMilli(), planId, scoped);
            this.sendUserExportStepCompletionPlatformEvents(migrationId, stepId, migrationScopeId, planId, cloudId, ExecutionStatus.FAILED, migrationTag);
            this.sendErrorOperationalEvent(migrationId, cloudId, Optional.ofNullable(e.getMessage()), Optional.empty());
            throw new StepExecutionException(MigrationErrorCode.USER_MIGRATION_ERROR, StepType.USERS_MIGRATION, migrationId, "Couldn't retrieve entities to build request", e);
        }
    }

    private void failAllUserGroupsMigrationIfDisableScopedGroupsFFIsOn(Plan plan, String stepId, MigrationTag migrationTag, boolean scoped) {
        Instant startTime = this.instantSupplier.get();
        if (!scoped && this.migrationDarkFeaturesManager.disableScopedGroupMigration()) {
            String ERR_MSG = "Cannot migrate all users and groups if migration-assistant.macquarie.viper.disable.scoped.groups.migration flag is enabled";
            this.saveTimerEvent(USERS_MIGRATION_V2_REQUEST_BUILT_ACTION, false, this.instantSupplier.get().toEpochMilli() - startTime.toEpochMilli(), plan.getId(), false);
            this.sendUserExportStepCompletionPlatformEvents(plan.getMigrationId(), stepId, plan.getMigrationScopeId(), plan.getId(), plan.getCloudSite().getCloudId(), ExecutionStatus.FAILED, migrationTag);
            this.sendErrorOperationalEvent(plan.getMigrationId(), plan.getCloudSite().getCloudId(), Optional.of("Cannot migrate all users and groups if migration-assistant.macquarie.viper.disable.scoped.groups.migration flag is enabled"), Optional.empty());
            throw new StepExecutionException(MigrationErrorCode.USER_MIGRATION_ERROR, StepType.USERS_MIGRATION, plan.getMigrationId(), "Cannot migrate all users and groups if migration-assistant.macquarie.viper.disable.scoped.groups.migration flag is enabled");
        }
    }

    private void saveTimerEvent(String action, boolean success, long timeTaken, String planId, UsersGroupsRequestMetadata requestMetadata) {
        this.saveTimerEvent(action, success, timeTaken, planId, requestMetadata.scoped, requestMetadata.userCount, requestMetadata.groupCount);
    }

    private void saveTimerEvent(String action, boolean success, long timeTaken, String planId, boolean scoped, int userCount, int groupCount) {
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildUserStepTimerEvent(success, timeTaken, action, planId, scoped, userCount, groupCount));
    }

    private void saveTimerEvent(String action, boolean success, long timeTaken, String planId, boolean scoped, int userCount) {
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildUserStepTimerEvent(success, timeTaken, action, planId, scoped, userCount, -1));
    }

    private void sendUserExportStepCompletionPlatformEvents(String migrationId, String stepId, String migrationScopeId, String planId, String cloudId, ExecutionStatus status, MigrationTag migrationTag) {
        this.analyticsEventService.sendAnalyticsEvents(() -> ImmutableList.of((Object)this.analyticsEventBuilder.buildPlatformStepCompletionOperationalEventUtil(new MigrationDetailsDto(migrationId, migrationScopeId, planId, cloudId, stepId), Optional.empty(), "USER_EXPORT", status, migrationTag), (Object)this.analyticsEventBuilder.buildPlatformStepCompletionMetricEvent("USER_EXPORT", status, migrationTag), (Object)this.analyticsEventBuilder.buildPlatformStepCompletionExtendedMetricEvent("USER_EXPORT", status, migrationTag)));
    }

    private void sendErrorOperationalEvent(String migrationId, String cloudId, Optional<String> errorReason, Optional<String> taskId) {
        String reason = errorReason.orElse("");
        ErrorEvent errorEvent = new ErrorEvent.ErrorEventBuilder(MigrationErrorCode.USER_MIGRATION_ERROR, MigrationErrorCode.USER_MIGRATION_ERROR.getContainerType(), migrationId, StepType.USERS_MIGRATION).setCloudid(cloudId).setReason(reason).build();
        this.analyticsEventService.sendAnalyticsEvent(() -> this.analyticsEventBuilder.buildErrorOperationalEventWithImportTaskId(errorEvent, taskId));
    }

    private void saveTimerEvent(String action, boolean success, long timeTaken, String planId, boolean scoped) {
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildUserStepTimerEvent(success, timeTaken, action, planId, scoped));
    }

    private long getStepTime(Step step) {
        Progress progress = step.getProgress();
        if (progress != null && progress.getStartTime().isPresent()) {
            return this.instantSupplier.get().toEpochMilli() - progress.getStartTime().get().toEpochMilli();
        }
        return -1L;
    }

    private boolean multipleStepsInUsersMigration(List<Step> steps, String stepId) {
        return steps.stream().anyMatch(step -> !stepId.equals(step.getId()));
    }

    @VisibleForTesting
    ImmutableMap.Builder<String, Object> getMigrationAttributes(String cloudId) {
        ImmutableMap.Builder attributes = new ImmutableMap.Builder();
        attributes.put((Object)"tenantId", (Object)cloudId);
        if (this.migrationDarkFeaturesManager.shouldHandleGlobalEmailFixes()) {
            attributes.put((Object)"invalidEmailsConfig", (Object)this.globalEmailFixesConfigService.getInvalidEmailsConfig().getActionOnMigration().name());
            attributes.put((Object)"duplicateEmailsConfig", (Object)this.globalEmailFixesConfigService.getDuplicateEmailsConfig().getActionOnMigration().name());
        }
        return attributes;
    }

    @Override
    public StepType getStepType() {
        return StepType.USERS_MIGRATION;
    }

    @Override
    public StepResult runStep(String stepId) {
        UsersMigrationExecutorJobMetadata jobMetadata = null;
        Step step = null;
        try {
            Optional<StepResult> result;
            step = this.ptx.read(() -> this.stepStore.getStep(stepId));
            String executionState = step.getExecutionState();
            if (StringUtils.isNotEmpty((CharSequence)executionState)) {
                jobMetadata = Jsons.readValue(executionState, UsersMigrationExecutorJobMetadata.class);
            } else {
                UsersMigrationExecutorJobMetadata metadata = this.startUsersMigrationAndBuildJobMetadata(stepId);
                this.ptx.write(() -> {
                    Step stepUpdated = this.stepStore.getStep(stepId);
                    stepUpdated.setExecutionState(Jsons.valueAsString(metadata));
                    this.stepStore.update(stepUpdated);
                });
                jobMetadata = metadata;
            }
            while (!(result = this.doProgressCheck(jobMetadata)).isPresent()) {
                Thread.sleep(POLLING_PERIOD.toMillis());
            }
            return result.get();
        }
        catch (UncheckedInterruptedException | InterruptedException e) {
            if (jobMetadata != null) {
                this.handlePlanStop(jobMetadata);
            }
            if (this.checkIfPlanIsStopped(stepId)) {
                return StepResult.stopped();
            }
            return StepResult.failed(String.format("An unexpected error occurred during step: %s. Error: %s", this.getStepType(), e.getMessage()), e);
        }
        catch (Exception e) {
            if (this.checkIfPlanIsStopped(stepId)) {
                return StepResult.stopped();
            }
            log.error("An error occurred while running step with id: {}", (Object)stepId, (Object)e);
            return StepResult.failed(String.format("An unexpected error occurred during step: %s. Error: %s", this.getStepType(), e.getMessage()), e);
        }
    }

    private boolean checkIfPlanIsStopped(String stepId) {
        Step step = this.ptx.read(() -> this.stepStore.getStep(stepId));
        ExecutionStatus planStatus = step.getPlan().getProgress().getStatus();
        return planStatus == ExecutionStatus.STOPPING || planStatus == ExecutionStatus.STOPPED;
    }

    private void handlePlanStop(UsersMigrationExecutorJobMetadata jobMetadata) {
        String taskId = jobMetadata.getTaskId();
        UsersGroupsRequestMetadata requestMetadata = jobMetadata.getRequestMetadata();
        String stepId = jobMetadata.getStepId();
        Step step = this.stepStore.getStep(stepId);
        Plan plan = step.getPlan();
        CloudSite cloudSite = plan.getCloudSite();
        String containerToken = cloudSite.getContainerToken();
        this.handlePlanStop(containerToken, taskId, step, plan, requestMetadata);
    }

    private void handlePlanStop(String containerToken, String taskId, Step step, Plan plan, UsersGroupsRequestMetadata requestMetadata) {
        String planId = plan.getId();
        log.info("User migration was stopped. StepId={}. PlanId={}.", (Object)step.getId(), (Object)planId);
        try {
            if (this.isUMSMigrationInProgress(containerToken, taskId)) {
                log.info("There was a running users & groups migration in UMS, cancelling. ContainerToken={}. TaskId={}. StepId={}. PlanId={}.", new Object[]{containerToken, taskId, step.getId(), planId});
                this.usersMigrationService.cancelUsersAndGroupsMigration(containerToken, taskId);
            } else {
                log.info("User migration was stopped but users & groups migration has already reached a terminal state. ContainerToken={}. TaskId={}. StepId={}. PlanId={}.", new Object[]{containerToken, taskId, step.getId(), planId});
            }
            this.saveTimerEvent(USERS_MIGRATION_ACTION, false, this.getStepTime(step), planId, requestMetadata);
        }
        catch (Exception e) {
            this.sendErrorOperationalEvent(plan.getMigrationId(), plan.getCloudSite().getCloudId(), Optional.ofNullable(e.getMessage()), Optional.of(taskId));
            throw new StepExecutionException(MigrationErrorCode.USER_MIGRATION_ERROR, StepType.USERS_MIGRATION, plan.getMigrationId(), "Failed to cancel users and groups migration", e);
        }
    }

    private boolean isUMSMigrationInProgress(String containerToken, String taskId) {
        UsersMigrationStatusResponse response = this.usersMigrationService.getUsersAndGroupsMigrationProgress(containerToken, taskId);
        return !response.isComplete();
    }

    private static /* synthetic */ Collection lambda$doProgressCheck$4(List metrics) {
        return metrics;
    }

    @VisibleForTesting
    static class UsersGroupsRequestMetadata
    implements Serializable {
        private final int userCount;
        private final int groupCount;
        private final boolean scoped;

        @JsonCreator
        UsersGroupsRequestMetadata(@JsonProperty(value="userCount") int userCount, @JsonProperty(value="groupCount") int groupCount, @JsonProperty(value="scoped") boolean scoped) {
            this.userCount = userCount;
            this.groupCount = groupCount;
            this.scoped = scoped;
        }

        @Generated
        public int getUserCount() {
            return this.userCount;
        }

        @Generated
        public int getGroupCount() {
            return this.groupCount;
        }

        @Generated
        public boolean isScoped() {
            return this.scoped;
        }

        @Generated
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof UsersGroupsRequestMetadata)) {
                return false;
            }
            UsersGroupsRequestMetadata other = (UsersGroupsRequestMetadata)o;
            if (!other.canEqual(this)) {
                return false;
            }
            if (this.getUserCount() != other.getUserCount()) {
                return false;
            }
            if (this.getGroupCount() != other.getGroupCount()) {
                return false;
            }
            return this.isScoped() == other.isScoped();
        }

        @Generated
        protected boolean canEqual(Object other) {
            return other instanceof UsersGroupsRequestMetadata;
        }

        @Generated
        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            result = result * 59 + this.getUserCount();
            result = result * 59 + this.getGroupCount();
            result = result * 59 + (this.isScoped() ? 79 : 97);
            return result;
        }

        @Generated
        public String toString() {
            return "UsersMigrationExecutor.UsersGroupsRequestMetadata(userCount=" + this.getUserCount() + ", groupCount=" + this.getGroupCount() + ", scoped=" + this.isScoped() + ")";
        }
    }

    @VisibleForTesting
    static class UsersMigrationExecutorJobMetadata
    implements Serializable {
        private final String stepId;
        private final String taskId;
        private final UsersGroupsRequestMetadata requestMetadata;

        @JsonCreator
        UsersMigrationExecutorJobMetadata(@JsonProperty(value="stepId") String stepId, @JsonProperty(value="taskId") String taskId, @JsonProperty(value="requestMetadata") UsersGroupsRequestMetadata requestMetadata) {
            this.stepId = stepId;
            this.taskId = taskId;
            this.requestMetadata = requestMetadata;
        }

        @Generated
        public String getStepId() {
            return this.stepId;
        }

        @Generated
        public String getTaskId() {
            return this.taskId;
        }

        @Generated
        public UsersGroupsRequestMetadata getRequestMetadata() {
            return this.requestMetadata;
        }

        @Generated
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof UsersMigrationExecutorJobMetadata)) {
                return false;
            }
            UsersMigrationExecutorJobMetadata other = (UsersMigrationExecutorJobMetadata)o;
            if (!other.canEqual(this)) {
                return false;
            }
            String this$stepId = this.getStepId();
            String other$stepId = other.getStepId();
            if (this$stepId == null ? other$stepId != null : !this$stepId.equals(other$stepId)) {
                return false;
            }
            String this$taskId = this.getTaskId();
            String other$taskId = other.getTaskId();
            if (this$taskId == null ? other$taskId != null : !this$taskId.equals(other$taskId)) {
                return false;
            }
            UsersGroupsRequestMetadata this$requestMetadata = this.getRequestMetadata();
            UsersGroupsRequestMetadata other$requestMetadata = other.getRequestMetadata();
            return !(this$requestMetadata == null ? other$requestMetadata != null : !((Object)this$requestMetadata).equals(other$requestMetadata));
        }

        @Generated
        protected boolean canEqual(Object other) {
            return other instanceof UsersMigrationExecutorJobMetadata;
        }

        @Generated
        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            String $stepId = this.getStepId();
            result = result * 59 + ($stepId == null ? 43 : $stepId.hashCode());
            String $taskId = this.getTaskId();
            result = result * 59 + ($taskId == null ? 43 : $taskId.hashCode());
            UsersGroupsRequestMetadata $requestMetadata = this.getRequestMetadata();
            result = result * 59 + ($requestMetadata == null ? 43 : ((Object)$requestMetadata).hashCode());
            return result;
        }

        @Generated
        public String toString() {
            return "UsersMigrationExecutor.UsersMigrationExecutorJobMetadata(stepId=" + this.getStepId() + ", taskId=" + this.getTaskId() + ", requestMetadata=" + this.getRequestMetadata() + ")";
        }
    }
}

