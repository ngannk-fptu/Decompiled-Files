/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.analytics.events.CounterMetricEvent$Builder
 *  com.atlassian.cmpt.analytics.events.TimerMetricEvent$Builder
 *  com.atlassian.cmpt.check.base.CheckContext
 *  com.atlassian.cmpt.check.base.CheckExecutionStatus
 *  com.atlassian.cmpt.check.base.CheckExecutor
 *  com.atlassian.cmpt.check.base.CheckRequest
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.CheckStatus
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.cmpt.check.email.EmailFormatChecker
 *  com.atlassian.cmpt.check.mapper.ExecutionErrorCodes
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
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.apache.commons.collections.CollectionUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.check;

import com.atlassian.cmpt.analytics.events.CounterMetricEvent;
import com.atlassian.cmpt.analytics.events.TimerMetricEvent;
import com.atlassian.cmpt.check.base.CheckContext;
import com.atlassian.cmpt.check.base.CheckExecutor;
import com.atlassian.cmpt.check.base.CheckRequest;
import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.CheckStatus;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.check.email.EmailFormatChecker;
import com.atlassian.cmpt.check.mapper.ExecutionErrorCodes;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.dto.DuplicateEmailsConfigDto;
import com.atlassian.migration.agent.dto.InvalidEmailsConfigDto;
import com.atlassian.migration.agent.entity.CheckExecutionStatus;
import com.atlassian.migration.agent.entity.CheckResultEntity;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.logging.LoggingContextBuilder;
import com.atlassian.migration.agent.service.FeatureFlagService;
import com.atlassian.migration.agent.service.MigrationMetric;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.analytics.FeatureFlagActionSubject;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.CheckRegistry;
import com.atlassian.migration.agent.service.check.CheckResultsService;
import com.atlassian.migration.agent.service.check.CheckType;
import com.atlassian.migration.agent.service.check.ContextProviderUtil;
import com.atlassian.migration.agent.service.email.GlobalEmailFixesConfigService;
import com.atlassian.migration.agent.service.email.InvalidEmailUserService;
import com.atlassian.migration.agent.service.email.UserBaseScanRunner;
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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public class AsyncCheckExecutor
implements CheckExecutor,
JobRunner {
    private static final Logger log = ContextLoggerFactory.getLogger(AsyncCheckExecutor.class);
    private static final String EXECUTION_ID_KEY = "checkExecutionId";
    private static final String CHECK_TYPE_KEY = "checkType";
    private static final String INPUT_PARAMETER_PREFIX = "inputParameter-";
    private static final String SUCCESS = "Success";
    private static final String FAILED = "Failed";
    private static final String EXECUTION_FAILED = "ExecutionFailed";
    private static final Set<CheckType> INVALID_EMAILS_CHECK_TYPES = ImmutableSet.of((Object)CheckType.INVALID_EMAILS);
    private static final Set<CheckType> DUPLICATE_EMAILS_CHECK_TYPES = ImmutableSet.of((Object)CheckType.SHARED_EMAILS);
    private final SchedulerService schedulerService;
    private final CheckResultsService checkResultService;
    private final CheckRegistry checkerRegistry;
    private final PluginTransactionTemplate ptx;
    private final AnalyticsEventService analyticsEventService;
    private final FeatureFlagService featureFlagService;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;
    private final InvalidEmailUserService invalidEmailUserService;
    private final GlobalEmailFixesConfigService globalEmailFixesConfigService;
    private final UserBaseScanRunner userBaseScanRunner;

    public AsyncCheckExecutor(SchedulerService schedulerService, CheckResultsService checkResultService, CheckRegistry checkerRegistry, PluginTransactionTemplate ptx, AnalyticsEventService analyticsEventService, MigrationDarkFeaturesManager migrationDarkFeaturesManager, InvalidEmailUserService invalidEmailUserService, GlobalEmailFixesConfigService globalEmailFixesConfigService, FeatureFlagService featureFlagService, UserBaseScanRunner userBaseScanRunner) {
        this.schedulerService = schedulerService;
        this.checkResultService = checkResultService;
        this.checkerRegistry = checkerRegistry;
        this.ptx = ptx;
        this.analyticsEventService = analyticsEventService;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
        this.invalidEmailUserService = invalidEmailUserService;
        this.globalEmailFixesConfigService = globalEmailFixesConfigService;
        this.featureFlagService = featureFlagService;
        this.userBaseScanRunner = userBaseScanRunner;
    }

    private static String getExecutionIdFromJobParams(Map<String, Serializable> params) {
        return (String)((Object)params.get(EXECUTION_ID_KEY));
    }

    private static CheckType getCheckTypeFromJobParams(Map<String, Serializable> params) {
        return CheckType.fromString((String)((Object)params.get(CHECK_TYPE_KEY)));
    }

    private static Map<String, Object> getInputParamsFromJobParams(Map<String, Serializable> params) {
        return params.entrySet().stream().filter(entry -> ((String)entry.getKey()).startsWith(INPUT_PARAMETER_PREFIX)).collect(Collectors.toMap(entry -> StringUtils.substringAfter((String)((String)entry.getKey()), (String)INPUT_PARAMETER_PREFIX), Map.Entry::getValue));
    }

    private static Map<String, Serializable> generateJobParameters(String executionId, CheckType checkType, @Nullable Map<String, Object> inputParameters) {
        HashMap<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(EXECUTION_ID_KEY, (Serializable)((Object)executionId));
        params.put(CHECK_TYPE_KEY, (Serializable)((Object)checkType.value()));
        if (inputParameters != null) {
            inputParameters.forEach((key, value) -> params.put(INPUT_PARAMETER_PREFIX + key, (Serializable)value));
        }
        return params;
    }

    @PostConstruct
    public void postConstruct() {
        CheckType.getStaticCheckTypes().forEach(checkType -> this.schedulerService.registerJobRunner(JobRunnerKey.of((String)this.jobKey((CheckType)checkType)), (JobRunner)this));
    }

    @PreDestroy
    public void cleanup() {
        CheckType.getStaticCheckTypes().forEach(checkType -> this.schedulerService.unregisterJobRunner(JobRunnerKey.of((String)this.jobKey((CheckType)checkType))));
    }

    public Optional<com.atlassian.cmpt.check.base.CheckExecutionStatus> getStatus(String executionId) {
        List<CheckResultEntity> results = this.checkResultService.getByExecutionId(executionId);
        if (CollectionUtils.isEmpty(results)) {
            return Optional.empty();
        }
        List statuses = results.stream().map(this::convertToCheckStatus).collect(Collectors.toList());
        return Optional.of(new com.atlassian.cmpt.check.base.CheckExecutionStatus(executionId, statuses));
    }

    public Optional<com.atlassian.cmpt.check.base.CheckExecutionStatus> getStatus(String executionId, CheckType checkType) {
        Optional<CheckResultEntity> checkResult = this.checkResultService.getByExecutionIdAndCheckType(executionId, checkType);
        return checkResult.map(checkResultEntity -> new com.atlassian.cmpt.check.base.CheckExecutionStatus(executionId, (List)ImmutableList.of((Object)this.convertToCheckStatus((CheckResultEntity)checkResultEntity))));
    }

    public void executeChecks(String executionId, List<CheckRequest> checkRequests) {
        List<String> enabledMigrationFeatures = this.featureFlagService.getEnabledMigrationPluginFeatures();
        this.featureFlagService.saveFeatureFlagAnalyticEvent(FeatureFlagActionSubject.PREFLIGHT, executionId, enabledMigrationFeatures.toString());
        log.info("Enabled Migration plugin feature flags for executionId {} are {}", (Object)executionId, enabledMigrationFeatures);
        checkRequests.forEach(checkRequest -> this.scheduleCheck(executionId, (CheckRequest)checkRequest));
    }

    public JobRunnerResponse runJob(JobRunnerRequest jobRunnerRequest) {
        Instant startTime = Instant.now();
        Map jobParams = jobRunnerRequest.getJobConfig().getParameters();
        String executionId = AsyncCheckExecutor.getExecutionIdFromJobParams(jobParams);
        CheckType checkType = AsyncCheckExecutor.getCheckTypeFromJobParams(jobParams);
        try {
            CheckResult checkResult = this.executeChecker(jobParams, executionId);
            Optional<Integer> errorCodes = Optional.ofNullable(Checker.retrieveExecutionErrorCode((CheckResult)checkResult));
            log.info("Finishing execution {} of check {}. Success: {}", new Object[]{executionId, checkType.value(), checkResult.success});
            this.saveAnalyticsEventAfterCheckWasExecuted(startTime, checkType, checkResult, executionId, errorCodes.map(error -> FAILED).orElse(checkResult.success ? SUCCESS : FAILED));
            return JobRunnerResponse.success((String)"ok");
        }
        catch (Exception e) {
            log.error("Couldn't execute check of type {} with id {}", new Object[]{checkType.value(), executionId, e});
            CheckResult failedCheckResult = Checker.buildCheckResultWithExecutionError((int)ExecutionErrorCodes.GENERIC.getErrorCode());
            this.checkResultService.saveCheckResult(executionId, checkType, failedCheckResult);
            this.saveAnalyticsEventAfterCheckWasExecuted(startTime, checkType, failedCheckResult, executionId, EXECUTION_FAILED);
            return JobRunnerResponse.failed((Throwable)e);
        }
    }

    private void saveAnalyticsEventAfterCheckWasExecuted(Instant startTime, CheckType checkType, CheckResult checkResult, String executionId, String resultState) {
        long totalTime = ChronoUnit.MILLIS.between(startTime, Instant.now());
        Map<String, String> tags = this.createTags(checkType.value(), resultState);
        ImmutableList analyticsCheckEvents = ImmutableList.of((Object)this.checkerRegistry.getAnalyticsEventModel(checkType, checkResult, executionId, totalTime), (Object)((CounterMetricEvent.Builder)new CounterMetricEvent.Builder(MigrationMetric.PREFLIGHT_CHECK_METRIC.metricName).tags(tags)).build(), (Object)((TimerMetricEvent.Builder)new TimerMetricEvent.Builder(MigrationMetric.PREFLIGHT_CHECK_METRIC_TIMER.metricName, Long.valueOf(totalTime)).tags(tags)).build());
        this.analyticsEventService.saveAnalyticsEvents(() -> AsyncCheckExecutor.lambda$saveAnalyticsEventAfterCheckWasExecuted$8((List)analyticsCheckEvents));
    }

    private CheckResult executeChecker(Map<String, Serializable> jobParams, String executionId) {
        return LoggingContextBuilder.logCtx().withCheckExecutionId(executionId).execute(() -> {
            CheckType checkType = AsyncCheckExecutor.getCheckTypeFromJobParams(jobParams);
            log.info("Starting execution id: {} of check {}", (Object)executionId, (Object)checkType.value());
            Map<String, Object> inputParameters = AsyncCheckExecutor.getInputParamsFromJobParams(jobParams);
            CheckContextProvider<CheckContext> contextProvider = this.checkerRegistry.getCheckContextProvider(checkType);
            CheckContext checkContext = (CheckContext)contextProvider.apply((CheckContext)inputParameters);
            Checker<CheckContext> checker = this.checkerRegistry.getChecker(checkType);
            CheckResult checkResult = this.applyCheck(checkType, checker, checkContext);
            if (this.migrationDarkFeaturesManager.shouldHandleGlobalEmailFixes()) {
                checkResult = this.handleGlobalEmailFixes(checkType, inputParameters, checkResult);
            } else if (!this.migrationDarkFeaturesManager.shouldHandleGlobalEmailFixes() && this.migrationDarkFeaturesManager.shouldHandleInvalidAndDuplicateEmailUsers()) {
                checkResult = this.captureInvalidEmailsAndOverrideCheckResult(jobParams, checkResult);
            }
            this.checkResultService.saveCheckResult(executionId, checkType, checkResult);
            return checkResult;
        });
    }

    private CheckResult captureInvalidEmailsAndOverrideCheckResult(Map<String, Serializable> jobParams, CheckResult checkResult) {
        List emails;
        CheckType checkType = AsyncCheckExecutor.getCheckTypeFromJobParams(jobParams);
        if (INVALID_EMAILS_CHECK_TYPES.contains(checkType) && !(emails = EmailFormatChecker.retrieveInvalidEmails((Map)checkResult.details)).isEmpty()) {
            this.invalidEmailUserService.saveInvalidEmailUsers(emails);
            return new CheckResult(true);
        }
        return checkResult;
    }

    private CheckResult handleGlobalEmailFixes(CheckType checkType, Map<String, Object> params, CheckResult checkResult) {
        if (INVALID_EMAILS_CHECK_TYPES.contains(checkType)) {
            return this.handleInvalidEmailsCheck(params, checkResult);
        }
        if (DUPLICATE_EMAILS_CHECK_TYPES.contains(checkType)) {
            return this.handleDuplicateEmailsCheck(params, checkResult);
        }
        return checkResult;
    }

    @NotNull
    private CheckResult handleDuplicateEmailsCheck(Map<String, Object> params, CheckResult checkResult) {
        DuplicateEmailsConfigDto duplicateEmailsConfig = this.globalEmailFixesConfigService.getDuplicateEmailsConfig();
        if (!checkResult.success) {
            if (!this.migrationDarkFeaturesManager.isGlobalEmailFixesNewEmailsFromDbEnabled()) {
                this.userBaseScanRunner.startUserBaseScan(ContextProviderUtil.getCloudId(params));
            }
            switch (duplicateEmailsConfig.getActionOnMigration()) {
                case DO_NOTHING: {
                    return new CheckResult(false, checkResult.details);
                }
                case MERGE_ALL: 
                case USE_NEW_EMAILS: {
                    return new CheckResult(true, checkResult.details);
                }
            }
            throw new IllegalStateException("Unexpected value: " + (Object)((Object)duplicateEmailsConfig.getActionOnMigration()));
        }
        return new CheckResult(true, checkResult.details);
    }

    @NotNull
    private CheckResult handleInvalidEmailsCheck(Map<String, Object> params, CheckResult checkResult) {
        InvalidEmailsConfigDto invalidEmailsConfig = this.globalEmailFixesConfigService.getInvalidEmailsConfig();
        if (!checkResult.success) {
            if (!this.migrationDarkFeaturesManager.isGlobalEmailFixesNewEmailsFromDbEnabled()) {
                this.userBaseScanRunner.startUserBaseScan(ContextProviderUtil.getCloudId(params));
            }
            switch (invalidEmailsConfig.getActionOnMigration()) {
                case DO_NOTHING: {
                    return new CheckResult(false, checkResult.details);
                }
                case TOMBSTONE_ALL: 
                case USE_NEW_EMAILS: {
                    return new CheckResult(true, checkResult.details);
                }
            }
            throw new IllegalStateException("Unexpected value: " + (Object)((Object)invalidEmailsConfig.getActionOnMigration()));
        }
        return new CheckResult(true, checkResult.details);
    }

    private CheckResult applyCheck(CheckType checkType, Checker<CheckContext> checker, CheckContext checkContext) {
        if (CheckType.SHARED_EMAILS.equals(checkType) && !this.migrationDarkFeaturesManager.shouldHandleGlobalEmailFixes() && this.migrationDarkFeaturesManager.shouldHandleInvalidAndDuplicateEmailUsers()) {
            log.info("Shared emails check has been disabled. Execution of check {} did not run and returning success.", (Object)checkType);
            return new CheckResult(true);
        }
        if (CheckType.MISSING_ATTACHMENTS.equals(checkType) && this.migrationDarkFeaturesManager.missingAttachmentsCheckDisabled()) {
            log.info("Missing attachments check has been disabled. Execution of check {} did not run and returning success.", (Object)checkType);
            return new CheckResult(true);
        }
        if (CheckType.APP_OUTDATED.equals(checkType) && this.migrationDarkFeaturesManager.appOutdatedCheckDisabled()) {
            log.info("App Outdated check has been disabled. Execution of check {} did not run and returning success.", (Object)checkType);
            return new CheckResult(true);
        }
        if (CheckType.NETWORK_HEALTH.equals(checkType) && this.migrationDarkFeaturesManager.networkHealthCheckDisabled()) {
            log.info("Network Health check has been disabled. Execution of check {} did not run and returning success.", (Object)checkType);
            return new CheckResult(true);
        }
        return checker.check(checkContext);
    }

    private void scheduleCheck(String executionId, CheckRequest request) {
        log.info("Scheduled check executionId: {} check: {}", (Object)executionId, (Object)request.checkType);
        CheckType checkType = CheckType.fromString(request.checkType);
        boolean isRunning = this.ptx.write(() -> {
            CheckResultEntity checkResultEntity = this.checkResultService.getOrCreate(executionId, checkType);
            if (checkResultEntity.getStatus() == CheckExecutionStatus.RUNNING) {
                log.info("Skipping check for executionID = {} and checkType = {} because status already is RUNNING.", (Object)executionId, (Object)checkType);
                return true;
            }
            this.checkResultService.updateStatusToRunning(checkResultEntity);
            return false;
        });
        if (!isRunning) {
            this.scheduleAsyncJob(executionId, checkType, request);
        }
    }

    private void scheduleAsyncJob(String executionId, CheckType checkType, CheckRequest request) {
        try {
            Map<String, Serializable> jobParams = AsyncCheckExecutor.generateJobParameters(executionId, checkType, request.parameters);
            JobConfig jobConfig = JobConfig.forJobRunnerKey((JobRunnerKey)JobRunnerKey.of((String)("migration-plugin:checks-runner." + checkType.value()))).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(Schedule.runOnce(null)).withParameters(jobParams);
            String jobId = executionId + checkType.value();
            this.schedulerService.scheduleJob(JobId.of((String)jobId), jobConfig);
            log.info("Scheduled check executionId: {} checkType: {}, jobId = {}.", new Object[]{executionId, checkType, jobId});
        }
        catch (SchedulerServiceException e) {
            throw new RuntimeException(String.format("Failed to schedule check %s", checkType), e);
        }
    }

    private CheckStatus convertToCheckStatus(CheckResultEntity entity) {
        CheckResult result = entity.getStatus() == CheckExecutionStatus.RUNNING ? null : (CheckResult)this.checkResultService.getCheckResult(entity).orElse(null);
        Long lastExecutionTime = entity.getLastExecutionTime() != null ? Long.valueOf(entity.getLastExecutionTime().toEpochMilli()) : null;
        return new CheckStatus(entity.getCheckType(), result, lastExecutionTime);
    }

    private String jobKey(CheckType checkType) {
        return "migration-plugin:checks-runner." + checkType.value();
    }

    private Map<String, String> createTags(String checkType, String resultState) {
        HashMap<String, String> tags = new HashMap<String, String>();
        tags.put("result", resultState);
        tags.put("product", "confluence");
        tags.put("checktype", checkType);
        return tags;
    }

    public void unscheduleCheckJobs(String executionId) {
        CheckType.getStaticCheckTypes().forEach(checkType -> {
            String jobId = executionId + checkType.value();
            this.schedulerService.unscheduleJob(JobId.of((String)jobId));
            log.info("unscheduled check job for executionId: {}, checkType: {}, jobId: {}", new Object[]{executionId, checkType, jobId});
        });
    }

    public void executeNonOverriddenChecks(String executionId, List<String> preflightChecksToOverride, List<CheckRequest> checkRequests) {
        if (!CollectionUtils.isEmpty(preflightChecksToOverride)) {
            preflightChecksToOverride.forEach(overridden -> {
                checkRequests.removeIf(it -> it.checkType.equals(overridden));
                log.info("Preflight check {} excluded from check execution because it is being overridden", overridden);
            });
        }
        this.executeChecks(executionId, checkRequests);
    }

    private static /* synthetic */ Collection lambda$saveAnalyticsEventAfterCheckWasExecuted$8(List analyticsCheckEvents) {
        return analyticsCheckEvents;
    }
}

