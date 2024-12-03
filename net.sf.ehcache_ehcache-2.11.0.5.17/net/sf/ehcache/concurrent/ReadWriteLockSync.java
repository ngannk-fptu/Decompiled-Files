/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.sf.ehcache.concurrent.LockType;
import net.sf.ehcache.concurrent.Sync;

public class ReadWriteLockSync
implements Sync {
    private final ReentrantReadWriteLock rrwl;

    public ReadWriteLockSync() {
        this(new ReentrantReadWriteLock());
    }

    public ReadWriteLockSync(ReentrantReadWriteLock lock) {
        this.rrwl = lock;
    }

    @Override
    public void lock(LockType type) {
        this.getLock(type).lock();
    }

    @Override
    public boolean tryLock(LockType type, long msec) throws InterruptedException {
        return this.getLock(type).tryLock(msec, TimeUnit.MILLISECONDS);
    }

    @Override
    public void unlock(LockType type) {
        this.getLock(type).unlock();
    }

    private Lock getLock(LockType type) {
        switch (type) {
            case READ: {
                return this.rrwl.readLock();
            }
            case WRITE: {
                return this.rrwl.writeLock();
            }
        }
        throw new IllegalArgumentException("We don't support any other lock type than READ or WRITE!");
    }

    public ReadWriteLock getReadWriteLock() {
        return this.rrwl;
    }

    @Override
    public boolean isHeldByCurrentThread(LockType type) {
        switch (type) {
            case READ: {
                throw new UnsupportedOperationException("Querying of read lock is not supported.");
            }
            case WRITE: {
                return this.rrwl.isWriteLockedByCurrentThread();
            }
        }
        throw new IllegalArgumentException("We don't support any other lock type than READ or WRITE!");
    }
}

