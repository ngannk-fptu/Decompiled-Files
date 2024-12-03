/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.util.counter.sampled;

import net.sf.ehcache.util.counter.Counter;
import net.sf.ehcache.util.counter.sampled.SampledCounterConfig;
import net.sf.ehcache.util.counter.sampled.SampledRateCounterImpl;

public class SampledRateCounterConfig
extends SampledCounterConfig {
    private final long initialNumeratorValue;
    private final long initialDenominatorValue;

    public SampledRateCounterConfig(int intervalSecs, int historySize, boolean isResetOnSample) {
        this(intervalSecs, historySize, isResetOnSample, 0L, 0L);
    }

    public SampledRateCounterConfig(int intervalSecs, int historySize, boolean isResetOnSample, long initialNumeratorValue, long initialDenominatorValue) {
        super(intervalSecs, historySize, isResetOnSample, 0L);
        this.initialNumeratorValue = initialNumeratorValue;
        this.initialDenominatorValue = initialDenominatorValue;
    }

    @Override
    public Counter createCounter() {
        SampledRateCounterImpl sampledRateCounter = new SampledRateCounterImpl(this);
        sampledRateCounter.setValue(this.initialNumeratorValue, this.initialDenominatorValue);
        return sampledRateCounter;
    }
}

