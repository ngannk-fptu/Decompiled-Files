/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.execution;

import com.atlassian.migration.agent.service.execution.CancellableFuture;
import com.atlassian.migration.agent.service.execution.StepExecutor;
import com.atlassian.migration.agent.service.stepexecutor.StepResult;

public interface AsyncStepExecutor
extends StepExecutor {
    public CancellableFuture<StepResult> runStepAsync(String var1);
}

