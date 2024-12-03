/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.ManagedCache
 *  io.atlassian.fugue.Option
 */
package com.atlassian.confluence.impl.cache.hibernate;

import com.atlassian.cache.ManagedCache;
import io.atlassian.fugue.Option;
import java.util.Collection;
import java.util.TreeMap;
import java.util.stream.Stream;

public interface HibernateManagedCacheSupplier {
    public Collection<ManagedCache> getAllManagedCaches();

    public Option<ManagedCache> getManagedCache(String var1);

    default public Collection<ManagedCache> getManagedCaches(Collection<ManagedCache> mergeWith) {
        TreeMap map = new TreeMap();
        Stream.of(this.getAllManagedCaches(), mergeWith).flatMap(Collection::stream).forEach(cache -> map.putIfAbsent(cache.getName(), cache));
        return map.values();
    }
}

