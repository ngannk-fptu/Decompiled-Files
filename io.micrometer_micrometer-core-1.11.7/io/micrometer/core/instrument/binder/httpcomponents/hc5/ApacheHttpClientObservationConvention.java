/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.observation.Observation$Context
 *  io.micrometer.observation.ObservationConvention
 */
package io.micrometer.core.instrument.binder.httpcomponents.hc5;

import io.micrometer.core.instrument.binder.httpcomponents.hc5.ApacheHttpClientContext;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;

public interface ApacheHttpClientObservationConvention
extends ObservationConvention<ApacheHttpClientContext> {
    default public boolean supportsContext(Observation.Context context) {
        return context instanceof ApacheHttpClientContext;
    }
}

