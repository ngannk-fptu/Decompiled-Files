/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.DistributedObject;
import java.util.concurrent.TimeUnit;

public interface ICountDownLatch
extends DistributedObject {
    public boolean await(long var1, TimeUnit var3) throws InterruptedException;

    public void countDown();

    public int getCount();

    public boolean trySetCount(int var1);
}

