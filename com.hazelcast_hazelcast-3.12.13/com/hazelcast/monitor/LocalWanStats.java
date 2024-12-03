/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor;

import com.hazelcast.monitor.LocalInstanceStats;
import com.hazelcast.monitor.LocalWanPublisherStats;
import java.util.Map;

public interface LocalWanStats
extends LocalInstanceStats {
    public Map<String, LocalWanPublisherStats> getLocalWanPublisherStats();
}

