/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.observation;

import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.GlobalObservationConvention;
import io.micrometer.observation.NoopObservationRegistry;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;
import io.micrometer.observation.ObservationFilter;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.observation.ObservationPredicate;
import io.micrometer.observation.SimpleObservationRegistry;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public interface ObservationRegistry {
    public static final ObservationRegistry NOOP = new NoopObservationRegistry();

    public static ObservationRegistry create() {
        return new SimpleObservationRegistry();
    }

    @Nullable
    public Observation getCurrentObservation();

    @Nullable
    public Observation.Scope getCurrentObservationScope();

    public void setCurrentObservationScope(@Nullable Observation.Scope var1);

    public ObservationConfig observationConfig();

    default public boolean isNoop() {
        return this == NOOP;
    }

    public static class ObservationConfig {
        private final List<ObservationHandler<?>> observationHandlers = new CopyOnWriteArrayList();
        private final List<ObservationPredicate> observationPredicates = new CopyOnWriteArrayList<ObservationPredicate>();
        private final List<ObservationConvention<?>> observationConventions = new CopyOnWriteArrayList();
        private final List<ObservationFilter> observationFilters = new CopyOnWriteArrayList<ObservationFilter>();

        public ObservationConfig observationHandler(ObservationHandler<?> handler) {
            this.observationHandlers.add(handler);
            return this;
        }

        public ObservationConfig observationPredicate(ObservationPredicate predicate) {
            this.observationPredicates.add(predicate);
            return this;
        }

        public ObservationConfig observationFilter(ObservationFilter observationFilter) {
            this.observationFilters.add(observationFilter);
            return this;
        }

        public ObservationConfig observationConvention(GlobalObservationConvention<?> observationConvention) {
            this.observationConventions.add(observationConvention);
            return this;
        }

        <T extends Observation.Context> ObservationConvention<T> getObservationConvention(T context, ObservationConvention<T> defaultConvention) {
            for (ObservationConvention<?> convention : this.observationConventions) {
                if (!convention.supportsContext(context)) continue;
                return convention;
            }
            return Objects.requireNonNull(defaultConvention, "Default ObservationConvention must not be null");
        }

        boolean isObservationEnabled(String name, @Nullable Observation.Context context) {
            for (ObservationPredicate predicate : this.observationPredicates) {
                if (predicate.test(name, context)) continue;
                return false;
            }
            return true;
        }

        Collection<ObservationHandler<?>> getObservationHandlers() {
            return this.observationHandlers;
        }

        Collection<ObservationFilter> getObservationFilters() {
            return this.observationFilters;
        }

        Collection<ObservationConvention<?>> getObservationConventions() {
            return this.observationConventions;
        }
    }
}

