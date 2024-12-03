/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.migration.agent.entity.AbstractSpaceTask;
import com.atlassian.migration.agent.entity.ExecutionStatus;
import com.atlassian.migration.agent.entity.MigrateAppsTask;
import com.atlassian.migration.agent.entity.MigrateUsersTask;
import com.atlassian.migration.agent.entity.Task;
import com.atlassian.migration.agent.store.TaskStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.util.List;
import java.util.Optional;

public class TaskStoreImpl
implements TaskStore {
    private static final String PARAM_PLANID = "planId";
    private static final String PARAM_TASKID = "taskId";
    private static final String PARAM_STATUS = "status";
    private final EntityManagerTemplate tmpl;

    public TaskStoreImpl(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public Optional<Task> findFirstTaskWithStatusForPlan(String planId, ExecutionStatus status) {
        return this.tmpl.query(Task.class, "select task from Task task where task.plan.id=:planId and task.progress.status=:status order by task.index").param(PARAM_PLANID, (Object)planId).param(PARAM_STATUS, (Object)status).first();
    }

    @Override
    public boolean existTaskByStatusByTypeInPlan(String planId, ExecutionStatus status, Class<? extends Task> taskType) {
        return this.tmpl.query(Task.class, "select task from Task task where task.plan.id=:planId and task.progress.status=:status and TYPE(task) = :taskType").param(PARAM_PLANID, (Object)planId).param(PARAM_STATUS, (Object)status).param("taskType", taskType).first().isPresent();
    }

    @Override
    public List<Task> getTasksForPlan(String planId) {
        return this.tmpl.query(Task.class, "select task from Task task where task.plan.id=:planId").param(PARAM_PLANID, (Object)planId).list();
    }

    @Override
    public boolean areTasksInTerminalState(String planId) {
        return !this.tmpl.query(Task.class, "select t1 from Task t1 left outer join Task t2 on t2.plan.id=:planId and TYPE(t2) = :userType and t2.progress.status = :failed where TYPE(t1) <> :appTaskType and t1.plan.id=:planId and t1.progress.status not in :terminalStatuses and t2.id is null").param(PARAM_PLANID, (Object)planId).param("terminalStatuses", ExecutionStatus.COMPLETE_STATUSES).param("failed", (Object)ExecutionStatus.FAILED).param("userType", MigrateUsersTask.class).param("appTaskType", MigrateAppsTask.class).first().isPresent();
    }

    @Override
    public Task getTask(String taskId) {
        return this.tmpl.query(Task.class, "select task from Task task where task.id = :taskId").param(PARAM_TASKID, (Object)taskId).single();
    }

    @Override
    public int calculatePlanPercent(String planId) {
        return this.tmpl.query(Long.class, "select sum(task.progress.percent * task.weight)/sum(task.weight) from Task task where task.plan.id=:planId").param(PARAM_PLANID, (Object)planId).first().orElse(0L).intValue();
    }

    @Override
    public void update(Task task) {
        this.tmpl.merge(task);
    }

    @Override
    public void stopCreatedTasks(String planId) {
        this.tmpl.query("update Task task set task.progress.status=:newStatus where task.progress.status=:oldStatus").param("newStatus", (Object)ExecutionStatus.STOPPED).param("oldStatus", (Object)ExecutionStatus.CREATED).update();
    }

    @Override
    public void stopInactiveTasks(String planId) {
        this.tmpl.query("update Task task set task.progress.status=:stoppedStatus where task.plan.id = :planId and (task.progress.status=:createdStatus or exists (select 1 from Step step where step.task.id = task.id and step.progress.status = :createdStatus))").param("stoppedStatus", (Object)ExecutionStatus.STOPPED).param("createdStatus", (Object)ExecutionStatus.CREATED).param(PARAM_PLANID, (Object)planId).update();
    }

    @Override
    public boolean hasRunningTasks(String planId) {
        return this.tmpl.query(Task.class, "select task from Task task where task.plan.id=:planId and task.progress.status=:status").param(PARAM_PLANID, (Object)planId).param(PARAM_STATUS, (Object)ExecutionStatus.RUNNING).first().isPresent();
    }

    @Override
    public Optional<AbstractSpaceTask> findTaskByPlanIdAndSpaceKey(String planId, String spaceKey) {
        return this.tmpl.query(AbstractSpaceTask.class, "select task from AbstractSpaceTask task where task.plan.id=:planId and task.spaceKey=:spaceKey").param(PARAM_PLANID, (Object)planId).param("spaceKey", (Object)spaceKey).first();
    }
}

