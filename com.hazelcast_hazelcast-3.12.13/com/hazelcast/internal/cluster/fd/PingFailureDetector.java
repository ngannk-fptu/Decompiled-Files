/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.fd;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PingFailureDetector<E> {
    private final int maxPingAttempts;
    private final ConcurrentMap<E, AtomicInteger> pingAttempts = new ConcurrentHashMap<E, AtomicInteger>();

    public PingFailureDetector(int maxPingAttempts) {
        this.maxPingAttempts = maxPingAttempts;
    }

    public int heartbeat(E endpoint) {
        return this.getAttempts(endpoint).getAndSet(0);
    }

    public void logAttempt(E endpoint) {
        this.getAttempts(endpoint).incrementAndGet();
    }

    public boolean isAlive(E endpoint) {
        AtomicInteger attempts = (AtomicInteger)this.pingAttempts.get(endpoint);
        return attempts != null && attempts.get() < this.maxPingAttempts;
    }

    public void remove(E endpoint) {
        this.pingAttempts.remove(endpoint);
    }

    public void reset() {
        this.pingAttempts.clear();
    }

    private AtomicInteger getAttempts(E endpoint) {
        AtomicInteger existing = (AtomicInteger)this.pingAttempts.get(endpoint);
        AtomicInteger newAttempts = null;
        if (existing == null) {
            newAttempts = new AtomicInteger();
            existing = this.pingAttempts.putIfAbsent(endpoint, newAttempts);
        }
        return existing != null ? existing : newAttempts;
    }
}

