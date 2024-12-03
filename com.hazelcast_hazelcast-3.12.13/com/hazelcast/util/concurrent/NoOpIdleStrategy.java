/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.concurrent;

import com.hazelcast.util.concurrent.IdleStrategy;

public class NoOpIdleStrategy
implements IdleStrategy {
    @Override
    public boolean idle(long n) {
        return true;
    }
}

