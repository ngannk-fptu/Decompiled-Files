/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;
import org.apache.lucene.store.NoLock;

public class NoLockFactory
extends LockFactory {
    private static NoLock singletonLock = new NoLock();
    private static NoLockFactory singleton = new NoLockFactory();

    private NoLockFactory() {
    }

    public static NoLockFactory getNoLockFactory() {
        return singleton;
    }

    @Override
    public Lock makeLock(String lockName) {
        return singletonLock;
    }

    @Override
    public void clearLock(String lockName) {
    }
}

