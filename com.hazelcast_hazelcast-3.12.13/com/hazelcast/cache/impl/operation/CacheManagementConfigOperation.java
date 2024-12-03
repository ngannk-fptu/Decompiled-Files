/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.impl.AbstractNamedOperation;
import java.io.IOException;

public class CacheManagementConfigOperation
extends AbstractNamedOperation
implements IdentifiedDataSerializable {
    private boolean isStat;
    private boolean enabled;

    public CacheManagementConfigOperation() {
    }

    public CacheManagementConfigOperation(String cacheNameWithPrefix, boolean isStat, boolean enabled) {
        super(cacheNameWithPrefix);
        this.isStat = isStat;
        this.enabled = enabled;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public void run() throws Exception {
        ICacheService service = (ICacheService)this.getService();
        if (this.isStat) {
            service.setStatisticsEnabled(null, this.name, this.enabled);
        } else {
            service.setManagementEnabled(null, this.name, this.enabled);
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeBoolean(this.isStat);
        out.writeBoolean(this.enabled);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.isStat = in.readBoolean();
        this.enabled = in.readBoolean();
    }

    @Override
    public int getId() {
        return 28;
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }
}

