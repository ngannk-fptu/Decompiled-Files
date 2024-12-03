/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.store.Lock;
import com.atlassian.lucene36.store.LockFactory;
import com.atlassian.lucene36.store.NoLock;

public class NoLockFactory
extends LockFactory {
    private static NoLock singletonLock = new NoLock();
    private static NoLockFactory singleton = new NoLockFactory();

    @Deprecated
    public NoLockFactory() {
    }

    public static NoLockFactory getNoLockFactory() {
        return singleton;
    }

    public Lock makeLock(String lockName) {
        return singletonLock;
    }

    public void clearLock(String lockName) {
    }
}

