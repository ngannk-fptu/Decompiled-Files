/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.composite;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;

interface CompositeMeter
extends Meter {
    public void add(MeterRegistry var1);

    public void remove(MeterRegistry var1);
}

