/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.supercsv.io.CsvListReader
 *  org.supercsv.io.CsvListWriter
 *  org.supercsv.prefs.CsvPreference
 *  org.supercsv.prefs.CsvPreference$Builder
 */
package com.atlassian.migration.agent.service.stepexecutor.space;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.entity.ConfluenceSpaceTask;
import com.atlassian.migration.agent.entity.ExecutionStatus;
import com.atlassian.migration.agent.entity.Plan;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.ExportDirManager;
import com.atlassian.migration.agent.service.MigrationErrorCode;
import com.atlassian.migration.agent.service.UserMappingsManager;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.analytics.ErrorEvent;
import com.atlassian.migration.agent.service.catalogue.EnterpriseGatekeeperClient;
import com.atlassian.migration.agent.service.execution.SpaceBoundStepExecutor;
import com.atlassian.migration.agent.service.execution.UncheckedInterruptedException;
import com.atlassian.migration.agent.service.impl.StepType;
import com.atlassian.migration.agent.service.stepexecutor.StepExecutionException;
import com.atlassian.migration.agent.service.stepexecutor.StepResult;
import com.atlassian.migration.agent.service.stepexecutor.TombstoneMappingsPublisher;
import com.atlassian.migration.agent.service.stepexecutor.TombstoneMappingsPublisherException;
import com.atlassian.migration.agent.service.stepexecutor.UsersGroupMigrationRequestData;
import com.atlassian.migration.agent.service.stepexecutor.space.TombstoneUser;
import com.atlassian.migration.agent.service.user.RetryingUsersMigrationService;
import com.atlassian.migration.agent.service.user.UsersMigrationRequestBuilder;
import com.atlassian.migration.agent.service.user.UsersMigrationStatusResponse;
import com.atlassian.migration.agent.service.user.request.v2.UsersMigrationV2FilePayload;
import com.atlassian.migration.agent.service.user.request.v2.UsersMigrationV2Request;
import com.atlassian.migration.agent.service.util.StopConditionCheckingUtil;
import com.atlassian.migration.agent.store.StepStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

