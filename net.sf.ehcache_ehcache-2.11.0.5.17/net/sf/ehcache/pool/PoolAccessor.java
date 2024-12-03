/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.pool;

import net.sf.ehcache.pool.PoolParticipant;

public interface PoolAccessor<T extends PoolParticipant> {
    public long add(Object var1, Object var2, Object var3, boolean var4);

    public boolean canAddWithoutEvicting(Object var1, Object var2, Object var3);

    public long delete(long var1) throws IllegalArgumentException;

    public long replace(long var1, Object var3, Object var4, Object var5, boolean var6);

    public long getSize();

    public void unlink();

    public void clear();

    public T getParticipant();

    public void setMaxSize(long var1);

    public long getPoolOccupancy();

    public long getPoolSize();

    public boolean hasAbortedSizeOf();
}

