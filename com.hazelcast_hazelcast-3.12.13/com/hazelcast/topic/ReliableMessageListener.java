/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.topic;

import com.hazelcast.core.MessageListener;

public interface ReliableMessageListener<E>
extends MessageListener<E> {
    public long retrieveInitialSequence();

    public void storeSequence(long var1);

    public boolean isLossTolerant();

    public boolean isTerminal(Throwable var1);
}

