/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.history.DetailedInvocation
 */
package com.atlassian.webhooks.internal.dao;

import com.atlassian.webhooks.history.DetailedInvocation;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class PendingInvocationData {
    private final AtomicInteger errorCount = new AtomicInteger();
    private final ReadWriteLock freezeLock;
    private final AtomicInteger failureCount = new AtomicInteger();
    private final AtomicInteger successCount;
    private boolean frozen;
    private AtomicReference<DetailedInvocation> latestError;
    private AtomicReference<DetailedInvocation> latestFailure;
    private AtomicReference<DetailedInvocation> latestSuccess;

    PendingInvocationData() {
        this.freezeLock = new ReentrantReadWriteLock();
        this.successCount = new AtomicInteger();
        this.latestError = new AtomicReference();
        this.latestFailure = new AtomicReference();
        this.latestSuccess = new AtomicReference();
    }

    void freeze() {
        if (this.frozen) {
            return;
        }
        Lock lock = this.freezeLock.writeLock();
        lock.lock();
        this.frozen = true;
        lock.unlock();
    }

    int getErrorCount() {
        return this.errorCount.get();
    }

    int getFailureCount() {
        return this.failureCount.get();
    }

    DetailedInvocation getLatestError() {
        return this.latestError.get();
    }

    DetailedInvocation getLatestFailure() {
        return this.latestFailure.get();
    }

    DetailedInvocation getLatestSuccess() {
        return this.latestSuccess.get();
    }

    int getSuccessCount() {
        return this.successCount.get();
    }

    boolean addAll(PendingInvocationData other) {
        return this.unlessFrozen(() -> {
            this.errorCount.accumulateAndGet(other.getErrorCount(), PendingInvocationData::add);
            this.latestError.accumulateAndGet(other.getLatestError(), PendingInvocationData::getLatest);
            this.failureCount.accumulateAndGet(other.getFailureCount(), PendingInvocationData::add);
            this.latestFailure.accumulateAndGet(other.getLatestFailure(), PendingInvocationData::getLatest);
            this.successCount.accumulateAndGet(other.getSuccessCount(), PendingInvocationData::add);
            this.latestSuccess.accumulateAndGet(other.getLatestSuccess(), PendingInvocationData::getLatest);
        });
    }

    boolean addCounts(int errors, int failures, int successes) {
        return this.unlessFrozen(() -> {
            this.errorCount.addAndGet(errors);
            this.failureCount.addAndGet(failures);
            this.successCount.addAndGet(successes);
        });
    }

    boolean onInvocation(DetailedInvocation invocation) {
        return this.unlessFrozen(() -> {
            switch (invocation.getResult().getOutcome()) {
                case SUCCESS: {
                    this.latestSuccess.accumulateAndGet(invocation, PendingInvocationData::getLatest);
                    break;
                }
                case FAILURE: {
                    this.latestFailure.accumulateAndGet(invocation, PendingInvocationData::getLatest);
                    break;
                }
                case ERROR: {
                    this.latestError.accumulateAndGet(invocation, PendingInvocationData::getLatest);
                }
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean unlessFrozen(Runnable action) {
        if (this.frozen) {
            return false;
        }
        Lock readLock = this.freezeLock.readLock();
        if (readLock.tryLock() && !this.frozen) {
            try {
                action.run();
                boolean bl = true;
                return bl;
            }
            finally {
                readLock.unlock();
            }
        }
        return false;
    }

    private static DetailedInvocation getLatest(DetailedInvocation first, DetailedInvocation second) {
        if (first == null) {
            return second;
        }
        if (second == null) {
            return first;
        }
        return first.getFinish().isAfter(second.getFinish()) ? first : second;
    }

    private static int add(int value1, int value2) {
        return value1 + value2;
    }
}

