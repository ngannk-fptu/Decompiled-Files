/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl.xa;

import com.hazelcast.core.TransactionalList;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.core.TransactionalMultiMap;
import com.hazelcast.core.TransactionalQueue;
import com.hazelcast.core.TransactionalSet;
import com.hazelcast.spi.TransactionalService;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionNotActiveException;
import com.hazelcast.transaction.TransactionalObject;
import com.hazelcast.transaction.impl.Transaction;
import com.hazelcast.transaction.impl.TransactionalObjectKey;
import com.hazelcast.transaction.impl.xa.XATransaction;
import java.util.HashMap;
import java.util.Map;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

public class XATransactionContextImpl
implements TransactionContext {
    private final NodeEngineImpl nodeEngine;
    private final XATransaction transaction;
    private final Map<TransactionalObjectKey, TransactionalObject> txnObjectMap = new HashMap<TransactionalObjectKey, TransactionalObject>(2);

    public XATransactionContextImpl(NodeEngineImpl nodeEngine, Xid xid, String txOwnerUuid, int timeout, boolean originatedFromClient) {
        this.nodeEngine = nodeEngine;
        this.transaction = new XATransaction(nodeEngine, xid, txOwnerUuid, timeout, originatedFromClient);
    }

    @Override
    public void beginTransaction() {
        throw new UnsupportedOperationException("XA Transaction cannot be started manually!");
    }

    @Override
    public void commitTransaction() throws TransactionException {
        throw new UnsupportedOperationException("XA Transaction cannot be committed manually!");
    }

    @Override
    public void rollbackTransaction() {
        throw new UnsupportedOperationException("XA Transaction cannot be rolled back manually!");
    }

    @Override
    public String getTxnId() {
        return this.transaction.getTxnId();
    }

    @Override
    public <K, V> TransactionalMap<K, V> getMap(String name) {
        return (TransactionalMap)this.getTransactionalObject("hz:impl:mapService", name);
    }

    @Override
    public <E> TransactionalQueue<E> getQueue(String name) {
        return (TransactionalQueue)this.getTransactionalObject("hz:impl:queueService", name);
    }

    @Override
    public <K, V> TransactionalMultiMap<K, V> getMultiMap(String name) {
        return (TransactionalMultiMap)this.getTransactionalObject("hz:impl:multiMapService", name);
    }

    @Override
    public <E> TransactionalList<E> getList(String name) {
        return (TransactionalList)this.getTransactionalObject("hz:impl:listService", name);
    }

    @Override
    public <E> TransactionalSet<E> getSet(String name) {
        return (TransactionalSet)this.getTransactionalObject("hz:impl:setService", name);
    }

    public TransactionalObject getTransactionalObject(String serviceName, String name) {
        if (this.transaction.getState() != Transaction.State.ACTIVE) {
            throw new TransactionNotActiveException("No transaction is found while accessing transactional object -> " + serviceName + "[" + name + "]!");
        }
        TransactionalObjectKey key = new TransactionalObjectKey(serviceName, name);
        TransactionalObject obj = this.txnObjectMap.get(key);
        if (obj != null) {
            return obj;
        }
        Object service = this.nodeEngine.getService(serviceName);
        if (!(service instanceof TransactionalService)) {
            throw new IllegalArgumentException("Service[" + serviceName + "] is not transactional!");
        }
        this.nodeEngine.getProxyService().initializeDistributedObject(serviceName, name);
        obj = ((TransactionalService)service).createTransactionalObject(name, this.transaction);
        this.txnObjectMap.put(key, obj);
        return obj;
    }

    XATransaction getTransaction() {
        return this.transaction;
    }

    @Override
    public XAResource getXaResource() {
        throw new UnsupportedOperationException("Use HazelcastInstance.getXAResource() instead!");
    }
}

