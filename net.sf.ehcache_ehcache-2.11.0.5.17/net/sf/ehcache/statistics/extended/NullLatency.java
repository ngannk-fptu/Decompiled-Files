/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics.extended;

import net.sf.ehcache.statistics.extended.ExtendedStatistics;
import net.sf.ehcache.statistics.extended.NullStatistic;

final class NullLatency
implements ExtendedStatistics.Latency {
    private static final ExtendedStatistics.Latency INSTANCE = new NullLatency();

    private NullLatency() {
    }

    static ExtendedStatistics.Latency instance() {
        return INSTANCE;
    }

    @Override
    public ExtendedStatistics.Statistic<Long> minimum() {
        return NullStatistic.instance(null);
    }

    @Override
    public ExtendedStatistics.Statistic<Long> maximum() {
        return NullStatistic.instance(null);
    }

    @Override
    public ExtendedStatistics.Statistic<Double> average() {
        return NullStatistic.instance(Double.NaN);
    }
}

