/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.plugin.notifications.admin;

import com.atlassian.plugin.notifications.admin.AbstractAdminServlet;
import com.atlassian.plugin.notifications.config.ServerConfigurationManager;
import com.atlassian.plugin.notifications.module.NotificationMediumManager;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ViewServersServlet
extends AbstractAdminServlet {
    private final ServerConfigurationManager serverConfigurationManager;
    private final NotificationMediumManager notificationMediumManager;

    public ViewServersServlet(WebSudoManager webSudoManager, TemplateRenderer renderer, UserManager userManager, LoginUriProvider loginUriProvider, WebResourceManager webResourceManager, ServerConfigurationManager serverConfigurationManager, NotificationMediumManager notificationMediumManager) {
        super(webSudoManager, renderer, userManager, loginUriProvider, webResourceManager);
        this.serverConfigurationManager = serverConfigurationManager;
        this.notificationMediumManager = notificationMediumManager;
    }

    @Override
    protected void requireResource(WebResourceManager webResourceManager) {
        webResourceManager.requireResource("com.atlassian.plugin.notifications.notifications-module:notification-server");
    }

    @Override
    protected void renderResponse(TemplateRenderer renderer, HttpServletRequest request, HttpServletResponse response) throws IOException {
        HashMap context = Maps.newHashMap();
        context.put("status", this.serverConfigurationManager.getNotificationStatus());
        context.put("servers", Lists.newArrayList(this.serverConfigurationManager.getServers()));
        context.put("notificationMediumManager", this.notificationMediumManager);
        renderer.render("templates/admin/view-notification-servers.vm", (Map)context, (Writer)response.getWriter());
    }
}

