/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.observation;

import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

public interface ObservationView {
    default public ObservationRegistry getObservationRegistry() {
        return ObservationRegistry.NOOP;
    }

    public Observation.ContextView getContextView();

    @Nullable
    default public Observation.Scope getEnclosingScope() {
        return Observation.Scope.NOOP;
    }
}

