/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.configuration.CacheEntryListenerConfiguration
 *  javax.cache.configuration.CompleteConfiguration
 *  javax.cache.configuration.Factory
 *  javax.cache.configuration.FactoryBuilder
 *  javax.cache.configuration.MutableCacheEntryListenerConfiguration
 *  javax.cache.expiry.AccessedExpiryPolicy
 *  javax.cache.expiry.CreatedExpiryPolicy
 *  javax.cache.expiry.Duration
 *  javax.cache.expiry.EternalExpiryPolicy
 *  javax.cache.expiry.ExpiryPolicy
 *  javax.cache.expiry.ModifiedExpiryPolicy
 *  javax.cache.expiry.TouchedExpiryPolicy
 */
package com.hazelcast.config;

import com.hazelcast.cache.impl.DeferredValue;
import com.hazelcast.config.AbstractCacheConfig;
import com.hazelcast.config.CacheConfigReadOnly;
import com.hazelcast.config.CacheEvictionConfig;
import com.hazelcast.config.CachePartitionLostListenerConfig;
import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.CacheSimpleEntryListenerConfig;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.HotRestartConfig;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.WanReplicationRef;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.spi.merge.SplitBrainMergeTypeProvider;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.tenantcontrol.TenantControl;
import com.hazelcast.util.Preconditions;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.expiry.EternalExpiryPolicy;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.expiry.ModifiedExpiryPolicy;
import javax.cache.expiry.TouchedExpiryPolicy;

