/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.observation;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.observation.ObservationPredicate;
import io.micrometer.observation.ObservationRegistry;
import java.util.Collection;
import java.util.Collections;

final class NoopObservationConfig
extends ObservationRegistry.ObservationConfig {
    static final NoopObservationConfig INSTANCE = new NoopObservationConfig();

    private NoopObservationConfig() {
    }

    @Override
    public ObservationRegistry.ObservationConfig observationHandler(ObservationHandler<?> handler) {
        return this;
    }

    @Override
    public ObservationRegistry.ObservationConfig observationPredicate(ObservationPredicate predicate) {
        return this;
    }

    @Override
    public boolean isObservationEnabled(String name, Observation.Context context) {
        return false;
    }

    @Override
    Collection<ObservationHandler<?>> getObservationHandlers() {
        return Collections.emptyList();
    }
}

