/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.glassfish.jersey.server.ContainerRequest
 *  org.glassfish.jersey.server.model.ResourceMethod
 *  org.glassfish.jersey.server.monitoring.RequestEvent
 *  org.glassfish.jersey.server.monitoring.RequestEventListener
 */
package io.micrometer.core.instrument.binder.jersey.server;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.jersey.server.AnnotationFinder;
import io.micrometer.core.instrument.binder.jersey.server.JerseyTagsProvider;
import io.micrometer.core.instrument.binder.jersey.server.TimedFinder;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

public class MetricsRequestEventListener
implements RequestEventListener {
    private final Map<ContainerRequest, Timer.Sample> shortTaskSample = Collections.synchronizedMap(new IdentityHashMap());
    private final Map<ContainerRequest, Collection<LongTaskTimer.Sample>> longTaskSamples = Collections.synchronizedMap(new IdentityHashMap());
    private final Map<ContainerRequest, Set<Timed>> timedAnnotationsOnRequest = Collections.synchronizedMap(new IdentityHashMap());
    private final MeterRegistry registry;
    private final JerseyTagsProvider tagsProvider;
    private boolean autoTimeRequests;
    private final TimedFinder timedFinder;
    private final String metricName;

    public MetricsRequestEventListener(MeterRegistry registry, JerseyTagsProvider tagsProvider, String metricName, boolean autoTimeRequests, AnnotationFinder annotationFinder) {
        this.registry = Objects.requireNonNull(registry);
        this.tagsProvider = Objects.requireNonNull(tagsProvider);
        this.metricName = Objects.requireNonNull(metricName);
        this.autoTimeRequests = autoTimeRequests;
        this.timedFinder = new TimedFinder(annotationFinder);
    }

    public void onEvent(RequestEvent event) {
        ContainerRequest containerRequest = event.getContainerRequest();
        switch (event.getType()) {
            case ON_EXCEPTION: {
                if (!this.isNotFoundException(event)) break;
            }
            case REQUEST_MATCHED: {
                Set<Timed> timedAnnotations = this.annotations(event);
                this.timedAnnotationsOnRequest.put(containerRequest, timedAnnotations);
                this.shortTaskSample.put(containerRequest, Timer.start(this.registry));
                List longTaskSamples = this.longTaskTimers(timedAnnotations, event).stream().map(LongTaskTimer::start).collect(Collectors.toList());
                if (longTaskSamples.isEmpty()) break;
                this.longTaskSamples.put(containerRequest, longTaskSamples);
                break;
            }
            case FINISHED: {
                Collection<LongTaskTimer.Sample> longSamples;
                Set<Timed> timedAnnotations = this.timedAnnotationsOnRequest.remove(containerRequest);
                Timer.Sample shortSample = this.shortTaskSample.remove(containerRequest);
                if (shortSample != null) {
                    for (Timer timer : this.shortTimers(timedAnnotations, event)) {
                        shortSample.stop(timer);
                    }
                }
                if ((longSamples = this.longTaskSamples.remove(containerRequest)) == null) break;
                for (LongTaskTimer.Sample longSample : longSamples) {
                    longSample.stop();
                }
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

    private Set<Timer> shortTimers(Set<Timed> timed, RequestEvent event) {
        if ((timed == null || timed.isEmpty()) && this.autoTimeRequests) {
            return Collections.singleton(this.registry.timer(this.metricName, this.tagsProvider.httpRequestTags(event)));
        }
        if (timed == null) {
            return Collections.emptySet();
        }
        return timed.stream().filter(annotation -> !annotation.longTask()).map(t -> ((Timer.Builder)Timer.builder(t, this.metricName).tags((Iterable)this.tagsProvider.httpRequestTags(event))).register(this.registry)).collect(Collectors.toSet());
    }

    private Set<LongTaskTimer> longTaskTimers(Set<Timed> timed, RequestEvent event) {
        return timed.stream().filter(Timed::longTask).map(LongTaskTimer::builder).map(b -> b.tags(this.tagsProvider.httpLongRequestTags(event)).register(this.registry)).collect(Collectors.toSet());
    }

    private Set<Timed> annotations(RequestEvent event) {
        HashSet<Timed> timed = new HashSet<Timed>();
        ResourceMethod matchingResourceMethod = event.getUriInfo().getMatchedResourceMethod();
        if (matchingResourceMethod != null) {
            timed.addAll(this.timedFinder.findTimedAnnotations(matchingResourceMethod.getInvocable().getHandlingMethod()));
            if (timed.isEmpty()) {
                timed.addAll(this.timedFinder.findTimedAnnotations(matchingResourceMethod.getInvocable().getHandlingMethod().getDeclaringClass()));
            }
        }
        return timed;
    }
}

