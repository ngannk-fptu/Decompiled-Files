/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.management.CacheConfiguration;
import net.sf.ehcache.management.CacheStatistics;

public interface CacheMBean {
    public void removeAll() throws IllegalStateException, CacheException;

    public void flush() throws IllegalStateException, CacheException;

    public String getStatus();

    public String getName();

    public boolean isTerracottaClustered();

    public boolean hasAbortedSizeOf();

    public CacheConfiguration getCacheConfiguration();

    public CacheStatistics getStatistics();
}

