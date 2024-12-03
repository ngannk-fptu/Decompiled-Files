/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.spi.Manifest
 *  com.atlassian.applinks.spi.link.ApplicationLinkDetails
 *  com.atlassian.applinks.spi.link.MutableApplicationLink
 *  com.atlassian.applinks.spi.link.MutatingApplicationLinkService
 *  com.atlassian.applinks.spi.manifest.ApplicationStatus
 *  com.atlassian.applinks.spi.manifest.ManifestNotFoundException
 *  com.atlassian.applinks.spi.manifest.ManifestRetriever
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.util.RestUrlBuilder
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.user.UserManager
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.core.rest.ui;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.core.InternalTypeAccessor;
import com.atlassian.applinks.core.manifest.AppLinksManifestDownloader;
import com.atlassian.applinks.core.rest.AbstractResource;
import com.atlassian.applinks.core.rest.auth.AdminApplicationLinksInterceptor;
import com.atlassian.applinks.core.rest.context.ContextInterceptor;
import com.atlassian.applinks.core.rest.util.RestUtil;
import com.atlassian.applinks.core.util.URIUtil;
import com.atlassian.applinks.internal.rest.interceptor.NoCacheHeaderInterceptor;
import com.atlassian.applinks.spi.Manifest;
import com.atlassian.applinks.spi.link.ApplicationLinkDetails;
import com.atlassian.applinks.spi.link.MutableApplicationLink;
import com.atlassian.applinks.spi.link.MutatingApplicationLinkService;
import com.atlassian.applinks.spi.manifest.ApplicationStatus;
import com.atlassian.applinks.spi.manifest.ManifestNotFoundException;
import com.atlassian.applinks.spi.manifest.ManifestRetriever;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.util.RestUrlBuilder;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.user.UserManager;
import com.sun.jersey.spi.resource.Singleton;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import java.io.Serializable;
import java.net.URI;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="relocateApplicationlink")
@Api
@Consumes(value={"application/xml", "application/json"})
@Produces(value={"application/xml", "application/json"})
@Singleton
@InterceptorChain(value={ContextInterceptor.class, AdminApplicationLinksInterceptor.class, NoCacheHeaderInterceptor.class})
public class RelocateApplicationLinkUIResource
extends AbstractResource {
    private static final Logger LOG = LoggerFactory.getLogger(RelocateApplicationLinkUIResource.class);
    private final MutatingApplicationLinkService applicationLinkService;
    private final ManifestRetriever manifestRetriever;
    private final AppLinksManifestDownloader manifestDownloader;
    private final I18nResolver i18nResolver;
    private final UserManager userManager;

    public RelocateApplicationLinkUIResource(RestUrlBuilder restUrlBuilder, MutatingApplicationLinkService applicationLinkService, I18nResolver i18nResolver, ManifestRetriever manifestRetriever, AppLinksManifestDownloader manifestDownloader, InternalTypeAccessor internalTypeAccessor, RequestFactory requestFactory, UserManager userManager) {
        super(restUrlBuilder, internalTypeAccessor, requestFactory, applicationLinkService);
        this.applicationLinkService = applicationLinkService;
        this.i18nResolver = i18nResolver;
        this.manifestRetriever = manifestRetriever;
        this.manifestDownloader = manifestDownloader;
        this.userManager = userManager;
    }

    @POST
    @ApiOperation(value="Update the RPC URL of the remote application", authorizations={@Authorization(value="SysAdmin")})
    @ApiResponses(value={@ApiResponse(code=204, message="Updated successfully (this is the only mutating operation)"), @ApiResponse(code=400, message="The application running at the new URL has a different application type than the existing applink"), @ApiResponse(code=404, message="The specified server ID doesn't have an application link on this server"), @ApiResponse(code=409, message="The server located at the specified URL is not responding, resubmit with \"?nowarning=true\" to force the update (display URL will be set to RPC URL)")})
    @Path(value="{applinkId}")
    public Response relocate(@PathParam(value="applinkId") String applicationId, @QueryParam(value="newUrl") String urlString, @QueryParam(value="nowarning") boolean nowarning) throws TypeNotInstalledException {
        URI url;
        if (!this.userManager.isSystemAdmin(this.userManager.getRemoteUsername())) {
            return RestUtil.forbidden(this.i18nResolver.getText("applinks.error.only.sysadmin.operation"));
        }
        MutableApplicationLink link = this.applicationLinkService.getApplicationLink(new ApplicationId(applicationId));
        try {
            url = URIUtil.uncheckedToUri(urlString);
        }
        catch (RuntimeException e) {
            return RestUtil.serverError(e.getMessage());
        }
        if (link == null) {
            return RestUtil.notFound(this.i18nResolver.getText("applinks.notfound", new Serializable[]{applicationId}));
        }
        if (this.manifestRetriever.getApplicationStatus(url, link.getType()) == ApplicationStatus.UNAVAILABLE) {
            if (nowarning) {
                return this.update(link, url, url);
            }
            return Response.status((int)409).build();
        }
        URI displayUrl = url;
        try {
            Manifest manifest = this.manifestDownloader.download(url);
            if (!this.typeAccessor.loadApplicationType(manifest.getTypeId()).equals(link.getType())) {
                return Response.status((int)400).entity((Object)this.i18nResolver.getText("applinks.error.relocate.type", new Serializable[]{urlString, this.i18nResolver.getText(this.typeAccessor.loadApplicationType(manifest.getTypeId()).getI18nKey()), this.i18nResolver.getText(link.getType().getI18nKey())})).build();
            }
            displayUrl = manifest.getUrl();
        }
        catch (ManifestNotFoundException manifestNotFoundException) {
            // empty catch block
        }
        return this.update(link, url, displayUrl);
    }

    private Response update(MutableApplicationLink link, URI rpcUrl, URI displayUrl) {
        link.update(ApplicationLinkDetails.builder((ApplicationLink)link).rpcUrl(rpcUrl).displayUrl(displayUrl).build());
        LOG.info("Changed RPC URL from {} to {} and display URL from {} to {} for ApplicationLink {} .", new Object[]{link.getRpcUrl(), rpcUrl, link.getDisplayUrl(), displayUrl, link.getId()});
        return RestUtil.noContent();
    }
}

