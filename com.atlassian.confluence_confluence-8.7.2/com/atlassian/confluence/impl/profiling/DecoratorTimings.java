/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.util.concurrent.Supplier
 *  com.google.common.base.Stopwatch
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.opensymphony.module.sitemesh.Decorator
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.profiling;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.AsynchronousPreferred;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.concurrent.Supplier;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.opensymphony.module.sitemesh.Decorator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecoratorTimings {
    private static final Logger log = LoggerFactory.getLogger(DecoratorTimings.class);
    private final ConcurrentMap<String, AtomicLong> timingsByDecorator = new ConcurrentHashMap<String, AtomicLong>();
    private final ConcurrentMap<String, AtomicInteger> invocationsByDecorator = new ConcurrentHashMap<String, AtomicInteger>();
    private final Supplier<EventPublisher> eventPublisherRef = new LazyComponentReference("eventPublisher");

    public static Runnable createTimingsPublisherAndAttach(ServletRequest servletRequest) {
        DecoratorTimings decoratorTimings = new DecoratorTimings();
        servletRequest.setAttribute(DecoratorTimings.class.getName(), (Object)decoratorTimings);
        return decoratorTimings::publishResults;
    }

    private static Optional<DecoratorTimings> forRequest(ServletRequest servletRequest) {
        return Optional.ofNullable((DecoratorTimings)servletRequest.getAttribute(DecoratorTimings.class.getName()));
    }

    public static DecoratorTimer newDecoratorTimer(Decorator decorator, HttpServletRequest servletRequest) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        String requestURI = servletRequest.getRequestURI();
        log.debug("Applying decorator '{}' to request '{}]", (Object)decorator.getName(), (Object)requestURI);
        return () -> {
            long elapsedMillis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
            log.debug("Applied decorator '{}' to request '{}' in {}ms", new Object[]{decorator.getName(), requestURI, elapsedMillis});
            DecoratorTimings.forRequest((ServletRequest)servletRequest).ifPresent(timings -> {
                timings.timingsByDecorator.computeIfAbsent(decorator.getName(), key -> new AtomicLong()).addAndGet(elapsedMillis);
                timings.invocationsByDecorator.computeIfAbsent(decorator.getName(), key -> new AtomicInteger()).incrementAndGet();
            });
        };
    }

    private void publishResults() {
        if (this.invocationsByDecorator.isEmpty()) {
            log.debug("No decorator invocations to report");
        } else {
            log.info("Decorator timings are {}, decorator invocation counts are {}", this.timingsByDecorator, this.invocationsByDecorator);
            if (ContainerManager.isContainerSetup()) {
                ((EventPublisher)this.eventPublisherRef.get()).publish((Object)new DecoratorTimingEvent(this.timingsByDecorator, this.invocationsByDecorator));
            }
        }
    }

    public static interface DecoratorTimer
    extends AutoCloseable {
        @Override
        public void close();
    }

    @EventName(value="confluence.decorator.metrics")
    @AsynchronousPreferred
    public static class DecoratorTimingEvent {
        private final Map<String, Map<String, Number>> metrics;

        DecoratorTimingEvent(Map<String, ? extends Number> timingsByDecorator, Map<String, ? extends Number> invocationsByDecorator) {
            this.metrics = ImmutableMap.copyOf((Map)Maps.transformEntries(timingsByDecorator, (decoratorName, elapsedMillis) -> ImmutableMap.of((Object)"elapsedMillis", (Object)elapsedMillis.longValue(), (Object)"invocationCount", (Object)((Number)invocationsByDecorator.get(decoratorName)).intValue())));
        }

        public String getDecoratorNames() {
            return this.metrics.keySet().stream().collect(Collectors.joining(" "));
        }

        public Map<String, Map<String, Number>> getDecorator() {
            return this.metrics;
        }
    }
}

