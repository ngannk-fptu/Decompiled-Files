/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.constructs.refreshahead;

import java.util.Properties;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.constructs.CacheDecoratorFactory;
import net.sf.ehcache.constructs.refreshahead.RefreshAheadCache;
import net.sf.ehcache.constructs.refreshahead.RefreshAheadCacheConfiguration;

public class RefreshAheadCacheFactory
extends CacheDecoratorFactory {
    @Override
    public Ehcache createDecoratedEhcache(Ehcache cache, Properties properties) {
        RefreshAheadCacheConfiguration config = new RefreshAheadCacheConfiguration().fromProperties(properties).build();
        RefreshAheadCache decorator = new RefreshAheadCache(cache, config);
        return decorator;
    }

    @Override
    public Ehcache createDefaultDecoratedEhcache(Ehcache cache, Properties properties) {
        RefreshAheadCacheConfiguration config = new RefreshAheadCacheConfiguration().fromProperties(properties).build();
        RefreshAheadCache decorator = new RefreshAheadCache(cache, config);
        return decorator;
    }
}

