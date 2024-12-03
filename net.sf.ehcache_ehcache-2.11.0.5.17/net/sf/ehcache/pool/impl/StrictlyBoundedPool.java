/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.pool.impl;

import net.sf.ehcache.pool.PoolAccessor;
import net.sf.ehcache.pool.PoolEvictor;
import net.sf.ehcache.pool.PoolParticipant;
import net.sf.ehcache.pool.SizeOfEngine;
import net.sf.ehcache.pool.impl.AbstractPool;
import net.sf.ehcache.pool.impl.LockedPoolAccessor;

public class StrictlyBoundedPool
extends AbstractPool {
    public StrictlyBoundedPool(long maximumPoolSize, PoolEvictor evictor, SizeOfEngine defaultSizeOfEngine) {
        super(maximumPoolSize, evictor, defaultSizeOfEngine);
    }

    @Override
    public PoolAccessor createPoolAccessor(PoolParticipant participant, SizeOfEngine sizeOfEngine) {
        LockedPoolAccessor accessor = new LockedPoolAccessor(this, participant, sizeOfEngine, 0L);
        this.registerPoolAccessor(accessor);
        return accessor;
    }
}

