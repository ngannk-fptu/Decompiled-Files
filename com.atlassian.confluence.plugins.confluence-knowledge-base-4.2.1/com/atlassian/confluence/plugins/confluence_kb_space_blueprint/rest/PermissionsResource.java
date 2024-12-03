/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.SpacePermission
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.json.jsonorg.JSONObject
 *  com.atlassian.plugins.rest.common.security.UnrestrictedAccess
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.user.User
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.confluence_kb_space_blueprint.rest;

import com.atlassian.confluence.plugins.confluence_kb_space_blueprint.rest.request.SetGlobalUnlicensedAccessRequest;
import com.atlassian.confluence.plugins.confluence_kb_space_blueprint.rest.request.SetViewSpacePermissionRequest;
import com.atlassian.confluence.plugins.confluence_kb_space_blueprint.rest.response.GlobalPermissionStateResponse;
import com.atlassian.confluence.plugins.confluence_kb_space_blueprint.rest.response.PermissionStateResponse;
import com.atlassian.confluence.plugins.confluence_kb_space_blueprint.rest.response.SpacePermissionStateResponse;
import com.atlassian.confluence.plugins.confluence_kb_space_blueprint.services.ApplicationLinkRequestVerifier;
import com.atlassian.confluence.plugins.confluence_kb_space_blueprint.services.SpacePermissionUpdateResult;
import com.atlassian.confluence.plugins.confluence_kb_space_blueprint.services.SpacePermissionUpdateService;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.json.jsonorg.JSONObject;
import com.atlassian.plugins.rest.common.security.UnrestrictedAccess;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.user.User;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/permissions")
@Consumes(value={"application/json;charset=UTF-8"})
@Produces(value={"application/json;charset=UTF-8"})
public class PermissionsResource {
    private static final String PERMISSIONS_DISABLED_DARK_FEATURE = "confluence.kb.permissions.resource.disabled";
    private static final Logger log = LoggerFactory.getLogger(PermissionsResource.class);
    private final SpacePermissionManager spacePermissionManager;
    private final SpaceManager spaceManager;
    private final SpacePermissionUpdateService spacePermissionUpdateService;
    private final ApplicationLinkRequestVerifier applicationLinkRequestVerifier;
    private final DarkFeatureManager darkFeatureManager;
    private final I18NBeanFactory i18NBeanFactory;

