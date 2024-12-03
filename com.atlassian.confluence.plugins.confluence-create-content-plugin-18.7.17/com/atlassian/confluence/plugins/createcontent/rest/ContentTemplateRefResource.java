/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AdminOnly
 *  com.atlassian.plugins.rest.common.security.AnonymousSiteAccess
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  javax.annotation.Nonnull
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.createcontent.rest;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.plugins.createcontent.ContentTemplateRefManager;
import com.atlassian.confluence.plugins.createcontent.activeobjects.ContentTemplateRefAo;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.ResourceErrorType;
import com.atlassian.confluence.plugins.createcontent.exceptions.ResourceException;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.confluence.plugins.createcontent.rest.AbstractRestResource;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AdminOnly;
import com.atlassian.plugins.rest.common.security.AnonymousSiteAccess;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path(value="/templatesRefs")
public class ContentTemplateRefResource
extends AbstractRestResource {
    public static final String PARAM_ID = "id";
    private final ContentTemplateRefManager contentTemplateRefManager;
    private final ActiveObjects activeObjects;
    private final UserManager userManager;

    public ContentTemplateRefResource(@ComponentImport PermissionManager permissionManager, @ComponentImport SpaceManager spaceManager, ContentTemplateRefManager contentTemplateRefManager, @ComponentImport ActiveObjects activeObjects, @ComponentImport AccessModeService accessModeService, @ComponentImport UserManager userManager) {
        super(permissionManager, spaceManager, accessModeService);
        this.contentTemplateRefManager = contentTemplateRefManager;
        this.activeObjects = activeObjects;
        this.userManager = userManager;
    }

    @AdminOnly
    @DELETE
    @ReadOnlyAccessAllowed
    @Path(value="deleteAll")
    public Integer deleteAll() {
        this.checkAdminPermission();
        return this.contentTemplateRefManager.deleteAll();
    }

    @AnonymousSiteAccess
    @GET
    @Path(value="{id}")
    public ContentTemplateRef get(@PathParam(value="id") UUID uuid) {
        this.forbidUnlicensedUsers();
        this.checkNullParameter(uuid, PARAM_ID);
        return (ContentTemplateRef)this.contentTemplateRefManager.getById(uuid);
    }

    @AdminOnly
    @DELETE
    @Path(value="{id}")
    public boolean delete(@PathParam(value="id") UUID uuid) {
        this.checkNullParameter(uuid, PARAM_ID);
        return this.contentTemplateRefManager.delete(uuid);
    }

    @AdminOnly
    @POST
    @Consumes(value={"application/json", "application/xml"})
    public UUID create(ContentTemplateRef contentTemplateRef) {
        this.checkNullEntity(contentTemplateRef);
        ContentTemplateRefAo ao = (ContentTemplateRefAo)this.activeObjects.executeInTransaction(() -> this.saveContentTemplateRef(contentTemplateRef));
        if (ao != null) {
            return UUID.fromString(ao.getUuid());
        }
        return null;
    }

    @AdminOnly
    @PUT
    @Consumes(value={"application/json", "application/xml"})
    public void update(ContentTemplateRef contentTemplateRef) {
        this.contentTemplateRefManager.update(contentTemplateRef);
        for (ContentTemplateRef child : contentTemplateRef.getChildren()) {
            this.update(child);
        }
    }

    @Nonnull
    private ContentTemplateRefAo saveContentTemplateRef(@Nonnull ContentTemplateRef contentTemplateRef) {
        ContentTemplateRefAo parentAo = (ContentTemplateRefAo)this.contentTemplateRefManager.createAo(contentTemplateRef);
        for (ContentTemplateRef child : contentTemplateRef.getChildren()) {
            ContentTemplateRefAo childAo = this.saveContentTemplateRef(child);
            childAo.setParent(parentAo);
            childAo.save();
        }
        return parentAo;
    }

    private void forbidUnlicensedUsers() {
        UserKey userKey = this.userManager.getRemoteUserKey();
        if (userKey != null && !this.userManager.isLicensed(userKey)) {
            throw new ResourceException("Only licensed user can make this request.", Response.Status.BAD_REQUEST, ResourceErrorType.PERMISSION_USER_CREATE);
        }
    }
}

