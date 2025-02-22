/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.cp;

import com.hazelcast.util.Preconditions;

public class FencedLockConfig {
    public static final int DEFAULT_LOCK_ACQUIRE_LIMIT = 0;
    private String name;
    private int lockAcquireLimit = 0;

    public FencedLockConfig() {
    }

    public FencedLockConfig(String name) {
        this.name = name;
    }

    public FencedLockConfig(String name, int lockAcquireLimit) {
        this.name = name;
        this.lockAcquireLimit = lockAcquireLimit;
    }

    FencedLockConfig(FencedLockConfig config) {
        this.name = config.name;
        this.lockAcquireLimit = config.lockAcquireLimit;
    }

    public String getName() {
        return this.name;
    }

    public FencedLockConfig setName(String name) {
        this.name = name;
        return this;
    }

    public int getLockAcquireLimit() {
        return this.lockAcquireLimit;
    }

    public FencedLockConfig setLockAcquireLimit(int lockAcquireLimit) {
        Preconditions.checkTrue(lockAcquireLimit >= 0, "reentrant lock acquire limit cannot be negative");
        this.lockAcquireLimit = lockAcquireLimit;
        return this;
    }

    public FencedLockConfig disableReentrancy() {
        this.lockAcquireLimit = 1;
        return this;
    }

    public FencedLockConfig enableReentrancy() {
        this.lockAcquireLimit = 0;
        return this;
    }
}

