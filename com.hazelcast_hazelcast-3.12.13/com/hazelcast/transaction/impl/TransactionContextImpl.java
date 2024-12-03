/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl;

import com.hazelcast.core.TransactionalList;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.core.TransactionalMultiMap;
import com.hazelcast.core.TransactionalQueue;
import com.hazelcast.core.TransactionalSet;
import com.hazelcast.spi.TransactionalService;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.proxyservice.InternalProxyService;
import com.hazelcast.transaction.HazelcastXAResource;
import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.TransactionNotActiveException;
import com.hazelcast.transaction.TransactionOptions;
import com.hazelcast.transaction.TransactionalObject;
import com.hazelcast.transaction.impl.Transaction;
import com.hazelcast.transaction.impl.TransactionImpl;
import com.hazelcast.transaction.impl.TransactionManagerServiceImpl;
import com.hazelcast.transaction.impl.TransactionalObjectKey;
import java.util.HashMap;
import java.util.Map;
import javax.transaction.xa.XAResource;

final class TransactionContextImpl
implements TransactionContext {
    private final NodeEngineImpl nodeEngine;
    private final TransactionImpl transaction;
    private final Map<TransactionalObjectKey, TransactionalObject> txnObjectMap = new HashMap<TransactionalObjectKey, TransactionalObject>(2);

    TransactionContextImpl(TransactionManagerServiceImpl transactionManagerService, NodeEngineImpl nodeEngine, TransactionOptions options, String ownerUuid, boolean originatedFromClient) {
        this.nodeEngine = nodeEngine;
        this.transaction = new TransactionImpl(transactionManagerService, nodeEngine, options, ownerUuid, originatedFromClient);
    }

    @Override
    public String getTxnId() {
        return this.transaction.getTxnId();
    }

    @Override
    public void beginTransaction() {
        this.transaction.begin();
    }

    @Override
    public void commitTransaction() throws TransactionException {
        if (this.transaction.requiresPrepare()) {
            this.transaction.prepare();
        }
        this.transaction.commit();
    }

    @Override
    public void rollbackTransaction() {
        this.transaction.rollback();
    }

    @Override
    public <K, V> TransactionalMap<K, V> getMap(String name) {
        return (TransactionalMap)this.getTransactionalObject("hz:impl:mapService", name);
    }

    @Override
    public <K, V> TransactionalMultiMap<K, V> getMultiMap(String name) {
        return (TransactionalMultiMap)this.getTransactionalObject("hz:impl:multiMapService", name);
    }

    @Override
    public <E> TransactionalQueue<E> getQueue(String name) {
        return (TransactionalQueue)this.getTransactionalObject("hz:impl:queueService", name);
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
        TransactionalObjectKey key;
        TransactionalObject obj;
        this.checkActive(serviceName, name);
        if (this.requiresBackupLogs(serviceName)) {
            this.transaction.ensureBackupLogsExist();
        }
        if ((obj = this.txnObjectMap.get(key = new TransactionalObjectKey(serviceName, name))) != null) {
            return obj;
        }
        TransactionalService transactionalService = this.getTransactionalService(serviceName);
        this.nodeEngine.getProxyService().initializeDistributedObject(serviceName, name);
        obj = transactionalService.createTransactionalObject(name, this.transaction);
        this.txnObjectMap.put(key, obj);
        return obj;
    }

    private boolean requiresBackupLogs(String serviceName) {
        if (serviceName.equals("hz:impl:mapService")) {
            return false;
        }
        return !serviceName.equals("hz:impl:multiMapService");
    }

    private TransactionalService getTransactionalService(String serviceName) {
        Object service = this.nodeEngine.getService(serviceName);
        if (!(service instanceof TransactionalService)) {
            throw new IllegalArgumentException("Service[" + serviceName + "] is not transactional!");
        }
        return (TransactionalService)service;
    }

    private void checkActive(String serviceName, String name) {
        if (this.transaction.getState() != Transaction.State.ACTIVE) {
            throw new TransactionNotActiveException("No transaction is found while accessing transactional object -> " + serviceName + "[" + name + "]!");
        }
    }

    Transaction getTransaction() {
        return this.transaction;
    }

    @Override
    public XAResource getXaResource() {
        InternalProxyService proxyService = this.nodeEngine.getProxyService();
        return (HazelcastXAResource)proxyService.getDistributedObject("hz:impl:xaService", "hz:impl:xaService");
    }
}

