/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.applinks.internal.rest.feature;

import com.atlassian.applinks.core.rest.context.ContextInterceptor;
import com.atlassian.applinks.internal.common.exception.ServiceException;
import com.atlassian.applinks.internal.feature.FeatureDiscoveryService;
import com.atlassian.applinks.internal.rest.interceptor.NoCacheHeaderInterceptor;
import com.atlassian.applinks.internal.rest.interceptor.ServiceExceptionInterceptor;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import java.util.Collections;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@AnonymousAllowed
@Path(value="feature-discovery")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@InterceptorChain(value={ContextInterceptor.class, ServiceExceptionInterceptor.class, NoCacheHeaderInterceptor.class})
public class FeatureDiscoveryResource {
    private static final String FEATURE_KEY_PARAM = "featureKey";
    private static final String FEATURE_KEY_URI_TEMPLATE = "{featureKey}";
    private final FeatureDiscoveryService featureDiscoveryService;

    public FeatureDiscoveryResource(FeatureDiscoveryService featureDiscoveryService) {
        this.featureDiscoveryService = featureDiscoveryService;
    }

    @GET
    @Path(value="{featureKey}")
    public Response isDiscovered(@PathParam(value="featureKey") String featureKey) throws ServiceException {
        return this.featureDiscoveryService.isDiscovered(featureKey) ? Response.ok(FeatureDiscoveryResource.featureKeyRestEntity(featureKey)).build() : Response.status((Response.Status)Response.Status.NOT_FOUND).entity(FeatureDiscoveryResource.featureKeyRestEntity(featureKey)).build();
    }

    @GET
    public Response getAllDiscoveredFeatures() throws ServiceException {
        return Response.ok(this.featureDiscoveryService.getAllDiscoveredFeatureKeys()).build();
    }

    @PUT
    @Path(value="{featureKey}")
    public Response discover(@PathParam(value="featureKey") String featureKey) throws ServiceException {
        this.featureDiscoveryService.discover(featureKey);
        return Response.ok(FeatureDiscoveryResource.featureKeyRestEntity(featureKey)).build();
    }

    private static Set<String> featureKeyRestEntity(String featureKey) {
        return Collections.singleton(featureKey);
    }
}

