/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.BaseQueue;
import com.hazelcast.transaction.TransactionalObject;
import java.util.concurrent.TimeUnit;

public interface TransactionalQueue<E>
extends TransactionalObject,
BaseQueue<E> {
    @Override
    public boolean offer(E var1);

    @Override
    public boolean offer(E var1, long var2, TimeUnit var4) throws InterruptedException;

    @Override
    public E take() throws InterruptedException;

    @Override
    public E poll();

    @Override
    public E poll(long var1, TimeUnit var3) throws InterruptedException;

    public E peek();

    public E peek(long var1, TimeUnit var3) throws InterruptedException;

    @Override
    public int size();
}

