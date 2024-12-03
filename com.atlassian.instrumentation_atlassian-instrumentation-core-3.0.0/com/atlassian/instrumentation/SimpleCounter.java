/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation;

import com.atlassian.instrumentation.Counter;
import com.atlassian.instrumentation.Instrument;

public class SimpleCounter
implements Counter {
    private String name;
    private long count;

    public SimpleCounter(String name) {
        this.name = name;
    }

    @Override
    public long incrementAndGet() {
        return ++this.count;
    }

    @Override
    public long addAndGet(long delta) {
        this.count += delta;
        return this.count;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public long getValue() {
        return this.count;
    }

    @Override
    public int compareTo(Instrument instrument) {
        return Long.compare(instrument.getValue(), this.getValue());
    }
}

