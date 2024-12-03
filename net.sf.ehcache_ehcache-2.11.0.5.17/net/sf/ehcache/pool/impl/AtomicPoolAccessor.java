/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.pool.impl;

import java.util.concurrent.atomic.AtomicLong;
import net.sf.ehcache.pool.Pool;
import net.sf.ehcache.pool.PoolParticipant;
import net.sf.ehcache.pool.SizeOfEngine;
import net.sf.ehcache.pool.impl.AbstractPoolAccessor;

final class AtomicPoolAccessor
extends AbstractPoolAccessor {
    private final AtomicLong size;

    AtomicPoolAccessor(Pool pool, PoolParticipant poolParticipant, SizeOfEngine sizeOfEngine, long currentSize) {
        super(pool, poolParticipant, sizeOfEngine);
        this.size = new AtomicLong(currentSize);
    }

    @Override
    protected long add(long sizeOf, boolean force) throws IllegalArgumentException {
        if (sizeOf < 0L) {
            throw new IllegalArgumentException("cannot add negative size");
        }
        long newSize = this.getPool().getSize() + sizeOf;
        if (newSize <= this.getPool().getMaxSize()) {
            this.size.addAndGet(sizeOf);
            return sizeOf;
        }
        if (!force && sizeOf > this.getPool().getMaxSize()) {
            return -1L;
        }
        long missingSize = newSize - this.getPool().getMaxSize();
        if (this.getPool().getEvictor().freeSpace(this.getPool().getPoolAccessors(), missingSize) || force) {
            this.size.addAndGet(sizeOf);
            return sizeOf;
        }
        return -1L;
    }

    @Override
    protected boolean canAddWithoutEvicting(long sizeOf) {
        long newSize = this.getPool().getSize() + sizeOf;
        return newSize <= this.getPool().getMaxSize();
    }

    @Override
    public long delete(long sizeOf) throws IllegalArgumentException {
        this.checkLinked();
        this.size.addAndGet(-sizeOf);
        return sizeOf;
    }

    @Override
    public long getSize() {
        return this.size.get();
    }

    @Override
    protected void doClear() {
        this.size.set(0L);
    }
}

