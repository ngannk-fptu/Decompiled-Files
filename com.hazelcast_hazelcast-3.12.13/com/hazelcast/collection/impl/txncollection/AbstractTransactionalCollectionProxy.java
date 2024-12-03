/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.txncollection;

import com.hazelcast.collection.impl.collection.CollectionItem;
import com.hazelcast.collection.impl.collection.operations.CollectionSizeOperation;
import com.hazelcast.collection.impl.txncollection.CollectionTransactionLogRecord;
import com.hazelcast.collection.impl.txncollection.CollectionTxnOperation;
import com.hazelcast.collection.impl.txncollection.operations.CollectionReserveAddOperation;
import com.hazelcast.collection.impl.txncollection.operations.CollectionReserveRemoveOperation;
import com.hazelcast.collection.impl.txncollection.operations.CollectionTxnAddOperation;
import com.hazelcast.collection.impl.txncollection.operations.CollectionTxnRemoveOperation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.RemoteService;
import com.hazelcast.spi.TransactionalDistributedObject;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionNotActiveException;
import com.hazelcast.transaction.impl.Transaction;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractTransactionalCollectionProxy<S extends RemoteService, E>
extends TransactionalDistributedObject<S> {
    protected final Set<Long> itemIdSet = new HashSet<Long>();
    protected final String name;
    protected final int partitionId;
    protected final OperationService operationService;

    public AbstractTransactionalCollectionProxy(String name, Transaction tx, NodeEngine nodeEngine, S service) {
        super(nodeEngine, service, tx);
        this.name = name;
        this.partitionId = nodeEngine.getPartitionService().getPartitionId(this.getNameAsPartitionAwareData());
        this.operationService = nodeEngine.getOperationService();
    }

    protected abstract Collection<CollectionItem> getCollection();

    @Override
    public String getName() {
        return this.name;
    }

    public boolean add(E e) {
        this.checkTransactionActive();
        this.checkObjectNotNull(e);
        Data value = this.getNodeEngine().toData(e);
        CollectionReserveAddOperation operation = new CollectionReserveAddOperation(this.name, this.tx.getTxnId(), null);
        try {
            InternalCompletableFuture future = this.operationService.invokeOnPartition(this.getServiceName(), operation, this.partitionId);
            Long itemId = (Long)future.get();
            if (itemId != null) {
                if (!this.itemIdSet.add(itemId)) {
                    throw new TransactionException("Duplicate itemId: " + itemId);
                }
                this.getCollection().add(new CollectionItem(itemId, value));
                CollectionTxnAddOperation op = new CollectionTxnAddOperation(this.name, itemId, value);
                this.putToRecord(op);
                return true;
            }
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
        return false;
    }

    protected void putToRecord(CollectionTxnOperation operation) {
        CollectionTransactionLogRecord logRecord = (CollectionTransactionLogRecord)this.tx.get(this.name);
        if (logRecord == null) {
            logRecord = new CollectionTransactionLogRecord(this.getServiceName(), this.tx.getTxnId(), this.name, this.partitionId);
            this.tx.add(logRecord);
        }
        logRecord.addOperation(operation);
    }

    private void removeFromRecord(long itemId) {
        CollectionTransactionLogRecord logRecord = (CollectionTransactionLogRecord)this.tx.get(this.name);
        int size = logRecord.removeOperation(itemId);
        if (size == 0) {
            this.tx.remove(this.name);
        }
    }

    public boolean remove(E e) {
        this.checkTransactionActive();
        this.checkObjectNotNull(e);
        Data value = this.getNodeEngine().toData(e);
        Iterator<CollectionItem> iterator = this.getCollection().iterator();
        long reservedItemId = -1L;
        while (iterator.hasNext()) {
            CollectionItem item = iterator.next();
            if (!value.equals(item.getValue())) continue;
            reservedItemId = item.getItemId();
            break;
        }
        CollectionReserveRemoveOperation operation = new CollectionReserveRemoveOperation(this.name, reservedItemId, value, this.tx.getTxnId());
        try {
            InternalCompletableFuture future = this.operationService.invokeOnPartition(this.getServiceName(), operation, this.partitionId);
            CollectionItem item = (CollectionItem)future.get();
            if (item != null) {
                if (reservedItemId == item.getItemId()) {
                    iterator.remove();
                    this.removeFromRecord(reservedItemId);
                    this.itemIdSet.remove(reservedItemId);
                    return true;
                }
                if (!this.itemIdSet.add(item.getItemId())) {
                    throw new TransactionException("Duplicate itemId: " + item.getItemId());
                }
                CollectionTxnRemoveOperation op = new CollectionTxnRemoveOperation(this.name, item.getItemId());
                this.putToRecord(op);
                return true;
            }
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
        return false;
    }

    public int size() {
        this.checkTransactionActive();
        try {
            CollectionSizeOperation operation = new CollectionSizeOperation(this.name);
            InternalCompletableFuture future = this.operationService.invokeOnPartition(this.getServiceName(), operation, this.partitionId);
            Integer size = (Integer)future.get();
            return size + this.getCollection().size();
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    protected void checkTransactionActive() {
        if (!this.tx.getState().equals((Object)Transaction.State.ACTIVE)) {
            throw new TransactionNotActiveException("Transaction is not active!");
        }
    }

    protected void checkObjectNotNull(Object o) {
        Preconditions.checkNotNull(o, "Object is null");
    }
}

