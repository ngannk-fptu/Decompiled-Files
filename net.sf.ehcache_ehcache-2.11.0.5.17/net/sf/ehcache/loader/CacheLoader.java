/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.loader;

import java.util.Collection;
import java.util.Map;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Status;

public interface CacheLoader {
    public Object load(Object var1) throws CacheException;

    public Map loadAll(Collection var1);

    public Object load(Object var1, Object var2);

    public Map loadAll(Collection var1, Object var2);

    public String getName();

    public CacheLoader clone(Ehcache var1) throws CloneNotSupportedException;

    public void init();

    public void dispose() throws CacheException;

    public Status getStatus();
}

