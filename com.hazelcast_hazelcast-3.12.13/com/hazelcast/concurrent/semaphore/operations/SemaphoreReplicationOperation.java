/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.semaphore.operations;

import com.hazelcast.concurrent.semaphore.SemaphoreContainer;
import com.hazelcast.concurrent.semaphore.SemaphoreDataSerializerHook;
import com.hazelcast.concurrent.semaphore.SemaphoreService;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import com.hazelcast.util.MapUtil;
import java.io.IOException;
import java.util.Map;

public class SemaphoreReplicationOperation
extends Operation
implements IdentifiedDataSerializable {
    private Map<String, SemaphoreContainer> migrationData;

    public SemaphoreReplicationOperation() {
    }

    public SemaphoreReplicationOperation(Map<String, SemaphoreContainer> migrationData) {
        this.migrationData = migrationData;
    }

    @Override
    public void run() throws Exception {
        SemaphoreService service = (SemaphoreService)this.getService();
        for (SemaphoreContainer semaphoreContainer : this.migrationData.values()) {
            semaphoreContainer.setInitialized();
        }
        service.insertMigrationData(this.migrationData);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:semaphoreService";
    }

    @Override
    public int getFactoryId() {
        return SemaphoreDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 14;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeInt(this.migrationData.size());
        for (Map.Entry<String, SemaphoreContainer> entry : this.migrationData.entrySet()) {
            String key = entry.getKey();
            SemaphoreContainer value = entry.getValue();
            out.writeUTF(key);
            value.writeData(out);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        this.migrationData = MapUtil.createHashMap(size);
        for (int i = 0; i < size; ++i) {
            String name = in.readUTF();
            SemaphoreContainer semaphoreContainer = new SemaphoreContainer();
            semaphoreContainer.readData(in);
            this.migrationData.put(name, semaphoreContainer);
        }
    }
}

