/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.base.Strings
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.annotation.Nonnull
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.applinks.internal.rest.capabilities;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.internal.capabilities.ApplinksCapabilitiesService;
import com.atlassian.applinks.internal.common.capabilities.ApplinksCapabilities;
import com.atlassian.applinks.internal.common.capabilities.RemoteApplicationCapabilities;
import com.atlassian.applinks.internal.common.capabilities.RemoteCapabilitiesService;
import com.atlassian.applinks.internal.common.exception.ServiceException;
import com.atlassian.applinks.internal.common.rest.util.RestApplicationIdParser;
import com.atlassian.applinks.internal.common.rest.util.RestEnumParser;
import com.atlassian.applinks.internal.common.rest.util.RestResponses;
import com.atlassian.applinks.internal.rest.RestUrlBuilder;
import com.atlassian.applinks.internal.rest.interceptor.NoCacheHeaderInterceptor;
import com.atlassian.applinks.internal.rest.interceptor.ServiceExceptionInterceptor;
import com.atlassian.applinks.internal.rest.model.capabilities.RestRemoteApplicationCapabilities;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.base.Strings;
import com.sun.jersey.spi.resource.Singleton;
import java.io.Serializable;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@AnonymousAllowed
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="capabilities")
@Singleton
@InterceptorChain(value={ServiceExceptionInterceptor.class, NoCacheHeaderInterceptor.class})
public class ApplinksCapabilitiesResource {
    public static final String CONTEXT = "capabilities";
    private static final String PARAM_APPLINK_ID = "applinkId";
    private static final String PARAM_CAPABILITY = "capability";
    private static final String PARAM_MAX_AGE = "maxAge";
    private static final String PARAM_TIME_UNIT = "timeUnit";
    private static final String NOT_FOUND_MESSAGE_KEY = "applinks.rest.capabilities.notfound";
    private final ApplinksCapabilitiesService capabilitiesService;
    private final RemoteCapabilitiesService remoteCapabilitiesService;
    private final I18nResolver i18nResolver;
    private final RestEnumParser<ApplinksCapabilities> restEnumParser;
    private final RestEnumParser<TimeUnit> timeUnitParser;

    public ApplinksCapabilitiesResource(ApplinksCapabilitiesService capabilitiesService, RemoteCapabilitiesService remoteCapabilitiesService, I18nResolver i18nResolver) {
        this.capabilitiesService = capabilitiesService;
        this.remoteCapabilitiesService = remoteCapabilitiesService;
        this.i18nResolver = i18nResolver;
        this.restEnumParser = new RestEnumParser<ApplinksCapabilities>(ApplinksCapabilities.class, i18nResolver, NOT_FOUND_MESSAGE_KEY, Response.Status.NOT_FOUND);
        this.timeUnitParser = new RestEnumParser<TimeUnit>(TimeUnit.class, i18nResolver);
    }

    @Nonnull
    public static RestUrlBuilder capabilitiesUrl() {
        return new RestUrlBuilder().addPath(CONTEXT);
    }

    @Nonnull
    public static RestUrlBuilder capabilityUrl(@Nonnull ApplinksCapabilities capability) {
        return ApplinksCapabilitiesResource.capabilitiesUrl().addPath(capability.name());
    }

    @GET
    public Response getAllCapabilities() {
        return Response.ok(this.capabilitiesService.getCapabilities()).build();
    }

    @GET
    @Path(value="/{capability}")
    public Response hasCapability(@PathParam(value="capability") String capabilityName) {
        capabilityName = Strings.nullToEmpty((String)capabilityName);
        ApplinksCapabilities capability = this.restEnumParser.parseEnumParameter(capabilityName, PARAM_CAPABILITY);
        return this.capabilitiesService.getCapabilities().contains((Object)capability) ? Response.ok(EnumSet.of(capability)).build() : RestResponses.error(Response.Status.NOT_FOUND, this.i18nResolver.getText(NOT_FOUND_MESSAGE_KEY, new Serializable[]{capabilityName}));
    }

    @GET
    @Path(value="/remote/{applinkId}")
    public Response getRemoteCapabilities(@PathParam(value="applinkId") String id, @QueryParam(value="maxAge") Long maxAge, @QueryParam(value="timeUnit") String timeUnit) throws ServiceException {
        ApplicationId applicationId = RestApplicationIdParser.parseApplicationId(id);
        RemoteApplicationCapabilities capabilities = maxAge != null ? this.remoteCapabilitiesService.getCapabilities(applicationId, (long)maxAge, this.timeUnitParser.parseEnumParameter(timeUnit, TimeUnit.MINUTES, PARAM_TIME_UNIT)) : this.remoteCapabilitiesService.getCapabilities(applicationId);
        return Response.ok((Object)new RestRemoteApplicationCapabilities(capabilities)).build();
    }
}

