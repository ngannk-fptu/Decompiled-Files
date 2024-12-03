/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
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

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.notifications.admin.AbstractAdminServlet;
import com.atlassian.plugin.notifications.api.macros.Macro;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ViewMacrosHelpServlet
extends AbstractAdminServlet {
    private final PluginAccessor pluginAccessor;

    public ViewMacrosHelpServlet(WebSudoManager webSudoManager, TemplateRenderer renderer, UserManager userManager, LoginUriProvider loginUriProvider, WebResourceManager webResourceManager, PluginAccessor pluginAccessor) {
        super(webSudoManager, renderer, userManager, loginUriProvider, webResourceManager);
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    protected void requireResource(WebResourceManager webResourceManager) {
    }

    @Override
    protected void renderResponse(TemplateRenderer renderer, HttpServletRequest request, HttpServletResponse response) throws IOException {
        HashMap context = Maps.newHashMap();
        List macros = this.pluginAccessor.getEnabledModulesByClass(Macro.class);
        context.put("macros", macros);
        renderer.render("templates/admin/view-macro-help.vm", (Map)context, (Writer)response.getWriter());
    }
}

