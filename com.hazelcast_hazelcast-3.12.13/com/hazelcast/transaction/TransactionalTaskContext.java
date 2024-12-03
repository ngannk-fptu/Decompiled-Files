/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction;

import com.hazelcast.core.TransactionalList;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.core.TransactionalMultiMap;
import com.hazelcast.core.TransactionalQueue;
import com.hazelcast.core.TransactionalSet;
import com.hazelcast.transaction.TransactionalObject;

public interface TransactionalTaskContext {
    public <K, V> TransactionalMap<K, V> getMap(String var1);

    public <E> TransactionalQueue<E> getQueue(String var1);

    public <K, V> TransactionalMultiMap<K, V> getMultiMap(String var1);

    public <E> TransactionalList<E> getList(String var1);

    public <E> TransactionalSet<E> getSet(String var1);

    public <T extends TransactionalObject> T getTransactionalObject(String var1, String var2);
}

