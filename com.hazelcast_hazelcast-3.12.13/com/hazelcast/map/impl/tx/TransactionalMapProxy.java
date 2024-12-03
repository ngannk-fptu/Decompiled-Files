/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.tx;

import com.hazelcast.core.TransactionalMap;
import com.hazelcast.internal.nearcache.NearCachingHook;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.query.Query;
import com.hazelcast.map.impl.query.QueryEngine;
import com.hazelcast.map.impl.query.QueryResult;
import com.hazelcast.map.impl.query.QueryResultUtils;
import com.hazelcast.map.impl.query.Target;
import com.hazelcast.map.impl.tx.TransactionalMapProxySupport;
import com.hazelcast.map.impl.tx.TxnValueWrapper;
import com.hazelcast.map.impl.tx.VersionedValue;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.TruePredicate;
import com.hazelcast.query.impl.CachedQueryEntry;
import com.hazelcast.query.impl.getters.Extractors;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.transaction.impl.Transaction;
import com.hazelcast.util.IterationType;
import com.hazelcast.util.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class TransactionalMapProxy
extends TransactionalMapProxySupport
implements TransactionalMap {
    private final Map<Data, TxnValueWrapper> txMap = new HashMap<Data, TxnValueWrapper>();

    public TransactionalMapProxy(String name, MapService mapService, NodeEngine nodeEngine, Transaction transaction) {
        super(name, mapService, nodeEngine, transaction);
    }

    @Override
    public boolean containsKey(Object key) {
        return this.containsKey(key, false);
    }

    public boolean containsKey(Object key, boolean skipNearCacheLookup) {
        this.checkTransactionState();
        Preconditions.checkNotNull(key, "key can't be null");
        Data keyData = this.mapServiceContext.toData(key, this.partitionStrategy);
        TxnValueWrapper valueWrapper = this.txMap.get(keyData);
        if (valueWrapper != null) {
            return valueWrapper.type != TxnValueWrapper.Type.REMOVED;
        }
        return this.containsKeyInternal(keyData, key, skipNearCacheLookup);
    }

    @Override
    public int size() {
        this.checkTransactionState();
        int currentSize = this.sizeInternal();
        for (Map.Entry<Data, TxnValueWrapper> entry : this.txMap.entrySet()) {
            VersionedValue versionedValue;
            TxnValueWrapper wrapper = entry.getValue();
            if (wrapper.type == TxnValueWrapper.Type.NEW) {
                ++currentSize;
                continue;
            }
            if (wrapper.type != TxnValueWrapper.Type.REMOVED || (versionedValue = (VersionedValue)this.valueMap.get(entry.getKey())) == null || versionedValue.value == null) continue;
            --currentSize;
        }
        return currentSize;
    }

    @Override
    public boolean isEmpty() {
        this.checkTransactionState();
        return this.size() == 0;
    }

    @Override
    public Object get(Object key) {
        return this.get(key, false);
    }

    public Object get(Object key, boolean skipNearCacheLookup) {
        this.checkTransactionState();
        Preconditions.checkNotNull(key, "key can't be null");
        Object nearCacheKey = this.toNearCacheKeyWithStrategy(key);
        Data keyData = this.mapServiceContext.toData(nearCacheKey, this.partitionStrategy);
        TxnValueWrapper currentValue = this.txMap.get(keyData);
        if (currentValue != null) {
            return this.checkIfRemoved(currentValue);
        }
        return this.toObjectIfNeeded(this.getInternal(nearCacheKey, keyData, skipNearCacheLookup));
    }

    public Object getForUpdate(Object key) {
        this.checkTransactionState();
        Preconditions.checkNotNull(key, "key can't be null");
        Data keyData = this.mapServiceContext.toData(key, this.partitionStrategy);
        TxnValueWrapper currentValue = this.txMap.get(keyData);
        if (currentValue != null) {
            return this.checkIfRemoved(currentValue);
        }
        return this.toObjectIfNeeded(this.getForUpdateInternal(keyData));
    }

    @Override
    public Object put(Object key, Object value) {
        return this.put(key, value, -1L, TimeUnit.MILLISECONDS);
    }

    @Override
    public Object put(Object key, Object value, long ttl, TimeUnit timeUnit) {
        this.checkTransactionState();
        Preconditions.checkNotNull(key, "key can't be null");
        Preconditions.checkNotNull(value, "value can't be null");
        Data keyData = this.mapServiceContext.toData(key, this.partitionStrategy);
        Data valueData = this.mapServiceContext.toData(value);
        NearCachingHook nearCachingHook = this.newNearCachingHook();
        nearCachingHook.beforeRemoteCall(key, keyData, value, valueData);
        Object valueBeforeTxn = this.toObjectIfNeeded(this.putInternal(keyData, valueData, ttl, timeUnit, nearCachingHook));
        TxnValueWrapper currentValue = this.txMap.get(keyData);
        TxnValueWrapper.Type type = valueBeforeTxn == null ? TxnValueWrapper.Type.NEW : TxnValueWrapper.Type.UPDATED;
        TxnValueWrapper wrapper = new TxnValueWrapper(value, type);
        this.txMap.put(keyData, wrapper);
        return currentValue == null ? valueBeforeTxn : this.checkIfRemoved(currentValue);
    }

    @Override
    public void set(Object key, Object value) {
        this.checkTransactionState();
        Preconditions.checkNotNull(key, "key can't be null");
        Preconditions.checkNotNull(value, "value can't be null");
        Data keyData = this.mapServiceContext.toData(key, this.partitionStrategy);
        Data valueData = this.mapServiceContext.toData(value);
        NearCachingHook nearCachingHook = this.newNearCachingHook();
        nearCachingHook.beforeRemoteCall(key, keyData, value, valueData);
        Data dataBeforeTxn = this.putInternal(keyData, valueData, -1L, TimeUnit.MILLISECONDS, nearCachingHook);
        TxnValueWrapper.Type type = dataBeforeTxn == null ? TxnValueWrapper.Type.NEW : TxnValueWrapper.Type.UPDATED;
        TxnValueWrapper wrapper = new TxnValueWrapper(value, type);
        this.txMap.put(keyData, wrapper);
    }

    @Override
    public Object putIfAbsent(Object key, Object value) {
        boolean haveTxnPast;
        this.checkTransactionState();
        Preconditions.checkNotNull(key, "key can't be null");
        Preconditions.checkNotNull(value, "value can't be null");
        Data keyData = this.mapServiceContext.toData(key, this.partitionStrategy);
        Data valueData = this.mapServiceContext.toData(value);
        NearCachingHook nearCachingHook = this.newNearCachingHook();
        nearCachingHook.beforeRemoteCall(key, keyData, value, valueData);
        TxnValueWrapper wrapper = this.txMap.get(keyData);
        boolean bl = haveTxnPast = wrapper != null;
        if (haveTxnPast) {
            if (wrapper.type != TxnValueWrapper.Type.REMOVED) {
                return wrapper.value;
            }
            this.putInternal(keyData, valueData, -1L, TimeUnit.MILLISECONDS, nearCachingHook);
            this.txMap.put(keyData, new TxnValueWrapper(value, TxnValueWrapper.Type.NEW));
            return null;
        }
        Data oldValue = this.putIfAbsentInternal(keyData, valueData, nearCachingHook);
        if (oldValue == null) {
            this.txMap.put(keyData, new TxnValueWrapper(value, TxnValueWrapper.Type.NEW));
        }
        return this.toObjectIfNeeded(oldValue);
    }

    @Override
    public Object replace(Object key, Object value) {
        boolean haveTxnPast;
        this.checkTransactionState();
        Preconditions.checkNotNull(key, "key can't be null");
        Preconditions.checkNotNull(value, "value can't be null");
        Data keyData = this.mapServiceContext.toData(key, this.partitionStrategy);
        Data valueData = this.mapServiceContext.toData(value);
        NearCachingHook nearCachingHook = this.newNearCachingHook();
        nearCachingHook.beforeRemoteCall(key, keyData, value, valueData);
        TxnValueWrapper wrapper = this.txMap.get(keyData);
        boolean bl = haveTxnPast = wrapper != null;
        if (haveTxnPast) {
            if (wrapper.type == TxnValueWrapper.Type.REMOVED) {
                return null;
            }
            this.putInternal(keyData, valueData, -1L, TimeUnit.MILLISECONDS, nearCachingHook);
            this.txMap.put(keyData, new TxnValueWrapper(value, TxnValueWrapper.Type.UPDATED));
            return wrapper.value;
        }
        Data oldValue = this.replaceInternal(keyData, valueData, nearCachingHook);
        if (oldValue != null) {
            this.txMap.put(keyData, new TxnValueWrapper(value, TxnValueWrapper.Type.UPDATED));
        }
        return this.toObjectIfNeeded(oldValue);
    }

    @Override
    public boolean replace(Object key, Object oldValue, Object newValue) {
        boolean haveTxnPast;
        this.checkTransactionState();
        Preconditions.checkNotNull(key, "key can't be null");
        Preconditions.checkNotNull(oldValue, "oldValue can't be null");
        Preconditions.checkNotNull(newValue, "newValue can't be null");
        Data keyData = this.mapServiceContext.toData(key, this.partitionStrategy);
        Data newValueData = this.mapServiceContext.toData(newValue);
        NearCachingHook nearCachingHook = this.newNearCachingHook();
        nearCachingHook.beforeRemoteCall(key, keyData, newValue, newValueData);
        TxnValueWrapper wrapper = this.txMap.get(keyData);
        boolean bl = haveTxnPast = wrapper != null;
        if (haveTxnPast) {
            if (!wrapper.value.equals(oldValue)) {
                return false;
            }
            this.putInternal(keyData, newValueData, -1L, TimeUnit.MILLISECONDS, nearCachingHook);
            this.txMap.put(keyData, new TxnValueWrapper(wrapper.value, TxnValueWrapper.Type.UPDATED));
            return true;
        }
        boolean success = this.replaceIfSameInternal(keyData, this.mapServiceContext.toData(oldValue), newValueData, nearCachingHook);
        if (success) {
            this.txMap.put(keyData, new TxnValueWrapper(newValue, TxnValueWrapper.Type.UPDATED));
        }
        return success;
    }

    @Override
    public boolean remove(Object key, Object value) {
        this.checkTransactionState();
        Preconditions.checkNotNull(key, "key can't be null");
        Preconditions.checkNotNull(value, "value can't be null");
        Data keyData = this.mapServiceContext.toData(key, this.partitionStrategy);
        NearCachingHook nearCachingHook = this.newNearCachingHook();
        nearCachingHook.beforeRemoteCall(key, keyData, null, null);
        TxnValueWrapper wrapper = this.txMap.get(keyData);
        if (wrapper == null) {
            boolean removed = this.removeIfSameInternal(keyData, value, nearCachingHook);
            if (removed) {
                this.txMap.put(keyData, new TxnValueWrapper(value, TxnValueWrapper.Type.REMOVED));
            }
            return removed;
        }
        if (wrapper.type == TxnValueWrapper.Type.REMOVED) {
            return false;
        }
        if (!this.isEquals(wrapper.value, value)) {
            return false;
        }
        this.removeInternal(keyData, nearCachingHook);
        this.txMap.put(keyData, new TxnValueWrapper(value, TxnValueWrapper.Type.REMOVED));
        return true;
    }

    @Override
    public Object remove(Object key) {
        this.checkTransactionState();
        Preconditions.checkNotNull(key, "key can't be null");
        Data keyData = this.mapServiceContext.toData(key, this.partitionStrategy);
        NearCachingHook nearCachingHook = this.newNearCachingHook();
        nearCachingHook.beforeRemoteCall(key, keyData, null, null);
        Object valueBeforeTxn = this.toObjectIfNeeded(this.removeInternal(keyData, nearCachingHook));
        TxnValueWrapper wrapper = null;
        if (valueBeforeTxn != null || this.txMap.containsKey(keyData)) {
            wrapper = this.txMap.put(keyData, new TxnValueWrapper(valueBeforeTxn, TxnValueWrapper.Type.REMOVED));
        }
        return wrapper == null ? valueBeforeTxn : this.checkIfRemoved(wrapper);
    }

    @Override
    public void delete(Object key) {
        this.checkTransactionState();
        Preconditions.checkNotNull(key, "key can't be null");
        Data keyData = this.mapServiceContext.toData(key, this.partitionStrategy);
        NearCachingHook nearCachingHook = this.newNearCachingHook();
        nearCachingHook.beforeRemoteCall(key, keyData, null, null);
        Data data = this.removeInternal(keyData, nearCachingHook);
        if (data != null || this.txMap.containsKey(keyData)) {
            this.txMap.put(keyData, new TxnValueWrapper(this.toObjectIfNeeded(data), TxnValueWrapper.Type.REMOVED));
        }
    }

    @Override
    public Set<Object> keySet() {
        return this.keySet(TruePredicate.INSTANCE);
    }

    @Override
    public Set keySet(Predicate predicate) {
        this.checkTransactionState();
        Preconditions.checkNotNull(predicate, "Predicate should not be null!");
        Preconditions.checkNotInstanceOf(PagingPredicate.class, predicate, "Paging is not supported for Transactional queries!");
        QueryEngine queryEngine = this.mapServiceContext.getQueryEngine(this.name);
        Query query = Query.of().mapName(this.name).predicate(predicate).iterationType(IterationType.KEY).build();
        QueryResult queryResult = (QueryResult)queryEngine.execute(query, Target.ALL_NODES);
        Set queryResultSet = QueryResultUtils.transformToSet(this.ss, queryResult, predicate, IterationType.KEY, true, this.tx.isOriginatedFromClient());
        Extractors extractors = this.mapServiceContext.getExtractors(this.name);
        HashSet<Object> returningKeySet = new HashSet<Object>(queryResultSet);
        CachedQueryEntry cachedQueryEntry = new CachedQueryEntry();
        for (Map.Entry<Data, TxnValueWrapper> entry : this.txMap.entrySet()) {
            if (entry.getValue().type == TxnValueWrapper.Type.REMOVED) {
                returningKeySet.remove(this.toObjectIfNeeded(entry.getKey()));
                continue;
            }
            Data keyData = entry.getKey();
            if (predicate == TruePredicate.INSTANCE) {
                returningKeySet.add(this.toObjectIfNeeded(keyData));
                continue;
            }
            cachedQueryEntry.init(this.ss, keyData, entry.getValue().value, extractors);
            if (!predicate.apply(cachedQueryEntry)) continue;
            returningKeySet.add(this.toObjectIfNeeded(keyData));
        }
        return returningKeySet;
    }

    @Override
    public Collection<Object> values() {
        return this.values(TruePredicate.INSTANCE);
    }

    @Override
    public Collection values(Predicate predicate) {
        this.checkTransactionState();
        Preconditions.checkNotNull(predicate, "Predicate can not be null!");
        Preconditions.checkNotInstanceOf(PagingPredicate.class, predicate, "Paging is not supported for Transactional queries");
        QueryEngine queryEngine = this.mapServiceContext.getQueryEngine(this.name);
        Query query = Query.of().mapName(this.name).predicate(predicate).iterationType(IterationType.ENTRY).build();
        QueryResult queryResult = (QueryResult)queryEngine.execute(query, Target.ALL_NODES);
        Set result = QueryResultUtils.transformToSet(this.ss, queryResult, predicate, IterationType.ENTRY, true, true);
        ArrayList<Object> valueSet = new ArrayList<Object>();
        HashSet<Data> keyWontBeIncluded = new HashSet<Data>();
        Extractors extractors = this.mapServiceContext.getExtractors(this.name);
        CachedQueryEntry cachedQueryEntry = new CachedQueryEntry();
        for (Map.Entry<Data, TxnValueWrapper> entry : this.txMap.entrySet()) {
            boolean isRemoved = TxnValueWrapper.Type.REMOVED.equals((Object)entry.getValue().type);
            boolean isUpdated = TxnValueWrapper.Type.UPDATED.equals((Object)entry.getValue().type);
            if (isRemoved) {
                keyWontBeIncluded.add(entry.getKey());
                continue;
            }
            if (isUpdated) {
                keyWontBeIncluded.add(entry.getKey());
            }
            Object entryValue = entry.getValue().value;
            cachedQueryEntry.init(this.ss, entry.getKey(), entryValue, extractors);
            if (!predicate.apply(cachedQueryEntry)) continue;
            valueSet.add(this.toObjectIfNeeded(cachedQueryEntry.getValueData()));
        }
        this.removeFromResultSet(result, valueSet, keyWontBeIncluded);
        return valueSet;
    }

    @Override
    public String toString() {
        return "TransactionalMap{name='" + this.name + '\'' + '}';
    }

    private Object checkIfRemoved(TxnValueWrapper wrapper) {
        this.checkTransactionState();
        return wrapper == null || wrapper.type == TxnValueWrapper.Type.REMOVED ? null : wrapper.value;
    }

    private void removeFromResultSet(Set<Map.Entry> queryResultSet, List<Object> valueSet, Set<Data> keyWontBeIncluded) {
        for (Map.Entry entry : queryResultSet) {
            if (keyWontBeIncluded.contains(entry.getKey())) continue;
            valueSet.add(this.toObjectIfNeeded(entry.getValue()));
        }
    }
}

