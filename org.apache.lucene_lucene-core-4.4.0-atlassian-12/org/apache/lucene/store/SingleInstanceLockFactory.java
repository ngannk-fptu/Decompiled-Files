/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.store;

import java.io.IOException;
import java.util.HashSet;
import org.apache.lucene.store.Lock;
import org.apache.lucene.store.LockFactory;
import org.apache.lucene.store.SingleInstanceLock;

public class SingleInstanceLockFactory
extends LockFactory {
    private HashSet<String> locks = new HashSet();

    @Override
    public Lock makeLock(String lockName) {
        return new SingleInstanceLock(this.locks, lockName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clearLock(String lockName) throws IOException {
        HashSet<String> hashSet = this.locks;
        synchronized (hashSet) {
            if (this.locks.contains(lockName)) {
                this.locks.remove(lockName);
            }
        }
    }
}

