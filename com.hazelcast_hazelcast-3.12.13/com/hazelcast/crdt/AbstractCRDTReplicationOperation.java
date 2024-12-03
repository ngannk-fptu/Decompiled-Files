/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.crdt;

import com.hazelcast.crdt.CRDTDataSerializerHook;
import com.hazelcast.crdt.CRDTReplicationAwareService;
import com.hazelcast.crdt.CRDTReplicationMigrationService;
import com.hazelcast.internal.partition.MigrationCycleOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.MapUtil;
import java.io.IOException;
import java.util.Map;

public abstract class AbstractCRDTReplicationOperation<T extends IdentifiedDataSerializable>
extends Operation
implements IdentifiedDataSerializable,
MigrationCycleOperation {
    private Map<String, T> replicationData;

    protected AbstractCRDTReplicationOperation() {
    }

    public AbstractCRDTReplicationOperation(Map<String, T> replicationData) {
        this.replicationData = replicationData;
    }

    @Override
    public void run() throws Exception {
        CRDTReplicationAwareService service = (CRDTReplicationAwareService)this.getService();
        for (Map.Entry<String, T> entry : this.replicationData.entrySet()) {
            service.merge(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void afterRun() throws Exception {
        super.afterRun();
        CRDTReplicationMigrationService replicationMigrationService = (CRDTReplicationMigrationService)this.getNodeEngine().getService("hz:impl:CRDTReplicationMigrationService");
        replicationMigrationService.scheduleMigrationTask(0L);
    }

    @Override
    public int getFactoryId() {
        return CRDTDataSerializerHook.F_ID;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeInt(this.replicationData.size());
        for (Map.Entry<String, T> entry : this.replicationData.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeObject(entry.getValue());
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        int mapSize = in.readInt();
        this.replicationData = MapUtil.createHashMap(mapSize);
        for (int i = 0; i < mapSize; ++i) {
            String name = in.readUTF();
            IdentifiedDataSerializable crdt = (IdentifiedDataSerializable)in.readObject();
            this.replicationData.put(name, crdt);
        }
    }
}

