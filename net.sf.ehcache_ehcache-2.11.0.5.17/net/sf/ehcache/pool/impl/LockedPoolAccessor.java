/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.pool.impl;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import net.sf.ehcache.pool.Pool;
import net.sf.ehcache.pool.PoolParticipant;
import net.sf.ehcache.pool.SizeOfEngine;
import net.sf.ehcache.pool.impl.AbstractPoolAccessor;

final class LockedPoolAccessor
extends AbstractPoolAccessor {
    private long size;
    private final Lock lock = new ReentrantLock();

    LockedPoolAccessor(Pool pool, PoolParticipant poolParticipant, SizeOfEngine sizeOfEngine, long currentSize) {
        super(pool, poolParticipant, sizeOfEngine);
        this.size = currentSize;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected long add(long sizeOf, boolean force) throws IllegalArgumentException {
        if (sizeOf < 0L) {
            throw new IllegalArgumentException("cannot add negative size");
        }
        this.lock.lock();
        try {
            do {
                long l;
                long newSize;
                if ((newSize = this.getPool().getSize() + sizeOf) <= this.getPool().getMaxSize()) {
                    this.size += sizeOf;
                    l = sizeOf;
                    return l;
                }
                if (!force && sizeOf > this.getPool().getMaxSize()) {
                    l = -1L;
                    return l;
                }
                long missingSize = newSize - this.getPool().getMaxSize();
                this.lock.unlock();
                try {
                    boolean successful = this.getPool().getEvictor().freeSpace(this.getPool().getPoolAccessors(), missingSize);
                    if (force || successful) continue;
                    long l2 = -1L;
                    return l2;
                }
                finally {
                    this.lock.lock();
                }
            } while (!force && this.getPool().getSize() + sizeOf > this.getPool().getMaxSize());
            this.size += sizeOf;
            long l = sizeOf;
            return l;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean canAddWithoutEvicting(long sizeOf) {
        this.lock.lock();
        try {
            long newSize = this.getPool().getSize() + sizeOf;
            boolean bl = newSize <= this.getPool().getMaxSize();
            return bl;
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    public long delete(long sizeOf) throws IllegalArgumentException {
        this.checkLinked();
        this.lock.lock();
        try {
            this.size -= sizeOf;
        }
        finally {
            this.lock.unlock();
        }
        return sizeOf;
    }

    @Override
    public long getSize() {
        this.lock.lock();
        try {
            long l = this.size;
            return l;
        }
        finally {
            this.lock.unlock();
        }
    }

    @Override
    protected void doClear() {
        this.lock.lock();
        try {
            this.size = 0L;
        }
        finally {
            this.lock.unlock();
        }
    }
}

