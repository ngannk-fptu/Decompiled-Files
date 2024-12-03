/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.statistics.extended;

import net.sf.ehcache.statistics.extended.ExtendedStatistics;
import net.sf.ehcache.statistics.extended.NullLatency;
import net.sf.ehcache.statistics.extended.NullStatistic;

final class NullOperation
implements ExtendedStatistics.Result {
    private static final ExtendedStatistics.Result INSTANCE = new NullOperation();

    private NullOperation() {
    }

    static final ExtendedStatistics.Result instance() {
        return INSTANCE;
    }

    @Override
    public ExtendedStatistics.Statistic<Long> count() {
        return NullStatistic.instance(0L);
    }

    @Override
    public ExtendedStatistics.Statistic<Double> rate() {
        return NullStatistic.instance(Double.NaN);
    }

    @Override
    public ExtendedStatistics.Latency latency() throws UnsupportedOperationException {
        return NullLatency.instance();
    }
}

