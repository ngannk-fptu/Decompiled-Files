/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.QueryParam
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.createcontent.rest;

import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.plugins.createcontent.BlueprintStateController;
import com.atlassian.confluence.plugins.createcontent.SpaceBlueprintStateController;
import com.atlassian.confluence.plugins.createcontent.rest.AbstractRestResource;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.UUID;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import org.apache.commons.lang3.StringUtils;

@Path(value="/modules")
public class BlueprintStateResource
extends AbstractRestResource {
    public static final String NON_SPACE_ADMIN_PERMISSION_DENIED_RESPONSE = "Only space administrators can enable / disable plugin modules per space";
    public static final String PARAM_SPACE_KEY = "spaceKey";
    public static final String PARAM_BLUEPRINT_ID = "blueprintId";
    private final BlueprintStateController contentBlueprintStateController;
    private final SpaceBlueprintStateController spaceBlueprintStateController;
    private final TransactionTemplate transactionTemplate;

    public BlueprintStateResource(BlueprintStateController contentBlueprintStateController, @ComponentImport SpaceManager spaceManager, @ComponentImport TransactionTemplate transactionTemplate, @ComponentImport PermissionManager permissionManager, SpaceBlueprintStateController spaceBlueprintStateController, @ComponentImport AccessModeService accessModeService) {
        super(permissionManager, spaceManager, accessModeService);
        this.contentBlueprintStateController = contentBlueprintStateController;
        this.transactionTemplate = transactionTemplate;
        this.spaceBlueprintStateController = spaceBlueprintStateController;
    }

    @Path(value="/{blueprintId}")
    @PUT
    @ReadOnlyAccessAllowed
    public void enableBlueprint(@PathParam(value="blueprintId") String blueprintId, @QueryParam(value="spaceKey") String spaceKey) {
        this.toggleContentBlueprint(blueprintId, spaceKey, true);
    }

    @Path(value="/{blueprintId}")
    @DELETE
    @ReadOnlyAccessAllowed
    public void disableBlueprint(@PathParam(value="blueprintId") String blueprintId, @QueryParam(value="spaceKey") String spaceKey) {
        this.toggleContentBlueprint(blueprintId, spaceKey, false);
    }

    @Path(value="/space-blueprint/{blueprintId}")
    @PUT
    @ReadOnlyAccessAllowed
    public void enableSpaceBlueprint(@PathParam(value="blueprintId") String blueprintId) {
        this.toggleSpaceBlueprint(blueprintId, true);
    }

    @Path(value="/space-blueprint/{blueprintId}")
    @DELETE
    @ReadOnlyAccessAllowed
    public void disableSpaceBlueprint(@PathParam(value="blueprintId") String blueprintId) {
        this.toggleSpaceBlueprint(blueprintId, false);
    }

    private UUID validateAndGetId(String blueprintId) {
        this.checkEmptyParameter(blueprintId, PARAM_BLUEPRINT_ID);
        return UUID.fromString(blueprintId);
    }

    private Space validatePermissionsAndGetSpace(String spaceKey) {
        Space space;
        if (StringUtils.isBlank((CharSequence)spaceKey)) {
            this.checkAdminPermission();
            space = null;
        } else {
            space = this.checkSpaceAdminPermission(spaceKey);
        }
        return space;
    }

    private void toggleContentBlueprint(String blueprintId, String spaceKey, boolean enable) {
        UUID id = this.validateAndGetId(blueprintId);
        Space space = this.validatePermissionsAndGetSpace(spaceKey);
        this.transactionTemplate.execute(() -> {
            if (enable) {
                this.contentBlueprintStateController.enableBlueprint(id, space);
            } else {
                this.contentBlueprintStateController.disableBlueprint(id, space);
            }
            return null;
        });
    }

    private void toggleSpaceBlueprint(String blueprintId, boolean enable) {
        UUID id = this.validateAndGetId(blueprintId);
        this.validatePermissionsAndGetSpace(null);
        this.transactionTemplate.execute(() -> {
            if (enable) {
                this.spaceBlueprintStateController.enableSpaceBlueprint(id);
            } else {
                this.spaceBlueprintStateController.disableSpaceBlueprint(id);
            }
            return null;
        });
    }
}

