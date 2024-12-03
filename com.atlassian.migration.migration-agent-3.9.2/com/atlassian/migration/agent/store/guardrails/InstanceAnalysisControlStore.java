/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.store.guardrails;

import com.atlassian.migration.agent.entity.InstanceAnalysisControl;
import java.util.Optional;

public interface InstanceAnalysisControlStore {
    public boolean isFinished(Long var1);

    public InstanceAnalysisControl createInstanceAnalysisControl(String var1);

    public void completeInstanceAnalysisControl(String var1);

    public void updateInstanceAnalysisControl(InstanceAnalysisControl var1);

    public Optional<InstanceAnalysisControl> findInstanceAnalysisControl(String var1);
}

