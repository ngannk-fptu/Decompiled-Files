/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.pool;

import java.util.Collection;
import net.sf.ehcache.pool.PoolAccessor;
import net.sf.ehcache.pool.PoolParticipant;

public interface PoolEvictor<T extends PoolParticipant> {
    public boolean freeSpace(Collection<PoolAccessor<T>> var1, long var2);
}

