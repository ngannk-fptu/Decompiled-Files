/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomicreference.operations;

import com.hazelcast.concurrent.atomicreference.AtomicReferenceContainer;
import com.hazelcast.concurrent.atomicreference.AtomicReferenceDataSerializerHook;
import com.hazelcast.concurrent.atomicreference.AtomicReferenceService;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.MapUtil;
import java.io.IOException;
import java.util.Map;

public class AtomicReferenceReplicationOperation
extends Operation
implements IdentifiedDataSerializable {
    private Map<String, Data> migrationData;

    public AtomicReferenceReplicationOperation() {
    }

    public AtomicReferenceReplicationOperation(Map<String, Data> migrationData) {
        this.migrationData = migrationData;
    }

    @Override
    public void run() throws Exception {
        AtomicReferenceService service = (AtomicReferenceService)this.getService();
        for (Map.Entry<String, Data> entry : this.migrationData.entrySet()) {
            String name = entry.getKey();
            AtomicReferenceContainer container = service.getReferenceContainer(name);
            Data value = entry.getValue();
            container.set(value);
        }
    }

    @Override
    public String getServiceName() {
        return "hz:impl:atomicReferenceService";
    }

    @Override
    public int getFactoryId() {
        return AtomicReferenceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 12;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeInt(this.migrationData.size());
        for (Map.Entry<String, Data> entry : this.migrationData.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeData(entry.getValue());
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        int mapSize = in.readInt();
        this.migrationData = MapUtil.createHashMap(mapSize);
        for (int i = 0; i < mapSize; ++i) {
            String name = in.readUTF();
            Data data = in.readData();
            this.migrationData.put(name, data);
        }
    }
}

