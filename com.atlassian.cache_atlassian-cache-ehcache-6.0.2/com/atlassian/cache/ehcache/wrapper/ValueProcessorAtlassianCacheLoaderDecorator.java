/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheLoader
 *  javax.annotation.Nonnull
 *  net.sf.ehcache.CacheException
 */
package com.atlassian.cache.ehcache.wrapper;

import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.ehcache.wrapper.ValueProcessor;
import javax.annotation.Nonnull;
import net.sf.ehcache.CacheException;

public class ValueProcessorAtlassianCacheLoaderDecorator
implements CacheLoader<Object, Object> {
    private final CacheLoader delegate;
    private final ValueProcessor valueProcessor;

    public ValueProcessorAtlassianCacheLoaderDecorator(CacheLoader delegate, ValueProcessor valueProcessor) {
        this.delegate = delegate;
        this.valueProcessor = valueProcessor;
    }

    @Nonnull
    public Object load(@Nonnull Object key) throws CacheException {
        return this.valueProcessor.wrap(this.delegate.load(this.valueProcessor.unwrap(key)));
    }
}

