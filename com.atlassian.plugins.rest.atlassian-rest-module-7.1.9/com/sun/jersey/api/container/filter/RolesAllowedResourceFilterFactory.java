/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.security.DenyAll
 *  javax.annotation.security.PermitAll
 *  javax.annotation.security.RolesAllowed
 */
package com.sun.jersey.api.container.filter;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import java.util.Collections;
import java.util.List;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public class RolesAllowedResourceFilterFactory
implements ResourceFilterFactory {
    @Context
    private SecurityContext sc;

    @Override
    public List<ResourceFilter> create(AbstractMethod am) {
        if (am.isAnnotationPresent(DenyAll.class)) {
            return Collections.singletonList(new Filter());
        }
        RolesAllowed ra = am.getAnnotation(RolesAllowed.class);
        if (ra != null) {
            return Collections.singletonList(new Filter(ra.value()));
        }
        if (am.isAnnotationPresent(PermitAll.class)) {
            return null;
        }
        ra = am.getResource().getAnnotation(RolesAllowed.class);
        if (ra != null) {
            return Collections.singletonList(new Filter(ra.value()));
        }
        return null;
    }

    private class Filter
    implements ResourceFilter,
    ContainerRequestFilter {
        private final boolean denyAll;
        private final String[] rolesAllowed;

        protected Filter() {
            this.denyAll = true;
            this.rolesAllowed = null;
        }

        protected Filter(String[] rolesAllowed) {
            this.denyAll = false;
            this.rolesAllowed = rolesAllowed != null ? rolesAllowed : new String[]{};
        }

        @Override
        public ContainerRequestFilter getRequestFilter() {
            return this;
        }

        @Override
        public ContainerResponseFilter getResponseFilter() {
            return null;
        }

        @Override
        public ContainerRequest filter(ContainerRequest request) {
            if (!this.denyAll) {
                for (String role : this.rolesAllowed) {
                    if (!RolesAllowedResourceFilterFactory.this.sc.isUserInRole(role)) continue;
                    return request;
                }
            }
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
    }
}

