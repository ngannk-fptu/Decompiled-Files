/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.api.core.HttpContext
 *  com.sun.jersey.core.spi.component.ComponentContext
 *  com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable
 *  com.sun.jersey.spi.inject.Injectable
 *  com.sun.jersey.spi.inject.PerRequestTypeInjectableProvider
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.MultivaluedMap
 *  javax.ws.rs.ext.Provider
 *  org.apache.commons.lang3.math.NumberUtils
 */
package com.atlassian.plugins.authentication.impl.rest;

import com.atlassian.plugins.authentication.impl.rest.model.RestPageRequest;
import com.atlassian.plugins.authentication.impl.rest.model.SimpleRestPageRequest;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.PerRequestTypeInjectableProvider;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import org.apache.commons.lang3.math.NumberUtils;

@Provider
public class RestPageRequestProvider
extends PerRequestTypeInjectableProvider<Context, RestPageRequest> {
    public static final int DEFAULT_START = 0;
    public static final int DEFAULT_LIMIT = 50;
    public static final String START_PARAM = "start";
    public static final String LIMIT_PARAM = "limit";

    public RestPageRequestProvider() {
        super(RestPageRequest.class);
    }

    public Injectable<RestPageRequest> getInjectable(ComponentContext componentContext, Context context) {
        return new AbstractHttpContextInjectable<RestPageRequest>(){

            public RestPageRequest getValue(HttpContext httpContext) {
                MultivaluedMap params = httpContext.getRequest().getQueryParameters();
                int start = NumberUtils.toInt((String)((String)params.getFirst((Object)RestPageRequestProvider.START_PARAM)), (int)0);
                int limit = NumberUtils.toInt((String)((String)params.getFirst((Object)RestPageRequestProvider.LIMIT_PARAM)), (int)50);
                return new SimpleRestPageRequest(start, limit);
            }
        };
    }
}

