/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Issue
 *  com.atlassian.diagnostics.MonitoringService
 *  com.atlassian.diagnostics.PluginDetails
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.message.HelpPath
 *  com.atlassian.sal.api.message.HelpPathResolver
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.atlassian.soy.renderer.SoyException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal.web;

import com.atlassian.diagnostics.Issue;
import com.atlassian.diagnostics.MonitoringService;
import com.atlassian.diagnostics.PluginDetails;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.message.HelpPath;
import com.atlassian.sal.api.message.HelpPathResolver;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiagnosticsServlet
extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(DiagnosticsServlet.class);
    private final ApplicationProperties applicationProperties;
    private final HelpPathResolver helpPathResolver;
    private final PermissionEnforcer permissionEnforcer;
    private final SoyTemplateRenderer templateRenderer;
    private final MonitoringService monitoringService;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!this.permissionEnforcer.isSystemAdmin()) {
            response.sendError(403);
        } else if (request.getPathInfo().startsWith("/overview")) {
            this.showOverview(request, response);
        } else if (request.getPathInfo().startsWith("/detail")) {
            this.showDetail(request, response);
        } else {
            response.sendError(404);
        }
    }

    private void render(HttpServletResponse response, String templateName, Map<String, Object> data) throws IOException, ServletException {
        response.setContentType("text/html;charset=UTF-8");
        try {
            this.templateRenderer.render((Appendable)response.getWriter(), "com.atlassian.diagnostics.atlassian-diagnostics-plugin:diagnostics-templates", templateName, data);
        }
        catch (SoyException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException)cause;
            }
            throw new ServletException((Throwable)e);
        }
    }

    private void showDetail(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String path = request.getPathInfo();
        if (path != null) {
            String[] split = path.split("/");
            if (path.length() >= 4) {
                String issueId = split[2];
                String pluginKey = split[3];
                HelpPath helpPath = this.helpPathResolver.getHelpPath("atlassian.diagnostics.help.link.detail." + issueId);
                String url = helpPath == null ? "" : helpPath.getUrl();
                Optional<PluginDetails> pluginDetails = this.monitoringService.findAllPluginsWithAlerts().stream().filter(x -> x.getKey().equalsIgnoreCase(pluginKey)).findFirst();
                Optional<Issue> issueDetails = this.monitoringService.findAllIssues().stream().filter(x -> x.getId().equalsIgnoreCase(issueId)).findFirst();
                if (!pluginDetails.isPresent() || !issueDetails.isPresent()) {
                    response.sendError(404);
                    return;
                }
                ImmutableMap params = ImmutableMap.of((Object)"overviewUrl", (Object)(this.applicationProperties.getBaseUrl(UrlMode.AUTO) + "/plugins/servlet/diagnostics/overview"), (Object)"nodes", (Object)this.monitoringService.findAllNodesWithAlerts(), (Object)"issue", (Object)issueDetails.get(), (Object)"plugin", (Object)pluginDetails.get(), (Object)"helpPath", (Object)url);
                this.render(response, "diagnostics.detail", (Map<String, Object>)params);
            }
        } else {
            response.sendError(404);
        }
    }

    private void showOverview(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HelpPath helpPath = this.helpPathResolver.getHelpPath("atlassian.diagnostics.help.link");
        String url = helpPath == null ? "" : helpPath.getUrl();
        ImmutableMap map = ImmutableMap.of((Object)"helpUrl", (Object)url, (Object)"components", (Object)this.monitoringService.findAllComponents(), (Object)"nodeNames", (Object)this.monitoringService.findAllNodesWithAlerts(), (Object)"plugins", (Object)this.monitoringService.findAllPluginsWithAlerts());
        logger.info("Returning items: [{}]", (Object)map);
        this.render(response, "diagnostics.overview", (Map<String, Object>)map);
    }

    public DiagnosticsServlet(ApplicationProperties applicationProperties, HelpPathResolver helpPathResolver, PermissionEnforcer permissionEnforcer, SoyTemplateRenderer templateRenderer, MonitoringService monitoringService) {
        this.applicationProperties = applicationProperties;
        this.helpPathResolver = helpPathResolver;
        this.permissionEnforcer = permissionEnforcer;
        this.templateRenderer = templateRenderer;
        this.monitoringService = monitoringService;
    }
}

