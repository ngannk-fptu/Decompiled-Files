/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.plugin.notifications.ui;

import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.notification.UserNotificationPreferencesManager;
import com.atlassian.plugin.notifications.config.UserServerManager;
import com.atlassian.plugin.notifications.spi.UserRolesProvider;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.annotation.Nullable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;

public class UserSettingsServlet
extends HttpServlet {
    private final TemplateRenderer renderer;
    private final UserManager userManager;
    private final LoginUriProvider loginUriProvider;
    private final WebResourceManager webResourceManager;
    private final UserNotificationPreferencesManager prefManager;
    private final UserServerManager userServerManager;
    private final I18nResolver i18n;
    private final UserRolesProvider rolesProvider;
    private final WebInterfaceManager webInterfaceManager;

    public UserSettingsServlet(TemplateRenderer renderer, UserManager userManager, LoginUriProvider loginUriProvider, WebResourceManager webResourceManager, UserNotificationPreferencesManager prefManager, UserServerManager userServerManager, @Qualifier(value="i18nResolver") I18nResolver i18n, UserRolesProvider rolesProvider, WebInterfaceManager webInterfaceManager) {
        this.renderer = renderer;
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.webResourceManager = webResourceManager;
        this.prefManager = prefManager;
        this.userServerManager = userServerManager;
        this.i18n = i18n;
        this.rolesProvider = rolesProvider;
        this.webInterfaceManager = webInterfaceManager;
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        UserKey remoteUserKey = this.userManager.getRemoteUserKey(request);
        if (remoteUserKey == null) {
            response.sendRedirect(this.loginUriProvider.getLoginUri(this.getUri(request)).toASCIIString());
            return;
        }
        this.webResourceManager.requireResource("com.atlassian.plugin.notifications.notifications-module:notification-prefs");
        HashMap<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> webPanelContext = this.makeWebPanelContext(remoteUserKey);
        ArrayList servers = Lists.newArrayList(this.userServerManager.getServers(remoteUserKey));
        webPanelContext.put("servers", servers);
        params.put("webPanelContext", webPanelContext);
        params.put("servers", servers);
        params.put("i18n", this.i18n);
        params.put("webInterfaceManager", this.webInterfaceManager);
        HashSet serverIdsVisited = Sets.newHashSet((Iterable)Iterables.transform((Iterable)servers, (Function)new Function<ServerConfiguration, Integer>(){

            public Integer apply(@Nullable ServerConfiguration input) {
                return input.getId();
            }
        }));
        this.userServerManager.setVisited(remoteUserKey, serverIdsVisited);
        this.renderer.render("templates/usersettings.vm", params, (Writer)response.getWriter());
    }

    private Map<String, Object> makeWebPanelContext(UserKey remoteUserKey) {
        HashMap params = Maps.newHashMap();
        params.put("i18n", this.i18n);
        params.put("profileUser", this.userManager.getUserProfile(remoteUserKey));
        params.put("userRoles", this.rolesProvider.getRoles());
        params.put("userPrefs", this.prefManager.getPreferences(remoteUserKey));
        return params;
    }

    private URI getUri(HttpServletRequest request) {
        StringBuffer builder = request.getRequestURL();
        if (request.getQueryString() != null) {
            builder.append("?");
            builder.append(request.getQueryString());
        }
        return URI.create(builder.toString());
    }
}

