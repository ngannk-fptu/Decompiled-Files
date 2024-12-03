/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.pool;

public interface PoolParticipant {
    public boolean evict(int var1, long var2);

    public float getApproximateHitRate();

    public float getApproximateMissRate();

    public long getApproximateCountSize();
}