@ParametersAreNonnullByDefault
public class SpaceUsersMigrationExecutor
implements SpaceBoundStepExecutor {
    private static final Logger log = ContextLoggerFactory.getLogger(SpaceUsersMigrationExecutor.class);
    private static final Duration POLLING_PERIOD = Duration.ofSeconds(5L);
    private static final String SPACE_USERS_MIGRATION_REQUEST_BUILT_ACTION = "spaceUsersMigrationRequestBuilt";
    private static final String SPACE_USERS_MIGRATION_ACTION = "spaceUsersMigrated";
    private static final String SPACE_TOMBSTONE_USERS_MIGRATED = "spaceTombstoneUsersMigrated";
    private static final String SPACE_USERS_MIGRATION_RETRIEVE_MAPPINGS = "spaceUsersMigrationRetrieveMappings";
    private static final String USERS_MIGRATION_PROGRESS_CHECK_FOR_SPACE_USERS_MIGRATION = "usersMigrationProgressCheckForSpaceUsersMigration";
    private static final String ADD_AAIDS_TO_USER_MAPPING_FILE_ACTION = "addAAIDsToUserMappingFile";
    private static final String READ_USERS_MAPPING_FILE_FOR_SPACE_USERS_MIGRATION_ACTION = "readUsersMappingFileForSpaceUsersMigration";
    private static final String PROGRESS_CHECK_IN_UMS_FOR_SPACE_USERS_MIGRATION_ACTION = "progressCheckInUmsForSpaceUsersMigration";
    private static final String SUCCESS_MESSAGE = "Space users migration completed.";
    @VisibleForTesting
    static final String SPACE_USERS_MIGRATION_JOB_SUBMITTED_ACTION = "spaceUsersMigrationJobSubmitted";
    private final StepStore stepStore;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;
    private final PluginTransactionTemplate ptx;
    private final ExportDirManager exportDirManager;
    private final AnalyticsEventService analyticsEventService;
    private final AnalyticsEventBuilder analyticsEventBuilder;
    private final SpaceManager spaceManager;
    private final UsersMigrationRequestBuilder usersMigrationRequestBuilder;
    private final RetryingUsersMigrationService usersMigrationService;
    private final EnterpriseGatekeeperClient enterpriseGatekeeperClient;
    private final TombstoneMappingsPublisher tombstoneMappingsPublisher;
    private final Supplier<Instant> instantSupplier;
    private static final CsvPreference DEFAULT_PREFERENCE = new CsvPreference.Builder(CsvPreference.EXCEL_PREFERENCE).useQuoteMode((csvColumn, csvContext, csvPreference) -> Objects.nonNull(csvColumn)).build();

    public SpaceUsersMigrationExecutor(StepStore stepStore, PluginTransactionTemplate ptx, MigrationDarkFeaturesManager migrationDarkFeaturesManager, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, SpaceManager spaceManager, ExportDirManager exportDirManager, UsersMigrationRequestBuilder usersMigrationRequestBuilder, RetryingUsersMigrationService usersMigrationService, EnterpriseGatekeeperClient enterpriseGatekeeperClient, TombstoneMappingsPublisher tombstoneMappingsPublisher) {
        this(stepStore, ptx, migrationDarkFeaturesManager, analyticsEventService, analyticsEventBuilder, spaceManager, exportDirManager, usersMigrationRequestBuilder, usersMigrationService, enterpriseGatekeeperClient, tombstoneMappingsPublisher, Instant::now);
    }

    @VisibleForTesting
    SpaceUsersMigrationExecutor(StepStore stepStore, PluginTransactionTemplate ptx, MigrationDarkFeaturesManager migrationDarkFeaturesManager, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, SpaceManager spaceManager, ExportDirManager exportDirManager, UsersMigrationRequestBuilder usersMigrationRequestBuilder, RetryingUsersMigrationService usersMigrationService, EnterpriseGatekeeperClient enterpriseGatekeeperClient, TombstoneMappingsPublisher tombstoneMappingsPublisher, Supplier<Instant> instantSupplier) {
        this.stepStore = stepStore;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
        this.ptx = ptx;
        this.analyticsEventService = analyticsEventService;
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.spaceManager = spaceManager;
        this.exportDirManager = exportDirManager;
        this.usersMigrationRequestBuilder = usersMigrationRequestBuilder;
        this.usersMigrationService = usersMigrationService;
        this.enterpriseGatekeeperClient = enterpriseGatekeeperClient;
        this.tombstoneMappingsPublisher = tombstoneMappingsPublisher;
        this.instantSupplier = instantSupplier;
    }

    private UsersToMigrate findUsersWithMissingAAIDs(String compressedFile, Step step, long spaceId) throws IOException {
        long startTime = this.instantSupplier.get().toEpochMilli();
        HashSet<String> validUserEmails = new HashSet<String>();
        HashSet<TombstoneUser> tombstoneUsers = new HashSet<TombstoneUser>();
        boolean success = true;
        try (FileInputStream fileInputStream = new FileInputStream(compressedFile);
             GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
             InputStreamReader inputStreamReader = new InputStreamReader((InputStream)gzipInputStream, StandardCharsets.UTF_8);
             CsvListReader csvListReader = new CsvListReader((Reader)inputStreamReader, DEFAULT_PREFERENCE);){
            List row;
            List headers = csvListReader.read();
            HashMap<String, String> headerValueMap = new HashMap<String, String>();
            while ((row = csvListReader.read()) != null) {
                headerValueMap.clear();
                int headerIndex = 0;
                for (String cellValue : row) {
                    headerValueMap.put((String)headers.get(headerIndex++), cellValue);
                }
                if (!StringUtils.isEmpty((CharSequence)((CharSequence)headerValueMap.get("aaid")))) continue;
                SpaceUsersMigrationExecutor.populateUsersEmailsToMigrate(validUserEmails, tombstoneUsers, headerValueMap);
            }
        }
        catch (IOException e) {
            success = false;
            this.sendErrorOperationalEvent(step.getPlan().getMigrationId(), step.getPlan().getCloudSite().getCloudId(), Optional.of(e.getMessage()), Optional.empty());
            throw new StepExecutionException(MigrationErrorCode.SPACE_USER_MIGRATION_ERROR, StepType.SPACE_USERS_MIGRATION, step.getPlan().getMigrationId(), "An error occurred when finding missing AAIDs", e);
        }
        finally {
            this.saveTimerEvent(READ_USERS_MAPPING_FILE_FOR_SPACE_USERS_MIGRATION_ACTION, success, this.instantSupplier.get().toEpochMilli() - startTime, step.getPlan().getMigrationId(), spaceId);
        }
        log.info("Number of valid emails : {} and number of accounts to tombstone: {}", (Object)validUserEmails.size(), (Object)tombstoneUsers.size());
        log.debug("User emails with missing AAIDs: {}", validUserEmails);
        log.debug("User emails for tombstoning: {}", tombstoneUsers);
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildSpaceUsersMigratedEvent(validUserEmails.size(), tombstoneUsers.size(), spaceId, step.getPlan().getMigrationId()));
        return new UsersToMigrate(new ArrayList<String>(validUserEmails), new ArrayList<TombstoneUser>(tombstoneUsers));
    }

    private static void populateUsersEmailsToMigrate(Set<String> validUserEmails, Set<TombstoneUser> tombstoneUsers, Map<String, String> headerValueMap) {
        String email = headerValueMap.get("email");
        if (!IdentityAcceptedEmailValidator.isValidEmailAddress((String)email) || IdentityAcceptedEmailValidator.hasBlockedDomain((String)email)) {
            TombstoneUser user = new TombstoneUser(headerValueMap.get("username"), headerValueMap.get("user_key"), email, headerValueMap.get("username"));
            tombstoneUsers.add(user);
        } else {
            validUserEmails.add(email);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @VisibleForTesting
    Optional<StepResult> doProgressCheck(SpaceUserMigrationRequestJobMetadata jobMetadata, long spaceId, long startTime) {
        UsersMigrationStatusResponse response;
        String stepId = jobMetadata.getStepId();
        String importTaskId = jobMetadata.getImportTaskId();
        int userCount = jobMetadata.getUserCount();
        Step step = this.ptx.read(() -> this.stepStore.getStep(stepId));
        Plan plan = step.getPlan();
        CloudSite cloudSite = plan.getCloudSite();
        String containerToken = cloudSite.getContainerToken();
        String cloudId = cloudSite.getCloudId();
        String migrationId = plan.getMigrationId();
        ExecutionStatus planStatus = plan.getProgress().getStatus();
        if (planStatus == ExecutionStatus.STOPPING || planStatus == ExecutionStatus.STOPPED) {
            this.handlePlanStop(jobMetadata);
            return Optional.of(StepResult.stopped());
        }
        boolean umsProgressCheckSuccess = true;
        long startTimeForUmsProgressCheck = this.instantSupplier.get().toEpochMilli();
        try {
            response = this.usersMigrationService.getUsersAndGroupsMigrationProgress(containerToken, importTaskId);
            log.debug("Got progress for task {}: {}  for migrationId: {}", new Object[]{importTaskId, response, migrationId});
        }
        catch (Exception e) {
            if (StopConditionCheckingUtil.isStoppingExceptionInCausalChain(e)) {
                log.warn("Space users migration was stopped while performing progress check for user migration.", (Throwable)e);
                throw new UncheckedInterruptedException(e);
            }
            umsProgressCheckSuccess = false;
            log.error("An error occurred while checking the progress for space users migration..", (Throwable)e);
            this.sendErrorOperationalEvent(migrationId, cloudId, Optional.ofNullable(e.getMessage()), Optional.of(importTaskId));
            throw new StepExecutionException(MigrationErrorCode.USERS_MIGRATION_PROGRESS_CHECK_FOR_SPACE_USERS_MIGRATION_ERROR, StepType.SPACE_USERS_MIGRATION, plan.getMigrationId(), "Failed to get space users migration progress");
        }
        finally {
            this.saveTimerEvent(PROGRESS_CHECK_IN_UMS_FOR_SPACE_USERS_MIGRATION_ACTION, umsProgressCheckSuccess, this.instantSupplier.get().toEpochMilli() - startTimeForUmsProgressCheck, migrationId, spaceId);
        }
        if (!response.isComplete()) {
            log.debug("Space users migration to cloud is still going on. Updated progress for task {} to {} for migrationId: {}.", new Object[]{importTaskId, response.getProgressPercentage(), migrationId});
            return Optional.empty();
        }
        boolean success = false;
        try {
            if (!response.isSuccessful()) {
                success = MigrationErrorCode.USERS_MIGRATION_PROGRESS_CHECK_FOR_SPACE_USERS_MIGRATION_ERROR.shouldBeTreatedAsGoodEventInReliabilitySlo();
                ErrorEvent errorEvent = new ErrorEvent.ErrorEventBuilder(MigrationErrorCode.USERS_MIGRATION_PROGRESS_CHECK_FOR_SPACE_USERS_MIGRATION_ERROR, MigrationErrorCode.SPACE_USER_MIGRATION_ERROR.getContainerType(), plan.getMigrationId(), StepType.SPACE_USERS_MIGRATION).setCloudid(cloudId).setReason(response.getErrorMessages().toString()).build();
                this.analyticsEventService.sendAnalyticsEventsAsync(() -> ImmutableList.of((Object)this.analyticsEventBuilder.buildErrorOperationalEventWithImportTaskId(errorEvent, Optional.of(importTaskId))));
                log.error("Space users migration failed for task: {} with errors: {} for migrationId: {}", new Object[]{importTaskId, response.getErrors(), migrationId});
                throw new StepExecutionException(MigrationErrorCode.USERS_MIGRATION_PROGRESS_CHECK_FOR_SPACE_USERS_MIGRATION_ERROR, StepType.SPACE_USERS_MIGRATION, plan.getMigrationId(), response.getFirstErrorMessage().orElse("Unknown error"));
            }
            success = true;
            Optional<StepResult> optional = Optional.of(StepResult.succeeded("Space users migration successful"));
            return optional;
        }
        finally {
            this.saveTimerEvent(USERS_MIGRATION_PROGRESS_CHECK_FOR_SPACE_USERS_MIGRATION, success, this.instantSupplier.get().toEpochMilli() - startTime, migrationId, userCount, spaceId);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @VisibleForTesting
    SpaceUserMigrationRequestJobMetadata startSpaceUsersMigrationAndBuildJobMetadata(Step step, List<String> validEmailsToMigrate, Space space) {
        Instant startTime = this.instantSupplier.get();
        boolean success = false;
        try {
            Plan plan = step.getPlan();
            String migrationScopeId = plan.getMigrationScopeId();
            String planId = plan.getId();
            String containerToken = plan.getCloudSite().getContainerToken();
            UsersGroupMigrationRequestData requestData = this.buildSpaceUserMigrationRequest(migrationScopeId, plan.getMigrationId(), planId, space.getId(), plan.getCloudSite().getCloudId(), validEmailsToMigrate);
            UsersMigrationV2FilePayload filePayload = requestData.getFilePayload();
            UsersMigrationV2Request v2Request = requestData.getUsersMigrationV2Request();
            String importTaskId = this.initiateSpaceUsersImport(containerToken, filePayload, v2Request, plan, space.getKey());
            success = true;
            SpaceUserMigrationRequestJobMetadata spaceUserMigrationRequestJobMetadata = new SpaceUserMigrationRequestJobMetadata(step.getId(), importTaskId, filePayload.getUsers().size());
            return spaceUserMigrationRequestJobMetadata;
        }
        finally {
            long timeTaken = this.instantSupplier.get().toEpochMilli() - startTime.toEpochMilli();
            boolean finalSuccess = success;
            this.analyticsEventService.saveAnalyticsEvents(() -> ImmutableList.of((Object)this.analyticsEventBuilder.buildSpaceUserStepTimerEvent(finalSuccess, timeTaken, SPACE_USERS_MIGRATION_JOB_SUBMITTED_ACTION, step.getPlan().getMigrationId(), space.getId())));
        }
    }

    @VisibleForTesting
    String initiateSpaceUsersImport(String containerToken, UsersMigrationV2FilePayload filePayload, UsersMigrationV2Request request, Plan plan, String spaceKey) {
        try {
            String taskId = this.usersMigrationService.initiateUsersAndGroupsMigrationV2(containerToken, request);
            log.info("Initiated space users migration import task for {} users and {} groups. taskId: {}, migrationScopeId: {}", new Object[]{filePayload.getUsers().size(), filePayload.getGroups().size(), taskId, request.getMigrationScopeId()});
            return taskId;
        }
        catch (Exception e) {
            if (StopConditionCheckingUtil.isStoppingExceptionInCausalChain(e)) {
                log.warn("Space users migration was stopped while initiating space users import.", (Throwable)e);
                throw new UncheckedInterruptedException(e);
            }
            log.error("An error occurred while initiate space users import for spaceKey: {}, plan: {}", new Object[]{spaceKey, plan.getName(), e});
            this.sendErrorOperationalEvent(plan.getMigrationId(), plan.getCloudSite().getCloudId(), Optional.ofNullable(e.getMessage()), Optional.empty());
            throw new StepExecutionException(MigrationErrorCode.SPACE_USER_MIGRATION_ERROR, StepType.SPACE_USERS_MIGRATION, plan.getMigrationId(), "Failed to initiate space users migration for spaceKey: " + spaceKey, e);
        }
    }

    @VisibleForTesting
    UsersGroupMigrationRequestData buildSpaceUserMigrationRequest(String migrationScopeId, String migrationId, String planId, long spaceId, String cloudId, List<String> emails) {
        Instant startTime = this.instantSupplier.get();
        try {
            UsersMigrationV2FilePayload filePayload = this.usersMigrationRequestBuilder.createUsersMigrationRequestFilePayloadForEmails(emails);
            UsersMigrationV2Request requestPayload = this.usersMigrationRequestBuilder.createUsersMigrationRequestV2(migrationScopeId, migrationId, Long.toString(spaceId), cloudId, filePayload);
            long timeTaken = this.instantSupplier.get().toEpochMilli() - startTime.toEpochMilli();
            this.saveTimerEvent(SPACE_USERS_MIGRATION_REQUEST_BUILT_ACTION, true, timeTaken, migrationId, filePayload.getUsers().size(), spaceId);
            return new UsersGroupMigrationRequestData(filePayload, requestPayload);
        }
        catch (UncheckedInterruptedException e) {
            throw e;
        }
        catch (Exception e) {
            log.error("An error occurred while building space user migration request for migrationScopeId: {}, migrationId: {}, planId: {}, cloudId: {}", new Object[]{migrationScopeId, migrationId, planId, cloudId, e});
            this.saveTimerEvent(SPACE_USERS_MIGRATION_REQUEST_BUILT_ACTION, false, this.instantSupplier.get().toEpochMilli() - startTime.toEpochMilli(), migrationId, spaceId);
            this.sendErrorOperationalEvent(migrationId, cloudId, Optional.ofNullable(e.getMessage()), Optional.empty());
            throw new StepExecutionException(MigrationErrorCode.SPACE_USER_MIGRATION_ERROR, StepType.SPACE_USERS_MIGRATION, migrationId, "Couldn't retrieve entities to build request", e);
        }
    }

    @Override
    public StepType getStepType() {
        return StepType.SPACE_USERS_MIGRATION;
    }

    @Override
    public StepResult runStep(String stepId) {
        long startTime = this.instantSupplier.get().toEpochMilli();
        Step step = this.ptx.read(() -> this.stepStore.getStep(stepId));
        String spaceKey = ((ConfluenceSpaceTask)step.getTask()).getSpaceKey();
        return this.wrapStepResultSupplier(this.analyticsEventBuilder, this.analyticsEventService, step, spaceKey, this.spaceManager, () -> this.getSpaceUsersMigrationResult(step, startTime, spaceKey));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    @NotNull
    private StepResult getSpaceUsersMigrationResult(Step step, long startTime, String spaceKey) {
        StepResult stepResult;
        StepResult stepResult2;
        String sourceFile;
        Path exportFilePath;
        Space space;
        Optional<Object> optionalJobMetadata;
        boolean success;
        String destFile;
        block14: {
            Object usersToMigrate;
            block13: {
                destFile = null;
                success = false;
                optionalJobMetadata = Optional.empty();
                space = Objects.requireNonNull(this.spaceManager.getSpace(spaceKey));
                Optional<StepResult> optionalStepResult = this.shouldSkipSpaceUsersMigration(step);
                if (!optionalStepResult.isPresent()) break block13;
                success = true;
                StepResult stepResult3 = optionalStepResult.get();
                SpaceUsersMigrationExecutor.cleanup(destFile);
                long elapsedTime = this.instantSupplier.get().toEpochMilli() - startTime;
                this.saveTimerEvent(SPACE_USERS_MIGRATION_ACTION, success, elapsedTime, step.getPlan().getMigrationId(), space.getId());
                boolean finalSuccess = success;
                this.analyticsEventService.sendAnalyticsEventsAsync(() -> ImmutableList.of((Object)this.analyticsEventBuilder.buildMigrationStepMetrics(StepType.SPACE_USERS_MIGRATION, finalSuccess)));
                this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildCompletedStepAnalyticsEvent(step));
                log.debug("Space users migration step is completed with success: {} for stepId: {} and fileId: {}, with elapsed time (in ms): {}.", new Object[]{success, step.getId(), step.getConfig(), elapsedTime});
                return stepResult3;
            }
            exportFilePath = this.exportDirManager.getExportFilePath(step.getConfig());
            ArrayList<TombstoneUser> tombstoneUsers = new ArrayList();
            sourceFile = SpaceUsersMigrationExecutor.getUserMappingFilePath(exportFilePath);
            destFile = SpaceUsersMigrationExecutor.getUpdatedUserMappingFilePath(exportFilePath);
            String executionState = step.getExecutionState();
            if (StringUtils.isNotEmpty((CharSequence)executionState)) {
                optionalJobMetadata = Optional.of(Jsons.readValue(executionState, SpaceUserMigrationRequestJobMetadata.class));
            } else {
                usersToMigrate = this.findUsersWithMissingAAIDs(sourceFile, step, space.getId());
                tombstoneUsers = ((UsersToMigrate)usersToMigrate).tombstoneUsers;
                this.createAndPublishTombstoneMappings(step, tombstoneUsers, space.getId());
                if (!((UsersToMigrate)usersToMigrate).validEmails.isEmpty()) {
                    Optional<Object> finalOptionalJobMetadata = optionalJobMetadata = Optional.of(this.startSpaceUsersMigrationAndBuildJobMetadata(step, ((UsersToMigrate)usersToMigrate).validEmails, space));
                    this.ptx.write(() -> {
                        step.setExecutionState(Jsons.valueAsString(finalOptionalJobMetadata.get()));
                        this.stepStore.update(step);
                    });
                }
            }
            if (optionalJobMetadata.isPresent() || !tombstoneUsers.isEmpty()) break block14;
            log.info("Space users migration completed. There were no records with missing AAIDs");
            success = true;
            usersToMigrate = StepResult.succeeded(SUCCESS_MESSAGE, step.getConfig());
            SpaceUsersMigrationExecutor.cleanup(destFile);
            long elapsedTime = this.instantSupplier.get().toEpochMilli() - startTime;
            this.saveTimerEvent(SPACE_USERS_MIGRATION_ACTION, success, elapsedTime, step.getPlan().getMigrationId(), space.getId());
            boolean finalSuccess = success;
            this.analyticsEventService.sendAnalyticsEventsAsync(() -> ImmutableList.of((Object)this.analyticsEventBuilder.buildMigrationStepMetrics(StepType.SPACE_USERS_MIGRATION, finalSuccess)));
            this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildCompletedStepAnalyticsEvent(step));
            log.debug("Space users migration step is completed with success: {} for stepId: {} and fileId: {}, with elapsed time (in ms): {}.", new Object[]{success, step.getId(), step.getConfig(), elapsedTime});
            return usersToMigrate;
        }
        try {
            if (optionalJobMetadata.isPresent()) {
                long startTimeForProgressCheck = this.instantSupplier.get().toEpochMilli();
                while (!this.doProgressCheck((SpaceUserMigrationRequestJobMetadata)optionalJobMetadata.get(), space.getId(), startTimeForProgressCheck).isPresent()) {
                    Thread.sleep(POLLING_PERIOD.toMillis());
                }
            }
            UserMappingsManager userMappingsManager = this.retrieveMappingsFromMMS(step.getPlan(), space.getId());
            this.addMissingAAIDsToUserMappingFile(userMappingsManager, sourceFile, destFile, step.getPlan(), space.getId());
            Files.move(Paths.get(destFile, new String[0]), Paths.get(sourceFile, new String[0]), StandardCopyOption.REPLACE_EXISTING);
            success = true;
            log.info("Space users migration has finished with export filePath: {}", (Object)exportFilePath.getFileName());
            stepResult2 = StepResult.succeeded(SUCCESS_MESSAGE, step.getConfig());
        }
        catch (UncheckedInterruptedException | InterruptedException e) {
            log.info("Space user migration was interrupted. Will try to cancel import in UMS. StepId={}", (Object)step.getId());
            optionalJobMetadata.ifPresent(this::handlePlanStop);
            success = true;
            stepResult = StepResult.stopped();
            SpaceUsersMigrationExecutor.cleanup(destFile);
            long elapsedTime = this.instantSupplier.get().toEpochMilli() - startTime;
            this.saveTimerEvent(SPACE_USERS_MIGRATION_ACTION, success, elapsedTime, step.getPlan().getMigrationId(), space.getId());
            boolean finalSuccess = success;
            this.analyticsEventService.sendAnalyticsEventsAsync(() -> ImmutableList.of((Object)this.analyticsEventBuilder.buildMigrationStepMetrics(StepType.SPACE_USERS_MIGRATION, finalSuccess)));
            this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildCompletedStepAnalyticsEvent(step));
            log.debug("Space users migration step is completed with success: {} for stepId: {} and fileId: {}, with elapsed time (in ms): {}.", new Object[]{success, step.getId(), step.getConfig(), elapsedTime});
            return stepResult;
        }
        catch (Exception e2) {
            log.error("An error occurred while doing space users migration. StepId: {}, fileId: {}", new Object[]{step.getId(), step.getConfig(), e2});
            stepResult = StepResult.failed("Space users migration has finished with an error.", e2);
            {
                catch (Throwable throwable) {
                    SpaceUsersMigrationExecutor.cleanup(destFile);
                    long elapsedTime = this.instantSupplier.get().toEpochMilli() - startTime;
                    this.saveTimerEvent(SPACE_USERS_MIGRATION_ACTION, success, elapsedTime, step.getPlan().getMigrationId(), space.getId());
                    boolean finalSuccess = success;
                    this.analyticsEventService.sendAnalyticsEventsAsync(() -> ImmutableList.of((Object)this.analyticsEventBuilder.buildMigrationStepMetrics(StepType.SPACE_USERS_MIGRATION, finalSuccess)));
                    this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildCompletedStepAnalyticsEvent(step));
                    log.debug("Space users migration step is completed with success: {} for stepId: {} and fileId: {}, with elapsed time (in ms): {}.", new Object[]{success, step.getId(), step.getConfig(), elapsedTime});
                    throw throwable;
                }
            }
            SpaceUsersMigrationExecutor.cleanup(destFile);
            long elapsedTime = this.instantSupplier.get().toEpochMilli() - startTime;
            this.saveTimerEvent(SPACE_USERS_MIGRATION_ACTION, success, elapsedTime, step.getPlan().getMigrationId(), space.getId());
            boolean finalSuccess = success;
            this.analyticsEventService.sendAnalyticsEventsAsync(() -> ImmutableList.of((Object)this.analyticsEventBuilder.buildMigrationStepMetrics(StepType.SPACE_USERS_MIGRATION, finalSuccess)));
            this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildCompletedStepAnalyticsEvent(step));
            log.debug("Space users migration step is completed with success: {} for stepId: {} and fileId: {}, with elapsed time (in ms): {}.", new Object[]{success, step.getId(), step.getConfig(), elapsedTime});
            return stepResult;
        }
        SpaceUsersMigrationExecutor.cleanup(destFile);
        long elapsedTime = this.instantSupplier.get().toEpochMilli() - startTime;
        this.saveTimerEvent(SPACE_USERS_MIGRATION_ACTION, success, elapsedTime, step.getPlan().getMigrationId(), space.getId());
        boolean finalSuccess = success;
        this.analyticsEventService.sendAnalyticsEventsAsync(() -> ImmutableList.of((Object)this.analyticsEventBuilder.buildMigrationStepMetrics(StepType.SPACE_USERS_MIGRATION, finalSuccess)));
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildCompletedStepAnalyticsEvent(step));
        log.debug("Space users migration step is completed with success: {} for stepId: {} and fileId: {}, with elapsed time (in ms): {}.", new Object[]{success, step.getId(), step.getConfig(), elapsedTime});
        return stepResult2;
    }

    private void createAndPublishTombstoneMappings(Step step, List<TombstoneUser> tombstoneUsers, long spaceId) {
        Instant startTime = this.instantSupplier.get();
        try {
            this.tombstoneMappingsPublisher.createAndPublishTombstoneMappings(step, tombstoneUsers);
            this.saveTimerEvent(SPACE_TOMBSTONE_USERS_MIGRATED, true, this.instantSupplier.get().toEpochMilli() - startTime.toEpochMilli(), step.getPlan().getMigrationId(), tombstoneUsers.size(), spaceId);
        }
        catch (TombstoneMappingsPublisherException e) {
            this.saveTimerEvent(SPACE_TOMBSTONE_USERS_MIGRATED, false, this.instantSupplier.get().toEpochMilli() - startTime.toEpochMilli(), step.getPlan().getMigrationId(), tombstoneUsers.size(), spaceId);
            this.sendErrorOperationalEvent(step.getPlan().getMigrationId(), step.getPlan().getCloudSite().getCloudId(), Optional.ofNullable(e.getMessage()), Optional.empty());
            throw new StepExecutionException(MigrationErrorCode.CREATE_AND_PUBLISHING_TOMBSTONE_MAPPINGS, StepType.SPACE_USERS_MIGRATION, step.getPlan().getMigrationId(), e.getMessage(), e);
        }
    }

    private static void cleanup(@Nullable String destFile) {
        if (destFile != null) {
            try {
                Files.deleteIfExists(Paths.get(destFile, new String[0]));
            }
            catch (IOException e) {
                log.error("An error occurred while deleting the destination file: {} for space users migration", (Object)destFile, (Object)e);
            }
        }
    }

    private void addMissingAAIDsToUserMappingFile(UserMappingsManager userMappingsManager, String sourceFile, String destFile, Plan plan, long spaceId) {
        long startTime = this.instantSupplier.get().toEpochMilli();
        boolean success = true;
        try (FileInputStream fileInputStream = new FileInputStream(sourceFile);
             GZIPInputStream gzipInputStream = new GZIPInputStream(fileInputStream);
             InputStreamReader inputStreamReader = new InputStreamReader((InputStream)gzipInputStream, StandardCharsets.UTF_8);
             CsvListReader csvListReader = new CsvListReader((Reader)inputStreamReader, DEFAULT_PREFERENCE);
             FileOutputStream fileOutputStream = new FileOutputStream(destFile);
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter((OutputStream)gzipOutputStream, StandardCharsets.UTF_8);
             CsvListWriter csvListWriter = new CsvListWriter((Writer)outputStreamWriter, DEFAULT_PREFERENCE);){
            List row;
            List headers = csvListReader.read();
            csvListWriter.write(headers);
            HashMap<Object, String> headerValueMap = new HashMap<Object, String>();
            while ((row = csvListReader.read()) != null) {
                headerValueMap.clear();
                int headerIndex = 0;
                for (String cellValue : row) {
                    headerValueMap.put(headers.get(headerIndex++), cellValue);
                }
                String userKey = (String)headerValueMap.get("user_key");
                String email = (String)headerValueMap.get("email");
                if (StringUtils.isEmpty((CharSequence)((CharSequence)headerValueMap.get("aaid")))) {
                    headerValueMap.put("aaid", userMappingsManager.getAaid(userKey, email, ""));
                }
                ArrayList updatedRow = new ArrayList();
                for (String header : headers) {
                    updatedRow.add(headerValueMap.get(header));
                }
                csvListWriter.write(updatedRow);
            }
        }
        catch (Exception e) {
            success = false;
            this.sendErrorOperationalEvent(plan.getMigrationId(), plan.getCloudSite().getCloudId(), Optional.ofNullable(e.getMessage()), Optional.empty());
            throw new StepExecutionException(MigrationErrorCode.SPACE_USER_MIGRATION_ERROR, StepType.SPACE_USERS_MIGRATION, plan.getMigrationId(), e.getMessage());
        }
        finally {
            this.saveTimerEvent(ADD_AAIDS_TO_USER_MAPPING_FILE_ACTION, success, this.instantSupplier.get().toEpochMilli() - startTime, plan.getMigrationId(), spaceId);
        }
    }

    private UserMappingsManager retrieveMappingsFromMMS(Plan plan, long spaceId) {
        long startTime = this.instantSupplier.get().toEpochMilli();
        boolean success = true;
        try {
            String cloudId = plan.getCloudSite().getCloudId();
            String migrationScopeId = plan.getMigrationScopeId();
            UserMappingsManager userMappingsManager = new UserMappingsManager(this.migrationDarkFeaturesManager, this.enterpriseGatekeeperClient, cloudId, migrationScopeId);
            return userMappingsManager;
        }
        catch (Exception e) {
            success = false;
            this.sendErrorOperationalEvent(plan.getMigrationId(), plan.getCloudSite().getCloudId(), Optional.ofNullable(e.getMessage()), Optional.empty());
            throw new StepExecutionException(MigrationErrorCode.SPACE_USERS_MIGRATION_RETRIEVE_MAPPINGS_ERROR, StepType.SPACE_USERS_MIGRATION, plan.getMigrationId(), e.getMessage(), e);
        }
        finally {
            this.saveTimerEvent(SPACE_USERS_MIGRATION_RETRIEVE_MAPPINGS, success, this.instantSupplier.get().toEpochMilli() - startTime, plan.getMigrationId(), -1, spaceId);
        }
    }

    private Optional<StepResult> shouldSkipSpaceUsersMigration(Step step) {
        if (!this.migrationDarkFeaturesManager.isSpaceUsersMigrationStepEnabled()) {
            log.info("Space users migration has been skipped.");
            return Optional.of(StepResult.succeeded("Space users migration has been skipped.", step.getConfig()));
        }
        return Optional.empty();
    }

    private void handlePlanStop(SpaceUserMigrationRequestJobMetadata jobMetadata) {
        String importTaskId = jobMetadata.getImportTaskId();
        String stepId = jobMetadata.getStepId();
        Step step = this.ptx.read(() -> this.stepStore.getStep(stepId));
        Plan plan = step.getPlan();
        String planId = plan.getId();
        CloudSite cloudSite = plan.getCloudSite();
        String containerToken = cloudSite.getContainerToken();
        log.info("Space User migration was stopped. StepId={}. PlanId={}.", (Object)step.getId(), (Object)planId);
        try {
            if (this.isUMSMigrationInProgress(containerToken, importTaskId)) {
                log.info("There was a running space users migration in UMS, cancelling. ContainerToken={}. TaskId={}. StepId={}. PlanId={}.", new Object[]{containerToken, importTaskId, step.getId(), planId});
                this.usersMigrationService.cancelUsersAndGroupsMigration(containerToken, importTaskId);
            } else {
                log.info("Space User migration was stopped but users & groups migration has already reached a terminal state. ContainerToken={}. TaskId={}. StepId={}. PlanId={}.", new Object[]{containerToken, importTaskId, step.getId(), planId});
            }
        }
        catch (Exception e) {
            this.sendErrorOperationalEvent(plan.getMigrationId(), plan.getCloudSite().getCloudId(), Optional.ofNullable(e.getMessage()), Optional.of(importTaskId));
            throw new StepExecutionException(MigrationErrorCode.USERS_MIGRATION_FOR_SPACE_USERS_MIGRATION_ERROR_DURING_CANCELLATION, StepType.SPACE_USERS_MIGRATION, plan.getMigrationId(), "Failed to cancel space users migration", e);
        }
    }

    private void saveTimerEvent(String action, boolean success, long timeTaken, String migrationId, int userCount, long spaceId) {
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildSpaceUserStepTimerEvent(success, timeTaken, action, migrationId, userCount, spaceId));
    }

    private void saveTimerEvent(String action, boolean success, long timeTaken, String migrationId, long spaceId) {
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildSpaceUserStepTimerEvent(success, timeTaken, action, migrationId, -1, spaceId));
    }

    private boolean isUMSMigrationInProgress(String containerToken, String taskId) {
        UsersMigrationStatusResponse response = this.usersMigrationService.getUsersAndGroupsMigrationProgress(containerToken, taskId);
        return !response.isComplete();
    }

    private void sendErrorOperationalEvent(String migrationId, String cloudId, Optional<String> errorReason, Optional<String> taskId) {
        String reason = errorReason.orElse("");
        ErrorEvent errorEvent = new ErrorEvent.ErrorEventBuilder(MigrationErrorCode.SPACE_USER_MIGRATION_ERROR, MigrationErrorCode.SPACE_USER_MIGRATION_ERROR.getContainerType(), migrationId, StepType.SPACE_USERS_MIGRATION).setCloudid(cloudId).setReason(reason).build();
        this.analyticsEventService.sendAnalyticsEventsAsync(() -> ImmutableList.of((Object)this.analyticsEventBuilder.buildErrorOperationalEventWithImportTaskId(errorEvent, taskId)));
    }

    @VisibleForTesting
    public static String getUserMappingFilePath(Path exportFilePath) {
        return exportFilePath + File.separator + "user_mapping" + ".csv.gz";
    }

    private static String getUpdatedUserMappingFilePath(Path exportFilePath) {
        return exportFilePath + File.separator + "user_mapping" + "_with_AAIDs.csv.gz";
    }

    private static class UsersToMigrate {
        private List<String> validEmails;
        private List<TombstoneUser> tombstoneUsers;

        @Generated
        public UsersToMigrate(List<String> validEmails, List<TombstoneUser> tombstoneUsers) {
            this.validEmails = validEmails;
            this.tombstoneUsers = tombstoneUsers;
        }
    }

    @VisibleForTesting
    public static class SpaceUserMigrationRequestJobMetadata
    implements Serializable {
        private final String stepId;
        private final String importTaskId;
        private final int userCount;

        @JsonCreator
        public SpaceUserMigrationRequestJobMetadata(@JsonProperty(value="stepId") String stepId, @JsonProperty(value="importTaskId") String importTaskId, @JsonProperty(value="userCount") int userCount) {
            this.stepId = stepId;
            this.importTaskId = importTaskId;
            this.userCount = userCount;
        }

        @Generated
        public String getStepId() {
            return this.stepId;
        }

        @Generated
        public String getImportTaskId() {
            return this.importTaskId;
        }

        @Generated
        public int getUserCount() {
            return this.userCount;
        }

        @Generated
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof SpaceUserMigrationRequestJobMetadata)) {
                return false;
            }
            SpaceUserMigrationRequestJobMetadata other = (SpaceUserMigrationRequestJobMetadata)o;
            if (!other.canEqual(this)) {
                return false;
            }
            String this$stepId = this.getStepId();
            String other$stepId = other.getStepId();
            if (this$stepId == null ? other$stepId != null : !this$stepId.equals(other$stepId)) {
                return false;
            }
            String this$importTaskId = this.getImportTaskId();
            String other$importTaskId = other.getImportTaskId();
            if (this$importTaskId == null ? other$importTaskId != null : !this$importTaskId.equals(other$importTaskId)) {
                return false;
            }
            return this.getUserCount() == other.getUserCount();
        }

        @Generated
        protected boolean canEqual(Object other) {
            return other instanceof SpaceUserMigrationRequestJobMetadata;
        }

        @Generated
        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            String $stepId = this.getStepId();
            result = result * 59 + ($stepId == null ? 43 : $stepId.hashCode());
            String $importTaskId = this.getImportTaskId();
            result = result * 59 + ($importTaskId == null ? 43 : $importTaskId.hashCode());
            result = result * 59 + this.getUserCount();
            return result;
        }

        @Generated
        public String toString() {
            return "SpaceUsersMigrationExecutor.SpaceUserMigrationRequestJobMetadata(stepId=" + this.getStepId() + ", importTaskId=" + this.getImportTaskId() + ", userCount=" + this.getUserCount() + ")";
        }
    }
}

