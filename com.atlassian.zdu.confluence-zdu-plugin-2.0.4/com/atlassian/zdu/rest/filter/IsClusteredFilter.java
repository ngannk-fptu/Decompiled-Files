/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.AuthorisationException
 *  com.sun.jersey.spi.container.ContainerRequestFilter
 *  com.sun.jersey.spi.container.ContainerResponseFilter
 *  com.sun.jersey.spi.container.ResourceFilter
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.zdu.rest.filter;

import com.atlassian.plugins.rest.common.security.AuthorisationException;
import com.atlassian.zdu.internal.api.ClusterManagerAdapter;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class IsClusteredFilter
implements ResourceFilter {
    private final ClusterManagerAdapter clusterManagerAdapter;

    public IsClusteredFilter(ClusterManagerAdapter clusterManagerAdapter) {
        this.clusterManagerAdapter = clusterManagerAdapter;
    }

    public ContainerRequestFilter getRequestFilter() {
        return containerRequest -> {
            if (!this.clusterManagerAdapter.isClustered()) {
                throw new AuthorisationException("Rolling upgrades feature requires a clustered installation.");
            }
            return containerRequest;
        };
    }

    public ContainerResponseFilter getResponseFilter() {
        return null;
    }
}

