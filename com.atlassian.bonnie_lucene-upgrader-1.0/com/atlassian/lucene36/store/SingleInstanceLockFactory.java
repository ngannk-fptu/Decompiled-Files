/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.store.Lock;
import com.atlassian.lucene36.store.LockFactory;
import com.atlassian.lucene36.store.SingleInstanceLock;
import java.io.IOException;
import java.util.HashSet;

public class SingleInstanceLockFactory
extends LockFactory {
    private HashSet<String> locks = new HashSet();

    public Lock makeLock(String lockName) {
        return new SingleInstanceLock(this.locks, lockName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clearLock(String lockName) throws IOException {
        HashSet<String> hashSet = this.locks;
        synchronized (hashSet) {
            if (this.locks.contains(lockName)) {
                this.locks.remove(lockName);
            }
        }
    }
}

