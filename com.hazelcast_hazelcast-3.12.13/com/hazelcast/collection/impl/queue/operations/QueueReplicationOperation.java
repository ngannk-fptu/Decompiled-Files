/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.queue.operations;

import com.hazelcast.collection.impl.queue.QueueContainer;
import com.hazelcast.collection.impl.queue.QueueDataSerializerHook;
import com.hazelcast.collection.impl.queue.QueueService;
import com.hazelcast.config.Config;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.MapUtil;
import java.io.IOException;
import java.util.Map;

public class QueueReplicationOperation
extends Operation
implements IdentifiedDataSerializable {
    private Map<String, QueueContainer> migrationData;

    public QueueReplicationOperation() {
    }

    public QueueReplicationOperation(Map<String, QueueContainer> migrationData, int partitionId, int replicaIndex) {
        this.setPartitionId(partitionId).setReplicaIndex(replicaIndex);
        this.migrationData = migrationData;
    }

    @Override
    public void run() {
        QueueService service = (QueueService)this.getService();
        NodeEngine nodeEngine = this.getNodeEngine();
        Config config = nodeEngine.getConfig();
        for (Map.Entry<String, QueueContainer> entry : this.migrationData.entrySet()) {
            String name = entry.getKey();
            QueueContainer container = entry.getValue();
            QueueConfig conf = config.findQueueConfig(name);
            container.setConfig(conf, nodeEngine, service);
            service.addContainer(name, container);
        }
    }

    @Override
    public String getServiceName() {
        return "hz:impl:queueService";
    }

    @Override
    public int getFactoryId() {
        return QueueDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 18;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeInt(this.migrationData.size());
        for (Map.Entry<String, QueueContainer> entry : this.migrationData.entrySet()) {
            out.writeUTF(entry.getKey());
            QueueContainer container = entry.getValue();
            container.writeData(out);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        int mapSize = in.readInt();
        this.migrationData = MapUtil.createHashMap(mapSize);
        for (int i = 0; i < mapSize; ++i) {
            String name = in.readUTF();
            QueueContainer container = new QueueContainer(name);
            container.readData(in);
            this.migrationData.put(name, container);
        }
    }
}

