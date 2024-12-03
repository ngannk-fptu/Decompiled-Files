/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.store;

import com.atlassian.lucene36.store.Lock;
import java.io.IOException;
import java.util.HashSet;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class SingleInstanceLock
extends Lock {
    String lockName;
    private HashSet<String> locks;

    public SingleInstanceLock(HashSet<String> locks, String lockName) {
        this.locks = locks;
        this.lockName = lockName;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean obtain() throws IOException {
        HashSet<String> hashSet = this.locks;
        synchronized (hashSet) {
            return this.locks.add(this.lockName);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void release() {
        HashSet<String> hashSet = this.locks;
        synchronized (hashSet) {
            this.locks.remove(this.lockName);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isLocked() {
        HashSet<String> hashSet = this.locks;
        synchronized (hashSet) {
            return this.locks.contains(this.lockName);
        }
    }

    public String toString() {
        return super.toString() + ": " + this.lockName;
    }
}