    public PermissionsResource(SpacePermissionManager spacePermissionManager, SpaceManager spaceManager, SpacePermissionUpdateService spacePermissionUpdateService, ApplicationLinkRequestVerifier applicationLinkRequestVerifier, DarkFeatureManager darkFeatureManager, I18NBeanFactory i18NBeanFactory) {
        this.spacePermissionManager = spacePermissionManager;
        this.spaceManager = spaceManager;
        this.spacePermissionUpdateService = spacePermissionUpdateService;
        this.applicationLinkRequestVerifier = applicationLinkRequestVerifier;
        this.darkFeatureManager = darkFeatureManager;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    @GET
    @UnrestrictedAccess
    public Response queryPermissions(@QueryParam(value="spaceKey") String spaceKey, @Context HttpServletRequest servletRequest, @Context HttpServletResponse servletResponse) {
        if (this.darkFeatureManager.isEnabledForAllUsers(PERMISSIONS_DISABLED_DARK_FEATURE).orElse(false).booleanValue()) {
            return this.featureDisabledResponse();
        }
        boolean skipPermissionChecks = this.applicationLinkRequestVerifier.isApplicationLinkRequest(servletRequest, servletResponse);
        boolean isSpaceSpecified = StringUtils.isNotEmpty((CharSequence)spaceKey);
        Space space = null;
        if (isSpaceSpecified) {
            space = this.spaceManager.getSpace(spaceKey);
        }
        if (skipPermissionChecks) {
            log.debug("Current request was made over Application Link, skipping permission check for viewing anonymous view permission");
        } else if (!this.spacePermissionManager.hasPermission("VIEWSPACE", space, (User)AuthenticatedUserThreadLocal.get())) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        if (isSpaceSpecified) {
            return Optional.ofNullable(space).map(sp -> Response.ok((Object)this.buildSpaceStateResponse((Space)sp)).build()).orElse(this.spaceNotFoundResponse());
        }
        return Response.ok((Object)this.buildSpaceStateResponse()).build();
    }

    @POST
    @UnrestrictedAccess
    @Path(value="space/anonymousview")
    public Response setAnonymousViewSpacePermission(SetViewSpacePermissionRequest setAnonymousViewSpaceRequest, @Context HttpServletRequest servletRequest, @Context HttpServletResponse servletResponse) {
        if (this.darkFeatureManager.isEnabledForAllUsers(PERMISSIONS_DISABLED_DARK_FEATURE).orElse(false).booleanValue()) {
            return this.featureDisabledResponse();
        }
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        boolean skipPermissionChecks = this.applicationLinkRequestVerifier.isApplicationLinkRequest(servletRequest, servletResponse);
        if (skipPermissionChecks) {
            log.debug("Current request was made over Application Link, skipping permission check for updating anonymous view permission");
        }
        return this.updateSpacePermission(setAnonymousViewSpaceRequest.spaceKey, space -> this.spacePermissionUpdateService.setEnableAnonymousViewSpace(user, (Space)space, setAnonymousViewSpaceRequest.enablePermission, skipPermissionChecks));
    }

    @POST
    @UnrestrictedAccess
    @Path(value="space/unlicensedview")
    public Response setUnlicensedViewSpacePermission(SetViewSpacePermissionRequest setUnlicensedViewSpaceRequest, @Context HttpServletRequest servletRequest, @Context HttpServletResponse servletResponse) {
        if (this.darkFeatureManager.isEnabledForAllUsers(PERMISSIONS_DISABLED_DARK_FEATURE).orElse(false).booleanValue()) {
            return this.featureDisabledResponse();
        }
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        boolean skipPermissionChecks = this.applicationLinkRequestVerifier.isApplicationLinkRequest(servletRequest, servletResponse);
        if (skipPermissionChecks) {
            log.debug("Current request was made over Application Link, skipping permission check for updating unlicensed user view permission");
        }
        return this.updateSpacePermission(setUnlicensedViewSpaceRequest.spaceKey, space -> this.spacePermissionUpdateService.setEnableUnlicensedViewSpace(user, (Space)space, setUnlicensedViewSpaceRequest.enablePermission, skipPermissionChecks));
    }

    private Response updateSpacePermission(String spaceKey, Function<Space, SpacePermissionUpdateResult> updatePermission) {
        return Optional.ofNullable(this.spaceManager.getSpace(spaceKey)).map(space -> {
            SpacePermissionUpdateResult updateResult = (SpacePermissionUpdateResult)updatePermission.apply((Space)space);
            if (updateResult.isSuccessful()) {
                return Response.ok((Object)this.buildSpaceStateResponse((Space)space)).build();
            }
            return PermissionsResource.toErrorResponse(updateResult);
        }).orElse(this.spaceNotFoundResponse());
    }

    @POST
    @UnrestrictedAccess
    @Path(value="global/unlicensedaccess")
    public Response setGlobalPermission(SetGlobalUnlicensedAccessRequest request, @Context HttpServletRequest servletRequest, @Context HttpServletResponse servletResponse) {
        SpacePermissionUpdateResult updateResult;
        if (this.darkFeatureManager.isEnabledForAllUsers(PERMISSIONS_DISABLED_DARK_FEATURE).orElse(false).booleanValue()) {
            return this.featureDisabledResponse();
        }
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        boolean skipPermissionChecks = this.applicationLinkRequestVerifier.isApplicationLinkRequest(servletRequest, servletResponse);
        if (skipPermissionChecks) {
            log.debug("Current request was made over Application Link, skipping permission check for updating global unlicensed access");
        }
        if ((updateResult = this.spacePermissionUpdateService.setEnableGlobalUnlicensedAccess(user, request.enablePermission, skipPermissionChecks)).isSuccessful()) {
            return Response.ok((Object)this.buildGlobalStateResponse()).build();
        }
        return PermissionsResource.toErrorResponse(updateResult);
    }

    private PermissionStateResponse buildSpaceStateResponse() {
        return new PermissionStateResponse(null, this.buildGlobalStateResponse());
    }

    private PermissionStateResponse buildSpaceStateResponse(Space space) {
        return new PermissionStateResponse(new SpacePermissionStateResponse(space.getKey(), this.isSpaceUnlicensedAuthenticatedViewEnabled(space), this.isSpaceAnonymousViewEnabled(space)), this.buildGlobalStateResponse());
    }

    private GlobalPermissionStateResponse buildGlobalStateResponse() {
        return new GlobalPermissionStateResponse(this.isGlobalUnlicensedAccessEnabled(), this.isGlobalAnonymousAccessEnabled());
    }

    private boolean isGlobalUnlicensedAccessEnabled() {
        SpacePermission unlicensedAuthenticatedAccessPermission = SpacePermission.createAuthenticatedUsersSpacePermission((String)"LIMITEDUSECONFLUENCE", null);
        return this.spacePermissionManager.permissionExists(unlicensedAuthenticatedAccessPermission);
    }

    private boolean isGlobalAnonymousAccessEnabled() {
        SpacePermission anonymousAccessPermission = SpacePermission.createAnonymousSpacePermission((String)"USECONFLUENCE", null);
        return this.spacePermissionManager.permissionExists(anonymousAccessPermission);
    }

    private boolean isSpaceUnlicensedAuthenticatedViewEnabled(Space space) {
        SpacePermission unlicensedViewPermission = SpacePermission.createAuthenticatedUsersSpacePermission((String)"VIEWSPACE", (Space)space);
        return this.spacePermissionManager.permissionExists(unlicensedViewPermission);
    }

    private boolean isSpaceAnonymousViewEnabled(Space space) {
        SpacePermission unlicensedViewPermission = SpacePermission.createAnonymousSpacePermission((String)"VIEWSPACE", (Space)space);
        return this.spacePermissionManager.permissionExists(unlicensedViewPermission);
    }

    private Response spaceNotFoundResponse() {
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean();
        return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)PermissionsResource.jsonErrorObject(i18NBean.getText("com.atlassian.confluence.plugins.confluence-knowledge-base.space.not.found"))).build();
    }

    private Response featureDisabledResponse() {
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean();
        return Response.status((Response.Status)Response.Status.GONE).entity((Object)PermissionsResource.jsonErrorObject(i18NBean.getText("com.atlassian.confluence.plugins.confluence-knowledge-base.permissions.resource.disabled"))).build();
    }

    private static Response toErrorResponse(SpacePermissionUpdateResult updateResult) {
        return Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)PermissionsResource.jsonErrorObject((String)updateResult.getI18ErrorOpt().getOrNull())).build();
    }

    private static String jsonErrorObject(String errorMessage) {
        return new JSONObject(Collections.singletonMap("errorMessage", StringUtils.defaultIfBlank((CharSequence)errorMessage, (CharSequence)""))).toString();
    }
}

