/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.observation.Observation$Context
 *  io.micrometer.observation.ObservationConvention
 */
package io.micrometer.core.instrument.binder.okhttp3;

import io.micrometer.core.instrument.binder.okhttp3.OkHttpContext;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;

public interface OkHttpObservationConvention
extends ObservationConvention<OkHttpContext> {
    default public boolean supportsContext(Observation.Context context) {
        return context instanceof OkHttpContext;
    }
}

