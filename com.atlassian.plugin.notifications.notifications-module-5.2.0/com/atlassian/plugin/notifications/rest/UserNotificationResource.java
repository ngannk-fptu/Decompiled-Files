/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserManager
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.notifications.rest;

import com.atlassian.plugin.notifications.api.HandleErrorFunction;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferences;
import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferencesManager;
import com.atlassian.plugin.notifications.config.ServerConfigurationManager;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.atlassian.plugin.notifications.spi.UserRolesProvider;
import com.atlassian.sal.api.user.UserManager;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.StringUtils;

@Path(value="user")
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
public class UserNotificationResource {
    private final ServerConfigurationManager serverConfigurationManager;
    private final UserNotificationPreferencesManager prefsManager;
    private final UserManager userManager;
    private final UserRolesProvider roleProvider;

    public UserNotificationResource(ServerConfigurationManager serverConfigurationManager, UserNotificationPreferencesManager prefsManager, UserManager userManager, UserRolesProvider roleProvider) {
        this.serverConfigurationManager = serverConfigurationManager;
        this.prefsManager = prefsManager;
        this.userManager = userManager;
        this.roleProvider = roleProvider;
    }

    @GET
    @Path(value="notifications/{role}/{serverId}")
    public Response getNotification(@PathParam(value="serverId") int id, @PathParam(value="role") String role) {
        UserNotificationPreferences preferences = this.prefsManager.getPreferences(this.userManager.getRemoteUserKey());
        UserRole userRole = this.convertUserRole(role);
        ServerConfiguration server = this.serverConfigurationManager.getServer(id);
        if (preferences == null || userRole == null || server == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).cacheControl(HandleErrorFunction.NO_CACHE).build();
        }
        boolean notificationEnabled = preferences.isNotificationEnabled(server, userRole);
        return Response.ok((Object)notificationEnabled).cacheControl(HandleErrorFunction.NO_CACHE).build();
    }

    @PUT
    @Path(value="notifications/{role}/{serverId}")
    public Response setNotification(@PathParam(value="serverId") int id, @PathParam(value="role") String role) {
        UserNotificationPreferences preferences = this.prefsManager.getPreferences(this.userManager.getRemoteUserKey());
        UserRole userRole = this.convertUserRole(role);
        ServerConfiguration server = this.serverConfigurationManager.getServer(id);
        if (preferences == null || userRole == null || server == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).cacheControl(HandleErrorFunction.NO_CACHE).build();
        }
        preferences.setNotificationEnabled(server, userRole, true);
        return Response.ok().cacheControl(HandleErrorFunction.NO_CACHE).build();
    }

    @GET
    @Path(value="notifications/ownNotifications/{serverId}")
    public Response getOwnNotification(@PathParam(value="serverId") int id) {
        UserNotificationPreferences preferences = this.prefsManager.getPreferences(this.userManager.getRemoteUserKey());
        ServerConfiguration server = this.serverConfigurationManager.getServer(id);
        if (preferences == null || server == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).cacheControl(HandleErrorFunction.NO_CACHE).build();
        }
        boolean notificationEnabled = preferences.isOwnEventNotificationsEnabled(server);
        return Response.ok((Object)notificationEnabled).cacheControl(HandleErrorFunction.NO_CACHE).build();
    }

    @PUT
    @Path(value="notifications/ownNotifications/{serverId}")
    public Response setOwnNotification(@PathParam(value="serverId") int id) {
        UserNotificationPreferences preferences = this.prefsManager.getPreferences(this.userManager.getRemoteUserKey());
        ServerConfiguration server = this.serverConfigurationManager.getServer(id);
        if (preferences == null || server == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).cacheControl(HandleErrorFunction.NO_CACHE).build();
        }
        preferences.setOwnEventNotificationsEnabled(server, true);
        return Response.ok().cacheControl(HandleErrorFunction.NO_CACHE).build();
    }

    @DELETE
    @Path(value="notifications/{role}/{serverId}")
    public Response unsetNotification(@PathParam(value="serverId") int id, @PathParam(value="role") String role) {
        UserNotificationPreferences preferences = this.prefsManager.getPreferences(this.userManager.getRemoteUserKey());
        UserRole userRole = this.convertUserRole(role);
        ServerConfiguration server = this.serverConfigurationManager.getServer(id);
        if (preferences == null || userRole == null || server == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).cacheControl(HandleErrorFunction.NO_CACHE).build();
        }
        preferences.setNotificationEnabled(server, userRole, false);
        return Response.ok().cacheControl(HandleErrorFunction.NO_CACHE).build();
    }

    @DELETE
    @Path(value="notifications/ownNotifications/{serverId}")
    public Response unsetNotification(@PathParam(value="serverId") int id) {
        UserNotificationPreferences preferences = this.prefsManager.getPreferences(this.userManager.getRemoteUserKey());
        ServerConfiguration server = this.serverConfigurationManager.getServer(id);
        if (preferences == null || server == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).cacheControl(HandleErrorFunction.NO_CACHE).build();
        }
        preferences.setOwnEventNotificationsEnabled(server, false);
        return Response.ok().cacheControl(HandleErrorFunction.NO_CACHE).build();
    }

    @PUT
    @Path(value="mapping")
    public Response updateServerMapping(ServerMapping mappings) {
        UserNotificationPreferences preferences = this.prefsManager.getPreferences(this.userManager.getRemoteUserKey());
        if (preferences == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).cacheControl(HandleErrorFunction.NO_CACHE).build();
        }
        for (Mapping mapping : mappings.getMappings()) {
            int serverId = Integer.parseInt(mapping.getName());
            ServerConfiguration server = this.serverConfigurationManager.getServer(serverId);
            if (server == null || !StringUtils.isNotBlank((CharSequence)mapping.getValue())) continue;
            preferences.setServerMapping(serverId, mapping.getValue());
        }
        return Response.ok().cacheControl(HandleErrorFunction.NO_CACHE).build();
    }

    private UserRole convertUserRole(String role) {
        return this.roleProvider.getRole(role);
    }

    @XmlRootElement
    public static class Mapping {
        @XmlElement
        private String name;
        @XmlElement
        private String value;

        private Mapping() {
        }

        public Mapping(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }
    }

    @XmlRootElement
    public static class ServerMapping {
        @XmlElement
        private List<Mapping> mappings;

        private ServerMapping() {
        }

        public ServerMapping(List<Mapping> mappings) {
            this.mappings = mappings;
        }

        public List<Mapping> getMappings() {
            return this.mappings;
        }
    }
}

