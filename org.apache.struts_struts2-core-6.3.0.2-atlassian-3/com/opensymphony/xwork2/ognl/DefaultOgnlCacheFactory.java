/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.BooleanUtils
 */
package com.opensymphony.xwork2.ognl;

import com.opensymphony.xwork2.ognl.OgnlCache;
import com.opensymphony.xwork2.ognl.OgnlCacheFactory;
import com.opensymphony.xwork2.ognl.OgnlCaffeineCache;
import com.opensymphony.xwork2.ognl.OgnlDefaultCache;
import com.opensymphony.xwork2.ognl.OgnlLRUCache;
import org.apache.commons.lang3.BooleanUtils;

public class DefaultOgnlCacheFactory<Key, Value>
implements OgnlCacheFactory<Key, Value> {
    private static final int DEFAULT_INIT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private OgnlCacheFactory.CacheType defaultCacheType;
    private int cacheMaxSize;

    @Deprecated
    public DefaultOgnlCacheFactory() {
        this(10000, OgnlCacheFactory.CacheType.BASIC);
    }

    public DefaultOgnlCacheFactory(int cacheMaxSize, OgnlCacheFactory.CacheType defaultCacheType) {
        this.cacheMaxSize = cacheMaxSize;
        this.defaultCacheType = defaultCacheType;
    }

    @Override
    public OgnlCache<Key, Value> buildOgnlCache() {
        return this.buildOgnlCache(this.getCacheMaxSize(), 16, 0.75f, this.defaultCacheType);
    }

    @Override
    public OgnlCache<Key, Value> buildOgnlCache(int evictionLimit, int initialCapacity, float loadFactor, OgnlCacheFactory.CacheType cacheType) {
        switch (cacheType) {
            case BASIC: {
                return new OgnlDefaultCache(evictionLimit, initialCapacity, loadFactor);
            }
            case LRU: {
                return new OgnlLRUCache(evictionLimit, initialCapacity, loadFactor);
            }
            case WTLFU: {
                return new OgnlCaffeineCache(evictionLimit, initialCapacity);
            }
        }
        throw new IllegalArgumentException("Unknown cache type: " + (Object)((Object)cacheType));
    }

    @Override
    public int getCacheMaxSize() {
        return this.cacheMaxSize;
    }

    @Deprecated
    protected void setCacheMaxSize(String maxSize) {
        this.cacheMaxSize = Integer.parseInt(maxSize);
    }

    @Override
    public OgnlCacheFactory.CacheType getDefaultCacheType() {
        return this.defaultCacheType;
    }

    @Deprecated
    protected void setUseLRUCache(String useLRUMode) {
        if (BooleanUtils.toBoolean((String)useLRUMode)) {
            this.defaultCacheType = OgnlCacheFactory.CacheType.LRU;
        }
    }
}

