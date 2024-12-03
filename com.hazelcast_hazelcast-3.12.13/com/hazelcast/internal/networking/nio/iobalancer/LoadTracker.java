/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.networking.nio.iobalancer;

import com.hazelcast.internal.networking.nio.MigratablePipeline;
import com.hazelcast.internal.networking.nio.NioThread;
import com.hazelcast.internal.networking.nio.iobalancer.LoadImbalance;
import com.hazelcast.logging.ILogger;
import com.hazelcast.util.ItemCounter;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.StringUtil;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class LoadTracker {
    private final ILogger logger;
    private final NioThread[] ioThreads;
    private final Map<NioThread, Set<MigratablePipeline>> ownerToPipelines;
    private final ItemCounter<MigratablePipeline> lastLoadCounter = new ItemCounter();
    private final ItemCounter<NioThread> ownerLoad = new ItemCounter();
    private final ItemCounter<MigratablePipeline> pipelineLoadCount = new ItemCounter();
    private final Set<MigratablePipeline> pipelines = new HashSet<MigratablePipeline>();
    private final LoadImbalance imbalance;

    LoadTracker(NioThread[] ioThreads, ILogger logger) {
        this.logger = logger;
        this.ioThreads = new NioThread[ioThreads.length];
        System.arraycopy(ioThreads, 0, this.ioThreads, 0, ioThreads.length);
        this.ownerToPipelines = MapUtil.createHashMap(ioThreads.length);
        for (NioThread selector : ioThreads) {
            this.ownerToPipelines.put(selector, new HashSet());
        }
        this.imbalance = new LoadImbalance(this.ownerToPipelines, this.pipelineLoadCount);
    }

    LoadImbalance updateImbalance() {
        this.clearWorkingImbalance();
        this.updateNewWorkingImbalance();
        this.updateNewFinalImbalance();
        this.printDebugTable();
        return this.imbalance;
    }

    Set<MigratablePipeline> getPipelines() {
        return this.pipelines;
    }

    ItemCounter<MigratablePipeline> getLastLoadCounter() {
        return this.lastLoadCounter;
    }

    ItemCounter<MigratablePipeline> getPipelineLoadCount() {
        return this.pipelineLoadCount;
    }

    private void updateNewFinalImbalance() {
        this.imbalance.minimumLoad = Long.MAX_VALUE;
        this.imbalance.maximumLoad = Long.MIN_VALUE;
        this.imbalance.srcOwner = null;
        this.imbalance.dstOwner = null;
        for (NioThread owner : this.ioThreads) {
            long load = this.ownerLoad.get(owner);
            int pipelineCount = this.ownerToPipelines.get(owner).size();
            if (load > this.imbalance.maximumLoad && pipelineCount > 1) {
                this.imbalance.maximumLoad = load;
                this.imbalance.srcOwner = owner;
            }
            if (load >= this.imbalance.minimumLoad) continue;
            this.imbalance.minimumLoad = load;
            this.imbalance.dstOwner = owner;
        }
    }

    private void updateNewWorkingImbalance() {
        for (MigratablePipeline pipeline : this.pipelines) {
            this.updatePipelineState(pipeline);
        }
    }

    private void updatePipelineState(MigratablePipeline pipeline) {
        long pipelineLoad = this.getLoadSinceLastCheck(pipeline);
        this.pipelineLoadCount.set(pipeline, pipelineLoad);
        NioThread owner = pipeline.owner();
        if (owner == null) {
            return;
        }
        this.ownerLoad.add(owner, pipelineLoad);
        this.ownerToPipelines.get(owner).add(pipeline);
    }

    private long getLoadSinceLastCheck(MigratablePipeline pipeline) {
        long load = pipeline.load();
        long lastLoad = this.lastLoadCounter.getAndSet(pipeline, load);
        return load - lastLoad;
    }

    private void clearWorkingImbalance() {
        this.pipelineLoadCount.reset();
        this.ownerLoad.reset();
        for (Set<MigratablePipeline> pipelines : this.ownerToPipelines.values()) {
            pipelines.clear();
        }
    }

    void addPipeline(MigratablePipeline pipeline) {
        this.pipelines.add(pipeline);
    }

    void removePipeline(MigratablePipeline pipeline) {
        this.pipelines.remove(pipeline);
        this.pipelineLoadCount.remove(pipeline);
        this.lastLoadCounter.remove(pipeline);
    }

    private void printDebugTable() {
        if (!this.logger.isFinestEnabled()) {
            return;
        }
        NioThread minThread = this.imbalance.dstOwner;
        NioThread maxThread = this.imbalance.srcOwner;
        if (minThread == null || maxThread == null) {
            return;
        }
        StringBuilder sb = new StringBuilder(StringUtil.LINE_SEPARATOR).append("------------").append(StringUtil.LINE_SEPARATOR);
        Long loadPerOwner = this.ownerLoad.get(minThread);
        sb.append("Min NioThread ").append(minThread).append(" receive-load ").append(loadPerOwner).append(" load. ");
        sb.append("It contains following pipelines: ").append(StringUtil.LINE_SEPARATOR);
        this.appendSelectorInfo(minThread, this.ownerToPipelines, sb);
        loadPerOwner = this.ownerLoad.get(maxThread);
        sb.append("Max NioThread ").append(maxThread).append(" receive-load ").append(loadPerOwner);
        sb.append("It contains following pipelines: ").append(StringUtil.LINE_SEPARATOR);
        this.appendSelectorInfo(maxThread, this.ownerToPipelines, sb);
        sb.append("Other NioThread: ").append(StringUtil.LINE_SEPARATOR);
        for (NioThread thread : this.ioThreads) {
            if (thread.equals(minThread) || thread.equals(maxThread)) continue;
            loadPerOwner = this.ownerLoad.get(thread);
            sb.append("NioThread ").append(thread).append(" contains ").append(loadPerOwner).append(" and has these pipelines: ").append(StringUtil.LINE_SEPARATOR);
            this.appendSelectorInfo(thread, this.ownerToPipelines, sb);
        }
        sb.append("------------").append(StringUtil.LINE_SEPARATOR);
        this.logger.finest(sb.toString());
    }

    private void appendSelectorInfo(NioThread minThread, Map<NioThread, Set<MigratablePipeline>> pipelinesPerOwner, StringBuilder sb) {
        Set<MigratablePipeline> pipelines = pipelinesPerOwner.get(minThread);
        for (MigratablePipeline pipeline : pipelines) {
            Long loadPerPipeline = this.pipelineLoadCount.get(pipeline);
            sb.append(pipeline).append(":  ").append(loadPerPipeline).append(StringUtil.LINE_SEPARATOR);
        }
        sb.append(StringUtil.LINE_SEPARATOR);
    }
}

