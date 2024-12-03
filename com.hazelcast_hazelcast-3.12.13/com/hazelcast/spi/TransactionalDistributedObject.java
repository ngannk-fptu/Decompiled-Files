/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.spi.AbstractDistributedObject;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.RemoteService;
import com.hazelcast.transaction.impl.Transaction;

public abstract class TransactionalDistributedObject<S extends RemoteService>
extends AbstractDistributedObject<S> {
    protected final Transaction tx;

    protected TransactionalDistributedObject(NodeEngine nodeEngine, S service, Transaction tx) {
        super(nodeEngine, service);
        this.tx = tx;
    }

    protected Object toObjectIfNeeded(Object data) {
        if (this.tx.isOriginatedFromClient()) {
            return data;
        }
        return this.getNodeEngine().toObject(data);
    }
}

