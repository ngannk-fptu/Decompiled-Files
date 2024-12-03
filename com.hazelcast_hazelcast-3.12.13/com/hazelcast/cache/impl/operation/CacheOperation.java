/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.CacheEntryView;
import com.hazelcast.cache.CacheNotExistsException;
import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.cache.impl.CacheEntryViews;
import com.hazelcast.cache.impl.ICacheRecordStore;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.cache.impl.event.CacheWanEventPublisher;
import com.hazelcast.cache.impl.record.CacheRecord;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.config.CacheConfigAccessor;
import com.hazelcast.internal.util.ToHeapDataConverter;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.BackupOperation;
import com.hazelcast.spi.ExceptionAction;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.ServiceNamespaceAware;
import com.hazelcast.spi.impl.AbstractNamedOperation;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.ExceptionUtil;
import java.io.Closeable;

public abstract class CacheOperation
extends AbstractNamedOperation
implements PartitionAwareOperation,
ServiceNamespaceAware,
IdentifiedDataSerializable {
    protected transient boolean dontCreateCacheRecordStoreIfNotExist;
    protected transient ICacheService cacheService;
    protected transient ICacheRecordStore recordStore;
    protected transient CacheWanEventPublisher wanEventPublisher;
    protected transient Closeable tenantContext;

    protected CacheOperation() {
    }

    protected CacheOperation(String name) {
        this(name, false);
    }

    protected CacheOperation(String name, boolean dontCreateCacheRecordStoreIfNotExist) {
        super(name);
        this.dontCreateCacheRecordStoreIfNotExist = dontCreateCacheRecordStoreIfNotExist;
    }

    @Override
    public final String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public final void beforeRun() throws Exception {
        this.cacheService = (ICacheService)this.getService();
        try {
            this.recordStore = this.getOrCreateStoreIfAllowed();
            CacheConfig cacheConfig = this.recordStore != null ? this.recordStore.getConfig() : this.cacheService.getCacheConfig(this.name);
            if (cacheConfig != null) {
                this.tenantContext = CacheConfigAccessor.getTenantControl(cacheConfig).setTenant(true);
            }
        }
        catch (CacheNotExistsException e) {
            this.dispose();
            this.rethrowOrSwallowIfBackup(e);
        }
        catch (Throwable t) {
            this.dispose();
            throw ExceptionUtil.rethrow(t, Exception.class);
        }
        if (this.recordStore != null && this.recordStore.isWanReplicationEnabled()) {
            this.wanEventPublisher = this.cacheService.getCacheWanEventPublisher();
        }
        this.beforeRunInternal();
    }

    @Override
    public void afterRun() throws Exception {
        if (this.tenantContext != null) {
            this.tenantContext.close();
        }
    }

    private void rethrowOrSwallowIfBackup(CacheNotExistsException e) throws Exception {
        if (!(this instanceof BackupOperation)) {
            throw ExceptionUtil.rethrow((Throwable)e, Exception.class);
        }
        this.getLogger().finest("Error while getting a cache", e);
    }

    private ICacheRecordStore getOrCreateStoreIfAllowed() {
        if (this.dontCreateCacheRecordStoreIfNotExist) {
            return this.cacheService.getRecordStore(this.name, this.getPartitionId());
        }
        return this.cacheService.getOrCreateRecordStore(this.name, this.getPartitionId());
    }

    protected void dispose() {
    }

    protected void beforeRunInternal() {
    }

    @Override
    public ExceptionAction onInvocationException(Throwable throwable) {
        ICacheService cacheService;
        if (throwable instanceof CacheNotExistsException && (cacheService = (ICacheService)this.getService()).getCacheConfig(this.name) != null) {
            this.getLogger().finest("Retry Cache Operation from node " + this.getNodeEngine().getLocalMember());
            return ExceptionAction.RETRY_INVOCATION;
        }
        return super.onInvocationException(throwable);
    }

    @Override
    public void logError(Throwable e) {
        if (e instanceof CacheNotExistsException) {
            ILogger logger = this.getLogger();
            if (logger.isFinestEnabled()) {
                logger.finest("failed to execute: " + this, e);
            }
            return;
        }
        super.logError(e);
    }

    @Override
    public final ObjectNamespace getServiceNamespace() {
        if (this.recordStore == null) {
            ICacheService service = (ICacheService)this.getService();
            this.recordStore = service.getOrCreateRecordStore(this.name, this.getPartitionId());
        }
        return this.recordStore.getObjectNamespace();
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    public final int getSyncBackupCount() {
        return this.recordStore != null ? this.recordStore.getConfig().getBackupCount() : 0;
    }

    public final int getAsyncBackupCount() {
        return this.recordStore != null ? this.recordStore.getConfig().getAsyncBackupCount() : 0;
    }

    protected final void publishWanUpdate(Data dataKey, CacheRecord record) {
        if (!this.recordStore.isWanReplicationEnabled() || record == null) {
            return;
        }
        NodeEngine nodeEngine = this.getNodeEngine();
        SerializationService serializationService = nodeEngine.getSerializationService();
        Data dataValue = ToHeapDataConverter.toHeapData(serializationService.toData(record.getValue()));
        this.publishWanUpdate(dataKey, dataValue, record);
    }

    protected final void publishWanUpdate(Data dataKey, Data dataValue, CacheRecord record) {
        if (!this.recordStore.isWanReplicationEnabled() || record == null) {
            return;
        }
        NodeEngine nodeEngine = this.getNodeEngine();
        SerializationService serializationService = nodeEngine.getSerializationService();
        Data dataExpiryPolicy = ToHeapDataConverter.toHeapData(serializationService.toData(record.getExpiryPolicy()));
        this.publishWanUpdate(dataKey, dataValue, dataExpiryPolicy, record);
    }

    protected final void publishWanUpdate(Data dataKey, Data dataValue, Data dataExpiryPolicy, CacheRecord record) {
        assert (dataValue != null);
        if (!this.recordStore.isWanReplicationEnabled() || record == null) {
            return;
        }
        CacheEntryView<Data, Data> entryView = CacheEntryViews.createDefaultEntryView(ToHeapDataConverter.toHeapData(dataKey), ToHeapDataConverter.toHeapData(dataValue), ToHeapDataConverter.toHeapData(dataExpiryPolicy), record);
        this.wanEventPublisher.publishWanUpdate(this.name, entryView);
    }

    protected final void publishWanRemove(Data dataKey) {
        if (!this.recordStore.isWanReplicationEnabled()) {
            return;
        }
        this.wanEventPublisher.publishWanRemove(this.name, ToHeapDataConverter.toHeapData(dataKey));
    }
}

