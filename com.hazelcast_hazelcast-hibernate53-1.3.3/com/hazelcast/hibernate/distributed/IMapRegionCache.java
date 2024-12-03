/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.EntryView
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.core.IMap
 *  org.hibernate.cache.spi.RegionFactory
 *  org.hibernate.cache.spi.access.SoftLock
 */
package com.hazelcast.hibernate.distributed;

import com.hazelcast.core.EntryView;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.hibernate.RegionCache;
import com.hazelcast.hibernate.serialization.Expirable;
import com.hazelcast.hibernate.serialization.Value;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.access.SoftLock;

public class IMapRegionCache
implements RegionCache {
    private final IMap<Object, Expirable> map;
    private final String name;
    private final RegionFactory regionFactory;

    public IMapRegionCache(RegionFactory regionFactory, String name, HazelcastInstance hazelcastInstance) {
        this.name = name;
        this.regionFactory = regionFactory;
        this.map = hazelcastInstance.getMap(this.name);
    }

    @Override
    public void afterUpdate(Object key, Object newValue, Object newVersion) {
    }

    @Override
    public boolean contains(Object key) {
        return this.map.containsKey(key);
    }

    @Override
    public void evictData() {
        this.map.evictAll();
    }

    @Override
    public void evictData(Object key) {
        this.map.remove(key);
    }

    @Override
    public Object get(Object key, long txTimestamp) {
        Expirable entry = (Expirable)this.map.get(key);
        return entry == null ? null : entry.getValue(txTimestamp);
    }

    public long getElementCountInMemory() {
        return this.map.size();
    }

    public String getName() {
        return this.name;
    }

    public RegionFactory getRegionFactory() {
        return this.regionFactory;
    }

    public long getSizeInMemory() {
        long size = 0L;
        for (Object key : this.map.keySet()) {
            EntryView entry = this.map.getEntryView(key);
            if (entry == null) continue;
            size += entry.getCost();
        }
        return size;
    }

    @Override
    public boolean put(Object key, Object value, long txTimestamp, Object version) {
        Value newValue = new Value(version, txTimestamp, value);
        this.map.put(key, (Object)newValue);
        return true;
    }

    @Override
    public void unlockItem(Object key, SoftLock lock) {
    }
}

