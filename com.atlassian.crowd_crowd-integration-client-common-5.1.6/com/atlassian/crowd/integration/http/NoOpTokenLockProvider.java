/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.integration.http;

import com.atlassian.crowd.integration.http.TokenLockProvider;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class NoOpTokenLockProvider
implements TokenLockProvider {
    private static final NoOpLock LOCK = new NoOpLock();

    @Override
    public Lock getLock(String token) {
        return LOCK;
    }

    private static class NoOpLock
    implements Lock {
        private NoOpLock() {
        }

        @Override
        public void lock() {
        }

        @Override
        public void lockInterruptibly() {
        }

        @Override
        public boolean tryLock() {
            return true;
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) {
            return true;
        }

        @Override
        public void unlock() {
        }

        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException("Not implemented");
        }
    }
}

