/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.CacheSimpleEntryListenerConfigReadOnly;
import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class CacheSimpleEntryListenerConfig
implements IdentifiedDataSerializable {
    private String cacheEntryListenerFactory;
    private String cacheEntryEventFilterFactory;
    private boolean oldValueRequired;
    private boolean synchronous;
    private CacheSimpleEntryListenerConfigReadOnly readOnly;

    public CacheSimpleEntryListenerConfig(CacheSimpleEntryListenerConfig listenerConfig) {
        this.cacheEntryEventFilterFactory = listenerConfig.cacheEntryEventFilterFactory;
        this.cacheEntryListenerFactory = listenerConfig.cacheEntryListenerFactory;
        this.oldValueRequired = listenerConfig.oldValueRequired;
        this.synchronous = listenerConfig.synchronous;
    }

    public CacheSimpleEntryListenerConfig() {
    }

    public CacheSimpleEntryListenerConfigReadOnly getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new CacheSimpleEntryListenerConfigReadOnly(this);
        }
        return this.readOnly;
    }

    public String getCacheEntryListenerFactory() {
        return this.cacheEntryListenerFactory;
    }

    public void setCacheEntryListenerFactory(String cacheEntryListenerFactory) {
        this.cacheEntryListenerFactory = cacheEntryListenerFactory;
    }

    public String getCacheEntryEventFilterFactory() {
        return this.cacheEntryEventFilterFactory;
    }

    public void setCacheEntryEventFilterFactory(String cacheEntryEventFilterFactory) {
        this.cacheEntryEventFilterFactory = cacheEntryEventFilterFactory;
    }

    public boolean isOldValueRequired() {
        return this.oldValueRequired;
    }

    public void setOldValueRequired(boolean oldValueRequired) {
        this.oldValueRequired = oldValueRequired;
    }

    public boolean isSynchronous() {
        return this.synchronous;
    }

    public void setSynchronous(boolean synchronous) {
        this.synchronous = synchronous;
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CacheSimpleEntryListenerConfig)) {
            return false;
        }
        CacheSimpleEntryListenerConfig that = (CacheSimpleEntryListenerConfig)o;
        if (this.oldValueRequired != that.oldValueRequired) {
            return false;
        }
        if (this.synchronous != that.synchronous) {
            return false;
        }
        if (this.cacheEntryListenerFactory != null ? !this.cacheEntryListenerFactory.equals(that.cacheEntryListenerFactory) : that.cacheEntryListenerFactory != null) {
            return false;
        }
        return this.cacheEntryEventFilterFactory != null ? this.cacheEntryEventFilterFactory.equals(that.cacheEntryEventFilterFactory) : that.cacheEntryEventFilterFactory == null;
    }

    public final int hashCode() {
        int result = this.cacheEntryListenerFactory != null ? this.cacheEntryListenerFactory.hashCode() : 0;
        result = 31 * result + (this.cacheEntryEventFilterFactory != null ? this.cacheEntryEventFilterFactory.hashCode() : 0);
        result = 31 * result + (this.oldValueRequired ? 1 : 0);
        result = 31 * result + (this.synchronous ? 1 : 0);
        return result;
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 47;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.cacheEntryEventFilterFactory);
        out.writeUTF(this.cacheEntryListenerFactory);
        out.writeBoolean(this.oldValueRequired);
        out.writeBoolean(this.synchronous);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.cacheEntryEventFilterFactory = in.readUTF();
        this.cacheEntryListenerFactory = in.readUTF();
        this.oldValueRequired = in.readBoolean();
        this.synchronous = in.readBoolean();
    }

    public String toString() {
        return "CacheSimpleEntryListenerConfig{cacheEntryListenerFactory='" + this.cacheEntryListenerFactory + '\'' + ", cacheEntryEventFilterFactory='" + this.cacheEntryEventFilterFactory + '\'' + ", oldValueRequired=" + this.oldValueRequired + ", synchronous=" + this.synchronous + '}';
    }
}

