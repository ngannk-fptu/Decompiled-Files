/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.pool.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.sf.ehcache.pool.Pool;
import net.sf.ehcache.pool.PoolAccessor;
import net.sf.ehcache.pool.PoolEvictor;
import net.sf.ehcache.pool.PoolParticipant;
import net.sf.ehcache.pool.SizeOfEngine;

public abstract class AbstractPool
implements Pool {
    private volatile long maximumPoolSize;
    private final PoolEvictor evictor;
    private final List<PoolAccessor> poolAccessors;
    private final List<PoolAccessor> poolAccessorsView;
    private final SizeOfEngine defaultSizeOfEngine;

    public AbstractPool(long maximumPoolSize, PoolEvictor evictor, SizeOfEngine defaultSizeOfEngine) {
        this.maximumPoolSize = maximumPoolSize;
        this.evictor = evictor;
        this.defaultSizeOfEngine = defaultSizeOfEngine;
        this.poolAccessors = new CopyOnWriteArrayList<PoolAccessor>();
        this.poolAccessorsView = Collections.unmodifiableList(this.poolAccessors);
    }

    @Override
    public long getSize() {
        long total = 0L;
        for (PoolAccessor poolAccessor : this.poolAccessors) {
            total += poolAccessor.getSize();
        }
        return total;
    }

    @Override
    public long getMaxSize() {
        return this.maximumPoolSize;
    }

    @Override
    public void setMaxSize(long newSize) {
        long oldSize = this.maximumPoolSize;
        this.maximumPoolSize = newSize;
        long sizeToEvict = oldSize - newSize;
        if (sizeToEvict > 0L) {
            this.getEvictor().freeSpace(this.getPoolAccessors(), sizeToEvict);
        }
    }

    @Override
    public PoolAccessor createPoolAccessor(PoolParticipant participant, int maxDepth, boolean abortWhenMaxDepthExceeded) {
        return this.createPoolAccessor(participant, this.defaultSizeOfEngine.copyWith(maxDepth, abortWhenMaxDepthExceeded));
    }

    @Override
    public void registerPoolAccessor(PoolAccessor accessor) {
        this.poolAccessors.add(accessor);
    }

    @Override
    public void removePoolAccessor(PoolAccessor accessor) {
        this.poolAccessors.remove(accessor);
    }

    @Override
    public Collection<PoolAccessor> getPoolAccessors() {
        return this.poolAccessorsView;
    }

    @Override
    public PoolEvictor getEvictor() {
        return this.evictor;
    }
}

