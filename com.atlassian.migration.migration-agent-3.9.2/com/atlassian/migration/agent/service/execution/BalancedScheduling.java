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

public class BalancedScheduling
implements SchedulingAlgorithm {
    @Override
    public Optional<String> findBestNode(StepAllocations currentAllocations, ClusterLimits clusterLimits, StepType stepType) {
        int clusterLimitForType = clusterLimits.getClusterConcurrencyLimit(stepType);
        int currentAllocation = currentAllocations.getAllAllocations().stream().mapToInt(node -> node.getCurrentAllocationCountForType(stepType)).sum();
        if (currentAllocation >= clusterLimitForType) {
            return Optional.empty();
        }
        return currentAllocations.getAllAllocations().stream().filter(node -> node.hasSpareCapacity(stepType, clusterLimits)).max(Comparator.comparingInt(node -> node.getFreeCapacityForType(stepType, clusterLimits))).map(StepAllocations.NodeStepAllocations::getNodeId);
    }
}

