/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.persistence.NoResultException
 *  lombok.Generated
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.dto.AppDto;
import com.atlassian.migration.agent.dto.AppsProgressDto;
import com.atlassian.migration.agent.dto.ConfluenceSpaceTaskDto;
import com.atlassian.migration.agent.dto.MigrateGlobalEntitiesTaskDto;
import com.atlassian.migration.agent.dto.PlanDto;
import com.atlassian.migration.agent.dto.ProgressDto;
import com.atlassian.migration.agent.dto.RequestValidationException;
import com.atlassian.migration.agent.dto.TaskDto;
import com.atlassian.migration.agent.entity.AbstractSpaceTask;
import com.atlassian.migration.agent.entity.CheckExecutionStatus;
import com.atlassian.migration.agent.entity.CheckResultEntity;
import com.atlassian.migration.agent.entity.ExecutionStatus;
import com.atlassian.migration.agent.entity.MigrateAppsTask;
import com.atlassian.migration.agent.entity.MigrateGlobalEntitiesTask;
import com.atlassian.migration.agent.entity.MigrateUsersTask;
import com.atlassian.migration.agent.entity.NeededInCloudApp;
import com.atlassian.migration.agent.entity.Plan;
import com.atlassian.migration.agent.entity.PlanActiveStatus;
import com.atlassian.migration.agent.entity.PlanSchedulerVersion;
import com.atlassian.migration.agent.entity.Progress;
import com.atlassian.migration.agent.entity.Task;
import com.atlassian.migration.agent.entity.TaskType;
import com.atlassian.migration.agent.service.PlanService;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.migration.agent.service.analytics.AppAssessmentAnalyticsEventService;
import com.atlassian.migration.agent.service.catalogue.MigrationDetails;
import com.atlassian.migration.agent.service.catalogue.PlatformService;
import com.atlassian.migration.agent.service.catalogue.model.ConfluenceSpaceContainer;
import com.atlassian.migration.agent.service.catalogue.model.SiteContainer;
import com.atlassian.migration.agent.service.check.CheckOverrideService;
import com.atlassian.migration.agent.service.check.CheckResultsService;
import com.atlassian.migration.agent.service.execution.PlanExecutionService;
import com.atlassian.migration.agent.service.impl.AppAssessmentFacade;
import com.atlassian.migration.agent.service.impl.InvalidPlanException;
import com.atlassian.migration.agent.service.impl.PlanConverter;
import com.atlassian.migration.agent.service.stepexecutor.ProgressTracker;
import com.atlassian.migration.agent.store.PlanStore;
import com.atlassian.migration.agent.store.TaskStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.persistence.NoResultException;
import lombok.Generated;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class DefaultPlanService
implements PlanService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(DefaultPlanService.class);
    private final PluginTransactionTemplate ptx;
    private final PlanStore planStore;
    private final TaskStore taskStore;
    private final AnalyticsEventService analyticsEventService;
    private final AnalyticsEventBuilder analyticsEventBuilder;
    private final PlanConverter planConverter;
    private final CheckResultsService checkResultsService;
    private final AppAssessmentAnalyticsEventService appAssessmentAnalyticsEventService;
    private final PlatformService platformService;
    private final CheckOverrideService checkOverrideService;
    private final MigrationDarkFeaturesManager darkFeaturesManager;
    private final AppAssessmentFacade appAssessmentFacade;
    private final ProgressTracker progressTracker;
    private final PlanExecutionService planExecutionService;
    private static final String COPIED_PLAN_PREFIX = "Copied - ";
    private static final Pattern COPIED_PLAN_SUFFIX = Pattern.compile("_(\\d+$)");
    private static final int MAX_PLAN_NAME_LENGTH = 255;

    public DefaultPlanService(PluginTransactionTemplate ptx, PlanStore planStore, TaskStore taskStore, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder, PlanConverter planConverter, CheckResultsService checkResultsService, AppAssessmentAnalyticsEventService appAssessmentAnalyticsEventService, PlatformService platformService, CheckOverrideService checkOverrideService, MigrationDarkFeaturesManager darkFeaturesManager, AppAssessmentFacade appAssessmentFacade, ProgressTracker progressTracker, PlanExecutionService planExecutionService) {
        this.ptx = ptx;
        this.planStore = planStore;
        this.taskStore = taskStore;
        this.analyticsEventService = analyticsEventService;
        this.analyticsEventBuilder = analyticsEventBuilder;
        this.planConverter = planConverter;
        this.checkResultsService = checkResultsService;
        this.appAssessmentAnalyticsEventService = appAssessmentAnalyticsEventService;
        this.platformService = platformService;
        this.checkOverrideService = checkOverrideService;
        this.darkFeaturesManager = darkFeaturesManager;
        this.appAssessmentFacade = appAssessmentFacade;
        this.progressTracker = progressTracker;
        this.planExecutionService = planExecutionService;
    }

    public static Optional<MigrateAppsTask> getMigrateAppsTask(Plan plan) {
        return plan.getTasks().stream().filter(MigrateAppsTask.class::isInstance).findFirst().map(MigrateAppsTask.class::cast);
    }

    @Override
    @Nonnull
    public List<PlanDto> getAllPlans() {
        ImmutableSet validStatuses = ImmutableSet.of((Object)((Object)PlanActiveStatus.ACTIVE), (Object)((Object)PlanActiveStatus.ARCHIVED));
        return this.ptx.read(() -> this.lambda$getAllPlans$0((Set)validStatuses));
    }

    @Override
    public PlanDto getPlan(String planId) {
        return this.ptx.read(() -> this.planConverter.entityToDto(this.getPlanById(planId), true));
    }

    @Override
    public boolean deletePlan(String planId) {
        return this.ptx.write(() -> {
            boolean deleted = false;
            try {
                Plan plan = this.planStore.getPlanAndLock(planId);
                if (ProgressDto.Status.READY.equals((Object)this.getPlanProgress(planId).getStatus())) {
                    this.planStore.removeTasks(plan);
                    this.planStore.deletePlan(planId);
                    deleted = true;
                }
            }
            catch (Exception ex) {
                log.warn("Failed to delete planId {} due to the following exception. {}", (Object)planId, (Object)ex);
                throw new IllegalStateException("Exception while deleting the planId" + planId, ex);
            }
            return deleted;
        });
    }

    @Override
    public boolean planNameExists(String planName, String planId) {
        return this.ptx.read(() -> this.planStore.planNameExists(planName, planId));
    }

    @Override
    public ProgressDto getPlanProgress(String planId) {
        return this.ptx.read(() -> ProgressDto.fromPlanEntity(this.getPlanById(planId).getProgress()));
    }

    @Override
    public Optional<AppsProgressDto> getAppsProgress(String planId) {
        return this.ptx.read(() -> {
            Plan plan = this.getPlanById(planId);
            return this.platformService.getAppsProgress(plan);
        });
    }

    @Override
    @Nonnull
    public PlanDto createPlan(PlanDto planDto) {
        return this.createPlan(planDto, false);
    }

    @Override
    @Nonnull
    public PlanDto createPlan(PlanDto planDto, boolean shouldOmitTasks) {
        if (this.hasRunningPreflights(planDto.getCheckExecutionId())) {
            log.error("Preflight checks running while saving the plan for the executionId: {}", (Object)planDto.getCheckExecutionId());
            throw new InvalidPlanException(String.format("Preflight checks still running for the executionId: %s, try saving the plan again once preflight finishes", planDto.getCheckExecutionId()));
        }
        log.info("No Preflight are running while saving the plan. Plan is good to save for executionId {}", (Object)planDto.getCheckExecutionId());
        this.validatePlanDto(planDto);
        return this.ptx.write(() -> {
            Plan plan = (Plan)this.planConverter.dtoToEntity(planDto);
            plan.setMigrationCreator(planDto.getMigrationCreator());
            if (this.planStore.planNameExists(plan.getName(), null)) {
                throw new InvalidPlanException("A migration with the same name already exists.");
            }
            Optional<MigrateAppsTask> maybeAppTask = plan.getTasks().stream().filter(MigrateAppsTask.class::isInstance).map(MigrateAppsTask.class::cast).findAny();
            if (maybeAppTask.isPresent()) {
                MigrateAppsTask task2 = maybeAppTask.get();
                task2.setNeededInCloudApps(this.getNeededInCloudApps(task2));
            } else if (this.darkFeaturesManager.appMigrationDevMode()) {
                MigrateAppsTask migrateAppsTask = new MigrateAppsTask();
                migrateAppsTask.setPlan(plan);
                migrateAppsTask.setExcludedApps(Collections.emptySet());
                migrateAppsTask.setNeededInCloudApps(this.getNeededInCloudApps(migrateAppsTask));
                plan.getTasks().add(migrateAppsTask);
            }
            if (!this.darkFeaturesManager.isGlobalEntitiesMigrationEnabled()) {
                if (planDto.getTasks().stream().anyMatch(MigrateGlobalEntitiesTaskDto.class::isInstance)) {
                    plan.setTasks(plan.getTasks().stream().filter(task -> !task.getType().equals((Object)TaskType.GLOBAL_ENTITIES)).collect(Collectors.toList()));
                }
            }
            boolean shouldExpandTasks = !shouldOmitTasks;
            PlanDto persistedPlan = this.planConverter.entityToDto(this.planStore.createPlan(plan), shouldExpandTasks);
            this.checkResultsService.bindCheckIdToPlanId(planDto.getCheckExecutionId(), persistedPlan.getId());
            this.checkOverrideService.bindCheckIdToPlanId(planDto.getCheckExecutionId(), persistedPlan.getId());
            return persistedPlan;
        });
    }

    @Override
    @Nonnull
    public PlanDto updatePlan(PlanDto planDto) {
        this.validatePlanDto(planDto);
        return this.ptx.write(() -> {
            Plan entity;
            String planId = planDto.getId();
            try {
                entity = this.planStore.getPlanAndLock(planId);
            }
            catch (NoResultException e) {
                throw new IllegalArgumentException("Failed to update, no plan with ID " + planId, e);
            }
            if (entity.getProgress().getStatus() != ExecutionStatus.CREATED) {
                throw new IllegalStateException("Plan only can be updated when in CREATED state");
            }
            if (this.planStore.planNameExists(planDto.getName(), planId)) {
                throw new InvalidPlanException("The plan name cannot be updated to the given value as another plan with that name already exists.");
            }
            this.planStore.removeTasks(entity);
            this.planConverter.copyDtoToEntity(planDto, entity);
            this.planStore.updatePlan(entity);
            return this.planConverter.entityToDto(entity, true);
        });
    }

    @Override
    public boolean stop(String planId) {
        boolean markedAsStopping = this.planExecutionService.stopPlan(planId);
        if (markedAsStopping) {
            this.ptx.write(() -> {
                Plan plan = this.planStore.getPlan(planId);
                PlanDto planDto = this.planConverter.entityToDto(plan, true);
                this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildUpdatedPlanStatusAnalyticEvent(planDto, ProgressDto.convertStatus(plan.getProgress().getStatus(), null)));
            });
        }
        return markedAsStopping;
    }

    @Override
    public boolean hasPlans(ExecutionStatus ... statuses) {
        return this.ptx.read(() -> this.planStore.hasPlans(statuses));
    }

    @Override
    public PlanDto copyPlan(String planId) {
        return this.ptx.write(() -> {
            Plan plan = this.getPlanById(planId);
            Plan copiedPlan = new Plan(plan);
            copiedPlan.setName(this.getCopiedPlanName(plan.getName()));
            return this.planConverter.entityToDto(this.planStore.createPlan(copiedPlan), true);
        });
    }

    @Override
    public PlanDto updateActiveStatus(String planId, PlanActiveStatus activeStatus) {
        return this.ptx.write(() -> {
            Plan plan = this.getPlanById(planId);
            plan.setActiveStatus(activeStatus);
            this.planStore.updatePlan(plan);
            return this.planConverter.entityToDto(plan, true);
        });
    }

    private Plan getPlanById(String planId) {
        try {
            return this.planStore.getPlan(planId);
        }
        catch (NoResultException e) {
            throw new IllegalArgumentException(String.format("Plan with ID %s could not be found", planId), e);
        }
    }

    @Override
    public PlanDto verifyPlan(String planId) {
        return this.ptx.write(() -> {
            Plan plan = this.planStore.getPlanAndLock(planId);
            ExecutionStatus status = plan.getProgress().getStatus();
            if (!status.canGo(ExecutionStatus.VALIDATING)) {
                throw new IllegalArgumentException(String.format("A migration plan with id=%s has already been started or completed", planId));
            }
            plan.setProgress(plan.getProgress().copy().validating());
            this.planStore.updatePlan(plan);
            return this.planConverter.entityToDto(plan, true);
        });
    }

    @Override
    public void setCreatedStatus(String planId) {
        this.ptx.write(() -> {
            Plan plan = this.planStore.getPlanAndLock(planId);
            ExecutionStatus status = plan.getProgress().getStatus();
            if (!status.canGo(ExecutionStatus.CREATED)) {
                log.error("Failed to change status to CREATED with plan ID {}", (Object)planId);
                return;
            }
            plan.setProgress(Progress.created());
            this.planStore.updatePlan(plan);
        });
    }

    @Override
    public void startPlan(String planId) {
        log.info("Plan has started with id: {}", (Object)planId);
        ConfluenceUser confluenceUser = AuthenticatedUserThreadLocal.get();
        try {
            this.ptx.write(() -> {
                if (!this.isAnyOtherPlanRunning(planId)) {
                    this.darkFeaturesManager.refreshFeatureFlags();
                }
                Plan plan = this.planStore.getPlanAndLock(planId);
                plan.setProgress(plan.getProgress().copy().started());
                MigrationDetails migrationDetails = this.retrieveMigrationDetails(plan);
                plan.setMigrationScopeId(migrationDetails.migrationScopeId);
                plan.setMigrationId(migrationDetails.migrationId);
                plan.setSchedulerVersion(PlanSchedulerVersion.PLAN_EXECUTION_SERVICE);
                this.planStore.updatePlan(plan);
                this.createContainersInMcs(plan.getCloudSite().getCloudId(), migrationDetails.migrationId, plan);
                this.platformService.updateMigrationStatusToMcs(plan);
                try {
                    PlanDto planDto = this.planConverter.entityToDto(plan, true);
                    this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildStartPlanAnalyticsEvent(planDto));
                    this.appAssessmentAnalyticsEventService.saveStartPlanEvent(confluenceUser);
                }
                catch (Exception e) {
                    log.warn("Analytics failure while registering start events", (Throwable)e);
                }
            });
            this.planExecutionService.runPlan(planId);
        }
        catch (Exception e) {
            String errorMsg = "Fail to start the plan: " + planId;
            log.error(errorMsg, (Throwable)e);
            this.progressTracker.failPlan(planId, errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }

    private boolean hasRunningPreflights(String executionId) {
        List<CheckResultEntity> runningPreflights = this.checkResultsService.getByExecutionId(executionId);
        return runningPreflights != null && runningPreflights.stream().anyMatch(checkResultEntity -> checkResultEntity.getStatus().equals((Object)CheckExecutionStatus.RUNNING));
    }

    private MigrationDetails retrieveMigrationDetails(Plan plan) {
        String cloudId = plan.getCloudSite().getCloudId();
        boolean createAppContainersForAllListeners = this.darkFeaturesManager.appMigrationDevMode();
        return this.platformService.createMigrationInMcs(cloudId, plan, createAppContainersForAllListeners);
    }

    private void createContainersInMcs(String cloudId, String migrationId, Plan plan) {
        try {
            boolean createAppContainersForAllListeners = this.darkFeaturesManager.appMigrationDevMode();
            this.platformService.createContainersInMcs(cloudId, migrationId, plan);
            this.platformService.createAppContainers(cloudId, migrationId, plan.getTasks(), createAppContainersForAllListeners);
            this.updateUserAndGlobalTemplatesTaskWithContainerId(plan);
            this.updateSpaceTaskWithContainerId(plan);
        }
        catch (Exception e) {
            log.error("Failed to create containers in mcs, cloudId: {}, {}", (Object)cloudId, (Object)e);
        }
    }

    private void updateUserAndGlobalTemplatesTaskWithContainerId(Plan plan) {
        try {
            Optional<MigrateUsersTask> usersTask = plan.getUserTaskOfPlan();
            Optional<MigrateGlobalEntitiesTask> globalEntitiesTask = plan.getGlobalEntitiesTaskOfPlan();
            Set<SiteContainer> containers = this.platformService.getSiteContainers(plan);
            containers.forEach(container -> {
                if (usersTask.isPresent() && container.getSelections().contains((Object)SiteContainer.SiteSelection.USERS)) {
                    ((MigrateUsersTask)usersTask.get()).setContainerId(container.getContainerId());
                    this.ptx.write(() -> this.taskStore.update((Task)usersTask.get()));
                }
                if (globalEntitiesTask.isPresent() && container.getSelections().contains((Object)SiteContainer.SiteSelection.GLOBAL_ENTITIES)) {
                    ((MigrateGlobalEntitiesTask)globalEntitiesTask.get()).setContainerId(container.getContainerId());
                    this.ptx.write(() -> this.taskStore.update((Task)globalEntitiesTask.get()));
                }
            });
        }
        catch (Exception e) {
            log.error("Failed to update user task with containerId for planId: {}", (Object)plan.getId(), (Object)e);
        }
    }

    private void updateSpaceTaskWithContainerId(Plan plan) {
        try {
            Set<ConfluenceSpaceContainer> containers = this.platformService.getConfluenceSpaceContainers(plan);
            HashMap spaceKeyToContainerId = new HashMap();
            containers.forEach(container -> spaceKeyToContainerId.put(container.getKey(), container.getContainerId()));
            ArrayList spaceTasks = new ArrayList();
            plan.getTasks().forEach(t -> {
                if (t.getType().equals((Object)TaskType.SPACE) || t.getType().equals((Object)TaskType.ATTACHMENTS)) {
                    spaceTasks.add((AbstractSpaceTask)t);
                }
            });
            this.ptx.write(() -> spaceTasks.forEach(task -> {
                task.setContainerId((String)spaceKeyToContainerId.get(task.getSpaceKey()));
                this.taskStore.update((Task)task);
            }));
        }
        catch (Exception e) {
            log.error("Failed to update space task with containerId for planId: {}", (Object)plan.getId(), (Object)e);
        }
    }

    @VisibleForTesting
    public String getCopiedPlanName(String planName) {
        String copiedPlanName = COPIED_PLAN_PREFIX + planName;
        List<String> possibleDuplicates = this.planStore.getPlanNamesStartingWithPrefix(copiedPlanName);
        if (!possibleDuplicates.isEmpty()) {
            int maxCountSuffix = 0;
            for (String possibleDuplicate : possibleDuplicates) {
                Matcher matcher = COPIED_PLAN_SUFFIX.matcher(possibleDuplicate);
                if (!matcher.find()) continue;
                int countSuffix = Integer.parseInt(matcher.group(1));
                maxCountSuffix = Integer.max(countSuffix, maxCountSuffix);
            }
            copiedPlanName = COPIED_PLAN_SUFFIX.matcher(copiedPlanName).replaceAll("") + "_" + (maxCountSuffix + 1);
        }
        return this.sanitizePlanName(copiedPlanName);
    }

    private String sanitizePlanName(String planName) {
        if (planName.length() >= 255) {
            String uuid = UUID.randomUUID().toString();
            return planName.substring(0, 255 - uuid.length() - 1) + "_" + uuid;
        }
        return planName;
    }

    private Set<String> getNeededInCloudApps() {
        return this.appAssessmentFacade.getAppsNeededInCloud().getApps().stream().map(AppDto::getKey).collect(Collectors.toSet());
    }

    @Nonnull
    @VisibleForTesting
    public void validatePlanDto(PlanDto planDto) {
        List<TaskDto> taskDtos = planDto.getTasks();
        if (taskDtos.isEmpty()) {
            throw new RequestValidationException("Plan doesn't includes any task.");
        }
        HashSet uniqueSpaceKeys = new HashSet();
        HashSet duplicateSpaceKeys = new HashSet();
        HashSet invalidSpaceKeys = new HashSet();
        taskDtos.stream().filter(ConfluenceSpaceTaskDto.class::isInstance).map(ConfluenceSpaceTaskDto.class::cast).forEach(taskDto -> {
            String spaceKey = taskDto.getSpace();
            if (uniqueSpaceKeys.contains(spaceKey)) {
                duplicateSpaceKeys.add(spaceKey);
            }
            uniqueSpaceKeys.add(spaceKey);
        });
        if (!duplicateSpaceKeys.isEmpty()) {
            throw new RequestValidationException("Plan includes duplicate space keys " + duplicateSpaceKeys);
        }
    }

    @NotNull
    private Set<NeededInCloudApp> getNeededInCloudApps(MigrateAppsTask migrateAppsTask) {
        return this.getNeededInCloudApps().stream().map(appKey -> new NeededInCloudApp(migrateAppsTask, (String)appKey)).collect(Collectors.toSet());
    }

    @Override
    public boolean isAnyOtherPlanRunning(String planId) {
        return this.getAllPlans().stream().anyMatch(p -> p.getProgress().getStatus() == ProgressDto.Status.RUNNING && !p.getId().equals(planId));
    }

    @Override
    public boolean hasPlansRunningOrStopping() {
        return this.planStore.hasPlansRunningOrStopping();
    }

    private /* synthetic */ List lambda$getAllPlans$0(Set validStatuses) {
        return this.planStore.getAllPlans(validStatuses).stream().map(this.planConverter::entityToDto).collect(Collectors.toList());
    }
}

