/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.planning;

import com.atlassian.migration.agent.entity.MigrateGlobalEntitiesTask;
import com.atlassian.migration.agent.entity.MigrateUsersTask;
import com.atlassian.migration.agent.entity.Task;
import com.atlassian.migration.agent.entity.TaskType;
import com.atlassian.migration.agent.store.TaskStore;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TaskPlanningEngine {
    private final TaskStore taskStore;

    public TaskPlanningEngine(TaskStore taskStore) {
        this.taskStore = taskStore;
    }

    public List<Task> getFirstTasks(String planId) {
        List<Task> tasks = this.taskStore.getTasksForPlan(planId);
        Optional<Task> firstTask = (tasks = this.filterOutTasks(tasks, Collections.singletonList(TaskType.APPS))).stream().filter(task -> task instanceof MigrateUsersTask).findFirst();
        if (!firstTask.isPresent()) {
            firstTask = tasks.stream().filter(task -> task instanceof MigrateGlobalEntitiesTask).findFirst();
        }
        return firstTask.map(Collections::singletonList).orElse(tasks);
    }

    public List<Task> getNextTasks(Task task) {
        TaskType taskType = task.getType();
        List<Task> tasks = this.taskStore.getTasksForPlan(task.getPlan().getId());
        if (taskType.equals((Object)TaskType.USERS)) {
            tasks = this.filterOutTasks(tasks, Arrays.asList(TaskType.USERS, TaskType.APPS));
            Optional<Task> globalEntityTask = tasks.stream().filter(geTask -> geTask instanceof MigrateGlobalEntitiesTask).findFirst();
            return globalEntityTask.map(Collections::singletonList).orElse(tasks);
        }
        if (taskType.equals((Object)TaskType.GLOBAL_ENTITIES)) {
            return this.filterOutTasks(tasks, Arrays.asList(TaskType.USERS, TaskType.APPS, TaskType.GLOBAL_ENTITIES));
        }
        return Collections.emptyList();
    }

    public boolean hasReachedTerminalState(String planId) {
        return this.taskStore.areTasksInTerminalState(planId);
    }

    private List<Task> filterOutTasks(List<Task> tasks, List<TaskType> taskTypes) {
        return tasks.stream().filter(task -> !taskTypes.contains((Object)task.getType())).collect(Collectors.toList());
    }
}

