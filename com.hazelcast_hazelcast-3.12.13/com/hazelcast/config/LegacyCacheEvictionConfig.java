/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.CacheEvictionConfig;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.TypedDataSerializable;
import java.io.IOException;

@BinaryInterface
public class LegacyCacheEvictionConfig
implements TypedDataSerializable {
    final CacheEvictionConfig config;

    public LegacyCacheEvictionConfig() {
        this.config = new CacheEvictionConfig();
    }

    public LegacyCacheEvictionConfig(CacheEvictionConfig evictionConfig) {
        this.config = evictionConfig;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.config.getSize());
        out.writeUTF(this.config.getMaxSizePolicy().toString());
        out.writeUTF(this.config.getEvictionPolicy().toString());
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.config.setSize(in.readInt());
        this.config.setMaximumSizePolicy(EvictionConfig.MaxSizePolicy.valueOf(in.readUTF()));
        this.config.setEvictionPolicy(EvictionPolicy.valueOf(in.readUTF()));
    }

    @Override
    public Class getClassType() {
        return CacheEvictionConfig.class;
    }

    public CacheEvictionConfig getConfig() {
        return this.config;
    }
}

