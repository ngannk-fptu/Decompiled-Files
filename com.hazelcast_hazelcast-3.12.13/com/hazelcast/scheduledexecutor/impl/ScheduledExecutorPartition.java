/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl;

import com.hazelcast.config.ScheduledExecutorConfig;
import com.hazelcast.logging.ILogger;
import com.hazelcast.scheduledexecutor.impl.AbstractScheduledExecutorContainerHolder;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorContainer;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskDescriptor;
import com.hazelcast.scheduledexecutor.impl.operations.ReplicationOperation;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.MapUtil;
import java.util.Iterator;
import java.util.Map;

public class ScheduledExecutorPartition
extends AbstractScheduledExecutorContainerHolder {
    private final ILogger logger;
    private final int partitionId;
    private final ConstructorFunction<String, ScheduledExecutorContainer> containerConstructorFunction = new ConstructorFunction<String, ScheduledExecutorContainer>(){

        @Override
        public ScheduledExecutorContainer createNew(String name) {
            if (ScheduledExecutorPartition.this.logger.isFinestEnabled()) {
                ScheduledExecutorPartition.this.logger.finest("[Partition:" + ScheduledExecutorPartition.this.partitionId + "]Create new scheduled executor container with name:" + name);
            }
            ScheduledExecutorConfig config = ScheduledExecutorPartition.this.nodeEngine.getConfig().findScheduledExecutorConfig(name);
            return new ScheduledExecutorContainer(name, ScheduledExecutorPartition.this.partitionId, ScheduledExecutorPartition.this.nodeEngine, config.getDurability(), config.getCapacity());
        }
    };

    ScheduledExecutorPartition(NodeEngine nodeEngine, int partitionId) {
        super(nodeEngine);
        this.logger = nodeEngine.getLogger(this.getClass());
        this.partitionId = partitionId;
    }

    public Operation prepareReplicationOperation(int replicaIndex, boolean migrationMode) {
        Map<String, Map<String, ScheduledTaskDescriptor>> map = MapUtil.createHashMap(this.containers.size());
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("[Partition: " + this.partitionId + "] Prepare replication(migration: " + migrationMode + ") for index: " + replicaIndex);
        }
        for (ScheduledExecutorContainer container : this.containers.values()) {
            if (replicaIndex > container.getDurability()) continue;
            map.put(container.getName(), container.prepareForReplication(migrationMode));
        }
        return new ReplicationOperation(map);
    }

    @Override
    public ConstructorFunction<String, ScheduledExecutorContainer> getContainerConstructorFunction() {
        return this.containerConstructorFunction;
    }

    void disposeObsoleteReplicas(int thresholdReplicaIndex) {
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("[Partition: " + this.partitionId + "] Dispose obsolete replicas with thresholdReplicaIndex: " + thresholdReplicaIndex);
        }
        if (thresholdReplicaIndex < 0) {
            for (ScheduledExecutorContainer container : this.containers.values()) {
                container.destroy();
            }
            this.containers.clear();
        } else {
            Iterator iterator = this.containers.values().iterator();
            while (iterator.hasNext()) {
                ScheduledExecutorContainer container = (ScheduledExecutorContainer)iterator.next();
                if (thresholdReplicaIndex <= container.getDurability()) continue;
                container.destroy();
                iterator.remove();
            }
        }
    }

    void promoteSuspended() {
        if (this.logger.isFinestEnabled()) {
            this.logger.finest("[Partition: " + this.partitionId + "] Promote suspended");
        }
        for (ScheduledExecutorContainer container : this.containers.values()) {
            container.promoteSuspended();
        }
    }
}

