/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store;

import com.atlassian.migration.agent.entity.ExecutionStatus;
import com.atlassian.migration.agent.entity.Plan;
import com.atlassian.migration.agent.entity.PlanActiveStatus;
import com.atlassian.migration.agent.entity.PlanSchedulerVersion;
import java.util.List;
import java.util.Set;

public interface PlanStore {
    public Plan getPlan(String var1);

    public Plan getPlanAndLock(String var1);

    public List<Plan> getAllPlans(Set<PlanActiveStatus> var1);

    public List<Plan> getAllPlansByCloudId(String var1);

    public List<String> getPlanIdsInStatusForSchedulerVersion(List<ExecutionStatus> var1, PlanSchedulerVersion var2);

    public Plan createPlan(Plan var1);

    public void updatePlan(Plan var1);

    public void deletePlan(String var1);

    public void removeTasks(Plan var1);

    public boolean hasPlans(ExecutionStatus ... var1);

    public boolean planNameExists(String var1, String var2);

    public List<String> getPlanNamesStartingWithPrefix(String var1);

    public boolean hasPlansRunningOrStopping();
}

