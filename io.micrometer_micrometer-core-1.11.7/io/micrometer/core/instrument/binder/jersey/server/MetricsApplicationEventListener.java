/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.glassfish.jersey.server.monitoring.ApplicationEvent
 *  org.glassfish.jersey.server.monitoring.ApplicationEventListener
 *  org.glassfish.jersey.server.monitoring.RequestEvent
 *  org.glassfish.jersey.server.monitoring.RequestEventListener
 */
package io.micrometer.core.instrument.binder.jersey.server;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jersey.server.AnnotationFinder;
import io.micrometer.core.instrument.binder.jersey.server.JerseyTagsProvider;
import io.micrometer.core.instrument.binder.jersey.server.MetricsRequestEventListener;
import java.util.Objects;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

public class MetricsApplicationEventListener
implements ApplicationEventListener {
    private final MeterRegistry meterRegistry;
    private final JerseyTagsProvider tagsProvider;
    private final String metricName;
    private final AnnotationFinder annotationFinder;
    private final boolean autoTimeRequests;

    public MetricsApplicationEventListener(MeterRegistry registry, JerseyTagsProvider tagsProvider, String metricName, boolean autoTimeRequests) {
        this(registry, tagsProvider, metricName, autoTimeRequests, AnnotationFinder.DEFAULT);
    }

    public MetricsApplicationEventListener(MeterRegistry registry, JerseyTagsProvider tagsProvider, String metricName, boolean autoTimeRequests, AnnotationFinder annotationFinder) {
        this.meterRegistry = Objects.requireNonNull(registry);
        this.tagsProvider = Objects.requireNonNull(tagsProvider);
        this.metricName = Objects.requireNonNull(metricName);
        this.annotationFinder = Objects.requireNonNull(annotationFinder);
        this.autoTimeRequests = autoTimeRequests;
    }

    public void onEvent(ApplicationEvent event) {
    }

    public RequestEventListener onRequest(RequestEvent requestEvent) {
        return new MetricsRequestEventListener(this.meterRegistry, this.tagsProvider, this.metricName, this.autoTimeRequests, this.annotationFinder);
    }
}

