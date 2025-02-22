/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.configuration.CacheEntryListenerConfiguration
 *  javax.cache.configuration.Factory
 *  javax.cache.expiry.ExpiryPolicy
 */
package com.hazelcast.config;

import com.hazelcast.config.CacheConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.LegacyCacheEvictionConfig;
import com.hazelcast.config.WanReplicationRef;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.TypedDataSerializable;
import java.io.IOException;
import java.util.Set;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.expiry.ExpiryPolicy;

@BinaryInterface
public class LegacyCacheConfig<K, V>
implements TypedDataSerializable {
    private CacheConfig<K, V> config;

    public LegacyCacheConfig() {
        this.config = new CacheConfig();
    }

    public LegacyCacheConfig(CacheConfig<K, V> config) {
        this.config = config;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.config.getName());
        out.writeUTF(this.config.getManagerPrefix());
        out.writeUTF(this.config.getUriString());
        out.writeInt(this.config.getBackupCount());
        out.writeInt(this.config.getAsyncBackupCount());
        out.writeUTF(this.config.getInMemoryFormat().name());
        out.writeObject(new LegacyCacheEvictionConfig(this.config.getEvictionConfig()));
        out.writeObject(this.config.getWanReplicationRef());
        out.writeObject(this.config.getKeyType());
        out.writeObject(this.config.getValueType());
        out.writeObject(this.config.getCacheLoaderFactory());
        out.writeObject(this.config.getCacheWriterFactory());
        out.writeObject(this.config.getExpiryPolicyFactory());
        out.writeBoolean(this.config.isReadThrough());
        out.writeBoolean(this.config.isWriteThrough());
        out.writeBoolean(this.config.isStoreByValue());
        out.writeBoolean(this.config.isManagementEnabled());
        out.writeBoolean(this.config.isStatisticsEnabled());
        out.writeBoolean(this.config.getHotRestartConfig().isEnabled());
        out.writeBoolean(this.config.getHotRestartConfig().isFsync());
        out.writeUTF(this.config.getQuorumName());
        Set cacheEntryListenerConfigurations = (Set)this.config.getCacheEntryListenerConfigurations();
        boolean listNotEmpty = cacheEntryListenerConfigurations != null && !cacheEntryListenerConfigurations.isEmpty();
        out.writeBoolean(listNotEmpty);
        if (listNotEmpty) {
            out.writeInt(cacheEntryListenerConfigurations.size());
            for (CacheEntryListenerConfiguration cc : cacheEntryListenerConfigurations) {
                out.writeObject(cc);
            }
        }
        out.writeUTF(this.config.getMergePolicy());
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.config.setName(in.readUTF());
        this.config.setManagerPrefix(in.readUTF());
        this.config.setUriString(in.readUTF());
        this.config.setBackupCount(in.readInt());
        this.config.setAsyncBackupCount(in.readInt());
        String resultInMemoryFormat = in.readUTF();
        this.config.setInMemoryFormat(InMemoryFormat.valueOf(resultInMemoryFormat));
        LegacyCacheEvictionConfig legacyConfig = (LegacyCacheEvictionConfig)in.readObject(LegacyCacheEvictionConfig.class);
        this.config.setEvictionConfig(legacyConfig.getConfig());
        this.config.setWanReplicationRef((WanReplicationRef)in.readObject());
        this.config.setKeyType((Class)in.readObject());
        this.config.setValueType((Class)in.readObject());
        this.config.setCacheLoaderFactory((Factory)in.readObject());
        this.config.setCacheWriterFactory((Factory)in.readObject());
        this.config.setExpiryPolicyFactory((Factory<ExpiryPolicy>)((Factory)in.readObject()));
        this.config.setReadThrough(in.readBoolean());
        this.config.setWriteThrough(in.readBoolean());
        this.config.setStoreByValue(in.readBoolean());
        this.config.setManagementEnabled(in.readBoolean());
        this.config.setStatisticsEnabled(in.readBoolean());
        this.config.getHotRestartConfig().setEnabled(in.readBoolean());
        this.config.getHotRestartConfig().setFsync(in.readBoolean());
        this.config.setQuorumName(in.readUTF());
        boolean listNotEmpty = in.readBoolean();
        if (listNotEmpty) {
            int size = in.readInt();
            this.config.setListenerConfigurations();
            Set listenerConfigurations = (Set)this.config.getCacheEntryListenerConfigurations();
            for (int i = 0; i < size; ++i) {
                listenerConfigurations.add((CacheEntryListenerConfiguration)in.readObject());
            }
        }
        this.config.setMergePolicy(in.readUTF());
    }

    @Override
    public Class getClassType() {
        return CacheConfig.class;
    }

    public CacheConfig<K, V> getConfigAndReset() {
        CacheConfig<K, V> actualConfig = this.config;
        this.config = null;
        return actualConfig;
    }
}

