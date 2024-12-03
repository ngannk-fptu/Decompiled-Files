/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation;

import com.atlassian.instrumentation.Counter;
import com.atlassian.instrumentation.Instrument;
import com.atlassian.instrumentation.InstrumentToStringBuilder;
import com.atlassian.instrumentation.compare.InstrumentComparator;
import com.atlassian.instrumentation.utils.dbc.Assertions;
import java.util.concurrent.atomic.AtomicLong;

public class AtomicCounter
implements Counter {
    protected final AtomicLong value;
    protected final String name;

    public AtomicCounter(String name) {
        this(name, new AtomicLong());
    }

    public AtomicCounter(String name, long value) {
        this(name, new AtomicLong(value));
    }

    public AtomicCounter(String name, AtomicLong atomicLongRef) {
        Assertions.notNull("name", name);
        Assertions.notNull("atomicLongRef", atomicLongRef);
        this.name = name;
        this.value = atomicLongRef;
    }

    @Override
    public long incrementAndGet() {
        return this.value.incrementAndGet();
    }

    @Override
    public long addAndGet(long delta) {
        Assertions.notNegative("delta", delta);
        return this.value.addAndGet(delta);
    }

    @Override
    public String getName() {
        return this.name;
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

