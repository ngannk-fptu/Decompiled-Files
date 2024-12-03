/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.docs.KeyName
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.observation.docs;

import io.micrometer.common.docs.KeyName;
import io.micrometer.common.lang.Nullable;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;
import io.micrometer.observation.ObservationRegistry;
import java.util.Objects;
import java.util.function.Supplier;

public interface ObservationDocumentation {
    public static final KeyName[] EMPTY = new KeyName[0];
    public static final Observation.Event[] EMPTY_EVENT_NAMES = new Observation.Event[0];

    @Nullable
    default public String getName() {
        return null;
    }

    @Nullable
    default public Class<? extends ObservationConvention<? extends Observation.Context>> getDefaultConvention() {
        return null;
    }

    @Nullable
    default public String getContextualName() {
        return null;
    }

    default public KeyName[] getLowCardinalityKeyNames() {
        return EMPTY;
    }

    default public KeyName[] getHighCardinalityKeyNames() {
        return EMPTY;
    }

    default public Observation.Event[] getEvents() {
        return EMPTY_EVENT_NAMES;
    }

    default public String getPrefix() {
        return "";
    }

    default public Observation observation(ObservationRegistry registry) {
        return this.observation(registry, Observation.Context::new);
    }

    default public Observation observation(ObservationRegistry registry, Supplier<Observation.Context> contextSupplier) {
        Observation observation = Observation.createNotStarted(this.getName(), contextSupplier, registry);
        if (this.getContextualName() != null) {
            observation.contextualName(this.getContextualName());
        }
        return observation;
    }

    default public <T extends Observation.Context> Observation observation(@Nullable ObservationConvention<T> customConvention, ObservationConvention<T> defaultConvention, Supplier<T> contextSupplier, ObservationRegistry registry) {
        if (this.getDefaultConvention() == null) {
            throw new IllegalStateException("You've decided to use convention based naming yet this observation [" + this.getClass() + "] has not defined any default convention");
        }
        if (!this.getDefaultConvention().isAssignableFrom(Objects.requireNonNull(defaultConvention, "You have not provided a default convention in the Observation factory method").getClass())) {
            throw new IllegalArgumentException("Observation [" + this.getClass() + "] defined default convention to be of type [" + this.getDefaultConvention() + "] but you have provided an incompatible one of type [" + defaultConvention.getClass() + "]");
        }
        Observation observation = Observation.createNotStarted(customConvention, defaultConvention, contextSupplier, registry);
        if (this.getName() != null) {
            observation.getContext().setName(this.getName());
        }
        if (this.getContextualName() != null) {
            observation.contextualName(this.getContextualName());
        }
        return observation;
    }

    default public Observation start(ObservationRegistry registry) {
        return this.start(registry, Observation.Context::new);
    }

    default public Observation start(ObservationRegistry registry, Supplier<Observation.Context> contextSupplier) {
        return this.observation(registry, contextSupplier).start();
    }

    default public <T extends Observation.Context> Observation start(@Nullable ObservationConvention<T> customConvention, ObservationConvention<T> defaultConvention, Supplier<T> contextSupplier, ObservationRegistry registry) {
        return this.observation(customConvention, defaultConvention, contextSupplier, registry).start();
    }
}

