/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl.txn;

import com.hazelcast.multimap.impl.MultiMapRecord;
import com.hazelcast.multimap.impl.MultiMapService;
import com.hazelcast.multimap.impl.txn.TransactionalMultiMapProxySupport;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.transaction.TransactionException;
import com.hazelcast.transaction.impl.Transaction;
import java.util.ArrayList;
import java.util.Collection;

public class TransactionalMultiMapProxy<K, V>
extends TransactionalMultiMapProxySupport<K, V> {
    public TransactionalMultiMapProxy(NodeEngine nodeEngine, MultiMapService service, String name, Transaction tx) {
        super(nodeEngine, service, name, tx);
    }

    @Override
    public boolean put(K key, V value) throws TransactionException {
        this.checkTransactionActive();
        Data dataKey = this.getNodeEngine().toData(key);
        Data dataValue = this.getNodeEngine().toData(value);
        return this.putInternal(dataKey, dataValue);
    }

    @Override
    public Collection<V> get(K key) {
        this.checkTransactionActive();
        Data dataKey = this.getNodeEngine().toData(key);
        Collection<MultiMapRecord> coll = this.getInternal(dataKey);
        ArrayList<Object> collection = new ArrayList<Object>(coll.size());
        for (MultiMapRecord record : coll) {
            collection.add(this.toObjectIfNeeded(record.getObject()));
        }
        return collection;
    }

    @Override
    public boolean remove(Object key, Object value) {
        this.checkTransactionActive();
        Data dataKey = this.getNodeEngine().toData(key);
        Data dataValue = this.getNodeEngine().toData(value);
        return this.removeInternal(dataKey, dataValue);
    }

    @Override
    public Collection<V> remove(Object key) {
        this.checkTransactionActive();
        Data dataKey = this.getNodeEngine().toData(key);
        Collection<MultiMapRecord> coll = this.removeAllInternal(dataKey);
        ArrayList<Object> result = new ArrayList<Object>(coll.size());
        for (MultiMapRecord record : coll) {
            result.add(this.toObjectIfNeeded(record.getObject()));
        }
        return result;
    }

    @Override
    public int valueCount(K key) {
        this.checkTransactionActive();
        Data dataKey = this.getNodeEngine().toData(key);
        return this.valueCountInternal(dataKey);
    }

    @Override
    public String toString() {
        return "TransactionalMultiMap{name=" + this.getName() + '}';
    }
}

