/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.pool.impl;

import net.sf.ehcache.pool.PoolAccessor;
import net.sf.ehcache.pool.PoolEvictor;
import net.sf.ehcache.pool.PoolParticipant;
import net.sf.ehcache.pool.SizeOfEngine;
import net.sf.ehcache.pool.impl.AbstractPool;
import net.sf.ehcache.pool.impl.AtomicPoolAccessor;

public class BoundedPool
extends AbstractPool {
    public BoundedPool(long maximumPoolSize, PoolEvictor evictor, SizeOfEngine defaultSizeOfEngine) {
        super(maximumPoolSize, evictor, defaultSizeOfEngine);
    }

    @Override
    public PoolAccessor createPoolAccessor(PoolParticipant participant, SizeOfEngine sizeOfEngine) {
        AtomicPoolAccessor accessor = new AtomicPoolAccessor(this, participant, sizeOfEngine, 0L);
        this.registerPoolAccessor(accessor);
        return accessor;
    }
}

