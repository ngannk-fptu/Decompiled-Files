/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.util.counter.sampled;

import net.sf.ehcache.util.counter.Counter;
import net.sf.ehcache.util.counter.sampled.TimeStampedCounterValue;

public interface SampledCounter
extends Counter {
    public void shutdown();

    public TimeStampedCounterValue getMostRecentSample();

    public TimeStampedCounterValue[] getAllSampleValues();

    public long getAndReset();
}

