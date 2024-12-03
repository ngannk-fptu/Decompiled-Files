/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.monitor.LocalInstanceStats;
import java.util.Map;

public interface StatisticsAwareService<T extends LocalInstanceStats> {
    public Map<String, T> getStats();
}

