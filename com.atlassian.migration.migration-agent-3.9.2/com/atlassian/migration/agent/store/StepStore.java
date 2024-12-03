/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store;

import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.service.impl.StepType;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StepStore {
    public Step getStep(String var1);

    public void addSteps(Collection<Step> var1);

    public void update(Step var1);

    public Step getAndLock(String var1);

    public List<Step> stepsCurrentlyRunning(String var1);

    public void stopCreatedSteps(String var1);

    public List<Step> getStepsByTaskId(String var1);

    public List<String> getStepIdsForPlan(String var1);

    public List<Step> getRunningStepsForPlan(String var1);

    public List<Step> getCreatedStepsOfType(String var1, StepType var2, int var3);

    public int setNodeHeartbeat(Set<String> var1, Instant var2);

    public List<Step> getHungStepsForPlan(String var1, Instant var2, long var3);

    public Optional<Step> getStep(String var1, StepType var2);
}

