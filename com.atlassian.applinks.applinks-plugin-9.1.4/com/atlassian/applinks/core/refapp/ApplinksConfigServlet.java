/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.applinks.core.refapp;

import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

public class ApplinksConfigServlet
extends HttpServlet {
    private final TemplateRenderer templateRenderer;
    private final InternalHostApplication internalHostApplication;
    private final PluginSettings pluginSettings;
    private final WebSudoManager webSudoManager;

    public ApplinksConfigServlet(TemplateRenderer templateRenderer, InternalHostApplication internalHostApplication, PluginSettingsFactory pluginSettingsFactory, WebSudoManager webSudoManager) {
        this.templateRenderer = templateRenderer;
        this.internalHostApplication = internalHostApplication;
        this.pluginSettings = pluginSettingsFactory.createGlobalSettings();
        this.webSudoManager = webSudoManager;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("serverName", this.internalHostApplication.getName());
        resp.setContentType("text/html");
        this.templateRenderer.render("templates/host/refapp/config.vm", params, (Writer)resp.getWriter());
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String serverName = req.getParameter("serverName");
        if (!StringUtils.isEmpty((CharSequence)serverName)) {
            this.pluginSettings.put("com.atlassian.applinks.host.refapp.instanceName", (Object)serverName);
        }
        resp.sendRedirect("./applinksconfig");
    }
}

