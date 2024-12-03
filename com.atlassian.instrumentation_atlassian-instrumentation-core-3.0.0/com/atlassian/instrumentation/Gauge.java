/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation;

import com.atlassian.instrumentation.Instrument;

public interface Gauge
extends Instrument {
    public long incrementAndGet();

    public long decrementAndGet();

    public long addAndGet(long var1);
}

