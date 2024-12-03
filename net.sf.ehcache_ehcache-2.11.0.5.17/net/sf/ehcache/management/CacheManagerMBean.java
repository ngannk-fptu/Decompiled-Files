/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management;

import java.util.List;
import net.sf.ehcache.management.Cache;

public interface CacheManagerMBean {
    public String getStatus();

    public String getName();

    public void shutdown();

    public void clearAll();

    public Cache getCache(String var1);

    public String[] getCacheNames() throws IllegalStateException;

    public List getCaches();

    public long getTransactionCommittedCount();

    public long getTransactionRolledBackCount();

    public long getTransactionTimedOutCount();
}

