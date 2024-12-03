/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.DeferredValue;
import com.hazelcast.config.AbstractCacheConfig;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.config.CacheConfigAccessor;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.spi.tenantcontrol.TenantControl;
import java.io.IOException;

public class PreJoinCacheConfig<K, V>
extends CacheConfig<K, V>
implements Versioned,
IdentifiedDataSerializable {
    public PreJoinCacheConfig() {
    }

    public PreJoinCacheConfig(CacheConfig cacheConfig) {
        this(cacheConfig, true);
    }

    public PreJoinCacheConfig(CacheConfig cacheConfig, boolean resolved) {
        cacheConfig.copy(this, resolved);
    }

    @Override
    protected void writeKeyValueTypes(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.getKeyClassName());
        out.writeUTF(this.getValueClassName());
    }

    @Override
    protected void readKeyValueTypes(ObjectDataInput in) throws IOException {
        this.setKeyClassName(in.readUTF());
        this.setValueClassName(in.readUTF());
    }

    @Override
    protected void writeTenant(ObjectDataOutput out) throws IOException {
        if (out.getVersion().isGreaterOrEqual(Versions.V3_12)) {
            out.writeObject(CacheConfigAccessor.getTenantControl(this));
        }
    }

    @Override
    protected void readTenant(ObjectDataInput in) throws IOException {
        if (in.getVersion().isGreaterOrEqual(Versions.V3_12)) {
            TenantControl tc = (TenantControl)in.readObject();
            CacheConfigAccessor.setTenantControl(this, tc);
        }
    }

    @Override
    protected void writeFactories(ObjectDataOutput out) throws IOException {
        SerializationService serializationService = out.getSerializationService();
        out.writeData(this.cacheLoaderFactory.getSerializedValue(serializationService));
        out.writeData(this.cacheWriterFactory.getSerializedValue(serializationService));
        out.writeData(this.expiryPolicyFactory.getSerializedValue(serializationService));
    }

    @Override
    protected void readFactories(ObjectDataInput in) throws IOException {
        this.cacheLoaderFactory = DeferredValue.withSerializedValue(in.readData());
        this.cacheWriterFactory = DeferredValue.withSerializedValue(in.readData());
        this.expiryPolicyFactory = DeferredValue.withSerializedValue(in.readData());
    }

    @Override
    protected void writeListenerConfigurations(ObjectDataOutput out) throws IOException {
        out.writeInt(this.listenerConfigurations.size());
        for (DeferredValue config : this.listenerConfigurations) {
            out.writeData(config.getSerializedValue(out.getSerializationService()));
        }
    }

    @Override
    protected void readListenerConfigurations(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        this.listenerConfigurations = this.createConcurrentSet();
        for (int i = 0; i < size; ++i) {
            DeferredValue serializedConfig = DeferredValue.withSerializedValue(in.readData());
            this.listenerConfigurations.add(serializedConfig);
        }
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 61;
    }

    CacheConfig asCacheConfig() {
        return this.copy(new CacheConfig(), false);
    }

    @Override
    protected boolean keyValueTypesEqual(AbstractCacheConfig that) {
        if (!this.getKeyClassName().equals(that.getKeyClassName())) {
            return false;
        }
        return this.getValueClassName().equals(that.getValueClassName());
    }

    public static CacheConfig asCacheConfig(CacheConfig cacheConfig) {
        if (!(cacheConfig instanceof PreJoinCacheConfig)) {
            return cacheConfig;
        }
        return ((PreJoinCacheConfig)cacheConfig).asCacheConfig();
    }

    public static PreJoinCacheConfig of(CacheConfig cacheConfig) {
        if (cacheConfig instanceof PreJoinCacheConfig) {
            return (PreJoinCacheConfig)cacheConfig;
        }
        return new PreJoinCacheConfig(cacheConfig, false);
    }
}

