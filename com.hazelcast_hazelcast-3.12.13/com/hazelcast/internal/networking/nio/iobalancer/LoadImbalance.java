/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking.nio.iobalancer;

import com.hazelcast.internal.networking.nio.MigratablePipeline;
import com.hazelcast.internal.networking.nio.NioThread;
import com.hazelcast.util.ItemCounter;
import java.util.Map;
import java.util.Set;

class LoadImbalance {
    long maximumLoad;
    long minimumLoad;
    NioThread srcOwner;
    NioThread dstOwner;
    private final Map<NioThread, Set<MigratablePipeline>> ownerToPipelines;
    private final ItemCounter<MigratablePipeline> pipelineLoadCounter;

    LoadImbalance(Map<NioThread, Set<MigratablePipeline>> ownerToPipelines, ItemCounter<MigratablePipeline> pipelineLoadCounter) {
        this.ownerToPipelines = ownerToPipelines;
        this.pipelineLoadCounter = pipelineLoadCounter;
    }

    Set<MigratablePipeline> getPipelinesOwnedBy(NioThread owner) {
        return this.ownerToPipelines.get(owner);
    }

    long getLoad(MigratablePipeline pipeline) {
        return this.pipelineLoadCounter.get(pipeline);
    }
}

