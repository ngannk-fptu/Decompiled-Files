/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.tx;

import com.hazelcast.config.MapConfig;
import com.hazelcast.core.PartitioningStrategy;
import com.hazelcast.internal.nearcache.NearCache;
import com.hazelcast.internal.nearcache.NearCachingHook;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.util.comparators.ValueComparator;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.nearcache.MapNearCacheManager;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.map.impl.tx.MapTransactionLogRecord;
import com.hazelcast.map.impl.tx.TxnUnlockOperation;
import com.hazelcast.map.impl.tx.VersionedValue;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.TransactionalDistributedObject;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.transaction.TransactionNotActiveException;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.transaction.TransactionTimedOutException;
import com.hazelcast.transaction.impl.Transaction;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.ThreadUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class TransactionalMapProxySupport
extends TransactionalDistributedObject<MapService> {
    protected final Map<Data, VersionedValue> valueMap = new HashMap<Data, VersionedValue>();
    protected final String name;
    protected final MapServiceContext mapServiceContext;
    protected final MapNearCacheManager mapNearCacheManager;
    protected final MapOperationProvider operationProvider;
    protected final PartitioningStrategy partitionStrategy;
    protected final IPartitionService partitionService;
    protected final OperationService operationService;
    protected final InternalSerializationService ss;
    private final boolean serializeKeys;
    private final boolean nearCacheEnabled;
    private final ValueComparator valueComparator;

    TransactionalMapProxySupport(String name, MapService mapService, NodeEngine nodeEngine, Transaction transaction) {
        super(nodeEngine, mapService, transaction);
        this.name = name;
        this.mapServiceContext = mapService.getMapServiceContext();
        this.mapNearCacheManager = this.mapServiceContext.getMapNearCacheManager();
        MapConfig mapConfig = nodeEngine.getConfig().findMapConfig(name);
        this.operationProvider = this.mapServiceContext.getMapOperationProvider(mapConfig);
        this.partitionStrategy = this.mapServiceContext.getPartitioningStrategy(name, mapConfig.getPartitioningStrategyConfig());
        this.partitionService = nodeEngine.getPartitionService();
        this.operationService = nodeEngine.getOperationService();
        this.ss = (InternalSerializationService)nodeEngine.getSerializationService();
        this.nearCacheEnabled = mapConfig.isNearCacheEnabled();
        this.serializeKeys = this.nearCacheEnabled && mapConfig.getNearCacheConfig().isSerializeKeys();
        this.valueComparator = this.mapServiceContext.getValueComparatorOf(mapConfig.getInMemoryFormat());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public final String getServiceName() {
        return "hz:impl:mapService";
    }

    boolean isEquals(Object value1, Object value2) {
        return this.valueComparator.isEqual(value1, value2, this.ss);
    }

    void checkTransactionState() {
        if (!this.tx.getState().equals((Object)Transaction.State.ACTIVE)) {
            throw new TransactionNotActiveException("Transaction is not active!");
        }
    }

    boolean containsKeyInternal(Data dataKey, Object objectKey, boolean skipNearCacheLookup) {
        Object nearCacheKey;
        Object cachedValue;
        if (!skipNearCacheLookup && this.nearCacheEnabled && (cachedValue = this.getCachedValue(nearCacheKey = this.serializeKeys ? dataKey : objectKey, false)) != NearCache.NOT_CACHED) {
            return cachedValue != null;
        }
        MapOperation operation = this.operationProvider.createContainsKeyOperation(this.name, dataKey);
        operation.setThreadId(ThreadUtil.getThreadId());
        int partitionId = this.partitionService.getPartitionId(dataKey);
        try {
            InternalCompletableFuture future = this.operationService.invokeOnPartition("hz:impl:mapService", operation, partitionId);
            return (Boolean)future.get();
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    Object getInternal(Object nearCacheKey, Data keyData, boolean skipNearCacheLookup) {
        Object value;
        if (!skipNearCacheLookup && this.nearCacheEnabled && (value = this.getCachedValue(nearCacheKey, true)) != NearCache.NOT_CACHED) {
            return value;
        }
        MapOperation operation = this.operationProvider.createGetOperation(this.name, keyData);
        operation.setThreadId(ThreadUtil.getThreadId());
        int partitionId = this.partitionService.getPartitionId(keyData);
        try {
            InternalCompletableFuture future = this.operationService.createInvocationBuilder("hz:impl:mapService", (Operation)operation, partitionId).setResultDeserialized(false).invoke();
            return future.get();
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    final Object toNearCacheKeyWithStrategy(Object key) {
        if (!this.nearCacheEnabled) {
            return key;
        }
        return this.serializeKeys ? this.ss.toData(key, this.partitionStrategy) : key;
    }

    final void invalidateNearCache(Object nearCacheKey) {
        if (!this.nearCacheEnabled) {
            return;
        }
        if (nearCacheKey == null) {
            return;
        }
        NearCache nearCache = this.mapNearCacheManager.getNearCache(this.name);
        if (nearCache == null) {
            return;
        }
        nearCache.invalidate(nearCacheKey);
    }

    private Object getCachedValue(Object nearCacheKey, boolean deserializeValue) {
        NearCache nearCache = this.mapNearCacheManager.getNearCache(this.name);
        if (nearCache == null) {
            return NearCache.NOT_CACHED;
        }
        Object value = nearCache.get(nearCacheKey);
        if (value == null) {
            return NearCache.NOT_CACHED;
        }
        if (value == NearCache.CACHED_AS_NULL) {
            return null;
        }
        this.mapServiceContext.interceptAfterGet(this.name, value);
        return deserializeValue ? this.ss.toObject(value) : value;
    }

    Object getForUpdateInternal(Data key) {
        VersionedValue versionedValue = this.lockAndGet(key, this.tx.getTimeoutMillis(), true);
        this.addUnlockTransactionRecord(key, versionedValue.version);
        return versionedValue.value;
    }

    int sizeInternal() {
        try {
            OperationFactory sizeOperationFactory = this.operationProvider.createMapSizeOperationFactory(this.name);
            Map<Integer, Object> results = this.operationService.invokeOnAllPartitions("hz:impl:mapService", sizeOperationFactory);
            int total = 0;
            for (Object result : results.values()) {
                Integer size = (Integer)this.getNodeEngine().toObject(result);
                total += size.intValue();
            }
            return total;
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    Data putInternal(Data key, Data value, long ttl, TimeUnit timeUnit, NearCachingHook hook) {
        VersionedValue versionedValue = this.lockAndGet(key, this.tx.getTimeoutMillis());
        long timeInMillis = TransactionalMapProxySupport.getTimeInMillis(ttl, timeUnit);
        MapOperation operation = this.operationProvider.createTxnSetOperation(this.name, key, value, versionedValue.version, timeInMillis);
        this.tx.add(new MapTransactionLogRecord(this.name, key, this.getPartitionId(key), operation, this.tx.getOwnerUuid(), hook));
        return versionedValue.value;
    }

    Data putIfAbsentInternal(Data key, Data value, NearCachingHook hook) {
        boolean unlockImmediately = !this.valueMap.containsKey(key);
        VersionedValue versionedValue = this.lockAndGet(key, this.tx.getTimeoutMillis());
        if (versionedValue.value != null) {
            if (unlockImmediately) {
                this.unlock(key, versionedValue);
                return versionedValue.value;
            }
            this.addUnlockTransactionRecord(key, versionedValue.version);
            return versionedValue.value;
        }
        MapOperation operation = this.operationProvider.createTxnSetOperation(this.name, key, value, versionedValue.version, -1L);
        this.tx.add(new MapTransactionLogRecord(this.name, key, this.getPartitionId(key), operation, this.tx.getOwnerUuid(), hook));
        return versionedValue.value;
    }

    Data replaceInternal(Data key, Data value, NearCachingHook hook) {
        boolean unlockImmediately = !this.valueMap.containsKey(key);
        VersionedValue versionedValue = this.lockAndGet(key, this.tx.getTimeoutMillis());
        if (versionedValue.value == null) {
            if (unlockImmediately) {
                this.unlock(key, versionedValue);
                return null;
            }
            this.addUnlockTransactionRecord(key, versionedValue.version);
            return null;
        }
        MapOperation operation = this.operationProvider.createTxnSetOperation(this.name, key, value, versionedValue.version, -1L);
        this.tx.add(new MapTransactionLogRecord(this.name, key, this.getPartitionId(key), operation, this.tx.getOwnerUuid(), hook));
        return versionedValue.value;
    }

    boolean replaceIfSameInternal(Data key, Object oldValue, Data newValue, NearCachingHook hook) {
        boolean unlockImmediately = !this.valueMap.containsKey(key);
        VersionedValue versionedValue = this.lockAndGet(key, this.tx.getTimeoutMillis());
        if (!this.isEquals(oldValue, versionedValue.value)) {
            if (unlockImmediately) {
                this.unlock(key, versionedValue);
                return false;
            }
            this.addUnlockTransactionRecord(key, versionedValue.version);
            return false;
        }
        MapOperation operation = this.operationProvider.createTxnSetOperation(this.name, key, newValue, versionedValue.version, -1L);
        this.tx.add(new MapTransactionLogRecord(this.name, key, this.getPartitionId(key), operation, this.tx.getOwnerUuid(), hook));
        return true;
    }

    Data removeInternal(Data key, NearCachingHook nearCachingHook) {
        VersionedValue versionedValue = this.lockAndGet(key, this.tx.getTimeoutMillis());
        this.tx.add(new MapTransactionLogRecord(this.name, key, this.getPartitionId(key), this.operationProvider.createTxnDeleteOperation(this.name, key, versionedValue.version), this.tx.getOwnerUuid(), nearCachingHook));
        return versionedValue.value;
    }

    boolean removeIfSameInternal(Data key, Object value, NearCachingHook hook) {
        boolean unlockImmediately = !this.valueMap.containsKey(key);
        VersionedValue versionedValue = this.lockAndGet(key, this.tx.getTimeoutMillis());
        if (!this.isEquals(versionedValue.value, value)) {
            if (unlockImmediately) {
                this.unlock(key, versionedValue);
                return false;
            }
            this.addUnlockTransactionRecord(key, versionedValue.version);
            return false;
        }
        this.tx.add(new MapTransactionLogRecord(this.name, key, this.getPartitionId(key), this.operationProvider.createTxnDeleteOperation(this.name, key, versionedValue.version), this.tx.getOwnerUuid(), hook));
        return true;
    }

    private void unlock(Data key, VersionedValue versionedValue) {
        try {
            TxnUnlockOperation unlockOperation = new TxnUnlockOperation(this.name, key, versionedValue.version);
            unlockOperation.setThreadId(ThreadUtil.getThreadId());
            unlockOperation.setOwnerUuid(this.tx.getOwnerUuid());
            int partitionId = this.partitionService.getPartitionId(key);
            InternalCompletableFuture future = this.operationService.invokeOnPartition("hz:impl:mapService", unlockOperation, partitionId);
            future.get();
            this.valueMap.remove(key);
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    private void addUnlockTransactionRecord(Data key, long version) {
        TxnUnlockOperation operation = new TxnUnlockOperation(this.name, key, version);
        this.tx.add(new MapTransactionLogRecord(this.name, key, this.getPartitionId(key), operation, this.tx.getOwnerUuid(), NearCachingHook.EMPTY_HOOK));
    }

    private VersionedValue lockAndGet(Data key, long timeout) {
        return this.lockAndGet(key, timeout, false);
    }

    private VersionedValue lockAndGet(Data key, long timeout, boolean shouldLoad) {
        VersionedValue versionedValue = this.valueMap.get(key);
        if (versionedValue != null) {
            return versionedValue;
        }
        boolean blockReads = this.tx.getTransactionType() == TransactionOptions.TransactionType.ONE_PHASE;
        MapOperation operation = this.operationProvider.createTxnLockAndGetOperation(this.name, key, timeout, timeout, this.tx.getOwnerUuid(), shouldLoad, blockReads);
        operation.setThreadId(ThreadUtil.getThreadId());
        try {
            int partitionId = this.partitionService.getPartitionId(key);
            InternalCompletableFuture future = this.operationService.invokeOnPartition("hz:impl:mapService", operation, partitionId);
            versionedValue = (VersionedValue)future.get();
            if (versionedValue == null) {
                throw new TransactionTimedOutException("Transaction couldn't obtain lock for the key: " + this.toObjectIfNeeded(key));
            }
            this.valueMap.put(key, versionedValue);
            return versionedValue;
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    private static long getTimeInMillis(long time, TimeUnit timeunit) {
        return timeunit != null ? timeunit.toMillis(time) : time;
    }

    protected NearCachingHook newNearCachingHook() {
        return this.nearCacheEnabled ? new InvalidationHook() : NearCachingHook.EMPTY_HOOK;
    }

    private class InvalidationHook
    implements NearCachingHook {
        private Object nearCacheKey;

        private InvalidationHook() {
        }

        public void beforeRemoteCall(Object key, Data keyData, Object value, Data valueData) {
            this.nearCacheKey = TransactionalMapProxySupport.this.serializeKeys ? keyData : TransactionalMapProxySupport.this.mapServiceContext.toObject(key);
        }

        @Override
        public void onRemoteCallSuccess() {
            TransactionalMapProxySupport.this.invalidateNearCache(this.nearCacheKey);
        }

        @Override
        public void onRemoteCallFailure() {
            TransactionalMapProxySupport.this.invalidateNearCache(this.nearCacheKey);
        }
    }
}

