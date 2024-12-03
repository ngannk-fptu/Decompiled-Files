/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.google.common.base.Preconditions
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.CacheControl
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$ResponseBuilder
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.streams.internal.applinks;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.streams.internal.applinks.ApplicationLinkServiceExtensions;
import com.google.common.base.Preconditions;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

@Path(value="/applinks/status")
public class ApplinkAuthStatusResource {
    private static final CacheControl NO_CACHE = new CacheControl();
    private final ApplicationLinkService appLinkService;
    private final ApplicationLinkServiceExtensions appLinkServiceExtensions;

    public ApplinkAuthStatusResource(ApplicationLinkService appLinkService, ApplicationLinkServiceExtensions appLinkServiceExtensions) {
        this.appLinkService = (ApplicationLinkService)Preconditions.checkNotNull((Object)appLinkService, (Object)"appLinkService");
        this.appLinkServiceExtensions = (ApplicationLinkServiceExtensions)Preconditions.checkNotNull((Object)appLinkServiceExtensions, (Object)"appLinkServiceExtensions");
    }

    @GET
    @Path(value="/{applinkId}")
    @Produces(value={"application/json"})
    public Response getApplinkAvailability(@PathParam(value="applinkId") String applinkId) {
        ApplicationLink appLink;
        try {
            appLink = this.appLinkService.getApplicationLink(new ApplicationId(applinkId));
        }
        catch (TypeNotInstalledException e) {
            appLink = null;
        }
        Response.ResponseBuilder response = appLink == null ? Response.status((Response.Status)Response.Status.NOT_FOUND) : (this.appLinkServiceExtensions.isAuthorised(appLink) ? Response.ok() : Response.status((Response.Status)Response.Status.FORBIDDEN));
        return response.cacheControl(NO_CACHE).build();
    }

    static {
        NO_CACHE.setNoStore(true);
        NO_CACHE.setNoCache(true);
    }
}

