/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.durableexecutor.impl;

import com.hazelcast.config.DurableExecutorConfig;
import com.hazelcast.durableexecutor.impl.DurableExecutorContainer;
import com.hazelcast.durableexecutor.impl.TaskRingBuffer;
import com.hazelcast.durableexecutor.impl.operations.ReplicationOperation;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.NodeEngineImpl;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DurableExecutorPartitionContainer {
    private final int partitionId;
    private final NodeEngineImpl nodeEngine;
    private final Map<String, DurableExecutorContainer> executorContainerMap = new HashMap<String, DurableExecutorContainer>();

    public DurableExecutorPartitionContainer(NodeEngineImpl nodeEngine, int partitionId) {
        this.nodeEngine = nodeEngine;
        this.partitionId = partitionId;
    }

    public DurableExecutorContainer getOrCreateContainer(String name) {
        DurableExecutorContainer executorContainer = this.executorContainerMap.get(name);
        if (executorContainer == null) {
            executorContainer = this.createExecutorContainer(name);
            this.executorContainerMap.put(name, executorContainer);
        }
        return executorContainer;
    }

    public void createExecutorContainer(String name, TaskRingBuffer ringBuffer) {
        DurableExecutorConfig durableExecutorConfig = this.nodeEngine.getConfig().findDurableExecutorConfig(name);
        int durability = durableExecutorConfig.getDurability();
        this.executorContainerMap.put(name, new DurableExecutorContainer(this.nodeEngine, name, this.partitionId, durability, ringBuffer));
    }

    public Operation prepareReplicationOperation(int replicaIndex) {
        HashMap<String, DurableExecutorContainer> map = new HashMap<String, DurableExecutorContainer>();
        for (DurableExecutorContainer executorContainer : this.executorContainerMap.values()) {
            if (replicaIndex > executorContainer.getDurability()) continue;
            map.put(executorContainer.getName(), executorContainer);
        }
        return new ReplicationOperation(map);
    }

    public void clearRingBuffersHavingLesserBackupCountThan(int thresholdReplicaIndex) {
        if (thresholdReplicaIndex < 0) {
            this.executorContainerMap.clear();
        }
        Iterator<DurableExecutorContainer> iterator = this.executorContainerMap.values().iterator();
        while (iterator.hasNext()) {
            DurableExecutorContainer executorContainer = iterator.next();
            if (thresholdReplicaIndex <= executorContainer.getDurability()) continue;
            iterator.remove();
        }
    }

    public void executeAll() {
        for (DurableExecutorContainer container : this.executorContainerMap.values()) {
            container.executeAll();
        }
    }

    public void removeContainer(String name) {
        this.executorContainerMap.remove(name);
    }

    DurableExecutorContainer getExistingExecutorContainer(String name) {
        return this.executorContainerMap.get(name);
    }

    private DurableExecutorContainer createExecutorContainer(String name) {
        DurableExecutorConfig durableExecutorConfig = this.nodeEngine.getConfig().findDurableExecutorConfig(name);
        int durability = durableExecutorConfig.getDurability();
        int ringBufferCapacity = durableExecutorConfig.getCapacity();
        TaskRingBuffer ringBuffer = new TaskRingBuffer(ringBufferCapacity);
        return new DurableExecutorContainer(this.nodeEngine, name, this.partitionId, durability, ringBuffer);
    }
}

