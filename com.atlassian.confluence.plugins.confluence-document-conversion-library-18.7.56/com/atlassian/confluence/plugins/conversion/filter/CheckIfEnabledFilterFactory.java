/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.capabilities.api.CapabilityService
 *  com.sun.jersey.api.model.AbstractMethod
 *  com.sun.jersey.spi.container.ContainerRequest
 *  com.sun.jersey.spi.container.ContainerRequestFilter
 *  com.sun.jersey.spi.container.ContainerResponse
 *  com.sun.jersey.spi.container.ContainerResponseFilter
 *  com.sun.jersey.spi.container.ResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilterFactory
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.conversion.filter;

import com.atlassian.confluence.plugins.conversion.annotation.CheckIfServiceIsEnabled;
import com.atlassian.confluence.plugins.conversion.impl.ConfigurationProperties;
import com.atlassian.plugins.capabilities.api.CapabilityService;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class CheckIfEnabledFilterFactory
implements ResourceFilterFactory {
    private final CapabilityService capabilityService;
    private final List<ResourceFilter> filters = Collections.singletonList(new CheckIfEnabledFilter());

    public CheckIfEnabledFilterFactory(CapabilityService capabilityService) {
        this.capabilityService = capabilityService;
    }

    public List<ResourceFilter> create(AbstractMethod am) {
        if (am.isAnnotationPresent(CheckIfServiceIsEnabled.class)) {
            return this.filters;
        }
        return Collections.emptyList();
    }

    private class CheckIfEnabledFilter
    implements ResourceFilter {
        private final ContainerResponseFilter containerResponseFilter = new ContainerResponseFilter(){

            public ContainerResponse filter(ContainerRequest request, ContainerResponse response) {
                if (CheckIfEnabledFilterFactory.this.capabilityService == null || !CheckIfEnabledFilterFactory.this.capabilityService.getHostApplication().hasCapability(ConfigurationProperties.PROP_CAPABILITY.toString())) {
                    throw new WebApplicationException(Response.Status.GONE);
                }
                return response;
            }
        };

        private CheckIfEnabledFilter() {
        }

        public ContainerRequestFilter getRequestFilter() {
            return null;
        }

        public ContainerResponseFilter getResponseFilter() {
            return this.containerResponseFilter;
        }
    }
}

