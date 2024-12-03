/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.status;

import com.atlassian.migration.agent.entity.AbstractSpaceTask;
import com.atlassian.migration.agent.entity.Plan;
import com.atlassian.migration.agent.entity.PlanActiveStatus;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.entity.Task;
import com.atlassian.migration.agent.service.status.PlanStatusDto;
import com.atlassian.migration.agent.service.status.StepStatusDto;
import com.atlassian.migration.agent.service.status.TaskStatusDto;
import com.atlassian.migration.agent.store.PlanStore;
import com.atlassian.migration.agent.store.StepStore;
import com.atlassian.migration.agent.store.TaskStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MigrationStatusService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(MigrationStatusService.class);
    private final PlanStore planStore;
    private final TaskStore taskStore;
    private final StepStore stepStore;
    private final PluginTransactionTemplate ptx;

    public MigrationStatusService(PlanStore planStore, TaskStore taskStore, StepStore stepStore, PluginTransactionTemplate ptx) {
        this.planStore = planStore;
        this.taskStore = taskStore;
        this.stepStore = stepStore;
        this.ptx = ptx;
    }

    public List<PlanStatusDto> getAllPlans() {
        return this.ptx.read(() -> {
            List<Plan> plans = this.planStore.getAllPlans((Set<PlanActiveStatus>)ImmutableSet.of((Object)((Object)PlanActiveStatus.ACTIVE), (Object)((Object)PlanActiveStatus.ARCHIVED)));
            return plans.stream().map(plan -> PlanStatusDto.builder().id(plan.getId()).name(plan.getName()).cloudId(plan.getCloudSite().getCloudId()).activeStatus(plan.getActiveStatus().name()).progress(plan.getProgress().getStatus().name()).elapsed(plan.getProgress().getElapsed()).startTime(plan.getProgress().getStartTime().orElse(null)).endTime(plan.getProgress().getEndTime().orElse(null)).build()).collect(Collectors.toList());
        });
    }

    public List<TaskStatusDto> getTasksByPlan(String planId) {
        return this.ptx.read(() -> {
            List<Task> tasks = this.taskStore.getTasksForPlan(planId);
            return tasks.stream().map(this::createTaskStatusDto).collect(Collectors.toList());
        });
    }

    public List<StepStatusDto> getStepsByTask(String taskId) {
        return this.ptx.read(() -> {
            List<Step> steps = this.stepStore.getStepsByTaskId(taskId);
            return this.createStepStatusDtos(steps);
        });
    }

    public Map<String, Object> getDetailsByPlanAndSpaceKey(String planId, String spaceKey) {
        return this.ptx.read(() -> {
            Optional<AbstractSpaceTask> spaceTask = this.taskStore.findTaskByPlanIdAndSpaceKey(planId, spaceKey);
            if (spaceTask.isPresent()) {
                List<StepStatusDto> steps = this.createStepStatusDtos(this.stepStore.getStepsByTaskId(spaceTask.get().getId()));
                return ImmutableMap.of((Object)"task", (Object)this.createTaskStatusDto(spaceTask.get()), (Object)"steps", steps);
            }
            return Collections.emptyMap();
        });
    }

    private TaskStatusDto createTaskStatusDto(Task task) {
        return TaskStatusDto.builder().name(task.getName()).taskType(task.getAnalyticsEventType()).progress(task.getProgress().getStatus().name()).spaceKey(task instanceof AbstractSpaceTask ? ((AbstractSpaceTask)task).getSpaceKey() : null).elapsed(task.getProgress().getElapsed()).startTime(task.getProgress().getEndTime().orElse(null)).endTime(task.getProgress().getEndTime().orElse(null)).build();
    }

    private List<StepStatusDto> createStepStatusDtos(List<Step> steps) {
        return steps.stream().map(step -> StepStatusDto.builder().type(step.getType()).progress(step.getProgress().getStatus().name()).elapsed(step.getProgress().getElapsed()).startTime(step.getProgress().getStartTime().orElse(null)).endTime(step.getProgress().getEndTime().orElse(null)).build()).collect(Collectors.toList());
    }
}

