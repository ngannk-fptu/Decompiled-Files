/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.transaction.TransactionalObject;

public interface TransactionalSet<E>
extends TransactionalObject {
    public boolean add(E var1);

    public boolean remove(E var1);

    public int size();
}

