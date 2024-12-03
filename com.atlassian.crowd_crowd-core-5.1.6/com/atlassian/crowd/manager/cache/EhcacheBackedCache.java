/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheEntryListener
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.Supplier
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.sf.ehcache.Ehcache
 *  net.sf.ehcache.Element
 */
package com.atlassian.crowd.manager.cache;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheEntryListener;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.Supplier;
import com.google.common.base.Preconditions;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

class EhcacheBackedCache<K, V>
implements Cache<K, V> {
    private final Ehcache ehcache;
    private final CacheLoader<K, V> loader;

    EhcacheBackedCache(Ehcache ehcache) {
        this(ehcache, null);
    }

    EhcacheBackedCache(Ehcache ehcache, CacheLoader<K, V> loader) {
        this.ehcache = (Ehcache)Preconditions.checkNotNull((Object)ehcache);
        this.loader = loader;
    }

    public boolean containsKey(@Nonnull K key) {
        return this.ehcache.get(key) != null;
    }

    private static <V> V contentsOrNull(Element e) {
        return (V)(e != null ? e.getObjectValue() : null);
    }

    @Nullable
    public V get(@Nonnull K key) {
        if (this.loader != null) {
            return this.get(key, () -> this.loader.load(key));
        }
        return EhcacheBackedCache.contentsOrNull(this.ehcache.get(key));
    }

    @Nonnull
    public V get(@Nonnull K key, @Nonnull Supplier<? extends V> valueSupplier) {
        Object val = EhcacheBackedCache.contentsOrNull(this.ehcache.get(key));
        if (val == null) {
            val = valueSupplier.get();
            this.put(key, val);
        }
        return val;
    }

    @Nonnull
    public String getName() {
        return this.ehcache.getName();
    }

    public void put(@Nonnull K key, @Nonnull V value) {
        this.ehcache.put(new Element(key, value));
    }

    @Nullable
    public V putIfAbsent(@Nonnull K key, @Nonnull V value) {
        Element current = this.ehcache.putIfAbsent(new Element(key, value));
        return EhcacheBackedCache.contentsOrNull(current);
    }

    public void remove(@Nonnull K key) {
        this.ehcache.remove(key);
    }

    public boolean remove(@Nonnull K key, @Nonnull V value) {
        return this.ehcache.removeElement(new Element(key, value));
    }

    public void removeAll() {
        this.ehcache.removeAll();
    }

    public boolean replace(@Nonnull K key, @Nonnull V oldValue, @Nonnull V newValue) {
        return this.ehcache.replace(new Element(key, oldValue), new Element(key, newValue));
    }

    @Nonnull
    public Collection<K> getKeys() {
        return this.ehcache.getKeys();
    }

    public void addListener(@Nonnull CacheEntryListener<K, V> listener, boolean includeValues) {
        throw new UnsupportedOperationException();
    }

    public void removeListener(@Nonnull CacheEntryListener<K, V> listener) {
        throw new UnsupportedOperationException();
    }
}

