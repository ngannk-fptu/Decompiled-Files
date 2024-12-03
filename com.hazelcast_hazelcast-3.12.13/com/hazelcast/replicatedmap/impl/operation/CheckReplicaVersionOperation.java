/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.operation;

import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.replicatedmap.impl.PartitionContainer;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.operation.AbstractSerializableOperation;
import com.hazelcast.replicatedmap.impl.operation.RequestMapDataOperation;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.util.MapUtil;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CheckReplicaVersionOperation
extends AbstractSerializableOperation
implements PartitionAwareOperation {
    private Map<String, Long> versions;

    public CheckReplicaVersionOperation() {
    }

    public CheckReplicaVersionOperation(PartitionContainer container) {
        ConcurrentMap<String, ReplicatedRecordStore> stores = container.getStores();
        this.versions = MapUtil.createConcurrentHashMap(stores.size());
        for (Map.Entry storeEntry : stores.entrySet()) {
            String name = (String)storeEntry.getKey();
            ReplicatedRecordStore store = (ReplicatedRecordStore)storeEntry.getValue();
            long version = store.getVersion();
            this.versions.put(name, version);
        }
    }

    @Override
    public void run() throws Exception {
        ILogger logger = this.getLogger();
        int partitionId = this.getPartitionId();
        ReplicatedMapService service = (ReplicatedMapService)this.getService();
        PartitionContainer container = service.getPartitionContainer(this.getPartitionId());
        ConcurrentMap<String, ReplicatedRecordStore> stores = container.getStores();
        for (Map.Entry<String, Long> entry : this.versions.entrySet()) {
            String name = entry.getKey();
            Long version = entry.getValue();
            ReplicatedRecordStore store = (ReplicatedRecordStore)stores.get(name);
            if (store == null) {
                if (logger.isFineEnabled()) {
                    logger.fine("Missing store on the replica of replicated map '" + name + "' (partitionId " + partitionId + ") (owner version " + version + ")");
                }
                this.requestDataFromOwner(name);
                continue;
            }
            if (!store.isStale(version)) continue;
            if (logger.isFineEnabled()) {
                logger.fine("Stale replica on replicated map '" + name + "' (partitionId " + partitionId + ") (owner version " + version + ") (replica version " + store.getVersion() + ")");
            }
            this.requestDataFromOwner(name);
        }
    }

    private void requestDataFromOwner(String name) {
        OperationService operationService = this.getNodeEngine().getOperationService();
        RequestMapDataOperation op = new RequestMapDataOperation(name);
        operationService.createInvocationBuilder("hz:impl:replicatedMapService", (Operation)op, this.getPartitionId()).setTryCount(3).invoke();
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeInt(this.versions.size());
        for (Map.Entry<String, Long> entry : this.versions.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeLong(entry.getValue());
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.versions = new ConcurrentHashMap<String, Long>();
        int size = in.readInt();
        for (int i = 0; i < size; ++i) {
            String name = in.readUTF();
            Long version = in.readLong();
            this.versions.put(name, version);
        }
    }

    @Override
    public int getId() {
        return 12;
    }
}

