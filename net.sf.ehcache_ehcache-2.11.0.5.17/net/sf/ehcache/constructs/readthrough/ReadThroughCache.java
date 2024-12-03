/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.constructs.readthrough;

import java.io.Serializable;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.EhcacheDecoratorAdapter;
import net.sf.ehcache.constructs.readthrough.ReadThroughCacheConfiguration;

public class ReadThroughCache
extends EhcacheDecoratorAdapter {
    private final ReadThroughCacheConfiguration readThroughCacheConfig;
    private final boolean isModeGet;

    public ReadThroughCache(Ehcache underlyingCache, ReadThroughCacheConfiguration config) {
        super(underlyingCache);
        this.readThroughCacheConfig = config;
        this.isModeGet = this.readThroughCacheConfig.isModeGet();
    }

    @Override
    public Element get(Object key) throws IllegalStateException, CacheException {
        if (this.isModeGet) {
            return super.getWithLoader(key, null, null);
        }
        return super.get(key);
    }

    @Override
    public Element get(Serializable key) throws IllegalStateException, CacheException {
        if (this.isModeGet) {
            return super.getWithLoader(key, null, null);
        }
        return super.get(key);
    }

    @Override
    public String getName() {
        if (this.readThroughCacheConfig.getName() != null) {
            return this.readThroughCacheConfig.getName();
        }
        return super.getName();
    }
}

