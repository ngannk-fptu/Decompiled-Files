/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation.operations;

import com.atlassian.instrumentation.Instrument;
import java.util.concurrent.TimeUnit;

public interface OpInstrument
extends Instrument {
    public long getInvocationCount();

    @Deprecated
    public long getMillisecondsTaken();

    public long getElapsedTotalTime(TimeUnit var1);

    public long getElapsedMinTime(TimeUnit var1);

    public long getElapsedMaxTime(TimeUnit var1);

    @Deprecated
    public long getCpuTime();

    public long getCpuTotalTime(TimeUnit var1);

    public long getCpuMinTime(TimeUnit var1);

    public long getCpuMaxTime(TimeUnit var1);

    public long getResultSetSize();
}

