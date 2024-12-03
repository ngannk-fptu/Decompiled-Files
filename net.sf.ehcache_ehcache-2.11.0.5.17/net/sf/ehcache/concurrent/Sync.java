/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.concurrent;

import net.sf.ehcache.concurrent.LockType;

public interface Sync {
    public static final long ONE_SECOND = 1000L;
    public static final long ONE_MINUTE = 60000L;
    public static final long ONE_HOUR = 3600000L;
    public static final long ONE_DAY = 86400000L;
    public static final long ONE_WEEK = 604800000L;
    public static final long ONE_YEAR = 31556952000L;
    public static final long ONE_CENTURY = 3155695200000L;

    public void lock(LockType var1);

    public boolean tryLock(LockType var1, long var2) throws InterruptedException;

    public void unlock(LockType var1);

    public boolean isHeldByCurrentThread(LockType var1);
}

