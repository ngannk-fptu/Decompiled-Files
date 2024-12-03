/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNull
 */
package io.micrometer.core.instrument.binder;

import io.micrometer.common.lang.NonNull;
import io.micrometer.core.instrument.MeterRegistry;

public interface MeterBinder {
    public void bindTo(@NonNull MeterRegistry var1);
}

