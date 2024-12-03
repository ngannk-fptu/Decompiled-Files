/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.operationexecutor.impl;

public interface OperationQueue {
    public void add(Object var1, boolean var2);

    public Object take(boolean var1) throws InterruptedException;

    public int normalSize();

    public int prioritySize();

    public int size();
}

