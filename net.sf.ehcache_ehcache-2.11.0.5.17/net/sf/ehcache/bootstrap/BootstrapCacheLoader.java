/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.bootstrap;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;

public interface BootstrapCacheLoader {
    public void load(Ehcache var1) throws CacheException;

    public boolean isAsynchronous();

    public Object clone() throws CloneNotSupportedException;
}

