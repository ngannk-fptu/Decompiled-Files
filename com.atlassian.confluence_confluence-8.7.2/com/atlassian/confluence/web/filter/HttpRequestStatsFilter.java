/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.instrumentation.driver.Instrumentation
 *  com.atlassian.instrumentation.driver.Instrumentation$Split
 *  com.atlassian.instrumentation.driver.Instrumentation$SplitFactory
 *  com.atlassian.instrumentation.instruments.Context
 *  com.atlassian.instrumentation.instruments.EventType
 *  com.google.common.annotations.VisibleForTesting
 *  javax.servlet.Filter
 *  javax.servlet.FilterChain
 *  javax.servlet.FilterConfig
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.web.filter;

import com.atlassian.confluence.event.events.analytics.HttpRequestStats;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.instrumentation.driver.Instrumentation;
import com.atlassian.instrumentation.instruments.Context;
import com.atlassian.instrumentation.instruments.EventType;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestStatsFilter
implements Filter {
    private static final Logger log = LoggerFactory.getLogger(HttpRequestStatsFilter.class);
    private final Instrumentation.SplitFactory splitFactory;
    private final EventPublisher eventPublisher;

    public HttpRequestStatsFilter(EventPublisher eventPublisher) {
        this(eventPublisher, Clock.systemUTC());
    }

    @VisibleForTesting
    HttpRequestStatsFilter(EventPublisher eventPublisher, Clock clock) {
        this.eventPublisher = eventPublisher;
        this.splitFactory = new SplitFactory(clock);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        Instrumentation.registerFactory((Instrumentation.SplitFactory)this.splitFactory);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpRequestStats.start((HttpServletRequest)request);
        try {
            chain.doFilter(request, response);
        }
        finally {
            HttpRequestStats.stop().ifPresent(arg_0 -> ((EventPublisher)this.eventPublisher).publish(arg_0));
        }
    }

    public void destroy() {
        Instrumentation.unregisterFactory((Instrumentation.SplitFactory)this.splitFactory);
    }

    private static class SplitFactory
    implements Instrumentation.SplitFactory {
        private final Clock clock;

        SplitFactory(Clock clock) {
            this.clock = clock;
        }

        public Instrumentation.Split startSplit(Context context) {
            return context.getEventType().filter(et -> et == EventType.EXECUTION).map(eventType -> {
                log.trace("Executed SQL: {}", (Object)context.getSql());
                Instant beforeOperation = Instant.now(this.clock);
                return () -> HttpRequestStats.logDbRequest(Duration.between(beforeOperation, Instant.now(this.clock)));
            }).orElse(() -> {});
        }
    }
}

