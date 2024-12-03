/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.txnqueue;

import com.hazelcast.collection.impl.queue.QueueItem;
import com.hazelcast.collection.impl.queue.QueueService;
import com.hazelcast.collection.impl.queue.operations.SizeOperation;
import com.hazelcast.collection.impl.txnqueue.QueueTransactionLogRecord;
import com.hazelcast.collection.impl.txnqueue.operations.BaseTxnQueueOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnOfferOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnPeekOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnPollOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnReserveOfferOperation;
import com.hazelcast.collection.impl.txnqueue.operations.TxnReservePollOperation;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.core.TransactionalQueue;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.TransactionalDistributedObject;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionNotActiveException;
import com.hazelcast.transaction.impl.Transaction;
import com.hazelcast.util.ExceptionUtil;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public abstract class TransactionalQueueProxySupport<E>
extends TransactionalDistributedObject<QueueService>
implements TransactionalQueue<E> {
    protected final String name;
    protected final int partitionId;
    protected final QueueConfig config;
    private final LinkedList<QueueItem> offeredQueue = new LinkedList();
    private final Set<Long> itemIdSet = new HashSet<Long>();

    TransactionalQueueProxySupport(NodeEngine nodeEngine, QueueService service, String name, Transaction tx) {
        super(nodeEngine, service, tx);
        this.name = name;
        this.partitionId = nodeEngine.getPartitionService().getPartitionId(this.getNameAsPartitionAwareData());
        this.config = nodeEngine.getConfig().findQueueConfig(name);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public final String getServiceName() {
        return "hz:impl:queueService";
    }

    @Override
    public int size() {
        this.checkTransactionState();
        SizeOperation operation = new SizeOperation(this.name);
        try {
            InternalCompletableFuture future = this.invoke(operation);
            Integer size = (Integer)future.get();
            return size + this.offeredQueue.size();
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    void checkTransactionState() {
        if (!this.tx.getState().equals((Object)Transaction.State.ACTIVE)) {
            throw new TransactionNotActiveException("Transaction is not active!");
        }
    }

    boolean offerInternal(Data data, long timeout) {
        TxnReserveOfferOperation operation = new TxnReserveOfferOperation(this.name, timeout, this.offeredQueue.size(), this.tx.getTxnId());
        operation.setCallerUuid(this.tx.getOwnerUuid());
        try {
            InternalCompletableFuture future = this.invoke(operation);
            Long itemId = (Long)future.get();
            if (itemId != null) {
                if (!this.itemIdSet.add(itemId)) {
                    throw new TransactionException("Duplicate itemId: " + itemId);
                }
                this.offeredQueue.offer(new QueueItem(null, itemId, data));
                TxnOfferOperation txnOfferOperation = new TxnOfferOperation(this.name, itemId, data);
                this.putToRecord(txnOfferOperation);
                return true;
            }
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
        return false;
    }

    Data pollInternal(long timeout) {
        QueueItem reservedOffer = this.offeredQueue.peek();
        long itemId = reservedOffer == null ? -1L : reservedOffer.getItemId();
        TxnReservePollOperation operation = new TxnReservePollOperation(this.name, timeout, itemId, this.tx.getTxnId());
        operation.setCallerUuid(this.tx.getOwnerUuid());
        try {
            InternalCompletableFuture future = this.invoke(operation);
            QueueItem item = (QueueItem)future.get();
            if (item != null) {
                if (reservedOffer != null && item.getItemId() == reservedOffer.getItemId()) {
                    this.offeredQueue.poll();
                    this.removeFromRecord(reservedOffer.getItemId());
                    this.itemIdSet.remove(reservedOffer.getItemId());
                    return reservedOffer.getData();
                }
                if (!this.itemIdSet.add(item.getItemId())) {
                    throw new TransactionException("Duplicate itemId: " + item.getItemId());
                }
                TxnPollOperation txnPollOperation = new TxnPollOperation(this.name, item.getItemId());
                this.putToRecord(txnPollOperation);
                return item.getData();
            }
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
        return null;
    }

    Data peekInternal(long timeout) {
        QueueItem offer = this.offeredQueue.peek();
        long itemId = offer == null ? -1L : offer.getItemId();
        TxnPeekOperation operation = new TxnPeekOperation(this.name, timeout, itemId, this.tx.getTxnId());
        try {
            InternalCompletableFuture future = this.invoke(operation);
            QueueItem item = (QueueItem)future.get();
            if (item != null) {
                if (offer != null && item.getItemId() == offer.getItemId()) {
                    return offer.getData();
                }
                return item.getData();
            }
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
        return null;
    }

    private void putToRecord(BaseTxnQueueOperation operation) {
        QueueTransactionLogRecord logRecord = (QueueTransactionLogRecord)this.tx.get(this.name);
        if (logRecord == null) {
            logRecord = new QueueTransactionLogRecord(this.tx.getTxnId(), this.name, this.partitionId);
            this.tx.add(logRecord);
        }
        logRecord.addOperation(operation);
    }

    private void removeFromRecord(long itemId) {
        QueueTransactionLogRecord logRecord = (QueueTransactionLogRecord)this.tx.get(this.name);
        int size = logRecord.removeOperation(itemId);
        if (size == 0) {
            this.tx.remove(this.name);
        }
    }

    private <T> InternalCompletableFuture<T> invoke(Operation operation) {
        OperationService operationService = this.getNodeEngine().getOperationService();
        return operationService.invokeOnPartition("hz:impl:queueService", operation, this.partitionId);
    }
}

