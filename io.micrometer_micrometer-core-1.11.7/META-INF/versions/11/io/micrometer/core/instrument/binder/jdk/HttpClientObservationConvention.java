/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.observation.Observation$Context
 *  io.micrometer.observation.ObservationConvention
 */
package io.micrometer.core.instrument.binder.jdk;

import io.micrometer.core.instrument.binder.jdk.HttpClientContext;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;

public interface HttpClientObservationConvention
extends ObservationConvention<HttpClientContext> {
    default public boolean supportsContext(Observation.Context context) {
        return context instanceof HttpClientContext;
    }
}

