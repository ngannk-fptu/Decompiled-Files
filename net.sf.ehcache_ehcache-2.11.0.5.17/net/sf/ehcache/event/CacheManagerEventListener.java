/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.event;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Status;

public interface CacheManagerEventListener {
    public void init() throws CacheException;

    public Status getStatus();

    public void dispose() throws CacheException;

    public void notifyCacheAdded(String var1);

    public void notifyCacheRemoved(String var1);
}

