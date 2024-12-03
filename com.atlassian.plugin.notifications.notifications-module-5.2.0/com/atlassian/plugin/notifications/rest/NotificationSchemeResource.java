/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Either
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.google.common.base.Function
 *  javax.annotation.Nullable
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.UriInfo
 */
package com.atlassian.plugin.notifications.rest;

import com.atlassian.fugue.Either;
import com.atlassian.plugin.notifications.api.ErrorCollection;
import com.atlassian.plugin.notifications.api.HandleErrorFunction;
import com.atlassian.plugin.notifications.api.notification.NotificationRepresentation;
import com.atlassian.plugin.notifications.api.notification.NotificationSchemeRepresentation;
import com.atlassian.plugin.notifications.api.notification.NotificationSchemeService;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.google.common.base.Function;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path(value="scheme")
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
@WebSudoRequired
public class NotificationSchemeResource {
    private final UserManager userManager;
    private final NotificationSchemeService notificationService;

    public NotificationSchemeResource(UserManager userManager, NotificationSchemeService notificationService) {
        this.userManager = userManager;
        this.notificationService = notificationService;
    }

    @GET
    public Response getDefaultScheme(@Context UriInfo uriInfo) {
        return this.getScheme(uriInfo);
    }

    @GET
    @Path(value="{schemeId}")
    public Response getScheme(@Context UriInfo uriInfo) {
        Either<ErrorCollection, NotificationSchemeRepresentation> result = this.notificationService.getScheme(this.userManager.getRemoteUsername());
        return (Response)result.fold((Function)new HandleErrorFunction(), (Function)new Function<NotificationSchemeRepresentation, Response>(){

            public Response apply(@Nullable NotificationSchemeRepresentation input) {
                return Response.ok((Object)input).cacheControl(HandleErrorFunction.NO_CACHE).build();
            }
        });
    }

    @POST
    @Path(value="{schemeId}/notification")
    public Response createNotification(@Context UriInfo uriInfo, @PathParam(value="schemeId") int schemeId, NotificationRepresentation notification) {
        Either<ErrorCollection, NotificationRepresentation> result = this.notificationService.validateAddNotification(this.userManager.getRemoteUsername(), notification);
        return (Response)result.fold((Function)new HandleErrorFunction(), (Function)new Function<NotificationRepresentation, Response>(){

            public Response apply(@Nullable NotificationRepresentation input) {
                NotificationRepresentation newNotification = NotificationSchemeResource.this.notificationService.addNotification(NotificationSchemeResource.this.userManager.getRemoteUsername(), input);
                return Response.ok((Object)newNotification).cacheControl(HandleErrorFunction.NO_CACHE).build();
            }
        });
    }

    @GET
    @Path(value="{schemeId}/notification/{notificationId}")
    public Response getNotification(@Context UriInfo uriInfo, @PathParam(value="schemeId") int schemeId, @PathParam(value="notificationId") int notificationId) {
        Either<ErrorCollection, NotificationRepresentation> result = this.notificationService.getSchemeNotification(this.userManager.getRemoteUsername(), notificationId);
        return (Response)result.fold((Function)new HandleErrorFunction(), (Function)new Function<NotificationRepresentation, Response>(){

            public Response apply(@Nullable NotificationRepresentation input) {
                return Response.ok((Object)input).cacheControl(HandleErrorFunction.NO_CACHE).build();
            }
        });
    }

    @PUT
    @Path(value="{schemeId}/notification/{notificationId}")
    public Response updateNotification(@Context UriInfo uriInfo, @PathParam(value="schemeId") int schemeId, final @PathParam(value="notificationId") int notificationId, NotificationRepresentation notification) {
        Either<ErrorCollection, NotificationRepresentation> result = this.notificationService.validateUpdateNotification(this.userManager.getRemoteUsername(), notificationId, notification);
        return (Response)result.fold((Function)new HandleErrorFunction(), (Function)new Function<NotificationRepresentation, Response>(){

            public Response apply(@Nullable NotificationRepresentation input) {
                NotificationRepresentation newNotification = NotificationSchemeResource.this.notificationService.updateNotification(NotificationSchemeResource.this.userManager.getRemoteUsername(), notificationId, input);
                return Response.ok((Object)newNotification).cacheControl(HandleErrorFunction.NO_CACHE).build();
            }
        });
    }

    @DELETE
    @Path(value="{schemeId}/notification/{notificationId}")
    public Response removeNotification(@PathParam(value="schemeId") int schemeId, @PathParam(value="notificationId") int notificationId) {
        ErrorCollection errors = this.notificationService.validateRemoveNotification(this.userManager.getRemoteUsername(), notificationId);
        if (errors.hasAnyErrors()) {
            return new HandleErrorFunction().apply(errors);
        }
        this.notificationService.removeNotification(notificationId);
        return Response.ok().cacheControl(HandleErrorFunction.NO_CACHE).build();
    }
}

