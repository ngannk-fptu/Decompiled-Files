/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.tx.TransactionalMapProxy;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.TransactionalService;
import com.hazelcast.transaction.impl.Transaction;

class MapTransactionalService
implements TransactionalService {
    private final MapServiceContext mapServiceContext;
    private final NodeEngine nodeEngine;

    public MapTransactionalService(MapServiceContext mapServiceContext) {
        this.mapServiceContext = mapServiceContext;
        this.nodeEngine = mapServiceContext.getNodeEngine();
    }

    public TransactionalMapProxy createTransactionalObject(String name, Transaction transaction) {
        return new TransactionalMapProxy(name, this.mapServiceContext.getService(), this.nodeEngine, transaction);
    }

    @Override
    public void rollbackTransaction(String transactionId) {
    }
}

