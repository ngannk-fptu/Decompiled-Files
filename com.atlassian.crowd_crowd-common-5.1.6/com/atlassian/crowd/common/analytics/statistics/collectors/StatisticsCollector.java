/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.common.analytics.statistics.collectors;

public interface StatisticsCollector<T> {
    public Iterable<T> collect();

    default public boolean isPerNodeCollector() {
        return false;
    }
}

