/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.collection.impl.txnlist;

import com.hazelcast.collection.impl.collection.CollectionItem;
import com.hazelcast.collection.impl.list.ListService;
import com.hazelcast.collection.impl.txncollection.AbstractTransactionalCollectionProxy;
import com.hazelcast.core.TransactionalList;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.transaction.impl.Transaction;
import java.util.Collection;
import java.util.LinkedList;

public class TransactionalListProxy<E>
extends AbstractTransactionalCollectionProxy<ListService, E>
implements TransactionalList<E> {
    private final LinkedList<CollectionItem> list = new LinkedList();

    public TransactionalListProxy(String name, Transaction tx, NodeEngine nodeEngine, ListService service) {
        super(name, tx, nodeEngine, service);
    }

    @Override
    public String getServiceName() {
        return "hz:impl:listService";
    }

    @Override
    protected Collection<CollectionItem> getCollection() {
        return this.list;
    }
}

