/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation.operations;

import com.atlassian.instrumentation.Instrument;
import com.atlassian.instrumentation.InstrumentToStringBuilder;
import com.atlassian.instrumentation.compare.InstrumentComparator;
import com.atlassian.instrumentation.operations.ExternalOpValue;
import com.atlassian.instrumentation.operations.OpInstrument;
import com.atlassian.instrumentation.utils.dbc.Assertions;
import java.util.concurrent.TimeUnit;

public class ExternalOpInstrument
implements OpInstrument {
    private final String name;
    private final ExternalOpValue externalOpValue;

    public ExternalOpInstrument(String name, ExternalOpValue externalOpValue) {
        Assertions.notNull("name", name);
        Assertions.notNull("externalOpValue", externalOpValue);
        this.name = name;
        this.externalOpValue = externalOpValue;
    }

    @Override
    public long getInvocationCount() {
        return this.externalOpValue.getSnapshot().getInvocationCount();
    }

    @Override
    public long getMillisecondsTaken() {
        return this.externalOpValue.getSnapshot().getMillisecondsTaken();
    }

    @Override
    public long getElapsedTotalTime(TimeUnit unit) {
        return this.externalOpValue.getSnapshot().getElapsedTotalTime(unit);
    }

    @Override
    public long getElapsedMinTime(TimeUnit unit) {
        return this.externalOpValue.getSnapshot().getElapsedMinTime(unit);
    }

    @Override
    public long getElapsedMaxTime(TimeUnit unit) {
        return this.externalOpValue.getSnapshot().getElapsedMaxTime(unit);
    }

    @Override
    public long getResultSetSize() {
        return this.externalOpValue.getSnapshot().getResultSetSize();
    }

    @Override
    public long getCpuTime() {
        return this.externalOpValue.getSnapshot().getCpuTime();
    }

    @Override
    public long getCpuTotalTime(TimeUnit unit) {
        return this.externalOpValue.getSnapshot().getCpuTotalTime(unit);
    }

    @Override
    public long getCpuMinTime(TimeUnit unit) {
        return this.externalOpValue.getSnapshot().getCpuMinTime(unit);
    }

    @Override
    public long getCpuMaxTime(TimeUnit unit) {
        return this.externalOpValue.getSnapshot().getCpuMaxTime(unit);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public long getValue() {
        return this.externalOpValue.getSnapshot().getValue();
    }

    @Override
    public int compareTo(Instrument that) {
        return new InstrumentComparator().compare(this, that);
    }

    public String toString() {
        return InstrumentToStringBuilder.toString(this);
    }
}

