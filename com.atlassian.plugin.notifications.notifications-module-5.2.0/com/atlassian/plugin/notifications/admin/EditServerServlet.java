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
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.notifications.admin;

import com.atlassian.plugin.notifications.admin.AbstractAdminServlet;
import com.atlassian.plugin.notifications.api.medium.NotificationMedium;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.config.ServerConfigurationManager;
import com.atlassian.plugin.notifications.spi.salext.GroupManager;
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
import org.apache.commons.lang3.StringUtils;

public class EditServerServlet
extends AbstractAdminServlet {
    private final ServerConfigurationManager serverConfigurationManager;
    private final GroupManager groupManager;

    public EditServerServlet(WebSudoManager webSudoManager, TemplateRenderer renderer, UserManager userManager, LoginUriProvider loginUriProvider, WebResourceManager webResourceManager, ServerConfigurationManager serverConfigurationManager, GroupManager groupManager) {
        super(webSudoManager, renderer, userManager, loginUriProvider, webResourceManager);
        this.serverConfigurationManager = serverConfigurationManager;
        this.groupManager = groupManager;
    }

    @Override
    protected void requireResource(WebResourceManager webResourceManager) {
        webResourceManager.requireResource("com.atlassian.plugin.notifications.notifications-module:notification-server");
    }

    @Override
    protected void renderResponse(TemplateRenderer renderer, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String serverId = request.getParameter("id");
        if (StringUtils.isBlank((CharSequence)serverId) || !StringUtils.isNumeric((CharSequence)serverId)) {
            response.setStatus(404);
            response.getWriter().write("Invalid server id specified.");
            return;
        }
        ServerConfiguration server = this.serverConfigurationManager.getServer(Integer.parseInt(serverId));
        if (server == null) {
            response.setStatus(404);
            response.getWriter().write("Invalid server id specified.");
            return;
        }
        NotificationMedium notificationMedium = server.getNotificationMedium();
        HashMap context = Maps.newHashMap();
        context.put("serverConfiguration", server);
        context.put("groups", Lists.newArrayList(this.groupManager.getGroups()));
        if (notificationMedium != null) {
            context.put("mediumConfigFormHtml", notificationMedium.getServerConfigurationTemplate(server));
        }
        renderer.render("templates/admin/editnotificationserver.vm", (Map)context, (Writer)response.getWriter());
    }
}

