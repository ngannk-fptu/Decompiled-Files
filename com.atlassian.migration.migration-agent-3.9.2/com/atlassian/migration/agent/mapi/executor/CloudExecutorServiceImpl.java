/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.dto.CheckResultDto
 *  com.atlassian.cmpt.check.dto.Status
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 *  org.apache.commons.collections.CollectionUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.mapi.executor;

import com.atlassian.cmpt.check.dto.CheckResultDto;
import com.atlassian.cmpt.check.dto.Status;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.migration.agent.dto.CloudSiteDto;
import com.atlassian.migration.agent.dto.CloudType;
import com.atlassian.migration.agent.dto.ConfluenceSpaceTaskDto;
import com.atlassian.migration.agent.dto.MigrateAppsTaskDto;
import com.atlassian.migration.agent.dto.MigrateGlobalEntitiesTaskDto;
import com.atlassian.migration.agent.dto.MigrateUsersTaskDto;
import com.atlassian.migration.agent.dto.PlanDto;
import com.atlassian.migration.agent.dto.SpaceAttachmentsTaskDto;
import com.atlassian.migration.agent.dto.TaskDto;
import com.atlassian.migration.agent.dto.assessment.AppAssessmentUpdateRequest;
import com.atlassian.migration.agent.entity.AppAssessmentProperty;
import com.atlassian.migration.agent.entity.AppAssessmentUserAttributedStatus;
import com.atlassian.migration.agent.entity.GlobalEntityType;
import com.atlassian.migration.agent.entity.MapiPlanMapping;
import com.atlassian.migration.agent.entity.MapiTaskMapping;
import com.atlassian.migration.agent.entity.MigrationTag;
import com.atlassian.migration.agent.entity.PlanActiveStatus;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.mapi.MigrationCreator;
import com.atlassian.migration.agent.mapi.entity.MapiOutcome;
import com.atlassian.migration.agent.mapi.entity.MapiStatus;
import com.atlassian.migration.agent.mapi.entity.MapiStatusDto;
import com.atlassian.migration.agent.mapi.entity.MapiTaskStatus;
import com.atlassian.migration.agent.mapi.executor.CloudExecutorService;
import com.atlassian.migration.agent.mapi.external.MapiMigrationService;
import com.atlassian.migration.agent.mapi.external.model.JobValidationException;
import com.atlassian.migration.agent.mapi.external.model.PublicApiException;
import com.atlassian.migration.agent.mapi.job.JobDefinition;
import com.atlassian.migration.agent.mapi.job.JobValidationService;
import com.atlassian.migration.agent.mapi.job.scope.MapiGlobalEntitiesType;
import com.atlassian.migration.agent.mapi.job.scope.ScopeMode;
import com.atlassian.migration.agent.mapi.job.scope.SpaceMode;
import com.atlassian.migration.agent.service.MigrationMetric;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.app.PluginManager;
import com.atlassian.migration.agent.service.check.PreflightService;
import com.atlassian.migration.agent.service.impl.AppAssessmentFacade;
import com.atlassian.migration.agent.service.impl.MapiPlanMappingService;
import com.atlassian.migration.agent.service.impl.MapiTaskMappingService;
import com.atlassian.migration.agent.service.impl.PlanDecoratorService;
import com.atlassian.migration.agent.service.prc.PrcPollerMetadataCache;
import com.atlassian.migration.agent.service.prc.model.CommandName;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class CloudExecutorServiceImpl
implements CloudExecutorService {
    private final PlanDecoratorService planDecoratorService;
    private final MapiMigrationService mapiMigrationService;
    private final JobValidationService jobValidationService;
    private final AnalyticsEventService analyticsEventService;
    private final AnalyticsEventBuilder analyticsEventBuilder;
    private final AppAssessmentFacade appAssessmentFacade;
    private final MapiPlanMappingService mapiPlanMappingService;
    private final MapiTaskMappingService mapiTaskMappingService;
    private final SpaceManager spaceManager;
    private final PluginManager pluginManager;
    private final PreflightService preflightService;
    private PrcPollerMetadataCache prcPollerMetadataCache;
    private final Supplier<Instant> instantSupplier;
    private static final Logger log = ContextLoggerFactory.getLogger(CloudExecutorServiceImpl.class);
    private static final String INVALID_APPS_EXCEPTION = "Migration includes invalid app keys = %s. These apps do not exist in the server. Please remove these apps from the job definition and try again.";

    public CloudExecutorServiceImpl(PlanDecoratorService planDecoratorService, MapiMigrationService mapiMigrationService, JobValidationService jobValidationService, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, AppAssessmentFacade appAssessmentFacade, MapiPlanMappingService mapiPlanMappingService, MapiTaskMappingService mapiTaskMappingService, SpaceManager spaceManager, PluginManager pluginManager, PreflightService preflightService, PrcPollerMetadataCache prcPollerMetadataCache) {
        this.planDecoratorService = planDecoratorService;
        this.mapiMigrationService = mapiMigrationService;
        this.jobValidationService = jobValidationService;
        this.analyticsEventService = analyticsEventService;
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.appAssessmentFacade = appAssessmentFacade;
        this.mapiPlanMappingService = mapiPlanMappingService;
        this.mapiTaskMappingService = mapiTaskMappingService;
        this.spaceManager = spaceManager;
        this.pluginManager = pluginManager;
        this.preflightService = preflightService;
        this.prcPollerMetadataCache = prcPollerMetadataCache;
        this.instantSupplier = Instant::now;
    }

    @Override
    public PlanDto createPlan(String jobId, Optional<String> cloudId) {
        if (this.mapiPlanMappingService.getMapiPlanMapping(jobId).isPresent()) {
            log.info("Plan already created for the jobId: {}", (Object)jobId);
            throw new PublicApiException.DuplicateRequestException(jobId);
        }
        PlanDto planDto = this.planDecoratorService.createPlan(this.buildPlanDto(jobId, cloudId));
        MapiPlanMapping mapiPlanMapping = new MapiPlanMapping(jobId, planDto.getId(), null);
        this.mapiPlanMappingService.saveMapiPlanMapping(mapiPlanMapping);
        log.info("Plan created for the jobId: {} planId: {} ", (Object)jobId, (Object)planDto.getId());
        return planDto;
    }

    @Override
    public void sendCreatePlanAnalyticsEvents(PlanDto planDto, ConfluenceUser confluenceUser, Optional<String> jobId) {
        this.analyticsEventService.sendAnalyticsEventsAsync(() -> this.analyticsEventBuilder.buildCreatePlanAndTasksAnalyticsEvents(planDto, confluenceUser, jobId));
    }

    @Override
    public void executePreflightChecks(String jobId, String taskId, String cloudId) {
        long startTime = this.instantSupplier.get().toEpochMilli();
        log.info("Executing checks for the jobId: {}", (Object)jobId);
        PlanDto planDto = null;
        String commandName = CommandName.CHECK.getName();
        int statusCode = 200;
        String errorReason = "";
        try {
            planDto = this.getOrCreatePlanDto(jobId, cloudId);
            if (this.checkForInvalidTaskExecution(jobId, taskId, planDto, commandName)) {
                return;
            }
            List<CheckResultDto> results = this.preflightService.getCheckExecutionStatus(planDto.getId());
            if (results.stream().anyMatch(checkResultDto -> checkResultDto.getStatus().equals((Object)Status.RUNNING))) {
                log.info("Checks already running for the jobId: {}", (Object)jobId);
                throw new PublicApiException.DuplicateRequestException("Checks already running for given jobId = %s", jobId);
            }
            this.preflightService.executeChecks(planDto.getId(), planDto, Collections.emptySet());
            this.saveMapiTaskMapping(taskId, jobId, planDto, cloudId, MapiTaskStatus.CHECKS_IN_PROGRESS, commandName);
            this.updateTaskStatusInMapi(commandName, MapiStatus.IN_PROGRESS, null, taskId, jobId, cloudId, "Preflight checks are running");
        }
        catch (Exception ex) {
            errorReason = ex.getMessage();
            statusCode = PublicApiException.getPublicApiErrorCode(ex);
            log.error("Error executing preflight checks for the jobId: {} and taskId: {}", new Object[]{jobId, taskId, ex});
            this.saveMapiTaskMapping(taskId, jobId, planDto, cloudId, MapiTaskStatus.FAILED, commandName);
            this.updateTaskStatusInMapi(commandName, MapiStatus.FINISHED, MapiOutcome.FAILED, taskId, jobId, cloudId, errorReason);
            throw ex;
        }
        finally {
            long totalTime = this.instantSupplier.get().toEpochMilli() - startTime;
            this.sendMapiJobAnalyticsEvents(jobId, planDto, statusCode, errorReason, totalTime, commandName, MigrationMetric.MAPI_EXECUTE_CHECKS_JOB_TIMER_METRIC_EVENT_NAME.metricName);
        }
    }

    @Override
    public void executeMigration(String jobId, String taskId, String cloudId) {
        long startTime = this.instantSupplier.get().toEpochMilli();
        log.info("Executing checks and migrate for the jobId: {}", (Object)jobId);
        PlanDto planDto = null;
        String commandName = CommandName.MIGRATE.getName();
        int statusCode = 200;
        String errorReason = "";
        try {
            planDto = this.getOrCreatePlanDto(jobId, cloudId);
            String planId = planDto.getId();
            if (this.checkForInvalidTaskExecution(jobId, taskId, planDto, commandName)) {
                return;
            }
            this.planDecoratorService.verifyAndStart(planId);
            this.saveMapiTaskMapping(taskId, jobId, planDto, cloudId, MapiTaskStatus.CHECKS_IN_PROGRESS, commandName);
            this.updateTaskStatusInMapi(commandName, MapiStatus.IN_PROGRESS, null, taskId, jobId, cloudId, "Preflight checks are running");
        }
        catch (Exception ex) {
            errorReason = ex.getMessage();
            statusCode = PublicApiException.getPublicApiErrorCode(ex);
            log.error("Error executing migration for the jobId: {} and taskId: {}", (Object)jobId, (Object)ex);
            this.saveMapiTaskMapping(taskId, jobId, planDto, cloudId, MapiTaskStatus.FAILED, commandName);
            this.updateTaskStatusInMapi(commandName, MapiStatus.FINISHED, MapiOutcome.FAILED, taskId, jobId, cloudId, errorReason);
            throw ex;
        }
        finally {
            long totalTime = this.instantSupplier.get().toEpochMilli() - startTime;
            this.sendMapiJobAnalyticsEvents(jobId, planDto, statusCode, errorReason, totalTime, commandName, MigrationMetric.MAPI_EXECUTE_CHECKS_AND_MIGRATE_JOB_TIMER_METRIC_EVENT_NAME.metricName);
        }
    }

    private boolean checkForInvalidTaskExecution(String jobId, String taskId, PlanDto planDto, String commandName) {
        Optional<MapiPlanMapping> mapiPlanMapping = this.mapiPlanMappingService.getMapiPlanMapping(jobId);
        if (mapiPlanMapping.isPresent() && mapiPlanMapping.get().getMigrationId() != null) {
            log.info("Task Not Allowed, migration has already been processed for JobId = {}", (Object)jobId);
            throw new PublicApiException(String.format("Task Not Allowed, migration has already been processed for JobId = %s", jobId));
        }
        ImmutableList statusesToCheck = ImmutableList.of((Object)((Object)MapiTaskStatus.CHECKS_IN_PROGRESS));
        Optional<MapiTaskMapping> mapiTaskMapping = this.mapiTaskMappingService.getTaskMapping(planDto.getId(), Optional.of(statusesToCheck), Optional.empty());
        if (mapiTaskMapping.isPresent()) {
            if (mapiTaskMapping.get().getTaskId().equals(taskId)) {
                log.info("Task \"{}\" already running for the jobId: {} with same taskId : {}", new Object[]{commandName, mapiTaskMapping.get().getJobId(), mapiTaskMapping.get().getTaskId()});
                return true;
            }
            log.info("Task \"{}\" already processed for the jobId: {} with another taskId : {}", new Object[]{commandName, mapiTaskMapping.get().getJobId(), mapiTaskMapping.get().getTaskId()});
            throw new PublicApiException(String.format("Task already running for given jobId = %s with taskId = %s", mapiTaskMapping.get().getJobId(), mapiTaskMapping.get().getTaskId()));
        }
        return false;
    }

    private void saveMapiTaskMapping(String taskId, String jobId, PlanDto planDto, String cloudId, MapiTaskStatus status, String commandName) {
        MapiTaskMapping mapiTaskMapping = new MapiTaskMapping(taskId, jobId, planDto == null ? null : planDto.getId(), cloudId, status.name(), commandName, Instant.now());
        this.mapiTaskMappingService.createTaskMapping(mapiTaskMapping);
    }

    private void updateTaskStatusInMapi(String commandName, MapiStatus status, MapiOutcome outcome, String taskId, String jobId, String cloudId, String errorReason) {
        try {
            ArrayList<MapiStatusDto> mapiStatusDtoList = new ArrayList<MapiStatusDto>();
            ImmutableList level = Collections.emptyList();
            if (commandName.equals(CommandName.CHECK.getName())) {
                mapiStatusDtoList.add(this.getMapiStatusDto((List<String>)level, status, outcome, errorReason));
            } else if (commandName.equals(CommandName.MIGRATE.getName())) {
                mapiStatusDtoList.add(this.getMapiStatusDto((List<String>)level, status, outcome, errorReason));
                if (status.equals((Object)MapiStatus.IN_PROGRESS)) {
                    level = ImmutableList.of((Object)CommandName.CHECK.getName());
                    mapiStatusDtoList.add(this.getMapiStatusDto((List<String>)level, status, outcome, errorReason));
                }
            }
            this.mapiMigrationService.sendTaskStatus(jobId, taskId, cloudId, mapiStatusDtoList);
        }
        catch (Exception e) {
            log.error("Error while sending status to MAPI for task: " + taskId, (Throwable)e);
        }
    }

    private MapiStatusDto getMapiStatusDto(List<String> level, MapiStatus status, MapiOutcome outcome, String errorReason) {
        return new MapiStatusDto(level, status, outcome, errorReason, null);
    }

    @VisibleForTesting
    public PlanDto buildPlanDto(String jobId, Optional<String> cloudId) {
        log.info("Plan creation started for the jobId: {}", (Object)jobId);
        JobDefinition jobDefinition = this.mapiMigrationService.getMigrationJobDefinition(jobId, cloudId);
        this.jobValidationService.validateJobDefinition(jobDefinition);
        List<TaskDto> taskDtos = this.populatePlanTasks(jobDefinition);
        log.info("Tasks created for the jobId: {} taskDtos: {}", (Object)jobId, taskDtos);
        return new PlanDto(null, UUID.randomUUID().toString(), jobDefinition.getName(), Instant.now(), Instant.now(), new CloudSiteDto(jobDefinition.getDestination().getUrl(), jobDefinition.getDestination().getCloudId(), null, CloudType.STANDARD), taskDtos, null, null, PlanActiveStatus.ACTIVE, null, MigrationTag.NOT_SPECIFIED, MigrationCreator.MAPI);
    }

    private List<String> getIncludedSpaceKeysForPlan(List<String> providedSpaceKeys) {
        Map<String, String> allSpaceKeysMap = this.spaceManager.getAllSpaces().stream().map(Space::getKey).collect(Collectors.toMap(StringUtils::lowerCase, spaceKey -> spaceKey));
        return providedSpaceKeys.stream().map(spaceKey -> allSpaceKeysMap.getOrDefault(StringUtils.lowerCase((String)spaceKey), (String)spaceKey)).collect(Collectors.toList());
    }

    private AppAssessmentUpdateRequest buildAppAssessmentUpdateRequest(String appKey) {
        return new AppAssessmentUpdateRequest(appKey, AppAssessmentProperty.MIGRATION_STATUS.getName(), AppAssessmentUserAttributedStatus.Needed.name());
    }

    @VisibleForTesting
    public List<TaskDto> populatePlanTasks(JobDefinition mapiJobDefinition) {
        LinkedList<TaskDto> taskDtos = new LinkedList<TaskDto>();
        boolean isAttachmentOnly = mapiJobDefinition.getScope().getSpaces().getIncludedData() == SpaceMode.ATTACHMENTS;
        List<String> includedSpaceKeys = this.getIncludedSpaceKeysForPlan(mapiJobDefinition.getScope().getSpaces().getIncludedKeys());
        includedSpaceKeys.forEach(spaceKey -> {
            if (isAttachmentOnly) {
                taskDtos.add(new SpaceAttachmentsTaskDto(null, null, (String)spaceKey, null, 0L, null));
            } else {
                taskDtos.add(new ConfluenceSpaceTaskDto(null, null, (String)spaceKey, null, 0L, null));
            }
        });
        if (!isAttachmentOnly) {
            List<MapiGlobalEntitiesType> mapiGlobalEntitiesTypes;
            boolean isScoped = mapiJobDefinition.getScope().getUsersAndGroups().getMode() == ScopeMode.REFERENCED;
            taskDtos.add(new MigrateUsersTaskDto(null, null, 0L, null, isScoped));
            if (mapiJobDefinition.getScope().getGlobalEntities() != null && !CollectionUtils.isEmpty(mapiGlobalEntitiesTypes = mapiJobDefinition.getScope().getGlobalEntities().getIncludedTypes())) {
                taskDtos.add(this.getGlobalEntitiesTask(mapiGlobalEntitiesTypes));
            }
            if (mapiJobDefinition.getScope().getApps() != null && !CollectionUtils.isEmpty(mapiJobDefinition.getScope().getApps().getIncludedKeys())) {
                Set providedAppKeys = mapiJobDefinition.getScope().getApps().getIncludedKeys().stream().map(StringUtils::lowerCase).collect(Collectors.toSet());
                Set actuallyInstalledAppKeys = this.pluginManager.getActualUserInstalledPlugins().stream().map(plugin -> StringUtils.lowerCase((String)plugin.getKey())).collect(Collectors.toSet());
                HashSet<String> appKeysTobeExcluded = new HashSet<String>();
                appKeysTobeExcluded.addAll(providedAppKeys.stream().filter(appKey -> !actuallyInstalledAppKeys.contains(appKey)).collect(Collectors.toSet()));
                if (!appKeysTobeExcluded.isEmpty()) {
                    throw new JobValidationException(String.format(INVALID_APPS_EXCEPTION, appKeysTobeExcluded));
                }
                Set<String> appKeysTobeIncluded = actuallyInstalledAppKeys.stream().filter(appKey -> {
                    if (providedAppKeys.contains(appKey)) {
                        AppAssessmentUpdateRequest appAssessmentUpdateRequest = this.buildAppAssessmentUpdateRequest((String)appKey);
                        this.appAssessmentFacade.updateAppAssessmentInfo((String)appKey, appAssessmentUpdateRequest);
                        return true;
                    }
                    appKeysTobeExcluded.add((String)appKey);
                    return false;
                }).collect(Collectors.toSet());
                log.info("App keys that are excluded from the plan: {}", appKeysTobeExcluded);
                MigrateAppsTaskDto migrateAppsTaskDto = new MigrateAppsTaskDto(null, null, appKeysTobeExcluded, null);
                migrateAppsTaskDto.setNeededInCloudApps(appKeysTobeIncluded);
                taskDtos.add(migrateAppsTaskDto);
            }
        }
        return taskDtos;
    }

    private MigrateGlobalEntitiesTaskDto getGlobalEntitiesTask(List<MapiGlobalEntitiesType> mapiGlobalEntitiesTypes) {
        GlobalEntityType globalEntityType = GlobalEntityType.GLOBAL_SYSTEM_TEMPLATES;
        if (mapiGlobalEntitiesTypes.size() == 1) {
            globalEntityType = mapiGlobalEntitiesTypes.get(0).equals((Object)MapiGlobalEntitiesType.GLOBAL_PAGE_TEMPLATES) ? GlobalEntityType.GLOBAL_TEMPLATES : GlobalEntityType.SYSTEM_TEMPLATES;
        }
        return new MigrateGlobalEntitiesTaskDto(null, null, 0L, null, globalEntityType, 0L, 0L);
    }

    private PlanDto getOrCreatePlanDto(String jobId, String cloudId) {
        Optional<MapiPlanMapping> mapiPlanMapping = this.mapiPlanMappingService.getMapiPlanMapping(jobId);
        PlanDto planDto = null;
        if (mapiPlanMapping.isPresent()) {
            planDto = this.planDecoratorService.getPlanDto(mapiPlanMapping.get().getPlanId());
            this.setUserContext(planDto.getCloudSite().getCloudUrl());
        } else {
            try {
                planDto = this.createPlan(jobId, Optional.of(cloudId));
                this.setUserContext(planDto.getCloudSite().getCloudUrl());
                this.sendCreatePlanAnalyticsEvents(planDto, AuthenticatedUserThreadLocal.get(), Optional.of(jobId));
            }
            catch (PublicApiException.DuplicateRequestException ex) {
                mapiPlanMapping = this.mapiPlanMappingService.getMapiPlanMapping(jobId);
                assert (mapiPlanMapping.isPresent());
                planDto = this.planDecoratorService.getPlanDto(mapiPlanMapping.get().getPlanId());
            }
            catch (Exception ex) {
                log.error("Error while creating plan for the jobId: {}", (Object)jobId, (Object)ex);
                throw ex;
            }
        }
        return planDto;
    }

    private void setUserContext(String cloudUrl) {
        ConfluenceUser confluenceUser = this.prcPollerMetadataCache.getPrcPollerUserContext(cloudUrl);
        if (confluenceUser != null) {
            AuthenticatedUserThreadLocal.set((ConfluenceUser)confluenceUser);
        } else {
            log.error("Unable to set prc poller user context for cloudUrl: {}", (Object)cloudUrl);
        }
    }

    @Override
    public void sendMapiJobAnalyticsEvents(String jobId, @Nullable PlanDto planDto, int statusCode, @Nullable String errorReason, long totalTime, String operationalEventActionName, String timerEventName) {
        ImmutableList events = ImmutableList.of((Object)this.analyticsEventBuilder.buildMapiJobOperationalEvent(jobId, Optional.ofNullable(planDto), statusCode, errorReason, totalTime, operationalEventActionName), (Object)this.analyticsEventBuilder.buildMapiJobTimerMetricEvent(statusCode, totalTime, timerEventName));
        this.analyticsEventService.sendAnalyticsEventsAsync(() -> CloudExecutorServiceImpl.lambda$sendMapiJobAnalyticsEvents$8((List)events));
    }

    private static /* synthetic */ Collection lambda$sendMapiJobAnalyticsEvents$8(List events) {
        return events;
    }
}

