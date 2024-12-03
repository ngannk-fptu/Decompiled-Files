/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.upm.notification.rest.resources;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.UpmUriEscaper;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.notification.Notification;
import com.atlassian.upm.notification.NotificationCache;
import com.atlassian.upm.notification.NotificationType;
import com.atlassian.upm.notification.rest.representations.NotificationRepresentation;
import com.atlassian.upm.notification.rest.representations.NotificationRepresentationFactory;
import java.util.Iterator;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/notifications/{userKey}/{typeKey}/{pluginKey}")
@WebSudoRequired
public class NotificationResource {
    private final NotificationRepresentationFactory notificationRepresentationFactory;
    private final NotificationCache cache;
    private final PermissionEnforcer permissionEnforcer;
    private final UserManager userManager;
    private final PluginRetriever pluginRetriever;

    public NotificationResource(NotificationRepresentationFactory notificationRepresentationFactory, NotificationCache cache, PermissionEnforcer permissionEnforcer, UserManager userManager, PluginRetriever pluginRetriever) {
        this.notificationRepresentationFactory = Objects.requireNonNull(notificationRepresentationFactory, "notificationRepresentationFactory");
        this.cache = Objects.requireNonNull(cache, "cache");
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    public Response getNotification(@PathParam(value="userKey") String userKey, @PathParam(value="typeKey") String typeKey, @PathParam(value="pluginKey") String pluginKey) {
        this.permissionEnforcer.enforcePermission(Permission.GET_NOTIFICATIONS);
        pluginKey = UpmUriEscaper.unescape(pluginKey);
        UserKey user = this.userManager.getRemoteUserKey();
        NotificationType type = NotificationType.fromKey(typeKey);
        if (type == null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        if (userKey == null || !userKey.equals(user.getStringValue())) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        for (Plugin p : this.pluginRetriever.getPlugin(pluginKey)) {
            if (this.permissionEnforcer.hasPermission(type.getRequiredPermission(), p)) continue;
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).build();
        }
        Iterator<Object> iterator = this.cache.getNotification(type, Option.some(user), pluginKey).iterator();
        if (iterator.hasNext()) {
            Notification notification = (Notification)iterator.next();
            return Response.ok((Object)this.notificationRepresentationFactory.getNotification(notification, Option.some(user))).build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    @POST
    @Consumes(value={"application/vnd.atl.plugins+json"})
    @Produces(value={"application/vnd.atl.plugins+json"})
    public Response setNotification(@PathParam(value="userKey") String userKey, @PathParam(value="typeKey") String typeKey, @PathParam(value="pluginKey") String pluginKey, NotificationRepresentation notification) {
        this.permissionEnforcer.enforcePermission(Permission.MANAGE_NOTIFICATIONS);
        pluginKey = UpmUriEscaper.unescape(pluginKey);
        UserKey user = this.userManager.getRemoteUserKey();
        NotificationType type = NotificationType.fromKey(typeKey);
        if (type == null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
        }
        if (userKey == null || !userKey.equals(user.getStringValue())) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        for (Plugin p : this.pluginRetriever.getPlugin(pluginKey)) {
            if (this.permissionEnforcer.hasPermission(type.getRequiredPermission(), p)) continue;
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).build();
        }
        if (!this.cache.getNotification(type, Option.some(user), pluginKey).isDefined()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        this.cache.setNotificationDismissal(type, user, pluginKey, notification.isDismissed());
        if (this.cache.isNotificationDismissed(type, Option.some(user), pluginKey) != notification.isDismissed().booleanValue()) {
            return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        Iterator<Object> iterator = this.cache.getNotification(type, Option.some(user), pluginKey).iterator();
        if (iterator.hasNext()) {
            Notification updatedNotification = (Notification)iterator.next();
            return Response.ok((Object)this.notificationRepresentationFactory.getNotification(updatedNotification, Option.some(user))).build();
        }
        return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).build();
    }
}

