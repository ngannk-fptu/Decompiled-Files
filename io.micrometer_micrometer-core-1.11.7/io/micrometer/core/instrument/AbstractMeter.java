/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.util.MeterEquivalence;

public abstract class AbstractMeter
implements Meter {
    private final Meter.Id id;

    public AbstractMeter(Meter.Id id) {
        this.id = id;
    }

    @Override
    public Meter.Id getId() {
        return this.id;
    }

    public boolean equals(@Nullable Object o) {
        return MeterEquivalence.equals(this, o);
    }

    public int hashCode() {
        return MeterEquivalence.hashCode(this);
    }
}

