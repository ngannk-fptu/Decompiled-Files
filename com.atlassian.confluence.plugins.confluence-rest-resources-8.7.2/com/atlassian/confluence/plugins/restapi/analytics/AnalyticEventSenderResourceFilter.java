/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.rest.api.annotation.SendsAnalytics
 *  com.atlassian.event.api.EventPublisher
 *  com.sun.jersey.api.model.AbstractMethod
 *  com.sun.jersey.spi.container.ContainerResponseFilter
 *  javax.ws.rs.Path
 */
package com.atlassian.confluence.plugins.restapi.analytics;

import com.atlassian.confluence.plugins.restapi.analytics.RestEndpointAnalyticsEvent;
import com.atlassian.confluence.plugins.restapi.filters.AbstractResponseResourceFilter;
import com.atlassian.confluence.rest.api.annotation.SendsAnalytics;
import com.atlassian.event.api.EventPublisher;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import javax.ws.rs.Path;

class AnalyticEventSenderResourceFilter
extends AbstractResponseResourceFilter {
    private final EventPublisher eventPublisher;
    private final AbstractMethod method;

    AnalyticEventSenderResourceFilter(EventPublisher eventPublisher, AbstractMethod method) {
        this.eventPublisher = eventPublisher;
        this.method = method;
    }

    public ContainerResponseFilter getResponseFilter() {
        return (request, response) -> {
            String httpMethod = request.getMethod();
            Class resourceClass = this.method.getResource().getResourceClass();
            if (resourceClass.isAnnotationPresent(SendsAnalytics.class)) {
                String path;
                if (resourceClass.isAnnotationPresent(Path.class)) {
                    Path classAnnotation = resourceClass.getAnnotation(Path.class);
                    path = classAnnotation.value();
                } else {
                    path = "/" + request.getPath();
                }
                if (this.method.isAnnotationPresent(Path.class)) {
                    path = path + ((Path)this.method.getAnnotation(Path.class)).value();
                }
                this.eventPublisher.publish((Object)new RestEndpointAnalyticsEvent(httpMethod, path));
            }
            return response;
        };
    }
}

