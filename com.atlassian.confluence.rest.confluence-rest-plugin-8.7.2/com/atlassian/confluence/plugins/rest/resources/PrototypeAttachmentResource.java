/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.rest.resources;

import com.atlassian.confluence.plugins.rest.entities.AttachmentEntity;
import com.atlassian.confluence.plugins.rest.manager.RestAttachmentManager;
import com.atlassian.confluence.plugins.rest.resources.AbstractResource;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Deprecated
@Path(value="/attachment")
@AnonymousAllowed
public class PrototypeAttachmentResource
extends AbstractResource {
    private final RestAttachmentManager restAttachmentManager;

    private PrototypeAttachmentResource() {
        this.restAttachmentManager = null;
    }

    public PrototypeAttachmentResource(UserAccessor userAccessor, RestAttachmentManager restAttachmentManager, SpacePermissionManager spacePermissionManager) {
        super(userAccessor, spacePermissionManager);
        this.restAttachmentManager = restAttachmentManager;
    }

    @GET
    @Produces(value={"application/xml", "application/json"})
    public Response get() {
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    @GET
    @Produces(value={"application/xml", "application/json"})
    @Path(value="/{id}")
    public Response getContent(@PathParam(value="id") Long id) {
        this.createRequestContext();
        AttachmentEntity entity = this.restAttachmentManager.getAttachmentEntity(id);
        return entity == null ? Response.status((Response.Status)Response.Status.NOT_FOUND).build() : Response.ok((Object)entity).build();
    }
}

