/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.observation.Observation$Context
 *  io.micrometer.observation.ObservationHandler
 */
package io.micrometer.core.instrument.observation;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;

public interface MeterObservationHandler<T extends Observation.Context>
extends ObservationHandler<T> {
    default public boolean supportsContext(Observation.Context context) {
        return true;
    }
}

