/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.query;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.search.Query;

public interface QueryManager {
    public Query createQuery(String var1) throws CacheException;
}

