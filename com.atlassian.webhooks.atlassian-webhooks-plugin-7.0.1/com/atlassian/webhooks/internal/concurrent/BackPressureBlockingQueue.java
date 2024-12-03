/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.webhooks.internal.concurrent;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class BackPressureBlockingQueue<T>
extends LinkedBlockingQueue<T> {
    private long offerTimeoutMs = 100L;

    public BackPressureBlockingQueue() {
    }

    public BackPressureBlockingQueue(int capacity) {
        super(capacity);
    }

    public BackPressureBlockingQueue(Collection<? extends T> c) {
        super(c);
    }

    @Override
    public boolean offer(T t) {
        try {
            return super.offer(t, this.offerTimeoutMs, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void setOfferTimeoutMs(long timeoutMs) {
        this.offerTimeoutMs = Math.max(0L, timeoutMs);
    }
}

