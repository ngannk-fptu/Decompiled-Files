/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.zdu;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.zdu.LicenseService;
import com.atlassian.zdu.internal.api.ClusterManagerAdapter;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateServlet
extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(TemplateServlet.class);
    private final SoyTemplateRenderer templateRenderer;
    private final PermissionEnforcer permissionEnforcer;
    private final LoginUriProvider loginUriProvider;
    private final ApplicationProperties applicationProperties;
    private final LicenseService licenseService;
    private final ClusterManagerAdapter clusterManagerAdapter;

    public TemplateServlet(SoyTemplateRenderer templateRenderer, PermissionEnforcer permissionEnforcer, LoginUriProvider loginUriProvider, ApplicationProperties applicationProperties, LicenseService licenseService, ClusterManagerAdapter clusterManagerAdapter) {
        this.templateRenderer = templateRenderer;
        this.permissionEnforcer = permissionEnforcer;
        this.loginUriProvider = loginUriProvider;
        this.applicationProperties = applicationProperties;
        this.licenseService = licenseService;
        this.clusterManagerAdapter = clusterManagerAdapter;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            if (this.permissionEnforcer.isSystemAdmin()) {
                this.render(response);
            } else if (this.permissionEnforcer.isAuthenticated()) {
                response.sendError(403);
            } else {
                response.sendRedirect(this.loginUriProvider.getLoginUri(URI.create(request.getRequestURL().toString())).toASCIIString());
            }
        }
        catch (IOException exception) {
            logger.error("Unable to render template", (Throwable)exception);
            response.sendError(500);
        }
    }

    private void render(HttpServletResponse response) throws IOException {
        String templateName;
        response.setContentType("text/html");
        String templateKey = "com.atlassian.zdu.refapp-zdu-plugin:soy-templates";
        String string = templateName = this.licenseService.isDataCenter() ? "zdu.clustered" : "zdu.server";
        if (this.applicationProperties.getDisplayName().equalsIgnoreCase("confluence")) {
            templateKey = "com.atlassian.zdu.confluence-zdu-plugin:soy-templates";
            if (this.licenseService.isDataCenter() && !this.clusterManagerAdapter.isClustered()) {
                templateName = "zdu.sndc";
            }
        }
        if (this.applicationProperties.getDisplayName().equalsIgnoreCase("bitbucket")) {
            templateKey = "com.atlassian.zdu.bitbucket-zdu-plugin:soy-templates";
        }
        this.templateRenderer.render((Appendable)response.getWriter(), templateKey, templateName, (Map)ImmutableMap.of((Object)"productName", (Object)this.applicationProperties.getDisplayName()));
    }
}

