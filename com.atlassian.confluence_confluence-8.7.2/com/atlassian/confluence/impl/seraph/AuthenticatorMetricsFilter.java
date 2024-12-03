/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.filters.AbstractHttpFilter
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.impl.seraph;

import com.atlassian.confluence.impl.seraph.AuthenticatorMetrics;
import com.atlassian.core.filters.AbstractHttpFilter;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticatorMetricsFilter
extends AbstractHttpFilter {
    private final LazyComponentReference<EventPublisher> eventPublisherRef = new LazyComponentReference("eventPublisher");

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        AuthenticatorMetrics.resetThreadLocal();
        try {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
        finally {
            if (AuthenticatorMetrics.hasEvents()) {
                this.eventPublisher().ifPresent(AuthenticatorMetrics::publishEvents);
            }
        }
    }

    private Optional<EventPublisher> eventPublisher() {
        return ContainerManager.isContainerSetup() ? Optional.ofNullable((EventPublisher)this.eventPublisherRef.get()) : Optional.empty();
    }
}

