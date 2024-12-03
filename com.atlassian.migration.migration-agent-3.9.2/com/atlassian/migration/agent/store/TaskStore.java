/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store;

import com.atlassian.migration.agent.entity.AbstractSpaceTask;
import com.atlassian.migration.agent.entity.ExecutionStatus;
import com.atlassian.migration.agent.entity.Task;
import java.util.List;
import java.util.Optional;

public interface TaskStore {
    public Optional<Task> findFirstTaskWithStatusForPlan(String var1, ExecutionStatus var2);

    public boolean existTaskByStatusByTypeInPlan(String var1, ExecutionStatus var2, Class<? extends Task> var3);

    public List<Task> getTasksForPlan(String var1);

    public boolean areTasksInTerminalState(String var1);

    public Task getTask(String var1);

    public int calculatePlanPercent(String var1);

    public void update(Task var1);

    public void stopCreatedTasks(String var1);

    public void stopInactiveTasks(String var1);

    public boolean hasRunningTasks(String var1);

    public Optional<AbstractSpaceTask> findTaskByPlanIdAndSpaceKey(String var1, String var2);
}

