/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.execution;

import com.atlassian.migration.agent.service.impl.StepType;
import com.atlassian.migration.agent.service.stepexecutor.StepResult;

public interface StepExecutor {
    public StepType getStepType();

    public StepResult runStep(String var1);
}

