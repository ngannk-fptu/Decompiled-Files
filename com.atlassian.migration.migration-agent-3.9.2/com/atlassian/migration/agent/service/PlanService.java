/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service;

import com.atlassian.migration.agent.dto.AppsProgressDto;
import com.atlassian.migration.agent.dto.PlanDto;
import com.atlassian.migration.agent.dto.ProgressDto;
import com.atlassian.migration.agent.entity.ExecutionStatus;
import com.atlassian.migration.agent.entity.PlanActiveStatus;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface PlanService {
    @Nonnull
    public List<PlanDto> getAllPlans();

    public PlanDto getPlan(String var1);

    public boolean planNameExists(String var1, String var2);

    public ProgressDto getPlanProgress(String var1);

    public Optional<AppsProgressDto> getAppsProgress(String var1);

    @Nonnull
    public PlanDto createPlan(PlanDto var1, boolean var2);

    @Nonnull
    public PlanDto createPlan(PlanDto var1);

    public PlanDto updatePlan(PlanDto var1);

    public PlanDto copyPlan(String var1);

    public PlanDto updateActiveStatus(String var1, PlanActiveStatus var2);

    public boolean stop(String var1);

    public boolean hasPlans(ExecutionStatus ... var1);

    public void setCreatedStatus(String var1);

    public void startPlan(String var1);

    public PlanDto verifyPlan(String var1);

    public boolean deletePlan(String var1);

    public boolean isAnyOtherPlanRunning(String var1);

    public boolean hasPlansRunningOrStopping();
}

