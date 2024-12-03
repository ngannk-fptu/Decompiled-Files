/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.spi.container.ContainerRequestFilter
 *  com.sun.jersey.spi.container.ContainerResponseFilter
 *  com.sun.jersey.spi.container.ResourceFilter
 *  javax.ws.rs.core.MultivaluedMap
 */
package com.atlassian.confluence.plugins.rest.jersey.cachecontrol;

import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import javax.ws.rs.core.MultivaluedMap;

public class PreventCachingResponseFilter
implements ResourceFilter {
    public static final PreventCachingResponseFilter INSTANCE = new PreventCachingResponseFilter();

    public ContainerRequestFilter getRequestFilter() {
        return null;
    }

    public ContainerResponseFilter getResponseFilter() {
        return (request, response) -> {
            MultivaluedMap httpHeaders = response.getHttpHeaders();
            httpHeaders.putSingle((Object)"Cache-Control", (Object)"no-store");
            httpHeaders.putSingle((Object)"Expires", (Object)"Thu, 01 Jan 1970 00:00:00 GMT");
            return response;
        };
    }
}

