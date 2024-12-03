/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.domain.Edition
 *  com.google.common.collect.Maps
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.apache.commons.lang.StringUtils
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.cmpt.domain.Edition;
import com.atlassian.migration.agent.dto.CloudSiteDto;
import com.atlassian.migration.agent.dto.ConfluenceSpaceTaskDto;
import com.atlassian.migration.agent.dto.MigrateAppsTaskDto;
import com.atlassian.migration.agent.dto.MigrateGlobalEntitiesTaskDto;
import com.atlassian.migration.agent.dto.MigrateUsersTaskDto;
import com.atlassian.migration.agent.dto.PlanDto;
import com.atlassian.migration.agent.dto.ProgressDto;
import com.atlassian.migration.agent.dto.SpaceAttachmentsTaskDto;
import com.atlassian.migration.agent.dto.TaskDto;
import com.atlassian.migration.agent.dto.util.PlanDtoUtil;
import com.atlassian.migration.agent.entity.CloudEdition;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.entity.ConfluenceSpaceTask;
import com.atlassian.migration.agent.entity.ExcludeApp;
import com.atlassian.migration.agent.entity.MigrateAppsTask;
import com.atlassian.migration.agent.entity.MigrateGlobalEntitiesTask;
import com.atlassian.migration.agent.entity.MigrateUsersTask;
import com.atlassian.migration.agent.entity.Plan;
import com.atlassian.migration.agent.entity.SpaceAttachmentsOnlyTask;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.entity.Task;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.model.stats.SpaceStats;
import com.atlassian.migration.agent.service.EntityDtoConverter;
import com.atlassian.migration.agent.service.StatisticsService;
import com.atlassian.migration.agent.service.catalogue.model.GlobalEntitiesExecutionState;
import com.atlassian.migration.agent.service.check.CheckOverrideService;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.impl.MigrationTimeEstimationUtils;
import com.atlassian.migration.agent.service.impl.StepType;
import com.atlassian.migration.agent.store.StepStore;
import com.atlassian.migration.agent.store.TaskStore;
import com.atlassian.migration.agent.store.impl.SpaceStore;
import com.google.common.collect.Maps;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class PlanConverter
extends EntityDtoConverter<PlanDto, Plan> {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(PlanConverter.class);
    private final CloudSiteService cloudSiteService;
    private final CheckOverrideService checkOverrideService;
    private final SpaceStore spaceStore;
    private final TaskStore taskStore;
    private final StepStore stepStore;
    private final StatisticsService statisticsService;
    private final MigrationTimeEstimationUtils migrationTimeEstimationUtils;

    public PlanConverter(CloudSiteService cloudSiteService, CheckOverrideService checkOverrideService, StatisticsService statisticsService, SpaceStore spaceStore, TaskStore taskStore, StepStore stepStore, MigrationTimeEstimationUtils migrationTimeEstimationUtils) {
        this.cloudSiteService = cloudSiteService;
        this.checkOverrideService = checkOverrideService;
        this.statisticsService = statisticsService;
        this.spaceStore = spaceStore;
        this.taskStore = taskStore;
        this.stepStore = stepStore;
        this.migrationTimeEstimationUtils = migrationTimeEstimationUtils;
    }

    @Override
    public void copyDtoToEntity(PlanDto src, Plan dest) {
        String oldCloudId;
        dest.setName(src.getName());
        dest.setLastUpdate(src.getLastUpdate() != null ? src.getLastUpdate() : Instant.now());
        dest.setId(src.getId());
        dest.setMigrationId(src.getMigrationId());
        dest.setMigrationTag(src.getMigrationTag());
        List<Task> tasks = src.getTasks().stream().map(taskDto -> {
            Task task = taskDto.toInternalType();
            task.setPlan(dest);
            return task;
        }).collect(Collectors.toList());
        this.removeUserTaskIfAttachmentsOnlyPlan(tasks, src);
        dest.setTasks(tasks);
        String newCloudId = src.getCloudSite() == null ? null : src.getCloudSite().getCloudId();
        String string = oldCloudId = dest.getCloudSite() == null ? null : dest.getCloudSite().getCloudId();
        if (newCloudId != null && !newCloudId.equals(oldCloudId)) {
            CloudSite cloudSite = this.cloudSiteService.getByCloudId(newCloudId).orElseThrow(() -> new IllegalArgumentException(String.format("cloudId is not known: %s", newCloudId)));
            dest.setCloudSite(cloudSite);
        }
        dest.setActiveStatus(src.getActiveStatus());
    }

    @Override
    public PlanDto entityToDto(Plan src) {
        return this.entityToDto(src, false);
    }

    public PlanDto entityToDto(Plan plan, boolean expandTasks) {
        CloudSite cloudSite = plan.getCloudSite();
        Edition cloudEditionKey = Optional.ofNullable(cloudSite.getEdition()).map(CloudEdition::getKey).orElse(null);
        PlanDto.PlanDtoBuilder builder = PlanDto.builder().id(plan.getId()).name(plan.getName()).lastUpdate(plan.getLastUpdate()).createdTime(plan.getCreatedTime()).cloudSite(new CloudSiteDto(cloudSite.getCloudUrl(), cloudSite.getCloudId(), cloudEditionKey, cloudSite.getCloudType())).preflightChecksToOverride(this.checkOverrideService.getOverridesByExecutionId(plan.getId())).progress(ProgressDto.fromPlanEntity(plan.getProgress())).activeStatus(plan.getActiveStatus()).migrationId(plan.getMigrationId()).migrationCreator(plan.getMigrationCreator()).migrationTag(plan.getMigrationTag());
        if (expandTasks) {
            builder.tasks(this.resolveTaskDTOsForPlan(plan));
        }
        return builder.build();
    }

    private void removeUserTaskIfAttachmentsOnlyPlan(List<Task> tasks, PlanDto planDto) {
        if (PlanDtoUtil.containsAttachmentsOnlyTask(planDto)) {
            tasks.removeIf(task -> task instanceof MigrateUsersTask && ((MigrateUsersTask)task).isScoped() != false);
        }
    }

    private List<TaskDto> resolveTaskDTOsForPlan(Plan plan) {
        Map<String, String> spaceKeyNamePairs = this.spaceStore.getSpaceKeyNamePairsForSpaceTasks(plan.getId());
        Map<String, SpaceStats> spaceStatistics = this.getSpaceStatistics(new ArrayList<String>(spaceKeyNamePairs.keySet()));
        return this.taskStore.getTasksForPlan(plan.getId()).parallelStream().map(task -> {
            ProgressDto progressDto = ProgressDto.fromTaskEntity(task.getProgress(), plan.getProgress().getStatus(), false);
            if (task instanceof ConfluenceSpaceTask) {
                return this.buildConfluenceSpaceTask(progressDto, spaceKeyNamePairs, spaceStatistics, (ConfluenceSpaceTask)task);
            }
            if (task instanceof SpaceAttachmentsOnlyTask) {
                return this.buildSpaceAttachmentsTask(progressDto, spaceKeyNamePairs, spaceStatistics, (SpaceAttachmentsOnlyTask)task);
            }
            if (task instanceof MigrateUsersTask) {
                return this.buildMigrateUsersTask(progressDto, plan, (MigrateUsersTask)task);
            }
            if (task instanceof MigrateAppsTask) {
                return this.buildMigrateAppsTask(progressDto, (MigrateAppsTask)task);
            }
            if (task instanceof MigrateGlobalEntitiesTask) {
                return this.buildMigrateGlobalEntitiesTask(progressDto, (MigrateGlobalEntitiesTask)task);
            }
            throw new IllegalStateException("Unknown task type: " + task.getClass().getName());
        }).collect(Collectors.toList());
    }

    @NotNull
    private MigrateAppsTaskDto buildMigrateAppsTask(ProgressDto progressDto, MigrateAppsTask task) {
        Set<String> excludedAppKeys = task.getExcludedApps().stream().map(ExcludeApp::getAppKey).collect(Collectors.toSet());
        return new MigrateAppsTaskDto(task.getId(), task.getName(), excludedAppKeys, progressDto);
    }

    @NotNull
    private SpaceAttachmentsTaskDto buildSpaceAttachmentsTask(ProgressDto progressDto, Map<String, String> spaceKeyNamePairs, Map<String, SpaceStats> spaceStatistics, SpaceAttachmentsOnlyTask task) {
        String spaceKey = task.getSpaceKey();
        SpaceStats statsForSpace = spaceStatistics.get(spaceKey);
        return new SpaceAttachmentsTaskDto(task.getId(), task.getName(), task.getSpaceKey(), spaceKeyNamePairs.get(spaceKey), this.estimateSpaceMigrationTimeWithErrorHandling(statsForSpace, spaceKey), progressDto);
    }

    @NotNull
    private MigrateUsersTaskDto buildMigrateUsersTask(ProgressDto progressDto, Plan plan, MigrateUsersTask task) {
        return new MigrateUsersTaskDto(task.getId(), task.getName(), this.getUsersMigrationEstimateSeconds(plan, this.statisticsService), progressDto, task.isScoped());
    }

    @NotNull
    private ConfluenceSpaceTaskDto buildConfluenceSpaceTask(ProgressDto progressDto, Map<String, String> spaceKeyNamePairs, Map<String, SpaceStats> spaceStatistics, ConfluenceSpaceTask task) {
        String spaceKey = task.getSpaceKey();
        return new ConfluenceSpaceTaskDto(task.getId(), task.getName(), task.getSpaceKey(), spaceKeyNamePairs.get(spaceKey), this.estimateSpaceMigrationTimeWithErrorHandling(spaceStatistics.get(spaceKey), spaceKey), progressDto);
    }

    @NotNull
    private MigrateGlobalEntitiesTaskDto buildMigrateGlobalEntitiesTask(ProgressDto progressDto, MigrateGlobalEntitiesTask task) {
        Long totalGlobalPageTemplatesExported = null;
        Long totalEditedSystemTemplatesExported = null;
        try {
            Optional<Step> step = this.stepStore.getStep(task.getPlan().getId(), StepType.GLOBAL_ENTITIES_EXPORT);
            if (step.isPresent() && step.get().getProgress().getStatus().isCompleted() && !StringUtils.isBlank((String)step.get().getExecutionState())) {
                GlobalEntitiesExecutionState file = Jsons.readValue(step.get().getExecutionState(), GlobalEntitiesExecutionState.class);
                totalGlobalPageTemplatesExported = file.getTotalGlobalPageTemplatesExported();
                totalEditedSystemTemplatesExported = file.getTotalEditedSystemTemplatesExported();
            }
        }
        catch (Exception e) {
            log.error("Error while getting count of global templates exported", (Throwable)e);
        }
        return new MigrateGlobalEntitiesTaskDto(task.getId(), task.getName(), PlanConverter.getGlobalEntitiesMigrationEstimateSeconds(this.statisticsService, task), progressDto, task.getGlobalEntityType(), totalGlobalPageTemplatesExported, totalEditedSystemTemplatesExported);
    }

    private Map<String, SpaceStats> getSpaceStatistics(Collection<String> spaceKeys) {
        Collection<SpaceStats> statsForSpaces = this.statisticsService.loadSpaceStatistics(spaceKeys);
        return Maps.uniqueIndex(statsForSpaces, SpaceStats::getSpaceKey);
    }

    private long estimateSpaceMigrationTimeWithErrorHandling(SpaceStats spaceStats, String spaceKey) {
        try {
            return this.migrationTimeEstimationUtils.estimateSpaceMigrationTime(spaceStats.getSummary()).getSeconds();
        }
        catch (Exception e) {
            log.error("Error calculating space migration time estimate for space {}", (Object)spaceKey, (Object)e);
            return 0L;
        }
    }

    private long getUsersMigrationEstimateSeconds(Plan plan, StatisticsService statisticsService) {
        try {
            return statisticsService.getUsersGroupsStatistics(plan.getUserMigrationTypeBasedOnUserTaskInPlan(), plan.getSpaceKeysBasedOnUserTaskInPlan()).getTotalMigrationTime().getSeconds();
        }
        catch (Exception e) {
            log.error("Error calculating users migration time estimate", (Throwable)e);
            return 0L;
        }
    }

    private static long getGlobalEntitiesMigrationEstimateSeconds(StatisticsService statisticsService, MigrateGlobalEntitiesTask task) {
        try {
            String planId = task.getPlan().getId();
            return statisticsService.getGlobalEntitiesStatistics(planId).getTotalMigrationTime().getSeconds();
        }
        catch (Exception e) {
            log.error("Error calculating migration time estimate for global templates", (Throwable)e);
            return 0L;
        }
    }
}

