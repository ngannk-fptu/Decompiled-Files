/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.constructs.readthrough;

import java.util.Properties;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.constructs.CacheDecoratorFactory;
import net.sf.ehcache.constructs.readthrough.ReadThroughCache;
import net.sf.ehcache.constructs.readthrough.ReadThroughCacheConfiguration;

public class ReadThroughCacheFactory
extends CacheDecoratorFactory {
    @Override
    public Ehcache createDecoratedEhcache(Ehcache cache, Properties properties) {
        ReadThroughCacheConfiguration config = new ReadThroughCacheConfiguration().fromProperties(properties).build();
        return new ReadThroughCache(cache, config);
    }

    @Override
    public Ehcache createDefaultDecoratedEhcache(Ehcache cache, Properties properties) {
        ReadThroughCacheConfiguration config = new ReadThroughCacheConfiguration().fromProperties(properties).build();
        return new ReadThroughCache(cache, config);
    }
}

