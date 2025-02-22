/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.NamedConfig;
import com.hazelcast.config.NearCacheConfigReadOnly;
import com.hazelcast.config.NearCachePreloaderConfig;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.io.Serializable;

public class NearCacheConfig
implements IdentifiedDataSerializable,
Serializable,
NamedConfig {
    public static final InMemoryFormat DEFAULT_MEMORY_FORMAT = InMemoryFormat.BINARY;
    public static final boolean DEFAULT_SERIALIZE_KEYS = false;
    public static final boolean DEFAULT_INVALIDATE_ON_CHANGE = true;
    public static final LocalUpdatePolicy DEFAULT_LOCAL_UPDATE_POLICY = LocalUpdatePolicy.INVALIDATE;
    public static final int DEFAULT_TTL_SECONDS = 0;
    public static final int DEFAULT_MAX_IDLE_SECONDS = 0;
    @Deprecated
    public static final int DEFAULT_MAX_SIZE = Integer.MAX_VALUE;
    @Deprecated
    public static final String DEFAULT_EVICTION_POLICY = EvictionConfig.DEFAULT_EVICTION_POLICY.name();
    private String name = "default";
    private InMemoryFormat inMemoryFormat = DEFAULT_MEMORY_FORMAT;
    private boolean serializeKeys = false;
    private boolean invalidateOnChange = true;
    private int timeToLiveSeconds = 0;
    private int maxIdleSeconds = 0;
    private int maxSize = Integer.MAX_VALUE;
    private String evictionPolicy = EvictionConfig.DEFAULT_EVICTION_POLICY.name();
    private EvictionConfig evictionConfig = new EvictionConfig();
    private boolean cacheLocalEntries;
    private LocalUpdatePolicy localUpdatePolicy = DEFAULT_LOCAL_UPDATE_POLICY;
    private NearCachePreloaderConfig preloaderConfig = new NearCachePreloaderConfig();
    private NearCacheConfigReadOnly readOnly;

    public NearCacheConfig() {
    }

    public NearCacheConfig(String name) {
        this.name = name;
    }

    public NearCacheConfig(int timeToLiveSeconds, int maxIdleSeconds, boolean invalidateOnChange, InMemoryFormat inMemoryFormat) {
        this(timeToLiveSeconds, maxIdleSeconds, invalidateOnChange, inMemoryFormat, null);
    }

    public NearCacheConfig(int timeToLiveSeconds, int maxIdleSeconds, boolean invalidateOnChange, InMemoryFormat inMemoryFormat, EvictionConfig evictionConfig) {
        this.inMemoryFormat = inMemoryFormat;
        this.invalidateOnChange = invalidateOnChange;
        this.timeToLiveSeconds = timeToLiveSeconds;
        this.maxIdleSeconds = maxIdleSeconds;
        this.maxSize = NearCacheConfig.calculateMaxSize(this.maxSize);
        if (evictionConfig != null) {
            this.maxSize = evictionConfig.getSize();
            this.evictionPolicy = evictionConfig.getEvictionPolicy().toString();
            this.evictionConfig = evictionConfig;
        }
    }

    @Deprecated
    public NearCacheConfig(int timeToLiveSeconds, int maxSize, String evictionPolicy, int maxIdleSeconds, boolean invalidateOnChange, InMemoryFormat inMemoryFormat) {
        this(timeToLiveSeconds, maxSize, evictionPolicy, maxIdleSeconds, invalidateOnChange, inMemoryFormat, null);
    }

    @Deprecated
    public NearCacheConfig(int timeToLiveSeconds, int maxSize, String evictionPolicy, int maxIdleSeconds, boolean invalidateOnChange, InMemoryFormat inMemoryFormat, EvictionConfig evictionConfig) {
        this.inMemoryFormat = inMemoryFormat;
        this.invalidateOnChange = invalidateOnChange;
        this.timeToLiveSeconds = timeToLiveSeconds;
        this.maxIdleSeconds = maxIdleSeconds;
        this.maxSize = NearCacheConfig.calculateMaxSize(maxSize);
        this.evictionPolicy = evictionPolicy;
        if (evictionConfig != null) {
            this.evictionConfig = evictionConfig;
        } else {
            this.evictionConfig.setSize(NearCacheConfig.calculateMaxSize(maxSize));
            this.evictionConfig.setEvictionPolicy(EvictionPolicy.valueOf(evictionPolicy));
            this.evictionConfig.setMaximumSizePolicy(EvictionConfig.MaxSizePolicy.ENTRY_COUNT);
        }
    }

    public NearCacheConfig(NearCacheConfig config) {
        this.name = config.name;
        this.inMemoryFormat = config.inMemoryFormat;
        this.serializeKeys = config.serializeKeys;
        this.invalidateOnChange = config.invalidateOnChange;
        this.timeToLiveSeconds = config.timeToLiveSeconds;
        this.maxIdleSeconds = config.maxIdleSeconds;
        this.maxSize = config.maxSize;
        this.evictionPolicy = config.evictionPolicy;
        if (config.evictionConfig != null) {
            this.evictionConfig = config.evictionConfig;
        }
        this.cacheLocalEntries = config.cacheLocalEntries;
        this.localUpdatePolicy = config.localUpdatePolicy;
        if (config.preloaderConfig != null) {
            this.preloaderConfig = config.preloaderConfig;
        }
    }

    @Deprecated
    public NearCacheConfigReadOnly getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new NearCacheConfigReadOnly(this);
        }
        return this.readOnly;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public NearCacheConfig setName(String name) {
        this.name = name;
        return this;
    }

    public InMemoryFormat getInMemoryFormat() {
        return this.inMemoryFormat;
    }

    public NearCacheConfig setInMemoryFormat(InMemoryFormat inMemoryFormat) {
        this.inMemoryFormat = Preconditions.isNotNull(inMemoryFormat, "In-Memory format cannot be null!");
        return this;
    }

    public NearCacheConfig setInMemoryFormat(String inMemoryFormat) {
        Preconditions.checkNotNull(inMemoryFormat, "In-Memory format cannot be null!");
        this.inMemoryFormat = InMemoryFormat.valueOf(inMemoryFormat);
        return this;
    }

    public boolean isSerializeKeys() {
        return this.serializeKeys || this.inMemoryFormat == InMemoryFormat.NATIVE;
    }

    public NearCacheConfig setSerializeKeys(boolean serializeKeys) {
        this.serializeKeys = serializeKeys;
        return this;
    }

    public boolean isInvalidateOnChange() {
        return this.invalidateOnChange;
    }

    public NearCacheConfig setInvalidateOnChange(boolean invalidateOnChange) {
        this.invalidateOnChange = invalidateOnChange;
        return this;
    }

    public int getTimeToLiveSeconds() {
        return this.timeToLiveSeconds;
    }

    public NearCacheConfig setTimeToLiveSeconds(int timeToLiveSeconds) {
        this.timeToLiveSeconds = Preconditions.checkNotNegative(timeToLiveSeconds, "TTL seconds cannot be negative!");
        return this;
    }

    public int getMaxIdleSeconds() {
        return this.maxIdleSeconds;
    }

    public NearCacheConfig setMaxIdleSeconds(int maxIdleSeconds) {
        this.maxIdleSeconds = Preconditions.checkNotNegative(maxIdleSeconds, "Max-Idle seconds cannot be negative!");
        return this;
    }

    @Deprecated
    public int getMaxSize() {
        return this.maxSize;
    }

    @Deprecated
    public NearCacheConfig setMaxSize(int maxSize) {
        Preconditions.checkNotNegative(maxSize, "maxSize cannot be a negative number!");
        this.maxSize = NearCacheConfig.calculateMaxSize(maxSize);
        this.evictionConfig.setSize(this.maxSize);
        this.evictionConfig.setMaximumSizePolicy(EvictionConfig.MaxSizePolicy.ENTRY_COUNT);
        return this;
    }

    @Deprecated
    public String getEvictionPolicy() {
        return this.evictionPolicy;
    }

    @Deprecated
    public NearCacheConfig setEvictionPolicy(String evictionPolicy) {
        this.evictionPolicy = Preconditions.checkNotNull(evictionPolicy, "Eviction policy cannot be null!");
        this.evictionConfig.setEvictionPolicy(EvictionPolicy.valueOf(evictionPolicy));
        this.evictionConfig.setMaximumSizePolicy(EvictionConfig.MaxSizePolicy.ENTRY_COUNT);
        return this;
    }

    public EvictionConfig getEvictionConfig() {
        return this.evictionConfig;
    }

    public NearCacheConfig setEvictionConfig(EvictionConfig evictionConfig) {
        this.evictionConfig = Preconditions.checkNotNull(evictionConfig, "EvictionConfig cannot be null!");
        return this;
    }

    public boolean isCacheLocalEntries() {
        return this.cacheLocalEntries;
    }

    public NearCacheConfig setCacheLocalEntries(boolean cacheLocalEntries) {
        this.cacheLocalEntries = cacheLocalEntries;
        return this;
    }

    public LocalUpdatePolicy getLocalUpdatePolicy() {
        return this.localUpdatePolicy;
    }

    public NearCacheConfig setLocalUpdatePolicy(LocalUpdatePolicy localUpdatePolicy) {
        this.localUpdatePolicy = Preconditions.checkNotNull(localUpdatePolicy, "Local update policy cannot be null!");
        return this;
    }

    public NearCachePreloaderConfig getPreloaderConfig() {
        return this.preloaderConfig;
    }

    public NearCacheConfig setPreloaderConfig(NearCachePreloaderConfig preloaderConfig) {
        this.preloaderConfig = Preconditions.checkNotNull(preloaderConfig, "NearCachePreloaderConfig cannot be null!");
        return this;
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 3;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeUTF(this.evictionPolicy);
        out.writeInt(this.timeToLiveSeconds);
        out.writeInt(this.maxIdleSeconds);
        out.writeInt(this.maxSize);
        out.writeBoolean(this.invalidateOnChange);
        out.writeBoolean(this.cacheLocalEntries);
        out.writeInt(this.inMemoryFormat.ordinal());
        out.writeInt(this.localUpdatePolicy.ordinal());
        out.writeObject(this.evictionConfig);
        out.writeObject(this.preloaderConfig);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.evictionPolicy = in.readUTF();
        this.timeToLiveSeconds = in.readInt();
        this.maxIdleSeconds = in.readInt();
        this.maxSize = in.readInt();
        this.invalidateOnChange = in.readBoolean();
        this.cacheLocalEntries = in.readBoolean();
        this.inMemoryFormat = InMemoryFormat.values()[in.readInt()];
        this.localUpdatePolicy = LocalUpdatePolicy.values()[in.readInt()];
        this.evictionConfig = (EvictionConfig)in.readObject();
        this.preloaderConfig = (NearCachePreloaderConfig)in.readObject();
    }

    public String toString() {
        return "NearCacheConfig{name=" + this.name + ", inMemoryFormat=" + (Object)((Object)this.inMemoryFormat) + ", invalidateOnChange=" + this.invalidateOnChange + ", timeToLiveSeconds=" + this.timeToLiveSeconds + ", maxIdleSeconds=" + this.maxIdleSeconds + ", maxSize=" + this.maxSize + ", evictionPolicy='" + this.evictionPolicy + '\'' + ", evictionConfig=" + this.evictionConfig + ", cacheLocalEntries=" + this.cacheLocalEntries + ", localUpdatePolicy=" + (Object)((Object)this.localUpdatePolicy) + ", preloaderConfig=" + this.preloaderConfig + '}';
    }

    private static int calculateMaxSize(int maxSize) {
        return maxSize == 0 ? Integer.MAX_VALUE : Preconditions.checkNotNegative(maxSize, "maxSize cannot be negative!");
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        NearCacheConfig that = (NearCacheConfig)o;
        if (this.serializeKeys != that.serializeKeys) {
            return false;
        }
        if (this.invalidateOnChange != that.invalidateOnChange) {
            return false;
        }
        if (this.timeToLiveSeconds != that.timeToLiveSeconds) {
            return false;
        }
        if (this.maxIdleSeconds != that.maxIdleSeconds) {
            return false;
        }
        if (this.maxSize != that.maxSize) {
            return false;
        }
        if (this.cacheLocalEntries != that.cacheLocalEntries) {
            return false;
        }
        if (!this.name.equals(that.name)) {
            return false;
        }
        if (this.inMemoryFormat != that.inMemoryFormat) {
            return false;
        }
        if (this.evictionPolicy != null ? !this.evictionPolicy.equals(that.evictionPolicy) : that.evictionPolicy != null) {
            return false;
        }
        if (this.evictionConfig != null ? !this.evictionConfig.equals(that.evictionConfig) : that.evictionConfig != null) {
            return false;
        }
        if (this.localUpdatePolicy != that.localUpdatePolicy) {
            return false;
        }
        return this.preloaderConfig != null ? this.preloaderConfig.equals(that.preloaderConfig) : that.preloaderConfig == null;
    }

    public int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + this.inMemoryFormat.hashCode();
        result = 31 * result + (this.serializeKeys ? 1 : 0);
        result = 31 * result + (this.invalidateOnChange ? 1 : 0);
        result = 31 * result + this.timeToLiveSeconds;
        result = 31 * result + this.maxIdleSeconds;
        result = 31 * result + this.maxSize;
        result = 31 * result + (this.evictionPolicy != null ? this.evictionPolicy.hashCode() : 0);
        result = 31 * result + (this.evictionConfig != null ? this.evictionConfig.hashCode() : 0);
        result = 31 * result + (this.cacheLocalEntries ? 1 : 0);
        result = 31 * result + (this.localUpdatePolicy != null ? this.localUpdatePolicy.hashCode() : 0);
        result = 31 * result + (this.preloaderConfig != null ? this.preloaderConfig.hashCode() : 0);
        return result;
    }

    public static enum LocalUpdatePolicy {
        INVALIDATE,
        CACHE_ON_UPDATE,
        CACHE;

    }
}

