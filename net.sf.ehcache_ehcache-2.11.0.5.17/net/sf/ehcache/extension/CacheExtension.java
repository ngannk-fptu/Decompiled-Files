/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.extension;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Status;

public interface CacheExtension {
    public void init();

    public void dispose() throws CacheException;

    public CacheExtension clone(Ehcache var1) throws CloneNotSupportedException;

    public Status getStatus();
}

