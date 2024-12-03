/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.collect.Maps
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.plugin.notifications.admin;

import com.atlassian.plugin.notifications.admin.AbstractAdminServlet;
import com.atlassian.plugin.notifications.spi.NotificationFilterProvider;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NotificationsAdminServlet
extends AbstractAdminServlet {
    private final NotificationFilterProvider filterProvider;

    public NotificationsAdminServlet(WebSudoManager webSudoManager, TemplateRenderer renderer, UserManager userManager, LoginUriProvider loginUriProvider, WebResourceManager webResourceManager, NotificationFilterProvider filterProvider) {
        super(webSudoManager, renderer, userManager, loginUriProvider, webResourceManager);
        this.filterProvider = filterProvider;
    }

    @Override
    protected void requireResource(WebResourceManager webResourceManager) {
        webResourceManager.requireResource("com.atlassian.plugin.notifications.notifications-module:notification-scheme");
        for (String resource : this.filterProvider.getWebResourcesToRequire()) {
            webResourceManager.requireResource(resource);
        }
    }

    @Override
    protected void renderResponse(TemplateRenderer renderer, HttpServletRequest request, HttpServletResponse response) throws IOException {
        HashMap context = Maps.newHashMap();
        renderer.render("templates/admin/notification-scheme-admin.vm", (Map)context, (Writer)response.getWriter());
    }
}

