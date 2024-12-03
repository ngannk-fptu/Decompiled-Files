/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.event.mau.MauEventService
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.atlassian.util.concurrent.Supplier
 *  com.google.common.annotations.VisibleForTesting
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.web.filter;

import com.atlassian.confluence.api.service.event.mau.MauEventService;
import com.atlassian.confluence.web.filter.AbstractStaticResourceAwareFilter;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import com.atlassian.util.concurrent.Supplier;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MauEventFilter
extends AbstractStaticResourceAwareFilter {
    @VisibleForTesting
    static final String IGNORE_MAU_HEADER = "x-atlassian-mau-ignore";
    private final Supplier<MauEventService> mauEventServiceRef = new LazyComponentReference("mauEventService");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (request.getHeader(IGNORE_MAU_HEADER) == null && ContainerManager.isContainerSetup()) {
            ((MauEventService)this.mauEventServiceRef.get()).clearApplicationActivities();
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
            ((MauEventService)this.mauEventServiceRef.get()).sendMauEvents();
        } else {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
    }
}

