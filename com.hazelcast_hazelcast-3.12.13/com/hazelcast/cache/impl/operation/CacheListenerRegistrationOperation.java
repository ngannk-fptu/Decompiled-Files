/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.configuration.CacheEntryListenerConfiguration
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.AbstractCacheService;
import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.impl.AbstractNamedOperation;
import java.io.IOException;
import javax.cache.configuration.CacheEntryListenerConfiguration;

public class CacheListenerRegistrationOperation
extends AbstractNamedOperation
implements IdentifiedDataSerializable {
    private CacheEntryListenerConfiguration cacheEntryListenerConfiguration;
    private boolean register;

    public CacheListenerRegistrationOperation() {
    }

    public CacheListenerRegistrationOperation(String name, CacheEntryListenerConfiguration cacheEntryListenerConfiguration, boolean register) {
        super(name);
        this.cacheEntryListenerConfiguration = cacheEntryListenerConfiguration;
        this.register = register;
    }

    @Override
    public void run() throws Exception {
        AbstractCacheService service = (AbstractCacheService)this.getService();
        CacheConfig cacheConfig = service.getCacheConfig(this.name);
        if (this.register) {
            service.cacheEntryListenerRegistered(this.name, this.cacheEntryListenerConfiguration);
        } else if (cacheConfig != null) {
            service.cacheEntryListenerDeregistered(this.name, this.cacheEntryListenerConfiguration);
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.cacheEntryListenerConfiguration);
        out.writeBoolean(this.register);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.cacheEntryListenerConfiguration = (CacheEntryListenerConfiguration)in.readObject();
        this.register = in.readBoolean();
    }

    @Override
    public int getId() {
        return 29;
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }
}

