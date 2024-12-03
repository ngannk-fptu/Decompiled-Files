/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.spi.container.ContainerRequest
 *  com.sun.jersey.spi.container.ContainerRequestFilter
 *  com.sun.jersey.spi.container.ContainerResponse
 *  com.sun.jersey.spi.container.ContainerResponseFilter
 *  com.sun.jersey.spi.container.ResourceFilter
 *  javax.ws.rs.core.SecurityContext
 */
package com.atlassian.crowd.plugin.rest.provider;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import java.util.Optional;
import javax.ws.rs.core.SecurityContext;

public class RestUsernameHeaderFilter
implements ContainerResponseFilter,
ResourceFilter {
    public ContainerResponse filter(ContainerRequest containerRequest, ContainerResponse containerResponse) {
        Optional<SecurityContext> securityContext = Optional.ofNullable(containerRequest.getSecurityContext());
        securityContext.map(SecurityContext::getUserPrincipal).ifPresent(principal -> containerResponse.getHttpHeaders().putSingle((Object)"X-AUSERNAME", (Object)principal.getName()));
        return containerResponse;
    }

    public ContainerRequestFilter getRequestFilter() {
        return null;
    }

    public ContainerResponseFilter getResponseFilter() {
        return this;
    }
}

