/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.upm.notification.rest.resources;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.notification.NotificationCache;
import com.atlassian.upm.notification.NotificationCollection;
import com.atlassian.upm.notification.NotificationType;
import com.atlassian.upm.notification.rest.representations.NotificationGroupRepresentation;
import com.atlassian.upm.notification.rest.representations.NotificationRepresentationFactory;
import java.util.List;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path(value="/notifications")
@WebSudoRequired
public class NotificationCollectionResource {
    public static final String HIDE_DISMISSED = "hide-dismissed";
    public static final String DEFAULT_HIDE_DISMISSED = "false";
    private final NotificationRepresentationFactory notificationRepresentationFactory;
    private final NotificationCache cache;
    private final PermissionEnforcer permissionEnforcer;
    private final UserManager userManager;

    public NotificationCollectionResource(NotificationRepresentationFactory notificationRepresentationFactory, NotificationCache cache, PermissionEnforcer permissionEnforcer, UserManager userManager) {
        this.notificationRepresentationFactory = Objects.requireNonNull(notificationRepresentationFactory, "notificationRepresentationFactory");
        this.cache = Objects.requireNonNull(cache, "cache");
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    public Response getNotifications() {
        this.permissionEnforcer.enforcePermission(Permission.GET_NOTIFICATIONS);
        List<NotificationCollection> notifications = this.cache.getNotifications();
        return Response.ok((Object)this.notificationRepresentationFactory.getNotificationGroupCollection(notifications, Option.none(UserKey.class))).build();
    }

    @GET
    @Path(value="{userKey}")
    @Produces(value={"application/vnd.atl.plugins+json"})
    public Response getNotifications(@PathParam(value="userKey") String userKey, @QueryParam(value="hide-dismissed") @DefaultValue(value="false") Boolean hideDismissed) {
        this.permissionEnforcer.enforcePermission(Permission.GET_NOTIFICATIONS);
        UserKey user = this.userManager.getRemoteUserKey();
        if (userKey == null || !userKey.equals(user.getStringValue())) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        List<NotificationCollection> notifications = this.cache.getNotifications(Option.some(user), hideDismissed);
        return Response.ok((Object)this.notificationRepresentationFactory.getNotificationGroupCollection(notifications, Option.some(user))).build();
    }

    @GET
    @Path(value="{userKey}/{typeKey}")
    @Produces(value={"application/vnd.atl.plugins+json"})
    public Response getNotifications(@PathParam(value="userKey") String userKey, @PathParam(value="typeKey") String typeKey, @QueryParam(value="hide-dismissed") @DefaultValue(value="false") Boolean hideDismissed) {
        this.permissionEnforcer.enforcePermission(Permission.GET_NOTIFICATIONS);
        UserKey user = this.userManager.getRemoteUserKey();
        NotificationType type = NotificationType.fromKey(typeKey);
        if (type == null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        if (userKey == null || !userKey.equals(user.getStringValue())) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        if (!this.permissionEnforcer.hasPermission(type.getRequiredPermission())) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).build();
        }
        NotificationCollection notifications = this.cache.getNotifications(type, Option.some(user), hideDismissed);
        return Response.ok((Object)this.notificationRepresentationFactory.getNotificationGroup(notifications, Option.some(user))).build();
    }

    @POST
    @Path(value="{userKey}/{typeKey}")
    @Consumes(value={"application/vnd.atl.plugins+json"})
    @Produces(value={"application/vnd.atl.plugins+json"})
    public Response setNotifications(@PathParam(value="userKey") String userKey, @PathParam(value="typeKey") String typeKey, NotificationGroupRepresentation notificationGroup) {
        this.permissionEnforcer.enforcePermission(Permission.MANAGE_NOTIFICATIONS);
        UserKey user = this.userManager.getRemoteUserKey();
        NotificationType type = NotificationType.fromKey(typeKey);
        if (type == null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        if (userKey == null || !userKey.equals(user.getStringValue())) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        if (!this.permissionEnforcer.hasPermission(type.getRequiredPermission())) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).build();
        }
        this.cache.setNotificationTypeDismissal(type, user, notificationGroup.isDismissed());
        if (this.cache.isNotificationTypeDismissed(type, Option.some(user)) != notificationGroup.isDismissed().booleanValue()) {
            return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok((Object)this.notificationRepresentationFactory.getNotificationGroup(this.cache.getNotifications(type, Option.some(user), true), Option.some(user))).build();
    }
}

