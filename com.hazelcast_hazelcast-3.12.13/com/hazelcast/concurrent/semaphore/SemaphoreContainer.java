/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.semaphore;

import com.hazelcast.concurrent.semaphore.SemaphoreDataSerializerHook;
import com.hazelcast.config.SemaphoreConfig;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.MapUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SemaphoreContainer
implements IdentifiedDataSerializable {
    public static final int INITIAL_CAPACITY = 10;
    private int available;
    private int partitionId;
    private Map<String, Integer> attachMap;
    private int backupCount;
    private int asyncBackupCount;
    private boolean initialized;

    public SemaphoreContainer() {
    }

    public SemaphoreContainer(int partitionId, SemaphoreConfig config) {
        this.partitionId = partitionId;
        this.backupCount = config.getBackupCount();
        this.asyncBackupCount = config.getAsyncBackupCount();
        this.available = config.getInitialPermits();
        this.attachMap = new HashMap<String, Integer>(10);
    }

    private void attach(String owner, int permitCount) {
        Integer attached = this.attachMap.get(owner);
        if (attached == null) {
            attached = 0;
        }
        this.attachMap.put(owner, attached + permitCount);
    }

    private void detach(String owner, int permitCount) {
        Integer attached = this.attachMap.get(owner);
        if (attached == null) {
            return;
        }
        if ((attached = Integer.valueOf(attached - permitCount)) <= 0) {
            this.attachMap.remove(owner);
        } else {
            this.attachMap.put(owner, attached);
        }
    }

    public boolean detachAll(String owner) {
        Integer attached = this.attachMap.remove(owner);
        if (attached != null) {
            this.available += attached.intValue();
            return true;
        }
        return false;
    }

    public boolean init(int permitCount) {
        if (this.initialized || this.available != 0) {
            return false;
        }
        this.available = permitCount;
        this.initialized = true;
        return true;
    }

    public int getAvailable() {
        return this.available;
    }

    public boolean isAvailable(int permitCount) {
        return this.available > 0 && this.available - permitCount >= 0;
    }

    public boolean acquire(String owner, int permitCount) {
        if (this.isAvailable(permitCount)) {
            this.available -= permitCount;
            this.attach(owner, permitCount);
            this.initialized = true;
            return true;
        }
        return false;
    }

    public int drain(String owner) {
        int drain = this.available;
        this.available = 0;
        if (drain > 0) {
            this.initialized = true;
            this.attach(owner, drain);
        }
        return drain;
    }

    public boolean increase(int permitCount) {
        if (permitCount == 0) {
            return false;
        }
        int newAvailable = this.available + permitCount;
        if (newAvailable < this.available) {
            return false;
        }
        this.available = newAvailable;
        return true;
    }

    public boolean reduce(int permitCount) {
        if (permitCount == 0) {
            return false;
        }
        int newAvailable = this.available - permitCount;
        if (newAvailable > this.available) {
            return false;
        }
        this.available = newAvailable;
        return true;
    }

    public void release(String owner, int permitCount) {
        this.available += permitCount;
        this.initialized = true;
        this.detach(owner, permitCount);
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    public int getSyncBackupCount() {
        return this.backupCount;
    }

    public int getAsyncBackupCount() {
        return this.asyncBackupCount;
    }

    public void setInitialized() {
        this.initialized = true;
    }

    public int getTotalBackupCount() {
        return this.backupCount + this.asyncBackupCount;
    }

    @Override
    public int getFactoryId() {
        return SemaphoreDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.available);
        out.writeInt(this.partitionId);
        out.writeInt(this.backupCount);
        out.writeInt(this.asyncBackupCount);
        out.writeInt(this.attachMap.size());
        for (Map.Entry<String, Integer> entry : this.attachMap.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeInt(entry.getValue());
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.available = in.readInt();
        this.partitionId = in.readInt();
        this.backupCount = in.readInt();
        this.asyncBackupCount = in.readInt();
        int size = in.readInt();
        this.attachMap = MapUtil.createHashMap(size);
        for (int i = 0; i < size; ++i) {
            String owner = in.readUTF();
            Integer val = in.readInt();
            this.attachMap.put(owner, val);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Permit");
        sb.append("{available=").append(this.available);
        sb.append(", partitionId=").append(this.partitionId);
        sb.append(", backupCount=").append(this.backupCount);
        sb.append(", asyncBackupCount=").append(this.asyncBackupCount);
        sb.append('}');
        sb.append("\n");
        for (Map.Entry<String, Integer> entry : this.attachMap.entrySet()) {
            sb.append("{owner=").append(entry.getKey());
            sb.append(", attached=").append(entry.getValue());
            sb.append("} ");
        }
        return sb.toString();
    }
}

