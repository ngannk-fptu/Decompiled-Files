/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessBlocked
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.core.AtlassianCoreException
 *  com.atlassian.core.user.preferences.UserPreferences
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousSiteAccess
 *  com.atlassian.user.User
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.QueryParam
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.createcontent.rest;

import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessBlocked;
import com.atlassian.confluence.plugins.createcontent.rest.AbstractRestResource;
import com.atlassian.confluence.plugins.createcontent.rest.SpaceResultsEntity;
import com.atlassian.confluence.plugins.createcontent.services.SpaceCollectionService;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.AtlassianCoreException;
import com.atlassian.core.user.preferences.UserPreferences;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousSiteAccess;
import com.atlassian.user.User;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/spaces")
public class SpaceResource
extends AbstractRestResource {
    private static final Logger log = LoggerFactory.getLogger(SpaceResource.class);
    public static final String PARAM_PROMOTED_SPACE_KEY = "promotedSpaceKey";
    public static final String PARAM_PROMOTED_SPACES_LIMIT = "promotedSpacesLimit";
    public static final String PARAM_OTHER_SPACES_LIMIT = "otherSpacesLimit";
    public static final String PARAM_SPACE_KEY = "spaceKey";
    private final SpaceManager spaceManager;
    private final SpacePermissionManager spacePermissionManager;
    private final UserAccessor userAccessor;
    private final SpaceCollectionService spaceCollectionService;

    public SpaceResource(@ComponentImport PermissionManager permissionManager, @ComponentImport SpaceManager spaceManager, @ComponentImport SpacePermissionManager spacePermissionManager, @ComponentImport UserAccessor userAccessor, SpaceCollectionService spaceCollectionService, @ComponentImport AccessModeService accessModeService) {
        super(permissionManager, spaceManager, accessModeService);
        this.spaceManager = spaceManager;
        this.spacePermissionManager = spacePermissionManager;
        this.userAccessor = userAccessor;
        this.spaceCollectionService = spaceCollectionService;
    }

    @GET
    @ReadOnlyAccessBlocked
    @AnonymousSiteAccess
    public Map<String, SpaceResultsEntity> getSpaces(@QueryParam(value="promotedSpaceKey") List<String> promotedSpaceKeys, @QueryParam(value="promotedSpacesLimit") @DefaultValue(value="10") int promotedSpacesLimit, @QueryParam(value="otherSpacesLimit") @DefaultValue(value="10") int otherSpacesLimit) {
        return this.spaceCollectionService.getSpaces(promotedSpaceKeys, promotedSpacesLimit, otherSpacesLimit, null);
    }

    @GET
    @Path(value="/adminCheck")
    @AnonymousSiteAccess
    public boolean spaceAdministrationPermissionCheck(@QueryParam(value="spaceKey") String spaceKey) {
        this.checkEmptyParameter(spaceKey, PARAM_SPACE_KEY);
        ConfluenceUser user = this.getUser();
        return this.hasAdminPermission(spaceKey, user);
    }

    @POST
    @ReadOnlyAccessAllowed
    @Path(value="/skip-space-welcome-dialog")
    @Consumes(value={"application/json"})
    public void skipSpaceWelcomeDialog() {
        ConfluenceUser user = this.getUser();
        UserPreferences userPreferences = this.userAccessor.getUserPreferences((User)user);
        try {
            userPreferences.setBoolean("confluence.user.create.content.space.welcome.dialog.dismissed", true);
        }
        catch (AtlassianCoreException e) {
            log.warn("Unable to skip space welcome dialog for user: {}", (Object)user);
        }
    }

    private boolean hasAdminPermission(String spaceKey, ConfluenceUser user) {
        Space space;
        if (user != null && (space = this.spaceManager.getSpace(spaceKey)) != null) {
            return this.spacePermissionManager.hasPermission("SETSPACEPERMISSIONS", space, (User)user);
        }
        return false;
    }

    @GET
    @Path(value="can-create-spaces")
    @AnonymousSiteAccess
    public Boolean canCreateSpaces() {
        return this.permissionManager.hasCreatePermission((User)this.getUser(), PermissionManager.TARGET_APPLICATION, Space.class);
    }
}

