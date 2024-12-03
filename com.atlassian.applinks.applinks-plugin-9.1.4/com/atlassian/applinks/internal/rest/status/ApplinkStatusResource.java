/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.plugins.rest.common.interceptor.InterceptorChain
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.annotation.Nonnull
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.WebApplicationException
 *  javax.ws.rs.core.Response
 */
package com.atlassian.applinks.internal.rest.status;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.core.ApplinkStatusService;
import com.atlassian.applinks.core.rest.context.ContextInterceptor;
import com.atlassian.applinks.core.rest.util.RestUtil;
import com.atlassian.applinks.internal.applink.ApplinkHelper;
import com.atlassian.applinks.internal.common.exception.NoAccessException;
import com.atlassian.applinks.internal.common.exception.NoSuchApplinkException;
import com.atlassian.applinks.internal.common.exception.ServiceException;
import com.atlassian.applinks.internal.common.rest.interceptor.RestRepresentationInterceptor;
import com.atlassian.applinks.internal.common.rest.util.RestApplicationIdParser;
import com.atlassian.applinks.internal.rest.RestUrl;
import com.atlassian.applinks.internal.rest.RestUrlBuilder;
import com.atlassian.applinks.internal.rest.RestVersion;
import com.atlassian.applinks.internal.rest.interceptor.NoCacheHeaderInterceptor;
import com.atlassian.applinks.internal.rest.interceptor.ServiceExceptionInterceptor;
import com.atlassian.applinks.internal.rest.model.status.RestApplinkOAuthStatus;
import com.atlassian.applinks.internal.rest.model.status.RestApplinkStatus;
import com.atlassian.applinks.internal.status.oauth.OAuthStatusService;
import com.atlassian.plugins.rest.common.interceptor.InterceptorChain;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import java.net.URI;
import java.net.URISyntaxException;
import javax.annotation.Nonnull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

@Api
@Path(value="status")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@AnonymousAllowed
@InterceptorChain(value={ContextInterceptor.class, ServiceExceptionInterceptor.class, RestRepresentationInterceptor.class, NoCacheHeaderInterceptor.class})
public class ApplinkStatusResource {
    public static final String CONTEXT = "status";
    public static final RestUrl STATUS_PATH = RestUrl.forPath("status");
    public static final RestUrl OAUTH_PATH = RestUrl.forPath("oauth");
    private static final String AUTHORISATION_CALLBACK = "authorisationCallback";
    private final ApplinkHelper applinkHelper;
    private final ApplinkStatusService applinkStatusService;
    private final OAuthStatusService oAuthStatusService;

    @Nonnull
    public static RestUrlBuilder statusUrl(@Nonnull ApplicationId id) {
        return new RestUrlBuilder().version(RestVersion.V3).addPath(STATUS_PATH).addApplicationId(id);
    }

    @Nonnull
    public static RestUrlBuilder oAuthStatusUrl(@Nonnull ApplicationId id) {
        return ApplinkStatusResource.statusUrl(id).addPath(OAUTH_PATH);
    }

    public ApplinkStatusResource(ApplinkHelper applinkHelper, ApplinkStatusService applinkStatusService, OAuthStatusService oAuthStatusService) {
        this.applinkHelper = applinkHelper;
        this.applinkStatusService = applinkStatusService;
        this.oAuthStatusService = oAuthStatusService;
    }

    @ApiOperation(value="Returns the status of the Applink with a given id", authorizations={@Authorization(value="Admin")}, response=RestApplinkStatus.class)
    @ApiResponses(value={@ApiResponse(code=200, message="Successful"), @ApiResponse(code=401, message="User does not have Administrator access"), @ApiResponse(code=404, message="No Applink with the given ID exists on this server")})
    @GET
    @Path(value="{id}")
    public Response getStatus(@PathParam(value="id") String id, @ApiParam(hidden=true) @QueryParam(value="authorisationCallback") String authorisationCallback) throws NoAccessException, NoSuchApplinkException {
        URI uriAuthorisationCallback = ApplinkStatusResource.parseAuthorisationCallback(authorisationCallback);
        return RestUtil.ok(new RestApplinkStatus(this.applinkStatusService.getApplinkStatus(RestApplicationIdParser.parseApplicationId(id)), uriAuthorisationCallback));
    }

    @ApiOperation(value="Get the Applink Oauth status for an applink with the given id", response=RestApplinkOAuthStatus.class)
    @ApiResponses(value={@ApiResponse(code=200, message="Successful"), @ApiResponse(code=404, message="No Applink with the given ID exists on this server")})
    @GET
    @Path(value="{id}/oauth")
    public Response getOAuthStatus(@PathParam(value="id") String id) throws ServiceException {
        return RestUtil.ok(new RestApplinkOAuthStatus(this.oAuthStatusService.getOAuthStatus(RestApplicationIdParser.parseApplicationId(id))));
    }

    @ApiOperation(value="Update the Applink Oauth status for an applink with the given id", authorizations={@Authorization(value="Admin")}, notes="NOTE: Enabling Two-legged OAuth with Impersonation requires Sysadmin access", response=RestApplinkOAuthStatus.class)
    @ApiResponses(value={@ApiResponse(code=204, message="Update successful"), @ApiResponse(code=401, message="User does not have Administrator access"), @ApiResponse(code=404, message="No Applink with the given ID exists on this server"), @ApiResponse(code=500, message="Malformed json body (this will hopefully be fixed in a future API release)"), @ApiResponse(code=409, message="Public key for remote host unavailable and no cached credenitals exist")})
    @PUT
    @Path(value="{id}/oauth")
    public Response updateOAuthStatus(@PathParam(value="id") String id, RestApplinkOAuthStatus restOAuthStatus) throws ServiceException {
        ApplicationLink link = this.applinkHelper.getApplicationLink(RestApplicationIdParser.parseApplicationId(id));
        this.oAuthStatusService.updateOAuthStatus(link, restOAuthStatus.asDomain());
        return RestUtil.noContent();
    }

    private static URI parseAuthorisationCallback(String authorisationCallback) {
        if (authorisationCallback == null) {
            return null;
        }
        try {
            URI callback = new URI(authorisationCallback);
            if (callback.isAbsolute()) {
                return callback;
            }
            throw new WebApplicationException(RestUtil.badRequest("authorisationCallback must be absolute"));
        }
        catch (URISyntaxException e) {
            throw new WebApplicationException(RestUtil.badRequest(e.getMessage()));
        }
    }
}

