/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl.operation;

import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.replicatedmap.impl.PartitionContainer;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.operation.AbstractSerializableOperation;
import com.hazelcast.replicatedmap.impl.operation.SyncReplicatedMapDataOperation;
import com.hazelcast.replicatedmap.impl.record.RecordMigrationInfo;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecord;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public class RequestMapDataOperation
extends AbstractSerializableOperation {
    private String name;

    public RequestMapDataOperation() {
    }

    public RequestMapDataOperation(String name) {
        this.name = name;
    }

    @Override
    public void run() throws Exception {
        ILogger logger = this.getLogger();
        Address callerAddress = this.getCallerAddress();
        int partitionId = this.getPartitionId();
        NodeEngine nodeEngine = this.getNodeEngine();
        if (logger.isFineEnabled()) {
            logger.fine("Caller " + callerAddress + " requested copy of replicated map '" + this.name + "' (partitionId " + partitionId + ") from " + nodeEngine.getThisAddress());
        }
        ReplicatedMapService service = (ReplicatedMapService)this.getService();
        PartitionContainer container = service.getPartitionContainer(partitionId);
        ReplicatedRecordStore store = container.getOrCreateRecordStore(this.name);
        store.setLoaded(true);
        if (nodeEngine.getThisAddress().equals(callerAddress)) {
            return;
        }
        long version = store.getVersion();
        Set<RecordMigrationInfo> recordSet = this.getRecordSet(store);
        Operation op = new SyncReplicatedMapDataOperation(this.name, recordSet, version).setPartitionId(partitionId).setValidateTarget(false);
        OperationService operationService = nodeEngine.getOperationService();
        operationService.createInvocationBuilder("hz:impl:replicatedMapService", op, callerAddress).setTryCount(3).invoke();
    }

    private Set<RecordMigrationInfo> getRecordSet(ReplicatedRecordStore store) {
        SerializationService serializationService = this.getNodeEngine().getSerializationService();
        Set<RecordMigrationInfo> recordSet = SetUtil.createHashSet(store.size());
        Iterator<ReplicatedRecord> iterator = store.recordIterator();
        while (iterator.hasNext()) {
            ReplicatedRecord record = iterator.next();
            Object dataKey = serializationService.toData(record.getKeyInternal());
            Object dataValue = serializationService.toData(record.getValueInternal());
            recordSet.add(new RecordMigrationInfo((Data)dataKey, (Data)dataValue, record.getTtlMillis()));
        }
        return recordSet;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
    }

    @Override
    public int getId() {
        return 20;
    }
}

