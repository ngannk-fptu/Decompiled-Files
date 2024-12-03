/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.util.UuidUtil
 *  org.hibernate.cache.spi.RegionFactory
 */
package com.hazelcast.hibernate.local;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.hibernate.RegionCache;
import com.hazelcast.hibernate.local.LocalRegionCache;
import com.hazelcast.hibernate.local.Timestamp;
import com.hazelcast.hibernate.serialization.Expirable;
import com.hazelcast.hibernate.serialization.Value;
import com.hazelcast.util.UuidUtil;
import java.util.UUID;
import org.hibernate.cache.spi.RegionFactory;

public class TimestampsRegionCache
extends LocalRegionCache
implements RegionCache {
    private UUID regionId = UuidUtil.newSecureUUID();

    public TimestampsRegionCache(RegionFactory regionFactory, String name, HazelcastInstance hazelcastInstance) {
        super(regionFactory, name, hazelcastInstance, null);
    }

    @Override
    public void evictData() {
        this.cache.clear();
        this.maybeNotifyTopic(null, -1L, null);
    }

    @Override
    public boolean put(Object key, Object value, long txTimestamp, Object version) {
        boolean succeed = super.put(key, value, txTimestamp, version);
        if (succeed) {
            this.maybeNotifyTopic(key, value, version);
        }
        return succeed;
    }

    @Override
    protected Object createMessage(Object key, Object value, Object currentVersion) {
        return new Timestamp(key, (Long)value, this.regionId);
    }

    @Override
    protected void maybeInvalidate(Object messageObject) {
        Timestamp ts = (Timestamp)messageObject;
        if (ts.getSenderId().equals(this.regionId)) {
            return;
        }
        Object key = ts.getKey();
        if (key == null) {
            this.cache.clear();
            return;
        }
        while (true) {
            long nextTime;
            Expirable value;
            Long current;
            Long l = current = (value = (Expirable)this.cache.get(key)) != null ? (Long)value.getValue() : null;
            if (current != null) {
                if (ts.getTimestamp() > current) {
                    nextTime = this.nextTimestamp();
                    if (!this.cache.replace(key, value, new Value(value.getVersion(), nextTime, nextTime))) continue;
                    return;
                }
                return;
            }
            nextTime = this.nextTimestamp();
            if (this.cache.putIfAbsent(key, new Value(null, nextTime, nextTime)) == null) break;
        }
    }

    @Override
    final void cleanup() {
    }
}

