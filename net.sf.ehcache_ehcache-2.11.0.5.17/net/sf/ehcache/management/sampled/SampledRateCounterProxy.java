/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management.sampled;

import net.sf.ehcache.management.sampled.SampledCounterProxy;
import net.sf.ehcache.statistics.extended.ExtendedStatistics;
import net.sf.ehcache.util.counter.sampled.SampledRateCounter;

public class SampledRateCounterProxy<E extends Number>
extends SampledCounterProxy<E>
implements SampledRateCounter {
    public SampledRateCounterProxy(ExtendedStatistics.Statistic<E> rate) {
        super(rate);
    }

    @Override
    public void increment(long numerator, long denominator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void decrement(long numerator, long denominator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setValue(long numerator, long denominator) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNumeratorValue(long newValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDenominatorValue(long newValue) {
        throw new UnsupportedOperationException();
    }
}

