/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.rest.jersey.cachecontrol.PreventCachingResponseFilter
 *  com.sun.jersey.api.model.AbstractMethod
 *  com.sun.jersey.core.header.HttpDateFormat
 *  com.sun.jersey.spi.container.ContainerRequestFilter
 *  com.sun.jersey.spi.container.ContainerResponseFilter
 *  com.sun.jersey.spi.container.ResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilterFactory
 *  javax.ws.rs.core.MultivaluedMap
 */
package com.atlassian.confluence.plugins.rest.filter;

import com.atlassian.confluence.plugins.rest.jersey.cachecontrol.PreventCachingResponseFilter;
import com.atlassian.confluence.plugins.rest.resources.I18nResource;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.core.header.HttpDateFormat;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;

public class PrototypeApiCacheControlResourceFilterFactory
implements ResourceFilterFactory {
    private static final int SHORT_TERM_EXPIRY_SECONDS = 600;
    private static final long SHORT_TERM_EXPIRY_MILLIS = 600000L;
    private static final List<ResourceFilter> PUBLIC_SHORT_TERM = Collections.singletonList(new PublicShortTermResponseFilter());
    private static final List<ResourceFilter> PREVENT_CACHING = Collections.singletonList(PreventCachingResponseFilter.INSTANCE);

    public List<ResourceFilter> create(AbstractMethod abstractMethod) {
        if (abstractMethod.getResource().getResourceClass().equals(I18nResource.class)) {
            return PUBLIC_SHORT_TERM;
        }
        return PREVENT_CACHING;
    }

    private static class PublicShortTermResponseFilter
    implements ResourceFilter {
        private PublicShortTermResponseFilter() {
        }

        public ContainerRequestFilter getRequestFilter() {
            return null;
        }

        public ContainerResponseFilter getResponseFilter() {
            return (request, response) -> {
                MultivaluedMap httpHeaders = response.getHttpHeaders();
                httpHeaders.putSingle((Object)"Cache-Control", (Object)"public, must-revalidate, max-age=600");
                httpHeaders.putSingle((Object)"Expires", (Object)HttpDateFormat.getPreferedDateFormat().format(new Date(System.currentTimeMillis() + 600000L)));
                return response;
            };
        }
    }
}

