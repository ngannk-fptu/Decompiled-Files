/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.atomiclong.operations;

import com.hazelcast.concurrent.atomiclong.AtomicLongContainer;
import com.hazelcast.concurrent.atomiclong.AtomicLongDataSerializerHook;
import com.hazelcast.concurrent.atomiclong.AtomicLongService;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.MapUtil;
import java.io.IOException;
import java.util.Map;

public class AtomicLongReplicationOperation
extends Operation
implements IdentifiedDataSerializable {
    private Map<String, Long> migrationData;

    public AtomicLongReplicationOperation() {
    }

    public AtomicLongReplicationOperation(Map<String, Long> migrationData) {
        this.migrationData = migrationData;
    }

    @Override
    public void run() throws Exception {
        AtomicLongService atomicLongService = (AtomicLongService)this.getService();
        for (Map.Entry<String, Long> longEntry : this.migrationData.entrySet()) {
            String name = longEntry.getKey();
            AtomicLongContainer container = atomicLongService.getLongContainer(name);
            Long value = longEntry.getValue();
            container.set(value);
        }
    }

    @Override
    public String getServiceName() {
        return "hz:impl:atomicLongService";
    }

    @Override
    public int getFactoryId() {
        return AtomicLongDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 12;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeInt(this.migrationData.size());
        for (Map.Entry<String, Long> entry : this.migrationData.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeLong(entry.getValue());
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        int mapSize = in.readInt();
        this.migrationData = MapUtil.createHashMap(mapSize);
        for (int i = 0; i < mapSize; ++i) {
            String name = in.readUTF();
            Long longContainer = in.readLong();
            this.migrationData.put(name, longContainer);
        }
    }
}

