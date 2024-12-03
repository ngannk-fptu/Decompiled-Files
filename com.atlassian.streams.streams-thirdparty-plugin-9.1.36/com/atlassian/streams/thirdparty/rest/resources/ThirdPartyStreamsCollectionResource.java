/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.streams.api.common.Either
 *  com.atlassian.streams.api.common.Iterables
 *  com.atlassian.streams.api.common.Option
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Iterables
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.streams.thirdparty.rest.resources;

import com.atlassian.sal.api.user.UserManager;
import com.atlassian.streams.api.common.Either;
import com.atlassian.streams.api.common.Iterables;
import com.atlassian.streams.api.common.Option;
import com.atlassian.streams.thirdparty.api.Activity;
import com.atlassian.streams.thirdparty.api.ActivityQuery;
import com.atlassian.streams.thirdparty.api.ActivityService;
import com.atlassian.streams.thirdparty.api.ValidationErrors;
import com.atlassian.streams.thirdparty.rest.ThirdPartyStreamsUriBuilder;
import com.atlassian.streams.thirdparty.rest.representations.ActivityRepresentation;
import com.atlassian.streams.thirdparty.rest.representations.ErrorRepresentation;
import com.atlassian.streams.thirdparty.rest.representations.RepresentationFactory;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.net.URI;
import java.util.Iterator;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path(value="/")
public class ThirdPartyStreamsCollectionResource {
    private final ActivityService activityService;
    private final RepresentationFactory factory;
    private final ThirdPartyStreamsUriBuilder uriBuilder;
    private final UserManager userManager;
    private Function<Activity, Iterable<Activity>> deletedActivities = new Function<Activity, Iterable<Activity>>(){

        public Iterable<Activity> apply(Activity activity) {
            for (Long id : activity.getActivityId()) {
                if (!ThirdPartyStreamsCollectionResource.this.activityService.delete(id)) continue;
                return Option.some((Object)activity);
            }
            return Option.none();
        }
    };

    public ThirdPartyStreamsCollectionResource(ActivityService activityService, RepresentationFactory factory, ThirdPartyStreamsUriBuilder uriBuilder, UserManager userManager) {
        this.activityService = (ActivityService)Preconditions.checkNotNull((Object)activityService, (Object)"activityService");
        this.factory = (RepresentationFactory)Preconditions.checkNotNull((Object)factory, (Object)"factory");
        this.uriBuilder = (ThirdPartyStreamsUriBuilder)Preconditions.checkNotNull((Object)uriBuilder, (Object)"uriBuilder");
        this.userManager = (UserManager)Preconditions.checkNotNull((Object)userManager, (Object)"userManager");
    }

    @GET
    @Produces(value={"application/vnd.atl.streams.thirdparty+json"})
    public Response fetchActivities(@DefaultValue(value="10") @QueryParam(value="max-results") Integer maxResults, @DefaultValue(value="0") @QueryParam(value="start-index") Integer startIndex) {
        ActivityQuery query = ActivityQuery.builder().startIndex(startIndex).maxResults(maxResults).build();
        return Response.ok((Object)this.factory.createActivityCollectionRepresentation(this.activityService.activities(query), query)).build();
    }

    @DELETE
    @Produces(value={"application/vnd.atl.streams.thirdparty+json"})
    public Response deleteAllActivities() {
        String user = this.userManager.getRemoteUsername();
        if (!this.userManager.isAdmin(user) || !this.userManager.isSystemAdmin(user)) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        return Response.ok((Object)this.factory.createActivityCollectionRepresentation(com.google.common.collect.Iterables.concat((Iterable)Iterables.memoize((Iterable)com.google.common.collect.Iterables.transform(this.activityService.activities(ActivityQuery.all()), this.deletedActivities))), ActivityQuery.all())).build();
    }

    @POST
    @Consumes(value={"application/vnd.atl.streams.thirdparty+json"})
    public Response postNewActivity(ActivityRepresentation representation) {
        Either<ValidationErrors, Activity> activityOrError = representation.toActivity(this.userManager.getRemoteUser());
        if (activityOrError.isLeft()) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).type("application/vnd.atl.streams.thirdparty+json").entity((Object)new ErrorRepresentation("invalid activity entry", ((ValidationErrors)activityOrError.left().get()).toString())).build();
        }
        Activity activity = this.activityService.postActivity((Activity)activityOrError.right().get());
        Iterator iterator = activity.getActivityId().iterator();
        if (iterator.hasNext()) {
            Long activityId = (Long)iterator.next();
            return Response.created((URI)this.uriBuilder.buildAbsoluteActivityUri(activityId)).type("application/vnd.atl.streams.thirdparty+json").entity((Object)this.factory.createActivityRepresentation(activity)).build();
        }
        return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}

