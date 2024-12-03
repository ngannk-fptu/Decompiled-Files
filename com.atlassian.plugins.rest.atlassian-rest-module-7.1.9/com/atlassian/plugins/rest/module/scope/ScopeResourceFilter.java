/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.scope.ScopeManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.rest.module.scope;

import com.atlassian.plugin.scope.ScopeManager;
import com.atlassian.plugins.rest.module.RestModuleDescriptor;
import com.atlassian.plugins.rest.module.scope.ScopeCheckFailedException;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScopeResourceFilter
implements ResourceFilter,
ContainerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(ScopeResourceFilter.class);
    private final RestModuleDescriptor descriptor;
    private final ScopeManager scopeManager;
    private static final Response.Status failureStatus = Response.Status.PRECONDITION_FAILED;

    public ScopeResourceFilter(ScopeManager scopeManager, RestModuleDescriptor descriptor) {
        this.descriptor = descriptor;
        this.scopeManager = scopeManager;
    }

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        log.debug("Applying scope filter for {} ", (Object)this.descriptor);
        Boolean permit = this.descriptor.getScopeKey().map(arg_0 -> ((ScopeManager)this.scopeManager).isScopeActive(arg_0)).orElse(true);
        if (!permit.booleanValue()) {
            log.debug("Scope is not active for matching descriptor {}", (Object)this.descriptor);
            throw new ScopeCheckFailedException(failureStatus);
        }
        return request;
    }

    @Override
    public ContainerRequestFilter getRequestFilter() {
        return this;
    }

    @Override
    public ContainerResponseFilter getResponseFilter() {
        return null;
    }
}

