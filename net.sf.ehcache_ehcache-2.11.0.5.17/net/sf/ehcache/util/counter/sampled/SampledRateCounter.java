/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.util.counter.sampled;

import net.sf.ehcache.util.counter.sampled.SampledCounter;

public interface SampledRateCounter
extends SampledCounter {
    public void increment(long var1, long var3);

    public void decrement(long var1, long var3);

    public void setValue(long var1, long var3);

    public void setNumeratorValue(long var1);

    public void setDenominatorValue(long var1);
}

