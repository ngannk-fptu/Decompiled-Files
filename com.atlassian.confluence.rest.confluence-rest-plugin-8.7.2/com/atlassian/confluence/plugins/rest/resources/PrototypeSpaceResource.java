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

import com.atlassian.confluence.plugins.rest.entities.SpaceEntity;
import com.atlassian.confluence.plugins.rest.entities.SpaceEntityListContext;
import com.atlassian.confluence.plugins.rest.manager.RestSpaceManager;
import com.atlassian.confluence.plugins.rest.resources.AbstractResource;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import java.util.Collections;
import java.util.Set;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Deprecated
@Path(value="/space")
@AnonymousAllowed
public class PrototypeSpaceResource
extends AbstractResource {
    private final RestSpaceManager restSpaceManager;

    private PrototypeSpaceResource() {
        this.restSpaceManager = null;
    }

    public PrototypeSpaceResource(UserAccessor userAccessor, RestSpaceManager restSpaceManager, SpacePermissionManager spacePermissionManager) {
        super(userAccessor, spacePermissionManager);
        this.restSpaceManager = restSpaceManager;
    }

    @GET
    @Produces(value={"application/xml", "application/json"})
    public Response get(@QueryParam(value="type") @DefaultValue(value="all") String spaceTypeString, @QueryParam(value="start-index") String startIndexString, @QueryParam(value="max-results") String maxResultsString, @QueryParam(value="spaceKey") Set<String> spaceKey) {
        this.createRequestContext();
        Integer startIndex = PrototypeSpaceResource.parseInt(startIndexString);
        Integer maxResults = PrototypeSpaceResource.parseInt(maxResultsString);
        if (spaceKey == null) {
            spaceKey = Collections.emptySet();
        }
        SpaceEntityListContext ctx = new SpaceEntityListContext(spaceTypeString, startIndex, maxResults, spaceKey);
        return Response.ok((Object)this.restSpaceManager.getSpaceEntityList(ctx)).build();
    }

    @GET
    @Produces(value={"application/xml", "application/json"})
    @Path(value="/{key}")
    public Response getSpace(@PathParam(value="key") String key) {
        this.createRequestContext();
        SpaceEntity spaceEntity = this.restSpaceManager.getSpaceEntity(key, true);
        if (spaceEntity == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        return Response.ok((Object)spaceEntity).build();
    }
}

