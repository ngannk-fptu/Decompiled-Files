/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.txnset;

import com.hazelcast.collection.impl.collection.CollectionItem;
import com.hazelcast.collection.impl.set.SetService;
import com.hazelcast.collection.impl.txncollection.AbstractTransactionalCollectionProxy;
import com.hazelcast.collection.impl.txncollection.operations.CollectionReserveAddOperation;
import com.hazelcast.collection.impl.txncollection.operations.CollectionTxnAddOperation;
import com.hazelcast.core.TransactionalSet;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.impl.Transaction;
import com.hazelcast.util.ExceptionUtil;
import java.util.Collection;
import java.util.HashSet;

public class TransactionalSetProxy<E>
extends AbstractTransactionalCollectionProxy<SetService, E>
implements TransactionalSet<E> {
    private final HashSet<CollectionItem> set = new HashSet();

    public TransactionalSetProxy(String name, Transaction tx, NodeEngine nodeEngine, SetService service) {
        super(name, tx, nodeEngine, service);
    }

    @Override
    public boolean add(E e) {
        this.checkTransactionActive();
        this.checkObjectNotNull(e);
        Data value = this.getNodeEngine().toData(e);
        if (!this.getCollection().add(new CollectionItem(-1L, value))) {
            return false;
        }
        CollectionReserveAddOperation operation = new CollectionReserveAddOperation(this.name, this.tx.getTxnId(), value);
        try {
            InternalCompletableFuture future = this.operationService.invokeOnPartition(this.getServiceName(), operation, this.partitionId);
            Long itemId = (Long)future.get();
            if (itemId != null) {
                if (!this.itemIdSet.add(itemId)) {
                    throw new TransactionException("Duplicate itemId: " + itemId);
                }
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

    @Override
    public String getServiceName() {
        return "hz:impl:setService";
    }

    @Override
    protected Collection<CollectionItem> getCollection() {
        return this.set;
    }
}

