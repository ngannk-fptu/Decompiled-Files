/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.ehcache.CacheException
 *  net.sf.ehcache.Ehcache
 *  net.sf.ehcache.Status
 *  net.sf.ehcache.loader.CacheLoader
 */
package com.atlassian.cache.ehcache.wrapper;

import com.atlassian.cache.ehcache.wrapper.ValueProcessor;
import com.atlassian.cache.ehcache.wrapper.WrapperUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Status;
import net.sf.ehcache.loader.CacheLoader;

public class ValueProcessorEhcacheLoaderDecorator
implements CacheLoader {
    private final CacheLoader delegate;
    private final ValueProcessor valueProcessor;

    public ValueProcessorEhcacheLoaderDecorator(CacheLoader delegate, ValueProcessor valueProcessor) {
        this.delegate = delegate;
        this.valueProcessor = valueProcessor;
    }

    public Object load(Object key) throws CacheException {
        return this.valueProcessor.wrap(this.delegate.load(this.valueProcessor.unwrap(key)));
    }

    public Map loadAll(Collection keys) {
        return this.wrapLoadedValues(this.delegate.loadAll(WrapperUtils.unwrapAllKeys(keys, this.valueProcessor)));
    }

    public Object load(Object key, Object argument) {
        return this.valueProcessor.wrap(this.delegate.load(this.valueProcessor.unwrap(key), argument));
    }

    public Map loadAll(Collection keys, Object argument) {
        return this.wrapLoadedValues(this.delegate.loadAll(WrapperUtils.unwrapAllKeys(keys, this.valueProcessor), argument));
    }

    public String getName() {
        return this.delegate.getName();
    }

    public CacheLoader clone(Ehcache cache) throws CloneNotSupportedException {
        return this.delegate.clone(cache);
    }

    public void init() {
        this.delegate.init();
    }

    public void dispose() throws CacheException {
        this.delegate.dispose();
    }

    public Status getStatus() {
        return this.delegate.getStatus();
    }

    private Map<Object, Object> wrapLoadedValues(Map<Object, Object> unwrappedValues) {
        HashMap<Object, Object> result = new HashMap<Object, Object>(unwrappedValues.size());
        for (Map.Entry<Object, Object> entry : unwrappedValues.entrySet()) {
            result.put(this.valueProcessor.wrap(entry.getKey()), this.valueProcessor.wrap(entry.getValue()));
        }
        return result;
    }
}

