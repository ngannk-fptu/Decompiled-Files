/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation;

import com.atlassian.instrumentation.Counter;
import com.atlassian.instrumentation.ExternalValue;
import com.atlassian.instrumentation.Instrument;
import com.atlassian.instrumentation.InstrumentToStringBuilder;
import com.atlassian.instrumentation.compare.InstrumentComparator;
import com.atlassian.instrumentation.utils.dbc.Assertions;

public class ExternalCounter
implements Counter {
    private final ExternalValue externalValue;
    private final String name;

    public ExternalCounter(String name, ExternalValue externalValue) {
        Assertions.notNull("name", name);
        Assertions.notNull("externalValue", externalValue);
        this.name = name;
        this.externalValue = externalValue;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public long incrementAndGet() {
        throw new UnsupportedOperationException("Changing the value from this object is not supported");
    }

    @Override
    public long addAndGet(long delta) {
        throw new UnsupportedOperationException("Changing the value from this object is not supported");
    }

    @Override
    public long getValue() {
        return this.externalValue.getValue();
    }

    @Override
    public int compareTo(Instrument that) {
        return new InstrumentComparator().compare(this, that);
    }

    public String toString() {
        return InstrumentToStringBuilder.toString(this);
    }
}

