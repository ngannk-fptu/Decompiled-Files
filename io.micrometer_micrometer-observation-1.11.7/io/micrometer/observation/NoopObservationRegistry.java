/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.observation;

import io.micrometer.observation.NoopObservationConfig;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

final class NoopObservationRegistry
implements ObservationRegistry {
    static final ObservationRegistry FOR_SCOPES = ObservationRegistry.create();
    private final ObservationRegistry.ObservationConfig observationConfig = NoopObservationConfig.INSTANCE;

    NoopObservationRegistry() {
    }

    @Override
    public Observation getCurrentObservation() {
        return FOR_SCOPES.getCurrentObservation();
    }

    @Override
    public Observation.Scope getCurrentObservationScope() {
        return FOR_SCOPES.getCurrentObservationScope();
    }

    @Override
    public void setCurrentObservationScope(Observation.Scope current) {
        FOR_SCOPES.setCurrentObservationScope(current);
    }

    @Override
    public ObservationRegistry.ObservationConfig observationConfig() {
        return this.observationConfig;
    }
}

