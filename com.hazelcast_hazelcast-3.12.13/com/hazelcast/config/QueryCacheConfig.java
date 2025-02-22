/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.config.PredicateConfig;
import com.hazelcast.config.QueryCacheConfigReadOnly;
import com.hazelcast.internal.serialization.impl.SerializationUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QueryCacheConfig
implements IdentifiedDataSerializable {
    public static final int DEFAULT_BATCH_SIZE = 1;
    public static final int DEFAULT_BUFFER_SIZE = 16;
    public static final int DEFAULT_DELAY_SECONDS = 0;
    public static final boolean DEFAULT_INCLUDE_VALUE = true;
    public static final boolean DEFAULT_POPULATE = true;
    public static final boolean DEFAULT_COALESCE = false;
    public static final InMemoryFormat DEFAULT_IN_MEMORY_FORMAT = InMemoryFormat.BINARY;
    private int batchSize = 1;
    private int bufferSize = 16;
    private int delaySeconds = 0;
    private boolean includeValue = true;
    private boolean populate = true;
    private boolean coalesce = false;
    private InMemoryFormat inMemoryFormat = DEFAULT_IN_MEMORY_FORMAT;
    private String name;
    private PredicateConfig predicateConfig = new PredicateConfig();
    private EvictionConfig evictionConfig = new EvictionConfig();
    private List<EntryListenerConfig> entryListenerConfigs;
    private List<MapIndexConfig> indexConfigs;
    private transient QueryCacheConfigReadOnly readOnly;

    public QueryCacheConfig() {
    }

    public QueryCacheConfig(String name) {
        this.setName(name);
    }

    public QueryCacheConfig(QueryCacheConfig other) {
        this.batchSize = other.batchSize;
        this.bufferSize = other.bufferSize;
        this.delaySeconds = other.delaySeconds;
        this.includeValue = other.includeValue;
        this.populate = other.populate;
        this.coalesce = other.coalesce;
        this.inMemoryFormat = other.inMemoryFormat;
        this.name = other.name;
        this.predicateConfig = other.predicateConfig;
        this.evictionConfig = other.evictionConfig;
        this.entryListenerConfigs = other.entryListenerConfigs;
        this.indexConfigs = other.indexConfigs;
    }

    public QueryCacheConfigReadOnly getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new QueryCacheConfigReadOnly(this);
        }
        return this.readOnly;
    }

    public String getName() {
        return this.name;
    }

    public QueryCacheConfig setName(String name) {
        Preconditions.checkHasText(name, "name");
        this.name = name;
        return this;
    }

    public PredicateConfig getPredicateConfig() {
        return this.predicateConfig;
    }

    public QueryCacheConfig setPredicateConfig(PredicateConfig predicateConfig) {
        this.predicateConfig = Preconditions.checkNotNull(predicateConfig, "predicateConfig can not be null");
        return this;
    }

    public int getBatchSize() {
        return this.batchSize;
    }

    public QueryCacheConfig setBatchSize(int batchSize) {
        this.batchSize = Preconditions.checkPositive(batchSize, "batchSize");
        return this;
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public QueryCacheConfig setBufferSize(int bufferSize) {
        this.bufferSize = Preconditions.checkPositive(bufferSize, "bufferSize");
        return this;
    }

    public int getDelaySeconds() {
        return this.delaySeconds;
    }

    public QueryCacheConfig setDelaySeconds(int delaySeconds) {
        this.delaySeconds = Preconditions.checkNotNegative(delaySeconds, "delaySeconds");
        return this;
    }

    public InMemoryFormat getInMemoryFormat() {
        return this.inMemoryFormat;
    }

    public QueryCacheConfig setInMemoryFormat(InMemoryFormat inMemoryFormat) {
        Preconditions.checkNotNull(inMemoryFormat, "inMemoryFormat cannot be null");
        Preconditions.checkFalse(inMemoryFormat == InMemoryFormat.NATIVE, "InMemoryFormat." + (Object)((Object)inMemoryFormat) + " is not supported.");
        this.inMemoryFormat = inMemoryFormat;
        return this;
    }

    public boolean isIncludeValue() {
        return this.includeValue;
    }

    public QueryCacheConfig setIncludeValue(boolean includeValue) {
        this.includeValue = includeValue;
        return this;
    }

    public boolean isPopulate() {
        return this.populate;
    }

    public QueryCacheConfig setPopulate(boolean populate) {
        this.populate = populate;
        return this;
    }

    public boolean isCoalesce() {
        return this.coalesce;
    }

    public QueryCacheConfig setCoalesce(boolean coalesce) {
        this.coalesce = coalesce;
        return this;
    }

    public EvictionConfig getEvictionConfig() {
        return this.evictionConfig;
    }

    public QueryCacheConfig setEvictionConfig(EvictionConfig evictionConfig) {
        Preconditions.checkNotNull(evictionConfig, "evictionConfig cannot be null");
        this.evictionConfig = evictionConfig;
        return this;
    }

    public QueryCacheConfig addEntryListenerConfig(EntryListenerConfig listenerConfig) {
        Preconditions.checkNotNull(listenerConfig, "listenerConfig cannot be null");
        this.getEntryListenerConfigs().add(listenerConfig);
        return this;
    }

    public List<EntryListenerConfig> getEntryListenerConfigs() {
        if (this.entryListenerConfigs == null) {
            this.entryListenerConfigs = new ArrayList<EntryListenerConfig>();
        }
        return this.entryListenerConfigs;
    }

    public QueryCacheConfig setEntryListenerConfigs(List<EntryListenerConfig> listenerConfigs) {
        Preconditions.checkNotNull(listenerConfigs, "listenerConfig cannot be null");
        this.entryListenerConfigs = listenerConfigs;
        return this;
    }

    public QueryCacheConfig addIndexConfig(MapIndexConfig mapIndexConfig) {
        this.getIndexConfigs().add(mapIndexConfig);
        return this;
    }

    public List<MapIndexConfig> getIndexConfigs() {
        if (this.indexConfigs == null) {
            this.indexConfigs = new ArrayList<MapIndexConfig>();
        }
        return this.indexConfigs;
    }

    public QueryCacheConfig setIndexConfigs(List<MapIndexConfig> indexConfigs) {
        this.indexConfigs = indexConfigs;
        return this;
    }

    public String toString() {
        return "QueryCacheConfig{batchSize=" + this.batchSize + ", bufferSize=" + this.bufferSize + ", delaySeconds=" + this.delaySeconds + ", includeValue=" + this.includeValue + ", populate=" + this.populate + ", coalesce=" + this.coalesce + ", inMemoryFormat=" + (Object)((Object)this.inMemoryFormat) + ", name='" + this.name + '\'' + ", predicateConfig=" + this.predicateConfig + ", evictionConfig=" + this.evictionConfig + ", entryListenerConfigs=" + this.entryListenerConfigs + ", indexConfigs=" + this.indexConfigs + '}';
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 18;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.batchSize);
        out.writeInt(this.bufferSize);
        out.writeInt(this.delaySeconds);
        out.writeBoolean(this.includeValue);
        out.writeBoolean(this.populate);
        out.writeBoolean(this.coalesce);
        out.writeUTF(this.inMemoryFormat.name());
        out.writeUTF(this.name);
        out.writeObject(this.predicateConfig);
        out.writeObject(this.evictionConfig);
        SerializationUtil.writeNullableList(this.entryListenerConfigs, out);
        SerializationUtil.writeNullableList(this.indexConfigs, out);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.batchSize = in.readInt();
        this.bufferSize = in.readInt();
        this.delaySeconds = in.readInt();
        this.includeValue = in.readBoolean();
        this.populate = in.readBoolean();
        this.coalesce = in.readBoolean();
        this.inMemoryFormat = InMemoryFormat.valueOf(in.readUTF());
        this.name = in.readUTF();
        this.predicateConfig = (PredicateConfig)in.readObject();
        this.evictionConfig = (EvictionConfig)in.readObject();
        this.entryListenerConfigs = SerializationUtil.readNullableList(in);
        this.indexConfigs = SerializationUtil.readNullableList(in);
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QueryCacheConfig)) {
            return false;
        }
        QueryCacheConfig that = (QueryCacheConfig)o;
        if (this.batchSize != that.batchSize) {
            return false;
        }
        if (this.bufferSize != that.bufferSize) {
            return false;
        }
        if (this.delaySeconds != that.delaySeconds) {
            return false;
        }
        if (this.includeValue != that.includeValue) {
            return false;
        }
        if (this.populate != that.populate) {
            return false;
        }
        if (this.coalesce != that.coalesce) {
            return false;
        }
        if (this.inMemoryFormat != that.inMemoryFormat) {
            return false;
        }
        if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
            return false;
        }
        if (this.predicateConfig != null ? !this.predicateConfig.equals(that.predicateConfig) : that.predicateConfig != null) {
            return false;
        }
        if (this.evictionConfig != null ? !this.evictionConfig.equals(that.evictionConfig) : that.evictionConfig != null) {
            return false;
        }
        if (this.entryListenerConfigs != null ? !this.entryListenerConfigs.equals(that.entryListenerConfigs) : that.entryListenerConfigs != null) {
            return false;
        }
        return this.indexConfigs != null ? this.indexConfigs.equals(that.indexConfigs) : that.indexConfigs == null;
    }

    public final int hashCode() {
        int result = this.batchSize;
        result = 31 * result + this.bufferSize;
        result = 31 * result + this.delaySeconds;
        result = 31 * result + (this.includeValue ? 1 : 0);
        result = 31 * result + (this.populate ? 1 : 0);
        result = 31 * result + (this.coalesce ? 1 : 0);
        result = 31 * result + (this.inMemoryFormat != null ? this.inMemoryFormat.hashCode() : 0);
        result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
        result = 31 * result + (this.predicateConfig != null ? this.predicateConfig.hashCode() : 0);
        result = 31 * result + (this.evictionConfig != null ? this.evictionConfig.hashCode() : 0);
        result = 31 * result + (this.entryListenerConfigs != null ? this.entryListenerConfigs.hashCode() : 0);
        result = 31 * result + (this.indexConfigs != null ? this.indexConfigs.hashCode() : 0);
        return result;
    }
}

