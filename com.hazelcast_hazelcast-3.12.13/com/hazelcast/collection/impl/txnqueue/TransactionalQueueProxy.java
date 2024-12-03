/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.txnqueue;

import com.hazelcast.collection.impl.queue.QueueService;
import com.hazelcast.collection.impl.txnqueue.TransactionalQueueProxySupport;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.transaction.impl.Transaction;
import com.hazelcast.util.Preconditions;
import java.util.concurrent.TimeUnit;

public class TransactionalQueueProxy<E>
extends TransactionalQueueProxySupport<E> {
    public TransactionalQueueProxy(NodeEngine nodeEngine, QueueService service, String name, Transaction tx) {
        super(nodeEngine, service, name, tx);
    }

    @Override
    public boolean offer(E e) {
        try {
            return this.offer(e, 0L, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        Preconditions.checkNotNull(e, "Offered item should not be null.");
        Preconditions.checkNotNull(unit, "TimeUnit should not be null.");
        this.checkTransactionState();
        Data data = this.getNodeEngine().toData(e);
        return this.offerInternal(data, unit.toMillis(timeout));
    }

    @Override
    public E take() throws InterruptedException {
        return this.poll(-1L, TimeUnit.MILLISECONDS);
    }

    @Override
    public E poll() {
        try {
            return this.poll(0L, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        Preconditions.checkNotNull(unit, "TimeUnit should not be null.");
        this.checkTransactionState();
        Data data = this.pollInternal(unit.toMillis(timeout));
        return (E)this.toObjectIfNeeded(data);
    }

    @Override
    public E peek() {
        try {
            return this.peek(0L, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    @Override
    public E peek(long timeout, TimeUnit unit) throws InterruptedException {
        Preconditions.checkNotNull(unit, "TimeUnit should not be null.");
        this.checkTransactionState();
        Data data = this.peekInternal(unit.toMillis(timeout));
        return (E)this.toObjectIfNeeded(data);
    }

    @Override
    public String toString() {
        return "TransactionalQueue{name=" + this.name + '}';
    }
}

