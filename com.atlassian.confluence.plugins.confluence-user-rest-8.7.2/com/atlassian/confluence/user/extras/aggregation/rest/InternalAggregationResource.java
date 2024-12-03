/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.user.extras.aggregation.rest;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.extras.aggregation.impl.AggregationWarningManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path(value="/aggregation-warning")
@Internal
public class InternalAggregationResource {
    private static final int NOT_IMPLEMENTED = 501;
    private final AggregationWarningManager warningManager;

    public InternalAggregationResource(AggregationWarningManager warningManager) {
        this.warningManager = warningManager;
    }

    @POST
    @Consumes(value={"application/json"})
    @Path(value="/disable")
    public Response disable() {
        return Response.status((int)501).entity((Object)"Global disable not yet implemented").build();
    }

    @PUT
    @Consumes(value={"application/json"})
    @Path(value="/acknowledge")
    public Response acknowledge() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        ValidationResult result = this.warningManager.setAcknowledged(user);
        if (!result.isAuthorized()) {
            Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)"Need to be authorised as an admin to acknowledge the user group aggregation warning").build();
        }
        if (result.isValid()) {
            return Response.ok().build();
        }
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)result).build();
    }
}

