/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.plugin.notifications.rest;

import com.atlassian.plugin.notifications.api.HandleErrorFunction;
import com.atlassian.plugin.notifications.api.medium.NotificationMedium;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.module.NotificationMediumManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path(value="config")
@Produces(value={"application/json", "text/html"})
@Consumes(value={"application/json"})
@WebSudoRequired
public class ConfigResource {
    private final UserManager userManager;
    private final NotificationMediumManager notificationMediumManager;

    public ConfigResource(UserManager userManager, NotificationMediumManager notificationMediumManager) {
        this.userManager = userManager;
        this.notificationMediumManager = notificationMediumManager;
    }

    @GET
    @Path(value="{mediumKey}")
    @Produces(value={"text/html"})
    public Response getConfigForm(@Context HttpServletRequest request, @PathParam(value="mediumKey") String mediumKey) {
        String remoteUsername = this.userManager.getRemoteUsername(request);
        if (!this.userManager.isSystemAdmin(remoteUsername)) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).cacheControl(HandleErrorFunction.NO_CACHE).build();
        }
        NotificationMedium notificationMedium = this.notificationMediumManager.getNotificationMedium(mediumKey);
        if (notificationMedium == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).cacheControl(HandleErrorFunction.NO_CACHE).build();
        }
        return Response.ok((Object)notificationMedium.getServerConfigurationTemplate(new EmptyServerConfiguration())).cacheControl(HandleErrorFunction.NO_CACHE).build();
    }

    public static class EmptyServerConfiguration
    implements ServerConfiguration {
        @Override
        public int getId() {
            return -1;
        }

        @Override
        public NotificationMedium getNotificationMedium() {
            return null;
        }

        @Override
        public String getServerName() {
            return null;
        }

        @Override
        public String getDefaultUserIDTemplate() {
            return null;
        }

        @Override
        public String getFullName(I18nResolver i18n) {
            return null;
        }

        @Override
        public String getCustomTemplatePath() {
            return null;
        }

        @Override
        public Iterable<String> getGroupsWithAccess() {
            return Collections.emptySet();
        }

        @Override
        public boolean isConfigurable() {
            return true;
        }

        @Override
        public String getProperty(String propertyKey) {
            return "";
        }

        @Override
        public boolean isEnabledForAllUsers() {
            return false;
        }
    }
}

