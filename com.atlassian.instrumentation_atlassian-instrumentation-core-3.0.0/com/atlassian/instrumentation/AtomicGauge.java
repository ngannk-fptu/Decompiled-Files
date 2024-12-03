/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation;

import com.atlassian.instrumentation.Gauge;
import com.atlassian.instrumentation.Instrument;
import com.atlassian.instrumentation.InstrumentToStringBuilder;
import com.atlassian.instrumentation.compare.InstrumentComparator;
import com.atlassian.instrumentation.utils.dbc.Assertions;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicGauge
implements Gauge {
    protected final AtomicLong value;
    protected final String name;

    public AtomicGauge(String name) {
        this(name, new AtomicLong());
    }

    public AtomicGauge(String name, long value) {
        this(name, new AtomicLong(value));
    }

    public AtomicGauge(String name, AtomicLong atomicLongRef) {
        Assertions.notNull("name", name);
        Assertions.notNull("atomicLongRef", atomicLongRef);
        this.name = name;
        this.value = atomicLongRef;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public long incrementAndGet() {
        return this.value.incrementAndGet();
    }

    @Override
    public long decrementAndGet() {
        return this.value.decrementAndGet();
    }

    @Override
    public long addAndGet(long delta) {
        return this.value.addAndGet(delta);
    }

    @Override
    public long getValue() {
        return this.value.get();
    }

    @Override
    public int compareTo(Instrument that) {
        return new InstrumentComparator().compare(this, that);
    }

    public String toString() {
        return InstrumentToStringBuilder.toString(this);
    }
}

