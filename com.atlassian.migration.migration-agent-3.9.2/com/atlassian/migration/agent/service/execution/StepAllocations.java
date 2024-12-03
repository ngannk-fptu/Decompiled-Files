/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.ListMultimap
 *  javax.annotation.Nullable
 *  lombok.Generated
 *  org.apache.commons.collections.CollectionUtils
 */
package com.atlassian.migration.agent.service.execution;

import com.atlassian.migration.agent.entity.Step;
import com.atlassian.migration.agent.service.ClusterLimits;
import com.atlassian.migration.agent.service.impl.StepType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import lombok.Generated;
import org.apache.commons.collections.CollectionUtils;

final class StepAllocations {
    private final Map<String, NodeStepAllocations> allocationsByNode;

    StepAllocations(Map<String, NodeStepAllocations> allocationsByNode) {
        this.allocationsByNode = allocationsByNode;
    }

    Collection<NodeStepAllocations> getAllAllocations() {
        return this.allocationsByNode.values();
    }

    void addStepToNode(String nodeId, Step step) {
        NodeStepAllocations stepAllocations = this.allocationsByNode.get(nodeId);
        if (stepAllocations == null) {
            throw new IllegalArgumentException("The given node does not exist.");
        }
        stepAllocations.addStepToNode(step);
    }

    static final class NodeStepAllocations {
        private final String nodeId;
        private final ListMultimap<StepType, Step> stepsByType;

        NodeStepAllocations(String nodeId, @Nullable List<Step> steps) {
            this.nodeId = nodeId;
            this.stepsByType = ArrayListMultimap.create();
            if (!CollectionUtils.isEmpty(steps)) {
                for (Step step : steps) {
                    StepType stepType = StepType.valueOf(step.getType());
                    this.stepsByType.put((Object)stepType, (Object)step);
                }
            }
        }

        NodeStepAllocations(String nodeId) {
            this(nodeId, null);
        }

        int getCurrentAllocationCountForType(StepType stepType) {
            return this.stepsByType.get((Object)stepType).size();
        }

        int getFreeCapacityForType(StepType stepType, ClusterLimits clusterLimits) {
            int nodeLimit = Math.min(clusterLimits.getConcurrencyPerNodeLimit(stepType), clusterLimits.getClusterConcurrencyLimit(stepType));
            int currentAllocationCount = this.stepsByType.get((Object)stepType).size();
            return nodeLimit - currentAllocationCount;
        }

        boolean hasSpareCapacity(StepType stepType, ClusterLimits clusterLimits) {
            return this.getFreeCapacityForType(stepType, clusterLimits) > 0;
        }

        int getTotalAllocationCount() {
            return this.stepsByType.values().size();
        }

        void addStepToNode(Step step) {
            StepType stepType = StepType.valueOf(step.getType());
            this.stepsByType.put((Object)stepType, (Object)step);
        }

        @Generated
        public String getNodeId() {
            return this.nodeId;
        }
    }
}

