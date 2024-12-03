/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service;

import com.atlassian.migration.agent.entity.Plan;
import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.logging.LoggingContextBuilder;
import com.atlassian.migration.agent.store.PlanStore;
import com.atlassian.migration.agent.store.StepStore;
import com.atlassian.migration.agent.store.tx.PluginTransactionTemplate;

public class LoggingContextProvider {
    private final PluginTransactionTemplate ptx;
    private final PlanStore planStore;
    private final StepStore stepStore;

    public LoggingContextProvider(PluginTransactionTemplate ptx, PlanStore planStore, StepStore stepStore) {
        this.ptx = ptx;
        this.planStore = planStore;
        this.stepStore = stepStore;
    }

    public LoggingContextBuilder forStep(String stepId) {
        return this.ptx.read(() -> {
            Step step = this.stepStore.getStep(stepId);
            Plan plan = step.getPlan();
            return LoggingContextBuilder.logCtx().withStep(step).withTask(step.getTask()).withPlan(plan).withCloudSite(plan.getCloudSite());
        });
    }

    public LoggingContextBuilder forPlan(String planId) {
        return this.ptx.read(() -> LoggingContextBuilder.logCtx().withPlan(this.planStore.getPlan(planId)));
    }
}

