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

class SimpleObservationRegistry
implements ObservationRegistry {
    private static final ThreadLocal<Observation.Scope> localObservationScope = new ThreadLocal();
    private final ObservationRegistry.ObservationConfig observationConfig = new ObservationRegistry.ObservationConfig();

    SimpleObservationRegistry() {
    }

    @Override
    @Nullable
    public Observation getCurrentObservation() {
        Observation.Scope scope = localObservationScope.get();
        if (scope != null) {
            return scope.getCurrentObservation();
        }
        return null;
    }

    @Override
    public Observation.Scope getCurrentObservationScope() {
        return localObservationScope.get();
    }

    @Override
    public void setCurrentObservationScope(Observation.Scope current) {
        localObservationScope.set(current);
    }

    @Override
    public ObservationRegistry.ObservationConfig observationConfig() {
        return this.observationConfig;
    }

    @Override
    public boolean isNoop() {
        return ObservationRegistry.super.isNoop() || this.observationConfig().getObservationHandlers().isEmpty();
    }
}

