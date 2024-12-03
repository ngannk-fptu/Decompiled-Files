/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.base.Preconditions
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.streams.thirdparty.rest.resources;

import com.atlassian.sal.api.user.UserManager;
import com.atlassian.streams.thirdparty.api.Activity;
import com.atlassian.streams.thirdparty.api.ActivityService;
import com.atlassian.streams.thirdparty.rest.representations.RepresentationFactory;
import com.google.common.base.Preconditions;
import java.util.Iterator;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path(value="/{activityId}")
public class ThirdPartyStreamsResource {
    private final ActivityService activityService;
    private final RepresentationFactory factory;
    private final UserManager userManager;

    public ThirdPartyStreamsResource(ActivityService activityService, RepresentationFactory factory, UserManager userManager) {
        this.activityService = (ActivityService)Preconditions.checkNotNull((Object)activityService, (Object)"activityService");
        this.factory = (RepresentationFactory)Preconditions.checkNotNull((Object)factory, (Object)"factory");
        this.userManager = (UserManager)Preconditions.checkNotNull((Object)userManager, (Object)"userManager");
    }

    @GET
    public Response get(@PathParam(value="activityId") long activityId) {
        Iterator iterator = this.activityService.getActivity(activityId).iterator();
        if (iterator.hasNext()) {
            Activity activity = (Activity)iterator.next();
            return Response.ok((Object)this.factory.createActivityRepresentation(activity)).type("application/vnd.atl.streams.thirdparty+json").build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    @DELETE
    public Response delete(@PathParam(value="activityId") long activityId) {
        String user = this.userManager.getRemoteUsername();
        if (!this.userManager.isAdmin(user) || !this.userManager.isSystemAdmin(user)) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        Iterator iterator = this.activityService.getActivity(activityId).iterator();
        if (iterator.hasNext()) {
            Activity activity = (Activity)iterator.next();
            if (this.activityService.delete(activityId)) {
                return Response.ok((Object)this.factory.createActivityRepresentation(activity)).type("application/vnd.atl.streams.thirdparty+json").build();
            }
            return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }
}

