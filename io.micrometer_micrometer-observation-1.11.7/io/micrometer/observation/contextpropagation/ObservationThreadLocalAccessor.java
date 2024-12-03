/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.util.internal.logging.InternalLogger
 *  io.micrometer.common.util.internal.logging.InternalLoggerFactory
 *  io.micrometer.context.ContextRegistry
 *  io.micrometer.context.ThreadLocalAccessor
 */
package io.micrometer.observation.contextpropagation;

import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.InternalLoggerFactory;
import io.micrometer.context.ContextRegistry;
import io.micrometer.context.ThreadLocalAccessor;
import io.micrometer.observation.NullObservation;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

public class ObservationThreadLocalAccessor
implements ThreadLocalAccessor<Observation> {
    private static final InternalLogger log = InternalLoggerFactory.getInstance(ObservationThreadLocalAccessor.class);
    public static final String KEY = "micrometer.observation";
    private ObservationRegistry observationRegistry = ObservationRegistry.create();
    private static ObservationThreadLocalAccessor instance;

    public ObservationThreadLocalAccessor() {
        instance = this;
    }

    public ObservationThreadLocalAccessor(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    public void setObservationRegistry(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
    }

    public ObservationRegistry getObservationRegistry() {
        return this.observationRegistry;
    }

    public static ObservationThreadLocalAccessor getInstance() {
        if (instance == null) {
            ContextRegistry.getInstance();
        }
        return instance;
    }

    public Object key() {
        return KEY;
    }

    public Observation getValue() {
        return this.observationRegistry.getCurrentObservation();
    }

    public void setValue(Observation value) {
        Observation.Scope scope = value.openScope();
        if (log.isTraceEnabled()) {
            log.trace("Called setValue(...) for Observation <{}> and opened scope <{}>", (Object)value, (Object)scope);
        }
    }

    public void setValue() {
        Observation currentObservation = this.observationRegistry.getCurrentObservation();
        if (currentObservation == null) {
            return;
        }
        if (log.isTraceEnabled()) {
            log.trace("Calling setValue(), currentObservation <{}> but we will open a NullObservation", (Object)currentObservation);
        }
        ObservationRegistry registryAttachedToCurrentObservation = currentObservation.getObservationRegistry();
        Observation.Scope scope = new NullObservation(registryAttachedToCurrentObservation).start().openScope();
        if (log.isTraceEnabled()) {
            log.trace("Created the NullObservation scope <{}>", (Object)scope);
        }
    }

    private void closeCurrentScope() {
        Observation.Scope scope = this.observationRegistry.getCurrentObservationScope();
        if (log.isTraceEnabled()) {
            log.trace("Closing current scope <{}>", (Object)scope);
        }
        if (scope != null) {
            scope.close();
        }
        if (log.isTraceEnabled()) {
            log.trace("After closing scope, current one is <{}>", (Object)this.observationRegistry.getCurrentObservationScope());
        }
    }

    public void restore() {
        if (log.isTraceEnabled()) {
            log.trace("Calling restore()");
        }
        this.closeCurrentScope();
    }

    public void restore(Observation value) {
        Observation.Scope previousObservationScope;
        Observation.Scope scope = this.observationRegistry.getCurrentObservationScope();
        if (log.isTraceEnabled()) {
            log.trace("Calling restore(...) with Observation <{}> and scope <{}>", (Object)value, (Object)scope);
        }
        if (scope == null) {
            String msg = "There is no current scope in thread local. This situation should not happen";
            log.warn(msg);
            this.assertFalse(msg);
        }
        Observation.Scope scope2 = previousObservationScope = scope != null ? scope.getPreviousObservationScope() : null;
        if (previousObservationScope == null || value != previousObservationScope.getCurrentObservation()) {
            Observation previousObservation = previousObservationScope != null ? previousObservationScope.getCurrentObservation() : null;
            String msg = "Observation <" + value + "> to which we're restoring is not the same as the one set as this scope's parent observation <" + previousObservation + ">. Most likely a manually created Observation has a scope opened that was never closed. This may lead to thread polluting and memory leaks.";
            log.warn(msg);
            this.assertFalse(msg);
        }
        this.closeCurrentScope();
    }

    void assertFalse(String msg) {
        assert (false) : msg;
    }

    @Deprecated
    public void reset() {
        if (log.isTraceEnabled()) {
            log.trace("Calling reset()");
        }
        super.reset();
    }
}

