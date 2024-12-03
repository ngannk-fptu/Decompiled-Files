/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.constructs;

import java.util.Properties;
import net.sf.ehcache.Ehcache;

public abstract class CacheDecoratorFactory {
    public static final String DASH = "-";

    public abstract Ehcache createDecoratedEhcache(Ehcache var1, Properties var2);

    public abstract Ehcache createDefaultDecoratedEhcache(Ehcache var1, Properties var2);

    public static String generateDefaultDecoratedCacheName(Ehcache cache, String cacheNameSuffix) {
        if (cacheNameSuffix == null || cacheNameSuffix.trim().length() == 0) {
            return cache.getName();
        }
        return cache.getName() + DASH + cacheNameSuffix;
    }
}

