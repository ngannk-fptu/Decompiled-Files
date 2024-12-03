/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.container.filter;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.server.impl.uri.UriHelper;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import java.net.URI;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

public class NormalizeFilter
implements ContainerRequestFilter {
    @Context
    ResourceConfig resourceConfig;

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        URI normalizedUri;
        URI uri;
        if (this.resourceConfig.getFeature("com.sun.jersey.config.feature.NormalizeURI") && (uri = request.getRequestUri()) != (normalizedUri = UriHelper.normalize(uri, !this.resourceConfig.getFeature("com.sun.jersey.config.feature.CanonicalizeURIPath")))) {
            if (this.resourceConfig.getFeature("com.sun.jersey.config.feature.Redirect")) {
                throw new WebApplicationException(Response.temporaryRedirect(normalizedUri).build());
            }
            URI baseUri = UriHelper.normalize(request.getBaseUri(), !this.resourceConfig.getFeature("com.sun.jersey.config.feature.CanonicalizeURIPath"));
            request.setUris(baseUri, normalizedUri);
        }
        return request;
    }
}

