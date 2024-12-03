/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Iterables
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.medium.ServerManager
 *  com.atlassian.plugin.notifications.api.notification.UserNotificationPreferences
 *  com.atlassian.plugin.notifications.api.notification.UserNotificationPreferencesManager
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.base.Predicate
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.FormParam
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.notifications.impl.rest;

import com.atlassian.confluence.notifications.ConfluenceUserRole;
import com.atlassian.confluence.notifications.UserNotificationsDefaultsService;
import com.atlassian.confluence.notifications.impl.analytics.UserNotificationPreferenceChangeEvent;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Iterables;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.ServerManager;
import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferences;
import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferencesManager;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.base.Predicate;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Qualifier;

@Path(value="user")
@Consumes(value={"application/json"})
@Produces(value={"text/plain"})
public class StaticUserPreferenceResource {
    private final UserNotificationPreferencesManager userNotificationPreferencesManager;
    private final ServerManager serverManager;
    private final UserManager userManager;
    private final EventPublisher eventPublisher;
    private final UserNotificationsDefaultsService userNotificationsDefaultsService;

    public StaticUserPreferenceResource(@Qualifier(value="confluenceNotificationPreferenceManager") UserNotificationPreferencesManager userNotificationPreferencesManager, ServerManager serverManager, UserManager userManager, EventPublisher eventPublisher, UserNotificationsDefaultsService userNotificationsDefaultsService) {
        this.userNotificationPreferencesManager = userNotificationPreferencesManager;
        this.serverManager = serverManager;
        this.userManager = userManager;
        this.eventPublisher = eventPublisher;
        this.userNotificationsDefaultsService = userNotificationsDefaultsService;
    }

    @PUT
    @Path(value="notifications/static/{mediumKey}/{role}")
    public Response putStatusFor(@PathParam(value="mediumKey") String mediumKey, @PathParam(value="role") String role) {
        return this.setStatusFor(mediumKey, role, true);
    }

    @DELETE
    @Path(value="notifications/static/{mediumKey}/{role}")
    public Response deleteStatusFor(@PathParam(value="mediumKey") String mediumKey, @PathParam(value="role") String role) {
        return this.setStatusFor(mediumKey, role, false);
    }

    @POST
    @Path(value="notifications/static/{mediumKey}/{role}")
    public Response setStatusFor(@PathParam(value="mediumKey") String mediumKey, @PathParam(value="role") String role, @FormParam(value="enabled") boolean enabled) {
        UserKey currentUserKey = this.userManager.getRemoteUserKey();
        Option medium = Iterables.findFirst((Iterable)this.serverManager.getServers(), this.withKey(mediumKey));
        if (!medium.isDefined()) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        UserNotificationPreferences userPreferences = this.userNotificationPreferencesManager.getPreferences(currentUserKey);
        ConfluenceUserRole confluenceUserRole = new ConfluenceUserRole(role);
        if (enabled != userPreferences.isNotificationEnabled((ServerConfiguration)medium.get(), (UserRole)confluenceUserRole)) {
            userPreferences.setNotificationEnabled((ServerConfiguration)medium.get(), (UserRole)confluenceUserRole, enabled);
            boolean defaults = this.userNotificationsDefaultsService.isUserSettingsDefaults(currentUserKey);
            this.eventPublisher.publish((Object)new UserNotificationPreferenceChangeEvent(mediumKey, role, enabled, defaults));
        }
        return Response.ok().build();
    }

    @GET
    @Path(value="notifications/static/{mediumKey}/{role}")
    public Response getStatusFor(@PathParam(value="mediumKey") String mediumKey, @PathParam(value="role") String role) {
        UserKey currentUserKey = this.userManager.getRemoteUserKey();
        Option medium = Iterables.findFirst((Iterable)this.serverManager.getServers(), this.withKey(mediumKey));
        if (!medium.isDefined()) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        UserNotificationPreferences userPreferences = this.userNotificationPreferencesManager.getPreferences(currentUserKey);
        boolean enabled = userPreferences.isNotificationEnabled((ServerConfiguration)medium.get(), (UserRole)new ConfluenceUserRole(role));
        return Response.ok((Object)String.valueOf(enabled)).build();
    }

    private Predicate<ServerConfiguration> withKey(String mediumKey) {
        return server -> server.getNotificationMedium().getKey().equals(mediumKey);
    }
}

