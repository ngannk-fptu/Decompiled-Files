/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.security.AnonymousSiteAccess
 *  com.atlassian.plugins.rest.common.security.UnlicensedSiteAccess
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.ResponseException
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.applinks.core.rest;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.core.rest.client.EntityRetriever;
import com.atlassian.applinks.core.rest.context.ContextInterceptor;
import com.atlassian.applinks.core.rest.model.ReferenceEntityList;
import com.atlassian.applinks.core.rest.util.RestUtil;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.rest.interceptor.NoCacheHeaderInterceptor;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.security.AnonymousSiteAccess;
import com.atlassian.plugins.rest.common.security.UnlicensedSiteAccess;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.ResponseException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="entities")
@Consumes(value={"application/xml", "application/json"})
@Produces(value={"application/xml", "application/json"})
@InterceptorChain(value={ContextInterceptor.class, NoCacheHeaderInterceptor.class})
public class EntityResource {
    public static final String CONTEXT = "entities";
    private final InternalHostApplication internalHostApplication;
    private final ApplicationLinkService applicationLinkService;
    private final EntityRetriever entityRetriever;
    private final I18nResolver i18nResolver;

    public EntityResource(InternalHostApplication internalHostApplication, ApplicationLinkService applicationLinkService, EntityRetriever entityRetriever, I18nResolver i18nResolver) {
        this.internalHostApplication = internalHostApplication;
        this.applicationLinkService = applicationLinkService;
        this.entityRetriever = entityRetriever;
        this.i18nResolver = i18nResolver;
    }

    @GET
    @AnonymousSiteAccess
    public Response listEntities() {
        Iterable refs = this.internalHostApplication.getLocalEntities();
        return RestUtil.ok(new ReferenceEntityList(refs));
    }

    @GET
    @Path(value="{applinkId}")
    @UnlicensedSiteAccess
    public Response listEntities(@PathParam(value="applinkId") String applicationId) {
        return this.listEntities(applicationId, false);
    }

    private Response listEntities(String applicationId, boolean useAnonymousAccess) {
        Response response;
        ApplicationLink link;
        try {
            link = this.applicationLinkService.getApplicationLink(new ApplicationId(applicationId));
        }
        catch (TypeNotInstalledException e) {
            return RestUtil.badRequest(String.format("Failed to load application %s as the %s type is not installed", applicationId, e.getType()));
        }
        if (link == null) {
            response = RestUtil.notFound("No application link found with id: " + applicationId);
        } else {
            try {
                response = useAnonymousAccess ? RestUtil.ok(new ReferenceEntityList(this.entityRetriever.getEntitiesForAnonymousAccess(link))) : RestUtil.ok(new ReferenceEntityList(this.entityRetriever.getEntities(link)));
            }
            catch (CredentialsRequiredException e) {
                response = RestUtil.credentialsRequired(this.i18nResolver);
            }
            catch (ResponseException e) {
                response = RestUtil.serverError(e.toString());
            }
        }
        return response;
    }

    @GET
    @Path(value="anonymous/{applinkId}")
    @UnlicensedSiteAccess
    public Response listEntitiesForAnonymousAccess(@PathParam(value="applinkId") String applicationId) {
        return this.listEntities(applicationId, true);
    }
}

