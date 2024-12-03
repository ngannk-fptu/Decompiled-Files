/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.rest.resources;

import com.atlassian.confluence.plugins.rest.entities.ContentEntity;
import com.atlassian.confluence.plugins.rest.manager.RestContentManager;
import com.atlassian.confluence.plugins.rest.resources.AbstractResource;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import java.util.Set;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Deprecated
@Path(value="/content")
@AnonymousAllowed
public class PrototypeContentResource
extends AbstractResource {
    private final RestContentManager restContentManager;
    private static final int DEFAULT_MAX_SIZE = 50;

    private PrototypeContentResource() {
        this.restContentManager = null;
    }

    public PrototypeContentResource(UserAccessor userAccessor, RestContentManager restContentManager, SpacePermissionManager spacePermissionManager) {
        super(userAccessor, spacePermissionManager);
        this.restContentManager = restContentManager;
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
        ContentEntity entity = this.restContentManager.getContentEntity(id, true);
        return entity == null ? Response.status((Response.Status)Response.Status.NOT_FOUND).build() : Response.ok((Object)entity).build();
    }

    @GET
    @Produces(value={"application/xml", "application/json"})
    @Path(value="/{id}/attachments")
    public Response getAttachments(@PathParam(value="id") Long id, @QueryParam(value="start-index") String startIndexString, @QueryParam(value="max-results") String maxResultsString, @QueryParam(value="mimeType") Set<String> mime, @QueryParam(value="attachmentType") Set<String> nice, @QueryParam(value="reverseOrder") @DefaultValue(value="false") boolean reverseOrder) {
        this.createRequestContext();
        ContentEntity entity = this.restContentManager.getContentEntity(id, true);
        if (entity == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        Integer startIndex = PrototypeContentResource.parseInt(startIndexString);
        Integer maxResults = PrototypeContentResource.parseInt(maxResultsString);
        if (startIndex == null && startIndexString != null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        if (startIndex == null) {
            startIndex = 0;
        }
        if (maxResults == null && maxResultsString != null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        if (maxResults == null) {
            maxResults = 50;
        }
        int start = Math.max(0, startIndex);
        if ((mime == null || mime.isEmpty()) && (nice == null || nice.isEmpty())) {
            entity.getAttachments().buildAttachmentListFromWrapper(startIndex, maxResults);
        } else if (mime != null && !mime.isEmpty()) {
            entity.getAttachments().buildFilteredByMimeTypeAttachmentList(start, maxResults, mime);
        } else {
            entity.getAttachments().buildFilteredByNiceTypeAttachmentList(start, maxResults, nice);
        }
        if (reverseOrder) {
            entity.getAttachments().reverse();
        }
        return Response.ok((Object)entity.getAttachments()).build();
    }
}

