/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.ehcache.Ehcache
 *  net.sf.ehcache.Element
 *  net.sf.ehcache.constructs.EhcacheDecoratorAdapter
 */
package com.atlassian.cache.ehcache;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.EhcacheDecoratorAdapter;

public class SynchronizedLoadingCacheDecorator
extends EhcacheDecoratorAdapter {
    private static final Object LOAD_PLACEHOLDER = new Object();
    private final Map synchronizationMap = new ConcurrentHashMap();

    public SynchronizedLoadingCacheDecorator(Ehcache delegate) {
        super(delegate);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected <V> Element synchronizedLoad(Object key, Function<Object, V> loader, Consumer<Element> postLoadProcessor) {
        Element result;
        Object value = null;
        try {
            this.synchronizationMap.put(key, LOAD_PLACEHOLDER);
            value = loader.apply(key);
        }
        finally {
            result = new Element(key, value);
            this.synchronizationMap.computeIfPresent(key, (a, b) -> {
                postLoadProcessor.accept(result);
                return null;
            });
        }
        return result;
    }

    public boolean remove(Serializable key, boolean doNotNotifyCacheReplicators) {
        this.synchronizationMap.remove(key);
        return super.remove(key, doNotNotifyCacheReplicators);
    }

    public boolean remove(Serializable key) {
        this.synchronizationMap.remove(key);
        return super.remove(key);
    }

    public boolean remove(Object key, boolean doNotNotifyCacheReplicators) {
        this.synchronizationMap.remove(key);
        return super.remove(key, doNotNotifyCacheReplicators);
    }

    public boolean remove(Object key) {
        this.synchronizationMap.remove(key);
        return super.remove(key);
    }

    public void removeAll() {
        this.synchronizationMap.clear();
        super.removeAll();
    }

    public void removeAll(boolean doNotNotifyCacheReplicators) {
        this.synchronizationMap.clear();
        super.removeAll(doNotNotifyCacheReplicators);
    }
}

