/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.LockModeType
 */
package com.atlassian.migration.agent.store.impl;

import com.atlassian.migration.agent.entity.ExecutionStatus;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.service.impl.StepType;
import com.atlassian.migration.agent.store.StepStore;
import com.atlassian.migration.agent.store.jpa.EntityManagerTemplate;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.persistence.LockModeType;

public class StepStoreImpl
implements StepStore {
    private final EntityManagerTemplate tmpl;
    private static final String STEPIDPARAM = "stepId";
    private static final String STEPQUERY = "select step from Step step where step.id=:stepId";

    public StepStoreImpl(EntityManagerTemplate tmpl) {
        this.tmpl = tmpl;
    }

    @Override
    public Step getStep(String stepId) {
        return this.tmpl.query(Step.class, STEPQUERY).param(STEPIDPARAM, (Object)stepId).single();
    }

    @Override
    public void addSteps(Collection<Step> steps) {
        steps.forEach(this.tmpl::persist);
        this.tmpl.flush();
    }

    @Override
    public void update(Step step) {
        this.tmpl.merge(step);
    }

    @Override
    public Step getAndLock(String stepId) {
        return this.tmpl.query(Step.class, STEPQUERY).param(STEPIDPARAM, (Object)stepId).lock(LockModeType.PESSIMISTIC_WRITE).single();
    }

    @Override
    public List<Step> stepsCurrentlyRunning(String stepType) {
        return this.tmpl.query(Step.class, "select step from Step step where step.type = :stepType and step.progress.status = :status").param("stepType", (Object)stepType).param("status", (Object)ExecutionStatus.RUNNING).list();
    }

    @Override
    public void stopCreatedSteps(String planId) {
        this.tmpl.query("update Step step set step.progress.status=:newStatus where step.progress.status=:oldStatus and step.plan.id = :planId").param("newStatus", (Object)ExecutionStatus.STOPPED).param("oldStatus", (Object)ExecutionStatus.CREATED).param("planId", (Object)planId).update();
    }

    @Override
    public List<Step> getStepsByTaskId(String taskId) {
        return this.tmpl.query(Step.class, "select step from Step step where step.task.id=:taskId").param("taskId", (Object)taskId).list();
    }

    @Override
    public List<Step> getRunningStepsForPlan(String planId) {
        return this.tmpl.query(Step.class, "select step from Step step where step.plan.id = :planId and step.progress.status = :status").param("planId", (Object)planId).param("status", (Object)ExecutionStatus.RUNNING).list();
    }

    @Override
    public List<String> getStepIdsForPlan(String planId) {
        return this.tmpl.query(String.class, "select step.id from Step step where step.plan.id = :planId").param("planId", (Object)planId).list();
    }

    @Override
    public List<Step> getCreatedStepsOfType(String planId, StepType stepType, int maxResults) {
        return this.tmpl.query(Step.class, "select step from Step step where step.plan.id = :planId and step.progress.status = :created and step.type = :stepType").param("planId", (Object)planId).param("stepType", (Object)stepType.toString()).param("created", (Object)ExecutionStatus.CREATED).max(maxResults).list();
    }

    @Override
    public List<Step> getHungStepsForPlan(String planId, Instant currentTime, long toleranceMillis) {
        Instant cutoff = currentTime.minusMillis(toleranceMillis);
        return this.tmpl.query(Step.class, "select step from Step step where step.plan.id = :planId and step.nodeHeartbeat < :heartbeatTime and step.progress.status in (:statuses)").param("planId", (Object)planId).param("heartbeatTime", (Object)cutoff).param("statuses", Arrays.asList(ExecutionStatus.VALIDATING, ExecutionStatus.RUNNING, ExecutionStatus.STOPPING)).list();
    }

    @Override
    public int setNodeHeartbeat(Set<String> executionIds, Instant heartbeatTime) {
        return this.tmpl.query("update Step s set s.nodeHeartbeat = :heartbeatTime where s.nodeExecutionId in (:executionIds)").param("heartbeatTime", (Object)heartbeatTime).param("executionIds", executionIds).update();
    }

    @Override
    public Optional<Step> getStep(String planId, StepType stepType) {
        return this.tmpl.query(Step.class, "select step from Step step where step.plan.id = :planId and step.type = :stepType").param("planId", (Object)planId).param("stepType", (Object)stepType.toString()).first();
    }
}

