/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.ehcache.Ehcache
 *  net.sf.ehcache.Element
 *  org.aspectj.lang.annotation.Around
 *  org.aspectj.lang.annotation.Aspect
 */
package com.atlassian.confluence.impl.hibernate;

import java.util.Objects;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
final class JCacheAspect<K, V> {
    private final Ehcache delegate;

    JCacheAspect(Ehcache delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    @Around(value="execution(void javax.cache.Cache.put(..)) && args(key, value)")
    void put(K key, V value) {
        this.delegate.put(new Element(key, value));
    }

    @Around(value="execution(Object javax.cache.Cache.get(..)) && args(key)")
    V get(K key) {
        Element element = this.delegate.get(key);
        return (V)(element == null ? null : element.getObjectValue());
    }

    @Around(value="execution(boolean javax.cache.Cache.containsKey(..)) && args(key)")
    public boolean containsKey(K key) {
        return this.delegate.isKeyInCache(key);
    }

    @Around(value="execution(boolean javax.cache.Cache.remove(..)) && args(key)")
    public boolean remove(K key) {
        return this.delegate.remove(key);
    }

    @Around(value="execution(void javax.cache.Cache.clear(..))")
    public void clear() {
        this.delegate.removeAll();
    }
}

