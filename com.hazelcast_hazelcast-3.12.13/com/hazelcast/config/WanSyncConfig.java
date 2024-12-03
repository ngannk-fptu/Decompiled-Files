/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.ConsistencyCheckStrategy;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class WanSyncConfig
implements IdentifiedDataSerializable {
    private ConsistencyCheckStrategy consistencyCheckStrategy = ConsistencyCheckStrategy.NONE;

    public ConsistencyCheckStrategy getConsistencyCheckStrategy() {
        return this.consistencyCheckStrategy;
    }

    public WanSyncConfig setConsistencyCheckStrategy(ConsistencyCheckStrategy consistencyCheckStrategy) {
        this.consistencyCheckStrategy = consistencyCheckStrategy;
        return this;
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 55;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeByte(this.consistencyCheckStrategy.getId());
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.consistencyCheckStrategy = ConsistencyCheckStrategy.getById(in.readByte());
    }

    public String toString() {
        return "WanSyncConfig{consistencyCheckStrategy=" + (Object)((Object)this.consistencyCheckStrategy) + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        WanSyncConfig that = (WanSyncConfig)o;
        return this.consistencyCheckStrategy == that.consistencyCheckStrategy;
    }

    public int hashCode() {
        return this.consistencyCheckStrategy != null ? this.consistencyCheckStrategy.hashCode() : 0;
    }
}

