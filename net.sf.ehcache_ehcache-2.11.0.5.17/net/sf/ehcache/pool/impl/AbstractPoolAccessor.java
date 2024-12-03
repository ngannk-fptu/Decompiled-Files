/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.pool.impl;

import java.util.concurrent.atomic.AtomicBoolean;
import net.sf.ehcache.pool.Pool;
import net.sf.ehcache.pool.PoolAccessor;
import net.sf.ehcache.pool.PoolParticipant;
import net.sf.ehcache.pool.Size;
import net.sf.ehcache.pool.SizeOfEngine;

public abstract class AbstractPoolAccessor
implements PoolAccessor<PoolParticipant> {
    protected final SizeOfEngine sizeOfEngine;
    private final AtomicBoolean unlinked = new AtomicBoolean();
    private final Pool pool;
    private final PoolParticipant poolParticipant;
    private volatile boolean abortedSizeOf = false;

    public AbstractPoolAccessor(Pool pool, PoolParticipant participant, SizeOfEngine sizeOfEngine) {
        this.pool = pool;
        this.poolParticipant = participant;
        this.sizeOfEngine = sizeOfEngine;
    }

    @Override
    public final long add(Object key, Object value, Object container, boolean force) {
        this.checkLinked();
        Size sizeOf = this.sizeOfEngine.sizeOf(key, value, container);
        if (!sizeOf.isExact()) {
            this.abortedSizeOf = true;
        }
        return this.add(sizeOf.getCalculated(), force);
    }

    @Override
    public final boolean canAddWithoutEvicting(Object key, Object value, Object container) {
        Size sizeOf = this.sizeOfEngine.sizeOf(key, value, container);
        return this.canAddWithoutEvicting(sizeOf.getCalculated());
    }

    protected abstract long add(long var1, boolean var3) throws IllegalArgumentException;

    protected abstract boolean canAddWithoutEvicting(long var1);

    @Override
    public final long replace(long currentSize, Object key, Object value, Object container, boolean force) {
        Size sizeOf = this.sizeOfEngine.sizeOf(key, value, container);
        long delta = sizeOf.getCalculated() - currentSize;
        if (delta == 0L) {
            return 0L;
        }
        if (delta < 0L) {
            return -this.delete(-delta);
        }
        long added = this.add(delta, force);
        return added == -1L ? Long.MIN_VALUE : added;
    }

    @Override
    public final void clear() {
        this.doClear();
        this.abortedSizeOf = false;
    }

    protected abstract void doClear();

    @Override
    public final void unlink() {
        if (this.unlinked.compareAndSet(false, true)) {
            this.getPool().removePoolAccessor(this);
        }
    }

    @Override
    public final PoolParticipant getParticipant() {
        return this.poolParticipant;
    }

    @Override
    public void setMaxSize(long newValue) {
        this.pool.setMaxSize(newValue);
    }

    @Override
    public long getPoolOccupancy() {
        return this.pool.getSize();
    }

    @Override
    public long getPoolSize() {
        return this.pool.getMaxSize();
    }

    protected final void checkLinked() throws IllegalStateException {
        if (this.unlinked.get()) {
            throw new IllegalStateException("BoundedPoolAccessor has been unlinked");
        }
    }

    protected final Pool getPool() {
        return this.pool;
    }

    @Override
    public boolean hasAbortedSizeOf() {
        return this.abortedSizeOf;
    }
}