@BinaryInterface
public class CacheConfig<K, V>
extends AbstractCacheConfig<K, V>
implements SplitBrainMergeTypeProvider {
    private String name;
    private String managerPrefix;
    private String uriString;
    private int asyncBackupCount = 0;
    private int backupCount = 1;
    private InMemoryFormat inMemoryFormat = CacheSimpleConfig.DEFAULT_IN_MEMORY_FORMAT;
    private CacheEvictionConfig evictionConfig = new CacheEvictionConfig();
    private WanReplicationRef wanReplicationRef;
    private List<CachePartitionLostListenerConfig> partitionLostListenerConfigs;
    private String quorumName;
    private String mergePolicy = CacheSimpleConfig.DEFAULT_CACHE_MERGE_POLICY;
    private boolean disablePerEntryInvalidationEvents;
    private TenantControl tenantControl = TenantControl.NOOP_TENANT_CONTROL;

    public CacheConfig() {
    }

    public CacheConfig(String name) {
        this.setName(name);
    }

    public CacheConfig(CompleteConfiguration<K, V> configuration) {
        super(configuration);
        if (configuration instanceof CacheConfig) {
            CacheConfig config = (CacheConfig)configuration;
            this.name = config.name;
            this.managerPrefix = config.managerPrefix;
            this.uriString = config.uriString;
            this.asyncBackupCount = config.asyncBackupCount;
            this.backupCount = config.backupCount;
            this.inMemoryFormat = config.inMemoryFormat;
            this.hotRestartConfig = new HotRestartConfig(config.hotRestartConfig);
            if (config.evictionConfig != null) {
                this.evictionConfig = new CacheEvictionConfig(config.evictionConfig);
            }
            if (config.wanReplicationRef != null) {
                this.wanReplicationRef = new WanReplicationRef(config.wanReplicationRef);
            }
            if (config.partitionLostListenerConfigs != null) {
                this.partitionLostListenerConfigs = new ArrayList<CachePartitionLostListenerConfig>(config.partitionLostListenerConfigs);
            }
            this.quorumName = config.quorumName;
            this.mergePolicy = config.mergePolicy;
            this.disablePerEntryInvalidationEvents = config.disablePerEntryInvalidationEvents;
            this.serializationService = config.serializationService;
            this.classLoader = config.classLoader;
        }
    }

    public CacheConfig(CacheSimpleConfig simpleConfig) throws Exception {
        this.name = simpleConfig.getName();
        if (simpleConfig.getKeyType() != null) {
            this.setKeyClassName(simpleConfig.getKeyType());
        }
        if (simpleConfig.getValueType() != null) {
            this.setValueClassName(simpleConfig.getValueType());
        }
        this.isStatisticsEnabled = simpleConfig.isStatisticsEnabled();
        this.isManagementEnabled = simpleConfig.isManagementEnabled();
        this.isReadThrough = simpleConfig.isReadThrough();
        this.isWriteThrough = simpleConfig.isWriteThrough();
        this.copyFactories(simpleConfig);
        this.initExpiryPolicyFactoryConfig(simpleConfig);
        this.asyncBackupCount = simpleConfig.getAsyncBackupCount();
        this.backupCount = simpleConfig.getBackupCount();
        this.inMemoryFormat = simpleConfig.getInMemoryFormat();
        if (simpleConfig.getEvictionConfig() != null) {
            this.evictionConfig = new CacheEvictionConfig(simpleConfig.getEvictionConfig());
        }
        if (simpleConfig.getWanReplicationRef() != null) {
            this.wanReplicationRef = new WanReplicationRef(simpleConfig.getWanReplicationRef());
        }
        this.copyListeners(simpleConfig);
        this.quorumName = simpleConfig.getQuorumName();
        this.mergePolicy = simpleConfig.getMergePolicy();
        this.hotRestartConfig = new HotRestartConfig(simpleConfig.getHotRestartConfig());
        this.disablePerEntryInvalidationEvents = simpleConfig.isDisablePerEntryInvalidationEvents();
    }

    private void initExpiryPolicyFactoryConfig(CacheSimpleConfig simpleConfig) throws Exception {
        CacheSimpleConfig.ExpiryPolicyFactoryConfig expiryPolicyFactoryConfig = simpleConfig.getExpiryPolicyFactoryConfig();
        if (expiryPolicyFactoryConfig != null) {
            if (expiryPolicyFactoryConfig.getClassName() != null) {
                this.setExpiryPolicyFactory((Factory<ExpiryPolicy>)((Factory)ClassLoaderUtil.newInstance(null, expiryPolicyFactoryConfig.getClassName())));
            } else {
                CacheSimpleConfig.ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig timedExpiryPolicyConfig = expiryPolicyFactoryConfig.getTimedExpiryPolicyFactoryConfig();
                if (timedExpiryPolicyConfig != null) {
                    CacheSimpleConfig.ExpiryPolicyFactoryConfig.DurationConfig durationConfig = timedExpiryPolicyConfig.getDurationConfig();
                    CacheSimpleConfig.ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig.ExpiryPolicyType expiryPolicyType = timedExpiryPolicyConfig.getExpiryPolicyType();
                    switch (expiryPolicyType) {
                        case CREATED: {
                            this.setExpiryPolicyFactory((Factory<ExpiryPolicy>)CreatedExpiryPolicy.factoryOf((Duration)new Duration(durationConfig.getTimeUnit(), durationConfig.getDurationAmount())));
                            break;
                        }
                        case MODIFIED: {
                            this.setExpiryPolicyFactory((Factory<ExpiryPolicy>)ModifiedExpiryPolicy.factoryOf((Duration)new Duration(durationConfig.getTimeUnit(), durationConfig.getDurationAmount())));
                            break;
                        }
                        case ACCESSED: {
                            this.setExpiryPolicyFactory((Factory<ExpiryPolicy>)AccessedExpiryPolicy.factoryOf((Duration)new Duration(durationConfig.getTimeUnit(), durationConfig.getDurationAmount())));
                            break;
                        }
                        case TOUCHED: {
                            this.setExpiryPolicyFactory((Factory<ExpiryPolicy>)TouchedExpiryPolicy.factoryOf((Duration)new Duration(durationConfig.getTimeUnit(), durationConfig.getDurationAmount())));
                            break;
                        }
                        case ETERNAL: {
                            this.setExpiryPolicyFactory((Factory<ExpiryPolicy>)EternalExpiryPolicy.factoryOf());
                            break;
                        }
                        default: {
                            throw new IllegalArgumentException("Unsupported expiry policy type: " + (Object)((Object)expiryPolicyType));
                        }
                    }
                }
            }
        }
    }

    public CacheConfigReadOnly<K, V> getAsReadOnly() {
        return new CacheConfigReadOnly(this);
    }

    public String getName() {
        return this.name;
    }

    public CacheConfig<K, V> setName(String name) {
        this.name = name;
        return this;
    }

    public String getManagerPrefix() {
        return this.managerPrefix;
    }

    public CacheConfig<K, V> setManagerPrefix(String managerPrefix) {
        this.managerPrefix = managerPrefix;
        return this;
    }

    public String getUriString() {
        return this.uriString;
    }

    public CacheConfig<K, V> setUriString(String uriString) {
        this.uriString = uriString;
        return this;
    }

    public String getNameWithPrefix() {
        return this.managerPrefix + this.name;
    }

    public int getBackupCount() {
        return this.backupCount;
    }

    public CacheConfig<K, V> setBackupCount(int backupCount) {
        this.backupCount = Preconditions.checkBackupCount(backupCount, this.asyncBackupCount);
        return this;
    }

    public int getAsyncBackupCount() {
        return this.asyncBackupCount;
    }

    public CacheConfig<K, V> setAsyncBackupCount(int asyncBackupCount) {
        this.asyncBackupCount = Preconditions.checkAsyncBackupCount(this.backupCount, asyncBackupCount);
        return this;
    }

    public int getTotalBackupCount() {
        return this.backupCount + this.asyncBackupCount;
    }

    public CacheEvictionConfig getEvictionConfig() {
        return this.evictionConfig;
    }

    public CacheConfig<K, V> setEvictionConfig(EvictionConfig evictionConfig) {
        Preconditions.isNotNull(evictionConfig, "evictionConfig");
        this.evictionConfig = evictionConfig instanceof CacheEvictionConfig ? (CacheEvictionConfig)evictionConfig : new CacheEvictionConfig(evictionConfig);
        return this;
    }

    public WanReplicationRef getWanReplicationRef() {
        return this.wanReplicationRef;
    }

    public CacheConfig<K, V> setWanReplicationRef(WanReplicationRef wanReplicationRef) {
        this.wanReplicationRef = wanReplicationRef;
        return this;
    }

    public List<CachePartitionLostListenerConfig> getPartitionLostListenerConfigs() {
        if (this.partitionLostListenerConfigs == null) {
            this.partitionLostListenerConfigs = new ArrayList<CachePartitionLostListenerConfig>();
        }
        return this.partitionLostListenerConfigs;
    }

    public CacheConfig<K, V> setPartitionLostListenerConfigs(List<CachePartitionLostListenerConfig> partitionLostListenerConfigs) {
        this.partitionLostListenerConfigs = partitionLostListenerConfigs;
        return this;
    }

    public InMemoryFormat getInMemoryFormat() {
        return this.inMemoryFormat;
    }

    public CacheConfig<K, V> setInMemoryFormat(InMemoryFormat inMemoryFormat) {
        this.inMemoryFormat = Preconditions.isNotNull(inMemoryFormat, "In-Memory format cannot be null!");
        return this;
    }

    public String getQuorumName() {
        return this.quorumName;
    }

    public CacheConfig<K, V> setQuorumName(String quorumName) {
        this.quorumName = quorumName;
        return this;
    }

    public String getMergePolicy() {
        return this.mergePolicy;
    }

    public void setMergePolicy(String mergePolicy) {
        this.mergePolicy = mergePolicy;
    }

    public Class getProvidedMergeTypes() {
        return SplitBrainMergeTypes.CacheMergeTypes.class;
    }

    public boolean isDisablePerEntryInvalidationEvents() {
        return this.disablePerEntryInvalidationEvents;
    }

    public void setDisablePerEntryInvalidationEvents(boolean disablePerEntryInvalidationEvents) {
        this.disablePerEntryInvalidationEvents = disablePerEntryInvalidationEvents;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeUTF(this.managerPrefix);
        out.writeUTF(this.uriString);
        out.writeInt(this.backupCount);
        out.writeInt(this.asyncBackupCount);
        out.writeUTF(this.inMemoryFormat.name());
        out.writeObject(this.evictionConfig);
        out.writeObject(this.wanReplicationRef);
        this.writeKeyValueTypes(out);
        this.writeTenant(out);
        this.writeFactories(out);
        out.writeBoolean(this.isReadThrough);
        out.writeBoolean(this.isWriteThrough);
        out.writeBoolean(this.isStoreByValue);
        out.writeBoolean(this.isManagementEnabled);
        out.writeBoolean(this.isStatisticsEnabled);
        out.writeBoolean(this.hotRestartConfig.isEnabled());
        out.writeBoolean(this.hotRestartConfig.isFsync());
        out.writeUTF(this.quorumName);
        out.writeBoolean(this.hasListenerConfiguration());
        if (this.hasListenerConfiguration()) {
            this.writeListenerConfigurations(out);
        }
        out.writeUTF(this.mergePolicy);
        out.writeBoolean(this.disablePerEntryInvalidationEvents);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.managerPrefix = in.readUTF();
        this.uriString = in.readUTF();
        this.backupCount = in.readInt();
        this.asyncBackupCount = in.readInt();
        String resultInMemoryFormat = in.readUTF();
        this.inMemoryFormat = InMemoryFormat.valueOf(resultInMemoryFormat);
        try (Closeable tenantContext = this.tenantControl.setTenant(false);){
            this.evictionConfig = (CacheEvictionConfig)in.readObject();
            this.wanReplicationRef = (WanReplicationRef)in.readObject();
            this.readKeyValueTypes(in);
            this.readTenant(in);
            this.readFactories(in);
            this.isReadThrough = in.readBoolean();
            this.isWriteThrough = in.readBoolean();
            this.isStoreByValue = in.readBoolean();
            this.isManagementEnabled = in.readBoolean();
            this.isStatisticsEnabled = in.readBoolean();
            this.hotRestartConfig.setEnabled(in.readBoolean());
            this.hotRestartConfig.setFsync(in.readBoolean());
            this.quorumName = in.readUTF();
            boolean listNotEmpty = in.readBoolean();
            if (listNotEmpty) {
                this.readListenerConfigurations(in);
            }
        }
        this.mergePolicy = in.readUTF();
        this.disablePerEntryInvalidationEvents = in.readBoolean();
        this.setClassLoader(in.getClassLoader());
        this.serializationService = in.getSerializationService();
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
        result = 31 * result + (this.managerPrefix != null ? this.managerPrefix.hashCode() : 0);
        result = 31 * result + (this.uriString != null ? this.uriString.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof CacheConfig)) {
            return false;
        }
        CacheConfig that = (CacheConfig)o;
        if (this.managerPrefix != null ? !this.managerPrefix.equals(that.managerPrefix) : that.managerPrefix != null) {
            return false;
        }
        if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
            return false;
        }
        if (this.uriString != null ? !this.uriString.equals(that.uriString) : that.uriString != null) {
            return false;
        }
        return super.equals(o);
    }

    public String toString() {
        return "CacheConfig{name='" + this.name + '\'' + ", managerPrefix='" + this.managerPrefix + '\'' + ", inMemoryFormat=" + (Object)((Object)this.inMemoryFormat) + ", backupCount=" + this.backupCount + ", hotRestart=" + this.hotRestartConfig + ", wanReplicationRef=" + this.wanReplicationRef + '}';
    }

    TenantControl getTenantControl() {
        return this.tenantControl;
    }

    void setTenantControl(TenantControl tenantControl) {
        this.tenantControl = tenantControl;
    }

    protected void writeTenant(ObjectDataOutput out) throws IOException {
    }

    protected void readTenant(ObjectDataInput in) throws IOException {
    }

    protected void writeKeyValueTypes(ObjectDataOutput out) throws IOException {
        out.writeObject(this.getKeyType());
        out.writeObject(this.getValueType());
    }

    protected void readKeyValueTypes(ObjectDataInput in) throws IOException {
        this.setKeyType((Class)in.readObject());
        this.setValueType((Class)in.readObject());
    }

    protected void writeFactories(ObjectDataOutput out) throws IOException {
        out.writeObject(this.getCacheLoaderFactory());
        out.writeObject(this.getCacheWriterFactory());
        out.writeObject(this.getExpiryPolicyFactory());
    }

    protected void readFactories(ObjectDataInput in) throws IOException {
        this.setCacheLoaderFactory((Factory)in.readObject());
        this.setCacheWriterFactory((Factory)in.readObject());
        this.setExpiryPolicyFactory((Factory<ExpiryPolicy>)((Factory)in.readObject()));
    }

    protected void writeListenerConfigurations(ObjectDataOutput out) throws IOException {
        out.writeInt(this.getListenerConfigurations().size());
        for (CacheEntryListenerConfiguration cc : this.getListenerConfigurations()) {
            out.writeObject(cc);
        }
    }

    protected void readListenerConfigurations(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        Set lc = this.createConcurrentSet();
        for (int i = 0; i < size; ++i) {
            lc.add(DeferredValue.withValue((CacheEntryListenerConfiguration)in.readObject()));
        }
        this.listenerConfigurations = lc;
    }

    public <T extends CacheConfig<K, V>> T copy(T target, boolean resolved) {
        target.setTenantControl(this.getTenantControl());
        target.setAsyncBackupCount(this.getAsyncBackupCount());
        target.setBackupCount(this.getBackupCount());
        target.setDisablePerEntryInvalidationEvents(this.isDisablePerEntryInvalidationEvents());
        target.setEvictionConfig(this.getEvictionConfig());
        target.setHotRestartConfig(this.getHotRestartConfig());
        target.setInMemoryFormat(this.getInMemoryFormat());
        if (resolved) {
            target.setKeyType(this.getKeyType());
            target.setValueType(this.getValueType());
        } else {
            target.setKeyClassName(this.getKeyClassName());
            target.setValueClassName(this.getValueClassName());
        }
        target.cacheLoaderFactory = this.cacheLoaderFactory.shallowCopy();
        target.cacheWriterFactory = this.cacheWriterFactory.shallowCopy();
        target.expiryPolicyFactory = this.expiryPolicyFactory.shallowCopy();
        target.listenerConfigurations = this.createConcurrentSet();
        for (DeferredValue lazyEntryListenerConfig : this.listenerConfigurations) {
            target.listenerConfigurations.add(lazyEntryListenerConfig.shallowCopy());
        }
        target.setManagementEnabled(this.isManagementEnabled());
        target.setManagerPrefix(this.getManagerPrefix());
        target.setMergePolicy(this.getMergePolicy());
        target.setName(this.getName());
        target.setPartitionLostListenerConfigs(this.getPartitionLostListenerConfigs());
        target.setQuorumName(this.getQuorumName());
        target.setReadThrough(this.isReadThrough());
        target.setStatisticsEnabled(this.isStatisticsEnabled());
        target.setStoreByValue(this.isStoreByValue());
        target.setUriString(this.getUriString());
        target.setWanReplicationRef(this.getWanReplicationRef());
        target.setWriteThrough(this.isWriteThrough());
        target.setClassLoader(this.classLoader);
        target.serializationService = this.serializationService;
        return target;
    }

    private void copyListeners(CacheSimpleConfig simpleConfig) throws Exception {
        for (CacheSimpleEntryListenerConfig simpleListener : simpleConfig.getCacheEntryListeners()) {
            Factory listenerFactory = null;
            Factory filterFactory = null;
            if (simpleListener.getCacheEntryListenerFactory() != null) {
                listenerFactory = (Factory)ClassLoaderUtil.newInstance(null, simpleListener.getCacheEntryListenerFactory());
            }
            if (simpleListener.getCacheEntryEventFilterFactory() != null) {
                filterFactory = (Factory)ClassLoaderUtil.newInstance(null, simpleListener.getCacheEntryEventFilterFactory());
            }
            boolean isOldValueRequired = simpleListener.isOldValueRequired();
            boolean synchronous = simpleListener.isSynchronous();
            MutableCacheEntryListenerConfiguration listenerConfiguration = new MutableCacheEntryListenerConfiguration(listenerFactory, filterFactory, isOldValueRequired, synchronous);
            this.addCacheEntryListenerConfiguration(listenerConfiguration);
        }
        for (CachePartitionLostListenerConfig listenerConfig : simpleConfig.getPartitionLostListenerConfigs()) {
            this.getPartitionLostListenerConfigs().add(listenerConfig);
        }
    }

    private void copyFactories(CacheSimpleConfig simpleConfig) throws Exception {
        if (simpleConfig.getCacheLoaderFactory() != null) {
            this.setCacheLoaderFactory((Factory)ClassLoaderUtil.newInstance(null, simpleConfig.getCacheLoaderFactory()));
        }
        if (simpleConfig.getCacheLoader() != null) {
            this.setCacheLoaderFactory(FactoryBuilder.factoryOf((String)simpleConfig.getCacheLoader()));
        }
        if (simpleConfig.getCacheWriterFactory() != null) {
            this.setCacheWriterFactory((Factory)ClassLoaderUtil.newInstance(null, simpleConfig.getCacheWriterFactory()));
        }
        if (simpleConfig.getCacheWriter() != null) {
            this.setCacheWriterFactory(FactoryBuilder.factoryOf((String)simpleConfig.getCacheWriter()));
        }
    }
}

