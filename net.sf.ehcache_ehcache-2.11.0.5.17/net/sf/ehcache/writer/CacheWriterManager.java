/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.writer;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;

public interface CacheWriterManager {
    public void init(Cache var1) throws CacheException;

    public void put(Element var1) throws CacheException;

    public void remove(CacheEntry var1) throws CacheException;

    public void dispose() throws CacheException;
}

