/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  com.atlassian.event.api.AsynchronousPreferred
 *  com.atlassian.event.api.EventPublisher
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.servlet;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.core.filters.AbstractHttpFilter;
import com.atlassian.event.api.AsynchronousPreferred;
import com.atlassian.event.api.EventPublisher;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestEventPublishingFilter
extends AbstractHttpFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestEventPublishingFilter.class);
    private final EventPublisher eventPublisher;
    private final List<Handler> handlers;

    RequestEventPublishingFilter(EventPublisher eventPublisher, Handler ... handlers) {
        this.eventPublisher = eventPublisher;
        this.handlers = Arrays.asList(handlers);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            this.publishEvent(request);
        }
        finally {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
    }

    private void publishEvent(HttpServletRequest request) {
        this.handlers.forEach(handler -> handler.publish(request, this.eventPublisher));
    }

    static Handler createHandler(String eventName, Predicate<HttpServletRequest> predicate) {
        return (request, eventPublisher) -> {
            if (predicate.test(request)) {
                log.debug("Found match for request servlet path {} and path info {}, publishing event", (Object)request.getServletPath(), (Object)request.getPathInfo());
                eventPublisher.publish((Object)new SimpleRequestEvent(eventName, request));
            }
        };
    }

    @AsynchronousPreferred
    public static class SimpleRequestEvent {
        private final String eventName;
        private final String requestMethod;
        private final String servletPath;
        private final String pathInfo;

        public SimpleRequestEvent(String eventName, HttpServletRequest request) {
            this.eventName = eventName;
            this.requestMethod = request.getMethod();
            this.servletPath = request.getServletPath();
            this.pathInfo = request.getPathInfo();
        }

        @EventName
        public String getEventName() {
            return this.eventName;
        }

        public String getRequestMethod() {
            return this.requestMethod;
        }

        public String getServletPath() {
            return this.servletPath;
        }

        public String getPathInfo() {
            return this.pathInfo;
        }
    }

    static interface Handler {
        public void publish(HttpServletRequest var1, EventPublisher var2);
    }
}

