/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.monitor.LocalWanPublisherStats;
import com.hazelcast.monitor.LocalWanStats;
import com.hazelcast.monitor.impl.LocalWanPublisherStatsImpl;
import com.hazelcast.util.Clock;
import java.util.HashMap;
import java.util.Map;

public class LocalWanStatsImpl
implements LocalWanStats {
    private volatile Map<String, LocalWanPublisherStats> localPublisherStatsMap = new HashMap<String, LocalWanPublisherStats>();
    private volatile long creationTime = Clock.currentTimeMillis();

    @Override
    public Map<String, LocalWanPublisherStats> getLocalWanPublisherStats() {
        return this.localPublisherStatsMap;
    }

    public void setLocalPublisherStatsMap(Map<String, LocalWanPublisherStats> localPublisherStatsMap) {
        this.localPublisherStatsMap = localPublisherStatsMap;
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public JsonObject toJson() {
        JsonObject wanStatsObject = new JsonObject();
        for (Map.Entry<String, LocalWanPublisherStats> entry : this.localPublisherStatsMap.entrySet()) {
            wanStatsObject.add(entry.getKey(), entry.getValue().toJson());
        }
        return wanStatsObject;
    }

    @Override
    public void fromJson(JsonObject json) {
        for (JsonObject.Member next : json) {
            LocalWanPublisherStatsImpl localPublisherStats = new LocalWanPublisherStatsImpl();
            localPublisherStats.fromJson(next.getValue().asObject());
            this.localPublisherStatsMap.put(next.getName(), localPublisherStats);
        }
    }

    public String toString() {
        return "LocalWanStatsImpl{localPublisherStatsMap=" + this.localPublisherStatsMap + ", creationTime=" + this.creationTime + '}';
    }
}

