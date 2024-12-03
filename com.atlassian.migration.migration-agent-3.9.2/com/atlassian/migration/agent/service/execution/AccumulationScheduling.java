/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.execution;

import com.atlassian.migration.agent.service.ClusterLimits;
import com.atlassian.migration.agent.service.execution.SchedulingAlgorithm;
import com.atlassian.migration.agent.service.execution.StepAllocations;
import com.atlassian.migration.agent.service.impl.StepType;
import java.util.Comparator;
import java.util.Optional;

public class AccumulationScheduling
implements SchedulingAlgorithm {
    @Override
    public Optional<String> findBestNode(StepAllocations currentAllocations, ClusterLimits clusterLimits, StepType stepType) {
        Optional<String> existingNode = currentAllocations.getAllAllocations().stream().filter(nodeAllocations -> nodeAllocations.getCurrentAllocationCountForType(stepType) > 0).findFirst().map(StepAllocations.NodeStepAllocations::getNodeId);
        if (!existingNode.isPresent()) {
            return currentAllocations.getAllAllocations().stream().min(Comparator.comparing(StepAllocations.NodeStepAllocations::getTotalAllocationCount)).map(StepAllocations.NodeStepAllocations::getNodeId);
        }
        return existingNode;
    }
}

