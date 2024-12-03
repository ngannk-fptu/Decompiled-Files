/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.pool;

import java.util.Collection;
import net.sf.ehcache.pool.PoolAccessor;
import net.sf.ehcache.pool.PoolEvictor;
import net.sf.ehcache.pool.PoolParticipant;
import net.sf.ehcache.pool.SizeOfEngine;

public interface Pool {
    public long getSize();

    public long getMaxSize();

    public void setMaxSize(long var1);

    public PoolAccessor createPoolAccessor(PoolParticipant var1, int var2, boolean var3);

    public void registerPoolAccessor(PoolAccessor var1);

    public void removePoolAccessor(PoolAccessor var1);

    public PoolAccessor createPoolAccessor(PoolParticipant var1, SizeOfEngine var2);

    public Collection<PoolAccessor> getPoolAccessors();

    public PoolEvictor getEvictor();
}

