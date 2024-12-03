/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.KeyValue
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.observation;

import io.micrometer.common.KeyValue;
import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.NoopObservationRegistry;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;
import io.micrometer.observation.SimpleObservation;

final class NoopObservation
implements Observation {
    private static final Observation.Context CONTEXT = new Observation.Context();

    NoopObservation() {
    }

    @Override
    public Observation contextualName(@Nullable String contextualName) {
        return this;
    }

    @Override
    public Observation parentObservation(@Nullable Observation parentObservation) {
        return this;
    }

    @Override
    public Observation lowCardinalityKeyValue(KeyValue keyValue) {
        return this;
    }

    @Override
    public Observation lowCardinalityKeyValue(String key, String value) {
        return this;
    }

    @Override
    public Observation highCardinalityKeyValue(KeyValue keyValue) {
        return this;
    }

    @Override
    public Observation highCardinalityKeyValue(String key, String value) {
        return this;
    }

    @Override
    public Observation observationConvention(ObservationConvention<?> observationConvention) {
        return this;
    }

    @Override
    public Observation error(Throwable error) {
        return this;
    }

    @Override
    public Observation event(Observation.Event event) {
        return this;
    }

    @Override
    public Observation start() {
        return this;
    }

    @Override
    public Observation.Context getContext() {
        return CONTEXT;
    }

    @Override
    public void stop() {
    }

    @Override
    public Observation.Scope openScope() {
        return new SimpleObservation.SimpleScope(NoopObservationRegistry.FOR_SCOPES, this);
    }

    static final class NoopScope
    implements Observation.Scope {
        static final Observation.Scope INSTANCE = new NoopScope();

        private NoopScope() {
        }

        @Override
        public Observation getCurrentObservation() {
            return Observation.NOOP;
        }

        @Override
        public void close() {
        }

        @Override
        public void reset() {
        }

        @Override
        public void makeCurrent() {
        }
    }
}

