/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.observation;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.SimpleObservation;

public class NullObservation
extends SimpleObservation {
    public NullObservation(ObservationRegistry registry) {
        super("null", registry, new Observation.Context());
    }

    @Override
    void notifyOnObservationStarted() {
    }

    @Override
    void notifyOnError() {
    }

    @Override
    void notifyOnEvent(Observation.Event event) {
    }

    @Override
    void notifyOnScopeMakeCurrent() {
    }

    @Override
    void notifyOnScopeReset() {
    }

    @Override
    void notifyOnObservationStopped(Observation.Context context) {
    }

    @Override
    public Observation start() {
        return this;
    }
}

