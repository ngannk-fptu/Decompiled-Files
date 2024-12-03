/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.loader;

import java.util.Properties;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.loader.CacheLoader;

public abstract class CacheLoaderFactory {
    public abstract CacheLoader createCacheLoader(Ehcache var1, Properties var2);
}

