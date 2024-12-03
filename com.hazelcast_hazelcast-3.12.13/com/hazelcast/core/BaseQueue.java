/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.DistributedObject;
import java.util.concurrent.TimeUnit;

public interface BaseQueue<E>
extends DistributedObject {
    public boolean offer(E var1);

    public boolean offer(E var1, long var2, TimeUnit var4) throws InterruptedException;

    public E take() throws InterruptedException;

    public E poll();

    public E poll(long var1, TimeUnit var3) throws InterruptedException;

    public int size();
}

