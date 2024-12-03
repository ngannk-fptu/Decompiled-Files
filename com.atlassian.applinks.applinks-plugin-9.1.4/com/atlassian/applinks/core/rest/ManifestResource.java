/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.Manifest
 *  com.atlassian.applinks.spi.manifest.ManifestNotFoundException
 *  com.atlassian.applinks.spi.manifest.ManifestRetriever
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  javax.annotation.Nonnull
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.core.rest;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.core.AppLinkPluginUtil;
import com.atlassian.applinks.core.rest.context.ContextInterceptor;
import com.atlassian.applinks.core.rest.model.ManifestEntity;
import com.atlassian.applinks.core.rest.util.RestUtil;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.rest.RestUrlBuilder;
import com.atlassian.applinks.internal.rest.interceptor.CorsInterceptor;
import com.atlassian.applinks.internal.rest.interceptor.NoCacheHeaderInterceptor;
import com.atlassian.applinks.spi.Manifest;
import com.atlassian.applinks.spi.manifest.ManifestNotFoundException;
import com.atlassian.applinks.spi.manifest.ManifestRetriever;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.lang.reflect.InvocationTargetException;
import javax.annotation.Nonnull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Api
@Path(value="manifest")
@Consumes(value={"application/xml", "application/json"})
@Produces(value={"application/xml", "application/json"})
@InterceptorChain(value={ContextInterceptor.class, NoCacheHeaderInterceptor.class, CorsInterceptor.class})
public class ManifestResource {
    private static final Logger LOG = LoggerFactory.getLogger(ManifestResource.class);
    public static final String CONTEXT = "manifest";
    private final InternalHostApplication internalHostApplication;
    private final ManifestRetriever manifestRetriever;
    private final ApplicationLinkService applicationLinkService;
    private final AppLinkPluginUtil pluginUtil;
    private final ApplicationProperties applicationProperties;
    private final UserManager userManager;

    @Nonnull
    public static RestUrlBuilder manifestUrl() {
        return new RestUrlBuilder().addPath(CONTEXT);
    }

    public ManifestResource(InternalHostApplication internalHostApplication, ManifestRetriever manifestRetriever, ApplicationLinkService applicationLinkService, ApplicationProperties applicationProperties, AppLinkPluginUtil pluginUtil, UserManager userManager) {
        this.internalHostApplication = internalHostApplication;
        this.manifestRetriever = manifestRetriever;
        this.applicationLinkService = applicationLinkService;
        this.applicationProperties = applicationProperties;
        this.pluginUtil = pluginUtil;
        this.userManager = userManager;
    }

    @GET
    @ApiOperation(value="Returns the manifest for this application", response=ManifestEntity.class)
    @ApiResponse(code=200, message="Successful")
    @AnonymousAllowed
    public Response getManifest() {
        return RestUtil.ok(new ManifestEntity(this.internalHostApplication, this.applicationProperties, this.pluginUtil));
    }

    @GET
    @ApiOperation(value="Returns the manifest for an applink with a given ID", response=ManifestEntity.class)
    @ApiResponses(value={@ApiResponse(code=404, message="Manifest not found on remote host"), @ApiResponse(code=400, message="No application link exists with provided ID"), @ApiResponse(code=200, message="Successful")})
    @Path(value="{id}")
    public Response getManifestFor(@PathParam(value="id") String id) throws TypeNotInstalledException {
        Manifest manifest;
        boolean includeId;
        ApplicationId applicationId = new ApplicationId(id);
        ApplicationLink applicationLink = null;
        UserProfile remoteUser = this.userManager.getRemoteUser();
        try {
            if (remoteUser == null) {
                includeId = (Boolean)this.userManager.getClass().getMethod("isAnonymousAccessEnabled", new Class[0]).invoke((Object)this.userManager, new Object[0]);
            } else {
                boolean isLicensed = (Boolean)this.userManager.getClass().getMethod("isLicensed", UserKey.class).invoke((Object)this.userManager, remoteUser.getUserKey());
                boolean isLimitedUnlicensedUser = (Boolean)this.userManager.getClass().getMethod("isLimitedUnlicensedUser", UserKey.class).invoke((Object)this.userManager, remoteUser.getUserKey());
                includeId = isLicensed || isLimitedUnlicensedUser;
            }
        }
        catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
            LOG.debug("Need to update SAL UserManger version to 4.8 or 5.1. Error Message: {}", (Object)ex.getMessage());
            includeId = remoteUser != null;
        }
        try {
            if (includeId) {
                applicationLink = this.applicationLinkService.getApplicationLink(applicationId);
            }
        }
        catch (TypeNotInstalledException ex) {
            // empty catch block
        }
        if (applicationLink == null) {
            return RestUtil.badRequest(String.format("No application link with id %s", applicationId));
        }
        try {
            manifest = this.manifestRetriever.getManifest(applicationLink.getRpcUrl(), applicationLink.getType());
        }
        catch (ManifestNotFoundException e) {
            return RestUtil.notFound(String.format("Couldn't retrieve manifest for link with id %s", applicationId));
        }
        return this.response(manifest);
    }

    private Response response(Manifest manifest) {
        return RestUtil.ok(new ManifestEntity(manifest));
    }
}

