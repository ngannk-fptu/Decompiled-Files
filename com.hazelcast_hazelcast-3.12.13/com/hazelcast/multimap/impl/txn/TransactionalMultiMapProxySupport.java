/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.txn;

import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.core.TransactionalMultiMap;
import com.hazelcast.multimap.impl.MultiMapRecord;
import com.hazelcast.multimap.impl.MultiMapService;
import com.hazelcast.multimap.impl.operations.CountOperation;
import com.hazelcast.multimap.impl.operations.GetAllOperation;
import com.hazelcast.multimap.impl.operations.MultiMapOperationFactory;
import com.hazelcast.multimap.impl.operations.MultiMapResponse;
import com.hazelcast.multimap.impl.txn.MultiMapTransactionLogRecord;
import com.hazelcast.multimap.impl.txn.TransactionRecordKey;
import com.hazelcast.multimap.impl.txn.TxnGenerateRecordIdOperation;
import com.hazelcast.multimap.impl.txn.TxnLockAndGetOperation;
import com.hazelcast.multimap.impl.txn.TxnPutOperation;
import com.hazelcast.multimap.impl.txn.TxnRemoveAllOperation;
import com.hazelcast.multimap.impl.txn.TxnRemoveOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.TransactionalDistributedObject;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.transaction.TransactionNotActiveException;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.transaction.impl.Transaction;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.ThreadUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public abstract class TransactionalMultiMapProxySupport<K, V>
extends TransactionalDistributedObject<MultiMapService>
implements TransactionalMultiMap<K, V> {
    private static final double TIMEOUT_EXTEND_MULTIPLIER = 1.5;
    protected final String name;
    protected final MultiMapConfig config;
    private final Map<Data, Collection<MultiMapRecord>> txMap = new HashMap<Data, Collection<MultiMapRecord>>();
    private final OperationService operationService;
    private final IPartitionService partitionService;

    TransactionalMultiMapProxySupport(NodeEngine nodeEngine, MultiMapService service, String name, Transaction tx) {
        super(nodeEngine, service, tx);
        this.name = name;
        this.config = nodeEngine.getConfig().findMultiMapConfig(name);
        this.operationService = nodeEngine.getOperationService();
        this.partitionService = nodeEngine.getPartitionService();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public final String getServiceName() {
        return "hz:impl:multiMapService";
    }

    @Override
    public int size() {
        this.checkTransactionActive();
        try {
            Map<Integer, Object> results = this.operationService.invokeOnAllPartitions("hz:impl:multiMapService", new MultiMapOperationFactory(this.name, MultiMapOperationFactory.OperationFactoryType.SIZE));
            int size = 0;
            for (Object obj : results.values()) {
                if (obj == null) continue;
                Integer result = (Integer)this.getNodeEngine().toObject(obj);
                size += result.intValue();
            }
            for (Data key : this.txMap.keySet()) {
                MultiMapTransactionLogRecord log = (MultiMapTransactionLogRecord)this.tx.get(this.getRecordLogKey(key));
                if (log == null) continue;
                size += log.size();
            }
            return size;
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    void checkTransactionActive() {
        if (!this.tx.getState().equals((Object)Transaction.State.ACTIVE)) {
            throw new TransactionNotActiveException("Transaction is not active!");
        }
    }

    boolean putInternal(Data key, Data value) {
        MultiMapTransactionLogRecord logRecord;
        this.checkObjectNotNull(key);
        this.checkObjectNotNull(value);
        Collection<MultiMapRecord> coll = this.txMap.get(key);
        long recordId = -1L;
        long timeout = this.tx.getTimeoutMillis();
        long ttl = this.extendTimeout(timeout);
        if (coll == null) {
            MultiMapResponse response = this.lockAndGet(key, timeout, ttl);
            if (response == null) {
                throw new ConcurrentModificationException("Transaction couldn't obtain lock " + this.getThreadId());
            }
            recordId = response.getNextRecordId();
            coll = this.createCollection(response.getRecordCollection(this.getNodeEngine()));
            this.txMap.put(key, coll);
            logRecord = new MultiMapTransactionLogRecord(this.getPartitionId(key), key, this.name, ttl, this.getThreadId());
            this.tx.add(logRecord);
        } else {
            logRecord = (MultiMapTransactionLogRecord)this.tx.get(this.getRecordLogKey(key));
        }
        MultiMapRecord record = new MultiMapRecord(this.config.isBinary() ? value : this.toObjectIfNeeded(value));
        if (coll.add(record)) {
            if (recordId == -1L) {
                recordId = this.nextId(key);
            }
            record.setRecordId(recordId);
            TxnPutOperation operation = new TxnPutOperation(this.name, key, value, recordId);
            logRecord.addOperation(operation);
            return true;
        }
        return false;
    }

    boolean removeInternal(Data key, Data value) {
        MultiMapTransactionLogRecord logRecord;
        this.checkObjectNotNull(key);
        this.checkObjectNotNull(value);
        Collection<MultiMapRecord> coll = this.txMap.get(key);
        long timeout = this.tx.getTimeoutMillis();
        long ttl = this.extendTimeout(timeout);
        if (coll == null) {
            MultiMapResponse response = this.lockAndGet(key, timeout, ttl);
            if (response == null) {
                throw new ConcurrentModificationException("Transaction couldn't obtain lock " + this.getThreadId());
            }
            coll = this.createCollection(response.getRecordCollection(this.getNodeEngine()));
            this.txMap.put(key, coll);
            logRecord = new MultiMapTransactionLogRecord(this.getPartitionId(key), key, this.name, ttl, this.getThreadId());
            this.tx.add(logRecord);
        } else {
            logRecord = (MultiMapTransactionLogRecord)this.tx.get(this.getRecordLogKey(key));
        }
        MultiMapRecord record = new MultiMapRecord(this.config.isBinary() ? value : this.toObjectIfNeeded(value));
        Iterator<MultiMapRecord> iterator = coll.iterator();
        long recordId = -1L;
        while (iterator.hasNext()) {
            MultiMapRecord r = iterator.next();
            if (!r.equals(record)) continue;
            iterator.remove();
            recordId = r.getRecordId();
            break;
        }
        if (recordId != -1L) {
            TxnRemoveOperation operation = new TxnRemoveOperation(this.name, key, recordId, value);
            logRecord.addOperation(operation);
            return true;
        }
        return false;
    }

    Collection<MultiMapRecord> removeAllInternal(Data key) {
        MultiMapTransactionLogRecord logRecord;
        this.checkObjectNotNull(key);
        long timeout = this.tx.getTimeoutMillis();
        long ttl = this.extendTimeout(timeout);
        Collection<MultiMapRecord> coll = this.txMap.get(key);
        if (coll == null) {
            MultiMapResponse response = this.lockAndGet(key, timeout, ttl);
            if (response == null) {
                throw new ConcurrentModificationException("Transaction couldn't obtain lock " + this.getThreadId());
            }
            coll = this.createCollection(response.getRecordCollection(this.getNodeEngine()));
            logRecord = new MultiMapTransactionLogRecord(this.getPartitionId(key), key, this.name, ttl, this.getThreadId());
            this.tx.add(logRecord);
        } else {
            logRecord = (MultiMapTransactionLogRecord)this.tx.get(this.getRecordLogKey(key));
        }
        this.txMap.put(key, this.createCollection());
        TxnRemoveAllOperation operation = new TxnRemoveAllOperation(this.name, key, coll);
        logRecord.addOperation(operation);
        return coll;
    }

    Collection<MultiMapRecord> getInternal(Data key) {
        this.checkObjectNotNull(key);
        Collection<MultiMapRecord> coll = this.txMap.get(key);
        if (coll == null) {
            GetAllOperation operation = new GetAllOperation(this.name, key);
            operation.setThreadId(ThreadUtil.getThreadId());
            try {
                int partitionId = this.partitionService.getPartitionId(key);
                InternalCompletableFuture future = this.operationService.invokeOnPartition("hz:impl:multiMapService", operation, partitionId);
                MultiMapResponse response = (MultiMapResponse)future.get();
                coll = response.getRecordCollection(this.getNodeEngine());
            }
            catch (Throwable t) {
                throw ExceptionUtil.rethrow(t);
            }
        }
        return coll;
    }

    int valueCountInternal(Data key) {
        this.checkObjectNotNull(key);
        Collection<MultiMapRecord> coll = this.txMap.get(key);
        if (coll == null) {
            CountOperation operation = new CountOperation(this.name, key);
            operation.setThreadId(ThreadUtil.getThreadId());
            try {
                int partitionId = this.partitionService.getPartitionId(key);
                InternalCompletableFuture future = this.operationService.invokeOnPartition("hz:impl:multiMapService", operation, partitionId);
                return (Integer)future.get();
            }
            catch (Throwable t) {
                throw ExceptionUtil.rethrow(t);
            }
        }
        return coll.size();
    }

    private TransactionRecordKey getRecordLogKey(Data key) {
        return new TransactionRecordKey(this.name, key);
    }

    private void checkObjectNotNull(Object o) {
        Preconditions.checkNotNull(o, "Object is null");
    }

    private long getThreadId() {
        return ThreadUtil.getThreadId();
    }

    private MultiMapResponse lockAndGet(Data key, long timeout, long ttl) {
        boolean blockReads = this.tx.getTransactionType() == TransactionOptions.TransactionType.ONE_PHASE;
        TxnLockAndGetOperation operation = new TxnLockAndGetOperation(this.name, key, timeout, ttl, this.getThreadId(), blockReads);
        try {
            int partitionId = this.partitionService.getPartitionId(key);
            InternalCompletableFuture future = this.operationService.invokeOnPartition("hz:impl:multiMapService", operation, partitionId);
            return (MultiMapResponse)future.get();
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    private long nextId(Data key) {
        TxnGenerateRecordIdOperation operation = new TxnGenerateRecordIdOperation(this.name, key);
        try {
            int partitionId = this.partitionService.getPartitionId(key);
            InternalCompletableFuture future = this.operationService.invokeOnPartition("hz:impl:multiMapService", operation, partitionId);
            return (Long)future.get();
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    private Collection<MultiMapRecord> createCollection() {
        if (this.config.getValueCollectionType().equals((Object)MultiMapConfig.ValueCollectionType.SET)) {
            return new HashSet<MultiMapRecord>();
        }
        if (this.config.getValueCollectionType().equals((Object)MultiMapConfig.ValueCollectionType.LIST)) {
            return new ArrayList<MultiMapRecord>();
        }
        return null;
    }

    private Collection<MultiMapRecord> createCollection(Collection<MultiMapRecord> coll) {
        if (this.config.getValueCollectionType().equals((Object)MultiMapConfig.ValueCollectionType.SET)) {
            return new HashSet<MultiMapRecord>(coll);
        }
        if (this.config.getValueCollectionType().equals((Object)MultiMapConfig.ValueCollectionType.LIST)) {
            return new ArrayList<MultiMapRecord>(coll);
        }
        return null;
    }

    private long extendTimeout(long timeout) {
        return (long)((double)timeout * 1.5);
    }
}

