/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.KeyValues
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.observation;

import io.micrometer.common.KeyValues;
import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.KeyValuesConvention;
import io.micrometer.observation.Observation;

public interface ObservationConvention<T extends Observation.Context>
extends KeyValuesConvention {
    public static final ObservationConvention<Observation.Context> EMPTY = context -> false;

    default public KeyValues getLowCardinalityKeyValues(T context) {
        return KeyValues.empty();
    }

    default public KeyValues getHighCardinalityKeyValues(T context) {
        return KeyValues.empty();
    }

    public boolean supportsContext(Observation.Context var1);

    @Nullable
    default public String getName() {
        return null;
    }

    @Nullable
    default public String getContextualName(T context) {
        return null;
    }
}

