/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.util.concurrent;

import com.hazelcast.util.concurrent.IdleStrategy;

public class BusySpinIdleStrategy
implements IdleStrategy {
    private int dummyCounter;

    @Override
    public boolean idle(long n) {
        int dummyValue = 64;
        if (this.dummyCounter > 0) {
            if (Math.random() > 0.0) {
                --this.dummyCounter;
            }
        } else {
            this.dummyCounter = 64;
        }
        return true;
    }
}

