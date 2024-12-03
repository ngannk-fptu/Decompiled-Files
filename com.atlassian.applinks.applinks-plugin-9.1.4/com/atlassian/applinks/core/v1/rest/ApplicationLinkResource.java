/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.applinks.spi.link.ApplicationLinkDetails
 *  com.atlassian.applinks.spi.link.MutableApplicationLink
 *  com.atlassian.applinks.spi.link.MutatingApplicationLinkService
 *  com.atlassian.applinks.spi.link.ReciprocalActionException
 *  com.atlassian.applinks.spi.manifest.ManifestNotFoundException
 *  com.atlassian.applinks.spi.manifest.ManifestRetriever
 *  com.atlassian.plugins.rest.common.Link
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.util.RestUrlBuilder
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoNotRequired
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 */
package com.atlassian.applinks.core.v1.rest;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.core.InternalTypeAccessor;
import com.atlassian.applinks.core.rest.AbstractResource;
import com.atlassian.applinks.core.rest.auth.AdminApplicationLinksInterceptor;
import com.atlassian.applinks.core.rest.context.ContextInterceptor;
import com.atlassian.applinks.core.rest.model.ApplicationLinkEntity;
import com.atlassian.applinks.core.rest.model.ApplicationLinkListEntity;
import com.atlassian.applinks.core.rest.util.RestUtil;
import com.atlassian.applinks.internal.rest.interceptor.NoCacheHeaderInterceptor;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.applinks.spi.link.ApplicationLinkDetails;
import com.atlassian.applinks.spi.link.MutableApplicationLink;
import com.atlassian.applinks.spi.link.MutatingApplicationLinkService;
import com.atlassian.applinks.spi.link.ReciprocalActionException;
import com.atlassian.applinks.spi.manifest.ManifestNotFoundException;
import com.atlassian.applinks.spi.manifest.ManifestRetriever;
import com.atlassian.plugins.rest.common.Link;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.util.RestUrlBuilder;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoNotRequired;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.sun.jersey.spi.resource.Singleton;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.Authorization;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Api
@Path(value="applicationlink")
@Consumes(value={"application/xml", "application/json"})
@Produces(value={"application/xml", "application/json"})
@Singleton
@WebSudoRequired
@InterceptorChain(value={ContextInterceptor.class, AdminApplicationLinksInterceptor.class, NoCacheHeaderInterceptor.class})
public class ApplicationLinkResource
extends AbstractResource {
    public static final String CONTEXT = "applicationlink";
    protected final MutatingApplicationLinkService applicationLinkService;
    protected final ManifestRetriever manifestRetriever;
    protected final I18nResolver i18nResolver;
    protected final UserManager userManager;

    public ApplicationLinkResource(MutatingApplicationLinkService applicationLinkService, I18nResolver i18nResolver, InternalTypeAccessor typeAccessor, ManifestRetriever manifestRetriever, RestUrlBuilder restUrlBuilder, RequestFactory requestFactory, UserManager userManager) {
        super(restUrlBuilder, typeAccessor, requestFactory, applicationLinkService);
        this.i18nResolver = i18nResolver;
        this.applicationLinkService = applicationLinkService;
        this.manifestRetriever = manifestRetriever;
        this.userManager = userManager;
    }

    @GET
    @ApiResponse(code=200, message="Successful")
    @ApiOperation(value="Returns a list of all Application Links on this server", response=ApplicationLinkListEntity.class, authorizations={@Authorization(value="Admin")}, responseContainer="Object")
    public Response getApplicationLinks() {
        ArrayList<ApplicationLinkEntity> applicationLinks = new ArrayList<ApplicationLinkEntity>();
        for (ApplicationLink application : this.applicationLinkService.getApplicationLinks()) {
            applicationLinks.add(this.toApplicationLinkEntity(application));
        }
        return RestUtil.ok(new ApplicationLinkListEntity(applicationLinks));
    }

    @GET
    @Path(value="type/{type}")
    public Response getApplicationLinks(@PathParam(value="type") TypeId typeId) {
        ApplicationType type = this.typeAccessor.loadApplicationType(typeId);
        if (type == null) {
            return RestUtil.typeNotInstalled(typeId);
        }
        ArrayList<ApplicationLinkEntity> applicationLinks = new ArrayList<ApplicationLinkEntity>();
        for (ApplicationLink application : this.applicationLinkService.getApplicationLinks(type.getClass())) {
            applicationLinks.add(this.toApplicationLinkEntity(application));
        }
        return RestUtil.ok(new ApplicationLinkListEntity(applicationLinks));
    }

    @GET
    @Path(value="{id}")
    public Response getApplicationLink(@PathParam(value="id") String id) throws TypeNotInstalledException {
        MutableApplicationLink application = this.applicationLinkService.getApplicationLink(new ApplicationId(id));
        return RestUtil.ok(this.toApplicationLinkEntity((ApplicationLink)application));
    }

    @GET
    @Path(value="primary/{type}")
    public Response getPrimaryApplicationLink(@PathParam(value="type") TypeId typeId) {
        ApplicationType type = this.typeAccessor.loadApplicationType(typeId);
        if (type == null) {
            return RestUtil.typeNotInstalled(typeId);
        }
        ApplicationLink application = this.applicationLinkService.getPrimaryApplicationLink(type.getClass());
        if (application == null) {
            return RestUtil.notFound(this.i18nResolver.getText("applinks.error.noprimary", new Serializable[]{type.getClass()}));
        }
        return RestUtil.ok(this.toApplicationLinkEntity(application));
    }

    @PUT
    @Path(value="{id}")
    public Response updateApplicationLink(@PathParam(value="id") String id, ApplicationLinkEntity applicationLink) throws TypeNotInstalledException {
        try {
            ApplicationType applicationType = this.typeAccessor.loadApplicationType(applicationLink.getTypeId());
            if (applicationType == null) {
                this.LOG.warn("Couldn't load type {} for application link id {}, name {}, rpc.url {}. Type is not installed?", new Object[]{applicationLink.getTypeId(), applicationLink.getId(), applicationLink.getName(), applicationLink.getRpcUrl()});
                throw new TypeNotInstalledException(applicationLink.getTypeId().get(), applicationLink.getName(), applicationLink.getRpcUrl());
            }
            this.manifestRetriever.getManifest(applicationLink.getRpcUrl(), applicationType);
            ApplicationId applicationId = new ApplicationId(id);
            MutableApplicationLink existing = this.applicationLinkService.getApplicationLink(applicationId);
            if (existing == null) {
                ApplicationType type = this.typeAccessor.loadApplicationType(applicationLink.getTypeId());
                this.applicationLinkService.addApplicationLink(applicationLink.getId(), type, applicationLink.getDetails());
                return RestUtil.created(this.createSelfLinkFor(applicationLink.getId()));
            }
            if (existing.isSystem() && !this.userManager.isSystemAdmin(this.userManager.getRemoteUsername())) {
                return RestUtil.forbidden(this.i18nResolver.getText("applinks.error.only.sysadmin.operation"));
            }
            ApplicationLinkDetails linkDetails = applicationLink.getDetails();
            if (this.applicationLinkService.isNameInUse(linkDetails.getName(), applicationId)) {
                return RestUtil.badRequest(this.i18nResolver.getText("applinks.error.duplicate.name", new Serializable[]{applicationLink.getName()}));
            }
            if (!existing.getRpcUrl().equals(linkDetails.getRpcUrl())) {
                return RestUtil.badRequest(this.i18nResolver.getText("applinks.error.cannot.update.rpcurl"));
            }
            existing.update(linkDetails);
            return RestUtil.updated(this.createSelfLinkFor(applicationLink.getId()));
        }
        catch (ManifestNotFoundException e) {
            return RestUtil.badRequest(this.i18nResolver.getText("applinks.error.url.application.not.reachable", new Serializable[]{applicationLink.getRpcUrl().toString()}));
        }
    }

    @DELETE
    @WebSudoNotRequired
    @Path(value="{id}")
    public Response deleteApplicationLink(@PathParam(value="id") String idString, @QueryParam(value="reciprocate") Boolean reciprocate) throws TypeNotInstalledException {
        ApplicationId id = new ApplicationId(idString);
        MutableApplicationLink link = this.applicationLinkService.getApplicationLink(id);
        if (link == null) {
            return RestUtil.notFound(this.i18nResolver.getText("applinks.notfound", new Serializable[]{id.get()}));
        }
        if (link.isSystem() && !this.userManager.isSystemAdmin(this.userManager.getRemoteUsername())) {
            return RestUtil.forbidden(this.i18nResolver.getText("applinks.error.only.sysadmin.operation"));
        }
        if (reciprocate != null && reciprocate.booleanValue()) {
            try {
                this.applicationLinkService.deleteReciprocatedApplicationLink((ApplicationLink)link);
            }
            catch (CredentialsRequiredException e) {
                return RestUtil.credentialsRequired(this.i18nResolver);
            }
            catch (ReciprocalActionException e) {
                return RestUtil.serverError(this.i18nResolver.getText("applinks.remote.delete.failed", new Serializable[]{e.getMessage()}));
            }
        } else {
            this.applicationLinkService.deleteApplicationLink((ApplicationLink)link);
        }
        return RestUtil.ok(this.i18nResolver.getText("applinks.deleted", new Serializable[]{id.get()}));
    }

    @POST
    @Path(value="primary/{id}")
    public Response makePrimary(@PathParam(value="id") String idString) throws TypeNotInstalledException {
        ApplicationId id = new ApplicationId(idString);
        this.applicationLinkService.makePrimary(id);
        return RestUtil.updated(Link.self((URI)this.applicationLinkService.createSelfLinkFor(id)), this.i18nResolver.getText("applinks.primary", new Serializable[]{id.get()}));
    }
}

