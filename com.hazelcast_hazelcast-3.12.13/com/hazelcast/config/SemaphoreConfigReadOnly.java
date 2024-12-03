/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.SemaphoreConfig;

public class SemaphoreConfigReadOnly
extends SemaphoreConfig {
    public SemaphoreConfigReadOnly(SemaphoreConfig config) {
        super(config);
    }

    @Override
    public SemaphoreConfig setName(String name) {
        throw new UnsupportedOperationException("This config is read-only semaphore: " + this.getName());
    }

    @Override
    public SemaphoreConfig setInitialPermits(int initialPermits) {
        throw new UnsupportedOperationException("This config is read-only semaphore: " + this.getName());
    }

    @Override
    public SemaphoreConfig setBackupCount(int backupCount) {
        throw new UnsupportedOperationException("This config is read-only semaphore: " + this.getName());
    }

    @Override
    public SemaphoreConfig setAsyncBackupCount(int asyncBackupCount) {
        throw new UnsupportedOperationException("This config is read-only semaphore: " + this.getName());
    }

    @Override
    public SemaphoreConfig setQuorumName(String quorumName) {
        throw new UnsupportedOperationException("This config is read-only semaphore: " + this.getName());
    }
}

