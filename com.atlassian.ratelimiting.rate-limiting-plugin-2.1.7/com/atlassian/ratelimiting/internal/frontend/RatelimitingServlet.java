/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
package com.atlassian.ratelimiting.internal.frontend;

import com.atlassian.ratelimiting.license.LicenseChecker;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RatelimitingServlet
extends HttpServlet {
    private static final long serialVersionUID = -8970549086101986249L;
    private static final Logger logger = LoggerFactory.getLogger(RatelimitingServlet.class);
    private final SoyTemplateRenderer templateRenderer;
    private final PermissionEnforcer permissionEnforcer;
    private final LoginUriProvider loginUriProvider;
    private final LicenseChecker licenseChecker;

    public RatelimitingServlet(SoyTemplateRenderer templateRenderer, PermissionEnforcer permissionEnforcer, LoginUriProvider loginUriProvider, LicenseChecker licenseChecker) {
        this.templateRenderer = templateRenderer;
        this.permissionEnforcer = permissionEnforcer;
        this.loginUriProvider = loginUriProvider;
        this.licenseChecker = licenseChecker;
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
        response.setContentType("text/html");
        this.templateRenderer.render((Appendable)response.getWriter(), "com.atlassian.ratelimiting.rate-limiting-plugin:ratelimiting-templates", "ratelimiting.init", (Map)ImmutableMap.of((Object)"isValidLicense", (Object)this.licenseChecker.isDataCenterLicensed()));
    }
}

