/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.observation.ObservationRegistry
 *  org.glassfish.jersey.server.monitoring.ApplicationEvent
 *  org.glassfish.jersey.server.monitoring.ApplicationEventListener
 *  org.glassfish.jersey.server.monitoring.RequestEvent
 *  org.glassfish.jersey.server.monitoring.RequestEventListener
 */
package io.micrometer.core.instrument.binder.jersey.server;

import io.micrometer.core.instrument.binder.jersey.server.JerseyObservationConvention;
import io.micrometer.core.instrument.binder.jersey.server.ObservationRequestEventListener;
import io.micrometer.observation.ObservationRegistry;
import java.util.Objects;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

public class ObservationApplicationEventListener
implements ApplicationEventListener {
    private final ObservationRegistry observationRegistry;
    private final String metricName;
    private final JerseyObservationConvention jerseyObservationConvention;

    public ObservationApplicationEventListener(ObservationRegistry observationRegistry, String metricName) {
        this(observationRegistry, metricName, null);
    }

    public ObservationApplicationEventListener(ObservationRegistry observationRegistry, String metricName, JerseyObservationConvention jerseyObservationConvention) {
        this.observationRegistry = Objects.requireNonNull(observationRegistry);
        this.metricName = Objects.requireNonNull(metricName);
        this.jerseyObservationConvention = jerseyObservationConvention;
    }

    public void onEvent(ApplicationEvent event) {
    }

    public RequestEventListener onRequest(RequestEvent requestEvent) {
        return new ObservationRequestEventListener(this.observationRegistry, this.metricName, this.jerseyObservationConvention);
    }
}

