/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.execution;

import com.atlassian.migration.agent.service.ClusterLimits;
import com.atlassian.migration.agent.service.execution.StepAllocations;
import com.atlassian.migration.agent.service.impl.StepType;
import java.util.Optional;

public interface SchedulingAlgorithm {
    public Optional<String> findBestNode(StepAllocations var1, ClusterLimits var2, StepType var3);
}

