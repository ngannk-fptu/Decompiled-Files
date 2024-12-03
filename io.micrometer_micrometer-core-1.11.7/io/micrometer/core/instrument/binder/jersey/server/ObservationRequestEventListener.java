/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.observation.Observation
 *  io.micrometer.observation.Observation$Scope
 *  io.micrometer.observation.ObservationRegistry
 *  org.glassfish.jersey.server.ContainerRequest
 *  org.glassfish.jersey.server.monitoring.RequestEvent
 *  org.glassfish.jersey.server.monitoring.RequestEventListener
 */
package io.micrometer.core.instrument.binder.jersey.server;

import io.micrometer.core.instrument.binder.jersey.server.DefaultJerseyObservationConvention;
import io.micrometer.core.instrument.binder.jersey.server.JerseyContext;
import io.micrometer.core.instrument.binder.jersey.server.JerseyObservationConvention;
import io.micrometer.core.instrument.binder.jersey.server.JerseyObservationDocumentation;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

public class ObservationRequestEventListener
implements RequestEventListener {
    private final Map<ContainerRequest, ObservationScopeAndContext> observations = Collections.synchronizedMap(new IdentityHashMap());
    private final ObservationRegistry registry;
    private final JerseyObservationConvention customConvention;
    private final String metricName;
    private final JerseyObservationConvention defaultConvention;

    public ObservationRequestEventListener(ObservationRegistry registry, String metricName) {
        this(registry, metricName, null);
    }

    public ObservationRequestEventListener(ObservationRegistry registry, String metricName, JerseyObservationConvention customConvention) {
        this.registry = Objects.requireNonNull(registry);
        this.metricName = Objects.requireNonNull(metricName);
        this.customConvention = customConvention;
        this.defaultConvention = new DefaultJerseyObservationConvention(this.metricName);
    }

    public void onEvent(RequestEvent event) {
        ContainerRequest containerRequest = event.getContainerRequest();
        switch (event.getType()) {
            case ON_EXCEPTION: {
                if (!this.isNotFoundException(event)) break;
            }
            case REQUEST_MATCHED: {
                JerseyContext jerseyContext = new JerseyContext(event);
                Observation observation = JerseyObservationDocumentation.DEFAULT.start(this.customConvention, this.defaultConvention, () -> jerseyContext, this.registry);
                Observation.Scope scope = observation.openScope();
                this.observations.put(event.getContainerRequest(), new ObservationScopeAndContext(scope, jerseyContext));
                break;
            }
            case RESP_FILTERS_START: {
                ObservationScopeAndContext observationScopeAndContext = this.observations.get(containerRequest);
                if (observationScopeAndContext == null) break;
                observationScopeAndContext.jerseyContext.setResponse(event.getContainerResponse());
                observationScopeAndContext.jerseyContext.setRequestEvent(event);
                break;
            }
            case FINISHED: {
                ObservationScopeAndContext finishedObservation = this.observations.remove(containerRequest);
                if (finishedObservation == null) break;
                finishedObservation.jerseyContext.setRequestEvent(event);
                Observation.Scope observationScope = finishedObservation.observationScope;
                observationScope.close();
                observationScope.getCurrentObservation().stop();
                break;
            }
        }
    }

    private boolean isNotFoundException(RequestEvent event) {
        Throwable t = event.getException();
        if (t == null) {
            return false;
        }
        String className = t.getClass().getCanonicalName();
        return className.equals("jakarta.ws.rs.NotFoundException") || className.equals("javax.ws.rs.NotFoundException");
    }

    private static class ObservationScopeAndContext {
        final Observation.Scope observationScope;
        final JerseyContext jerseyContext;

        ObservationScopeAndContext(Observation.Scope observationScope, JerseyContext jerseyContext) {
            this.observationScope = observationScope;
            this.jerseyContext = jerseyContext;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ObservationScopeAndContext that = (ObservationScopeAndContext)o;
            return Objects.equals(this.observationScope, that.observationScope) && Objects.equals((Object)this.jerseyContext, (Object)that.jerseyContext);
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.observationScope, this.jerseyContext});
        }
    }
}

