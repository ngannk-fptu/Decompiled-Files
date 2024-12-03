/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.applinks.spi.link.MutableApplicationLink
 *  com.atlassian.applinks.spi.link.MutatingApplicationLinkService
 *  com.atlassian.applinks.spi.link.MutatingEntityLinkService
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.util.RestUrlBuilder
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.net.ReturningResponseHandler
 *  com.atlassian.sal.api.user.UserManager
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
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.core.InternalTypeAccessor;
import com.atlassian.applinks.core.rest.AbstractResource;
import com.atlassian.applinks.core.rest.model.PermissionCodeEntity;
import com.atlassian.applinks.core.rest.permission.PermissionCode;
import com.atlassian.applinks.core.rest.util.RestUtil;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.rest.interceptor.NoCacheHeaderInterceptor;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.applinks.spi.link.MutableApplicationLink;
import com.atlassian.applinks.spi.link.MutatingApplicationLinkService;
import com.atlassian.applinks.spi.link.MutatingEntityLinkService;
import com.atlassian.applinks.ui.auth.AdminUIAuthenticator;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.util.RestUrlBuilder;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ReturningResponseHandler;
import com.atlassian.sal.api.user.UserManager;
import java.net.URI;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="permission")
@Consumes(value={"application/xml", "application/json"})
@Produces(value={"application/xml", "application/json"})
@InterceptorChain(value={NoCacheHeaderInterceptor.class})
public class PermissionResource
extends AbstractResource {
    private static final Logger LOG = LoggerFactory.getLogger(PermissionResource.class);
    private final UserManager userManager;
    private final AdminUIAuthenticator uiAuthenticator;
    private final MutatingApplicationLinkService applicationLinkService;
    private final InternalHostApplication internalHostApplication;
    private final MutatingEntityLinkService entityLinkService;

    public PermissionResource(UserManager userManager, AdminUIAuthenticator uiAuthenticator, MutatingApplicationLinkService applicationLinkService, InternalHostApplication internalHostApplication, MutatingEntityLinkService entityLinkService, InternalTypeAccessor typeAccessor, RestUrlBuilder restUrlBuilder, RequestFactory requestFactory) {
        super(restUrlBuilder, typeAccessor, requestFactory, applicationLinkService);
        this.userManager = userManager;
        this.uiAuthenticator = uiAuthenticator;
        this.applicationLinkService = applicationLinkService;
        this.internalHostApplication = internalHostApplication;
        this.entityLinkService = entityLinkService;
    }

    @GET
    @Path(value="delete-application/{id}")
    public Response canDeleteApplicationLink(@PathParam(value="id") ApplicationId id) {
        return this.response(this.hasPermissionToModify(id));
    }

    @GET
    @Path(value="reciprocate-application-delete/{id}")
    public Response canDeleteReciprocalApplicationLink(@PathParam(value="id") ApplicationId id) {
        return this.checkPermissionFor(id, new RestMethodUrlProvider(){

            @Override
            public String getRestMethodUrl(ApplicationLink link) {
                return PermissionResource.this.getUrlFor(RestUtil.getBaseRestUri(link), PermissionResource.class).canDeleteApplicationLink(PermissionResource.this.internalHostApplication.getId()).toString();
            }
        });
    }

    @GET
    @Path(value="create-entity/{id}")
    public Response canCreateEntityLink(@PathParam(value="id") ApplicationId id) {
        return this.response(this.hasPermissionToModify(id));
    }

    @GET
    @Path(value="reciprocate-entity-create/{id}")
    public Response canCreateReciprocalEntityLink(@PathParam(value="id") ApplicationId id) {
        return this.checkPermissionFor(id, new RestMethodUrlProvider(){

            @Override
            public String getRestMethodUrl(ApplicationLink link) {
                return PermissionResource.this.getUrlFor(RestUtil.getBaseRestUri(link), PermissionResource.class).canCreateEntityLink(PermissionResource.this.internalHostApplication.getId()).toString();
            }
        });
    }

    @GET
    @Path(value="delete-entity/{id}/{localType}/{localKey}/{remoteType}/{remoteKey}")
    public Response canDeleteEntityLink(@PathParam(value="id") ApplicationId applicationId, @PathParam(value="localType") TypeId localTypeId, @PathParam(value="localKey") String localKey, @PathParam(value="remoteType") TypeId remoteTypeId, @PathParam(value="remoteKey") String remoteKey) {
        PermissionCode canModifyApp = this.hasPermissionToModify(applicationId);
        if (canModifyApp != PermissionCode.ALLOWED) {
            return this.response(canModifyApp);
        }
        EntityType localType = this.typeAccessor.loadEntityType(localTypeId);
        if (localType == null) {
            return RestUtil.typeNotInstalled(localTypeId);
        }
        EntityType remoteType = this.typeAccessor.loadEntityType(remoteTypeId);
        if (remoteType == null) {
            return RestUtil.typeNotInstalled(remoteTypeId);
        }
        if (this.entityLinkService.getEntityLink(localKey, localType.getClass(), remoteKey, remoteType.getClass(), applicationId) == null) {
            return this.response(PermissionCode.MISSING);
        }
        return this.response(PermissionCode.ALLOWED);
    }

    @GET
    @Path(value="reciprocate-entity-delete/{id}/{localType}/{localKey}/{remoteType}/{remoteKey}")
    public Response canDeleteReciprocalEntityLink(@PathParam(value="id") ApplicationId applicationId, final @PathParam(value="localType") TypeId localTypeId, final @PathParam(value="localKey") String localKey, final @PathParam(value="remoteType") TypeId remoteTypeId, final @PathParam(value="remoteKey") String remoteKey) {
        return this.checkPermissionFor(applicationId, new RestMethodUrlProvider(){

            @Override
            public String getRestMethodUrl(ApplicationLink link) {
                return PermissionResource.this.getUrlFor(RestUtil.getBaseRestUri(link), PermissionResource.class).canDeleteEntityLink(PermissionResource.this.internalHostApplication.getId(), remoteTypeId, remoteKey, localTypeId, localKey).toString();
            }
        });
    }

    private PermissionCode hasPermissionToModify(ApplicationId id) {
        if (this.userManager.getRemoteUsername() == null) {
            return PermissionCode.NO_AUTHENTICATION;
        }
        if (!this.uiAuthenticator.isCurrentUserAdmin()) {
            return PermissionCode.NO_PERMISSION;
        }
        MutableApplicationLink applicationLink = null;
        try {
            applicationLink = this.applicationLinkService.getApplicationLink(id);
        }
        catch (TypeNotInstalledException typeNotInstalledException) {
            // empty catch block
        }
        if (applicationLink == null) {
            return PermissionCode.MISSING;
        }
        return PermissionCode.ALLOWED;
    }

    private Response checkPermissionFor(ApplicationId id, RestMethodUrlProvider restMethodProvider) {
        PermissionCode permissionState;
        MutableApplicationLink tempLink = null;
        try {
            tempLink = this.applicationLinkService.getApplicationLink(id);
        }
        catch (TypeNotInstalledException typeNotInstalledException) {
            // empty catch block
        }
        if (tempLink == null) {
            return RestUtil.notFound(String.format("No link found with id %s", id));
        }
        MutableApplicationLink applicationLink = tempLink;
        ApplicationLinkRequestFactory authenticatedRequestFactory = applicationLink.createAuthenticatedRequestFactory();
        String url = restMethodProvider.getRestMethodUrl((ApplicationLink)applicationLink);
        try {
            permissionState = (PermissionCode)((Object)authenticatedRequestFactory.createRequest(Request.MethodType.GET, url).executeAndReturn((ReturningResponseHandler)new ReturningResponseHandler<com.atlassian.sal.api.net.Response, PermissionCode>((ApplicationLink)applicationLink){
                final /* synthetic */ ApplicationLink val$applicationLink;
                {
                    this.val$applicationLink = applicationLink;
                }

                public PermissionCode handle(com.atlassian.sal.api.net.Response response) throws ResponseException {
                    if (response.getStatusCode() == 200) {
                        try {
                            return ((PermissionCodeEntity)response.getEntity(PermissionCodeEntity.class)).getCode();
                        }
                        catch (Exception e) {
                            throw new ResponseException(String.format("Permission check failed, exception encountered processing response: %s", e));
                        }
                    }
                    if (response.getStatusCode() == 401) {
                        ApplicationLinkRequestFactory authenticatedRequestFactory = this.val$applicationLink.createImpersonatingAuthenticatedRequestFactory();
                        if (authenticatedRequestFactory == null) {
                            authenticatedRequestFactory = this.val$applicationLink.createNonImpersonatingAuthenticatedRequestFactory();
                        }
                        if (authenticatedRequestFactory != null) {
                            LOG.warn("Authentication failed for application link " + this.val$applicationLink + ". Response headers: " + response.getHeaders().toString() + " body: " + response.getResponseBodyAsString());
                        } else if (LOG.isDebugEnabled()) {
                            LOG.debug("Authentication failed for application link " + this.val$applicationLink + ". Response headers: " + response.getHeaders().toString() + " body: " + response.getResponseBodyAsString());
                        }
                        return PermissionCode.AUTHENTICATION_FAILED;
                    }
                    throw new ResponseException(String.format("Permission check failed, received %s", response.getStatusCode()));
                }
            }));
        }
        catch (CredentialsRequiredException e) {
            permissionState = PermissionCode.CREDENTIALS_REQUIRED;
        }
        catch (ResponseException e) {
            LOG.error(String.format("Failed to perform permission check for %s", applicationLink.getRpcUrl()), (Throwable)e);
            permissionState = PermissionCode.NO_CONNECTION;
        }
        switch (permissionState) {
            case CREDENTIALS_REQUIRED: 
            case AUTHENTICATION_FAILED: 
            case NO_AUTHENTICATION: {
                return this.response(permissionState, authenticatedRequestFactory.getAuthorisationURI());
            }
        }
        return this.response(permissionState);
    }

    private Response response(PermissionCode code) {
        return RestUtil.ok(new PermissionCodeEntity(code));
    }

    private Response response(PermissionCode code, URI authorisationUri) {
        return RestUtil.ok(new PermissionCodeEntity(code, authorisationUri));
    }

    private static interface RestMethodUrlProvider {
        public String getRestMethodUrl(ApplicationLink var1);
    }
}

