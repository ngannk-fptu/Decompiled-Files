/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.KeyValues
 */
package io.micrometer.observation;

import io.micrometer.common.KeyValues;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;

final class NoopObservationConvention
implements ObservationConvention<Observation.Context> {
    static final NoopObservationConvention INSTANCE = new NoopObservationConvention();

    private NoopObservationConvention() {
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getContextualName(Observation.Context context) {
        return "";
    }

    @Override
    public boolean supportsContext(Observation.Context context) {
        return ObservationConvention.EMPTY.supportsContext(context);
    }

    @Override
    public KeyValues getLowCardinalityKeyValues(Observation.Context context) {
        return ObservationConvention.EMPTY.getLowCardinalityKeyValues(context);
    }

    @Override
    public KeyValues getHighCardinalityKeyValues(Observation.Context context) {
        return ObservationConvention.EMPTY.getHighCardinalityKeyValues(context);
    }
}

