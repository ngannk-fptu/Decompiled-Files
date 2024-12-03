/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.soy.renderer.SoyException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.plugins.cleanuphub;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CleanupHubServlet
extends HttpServlet {
    private static final String PLUGIN_KEY = "com.atlassian.plugins.cleanup-hub-plugin";
    private static final String ENCODING = StandardCharsets.UTF_8.name();
    @VisibleForTesting
    static final String RESOURCE_KEY = "com.atlassian.plugins.cleanup-hub-plugin:cleanup-hub-soy-resources";
    private static final String TEMPLATE_KEY = "atlassian.cleanup.base";
    private final SoyTemplateRenderer soyTemplateRenderer;
    private final UserManager userManager;
    private final LoginUriProvider loginUriProvider;
    private final ApplicationProperties applicationProperties;
    private final DarkFeatureManager darkFeatureManager;
    private final PermissionEnforcer permissionEnforcer;

    public CleanupHubServlet(SoyTemplateRenderer soyTemplateRenderer, UserManager userManager, LoginUriProvider loginUriProvider, ApplicationProperties applicationProperties, DarkFeatureManager darkFeatureManager, PermissionEnforcer permissionEnforcer) {
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.applicationProperties = applicationProperties;
        this.darkFeatureManager = darkFeatureManager;
        this.permissionEnforcer = permissionEnforcer;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserProfile user = this.userManager.getRemoteUser(req);
        if (user == null) {
            this.redirectToLogin(req, resp);
            return;
        }
        if (this.userManager.isAdmin(user.getUserKey())) {
            HashMap<String, Object> params = new HashMap<String, Object>();
            resp.setContentType("text/html;charset=" + ENCODING);
            Boolean isSysAdmin = this.userManager.isSystemAdmin(user.getUserKey());
            String platformId = this.applicationProperties.getPlatformId();
            if (platformId.equals("conf")) {
                Optional darkFeatureEnabled = this.darkFeatureManager.isEnabledForCurrentUser("confluence.retention.rules");
                Boolean isDarkFeatureEnabled = darkFeatureEnabled.isPresent() && (Boolean)darkFeatureEnabled.get() != false;
                params.put("isRetentionRulesEnabled", isSysAdmin != false && isDarkFeatureEnabled != false);
            }
            this.renderView(resp, params);
        } else if (this.permissionEnforcer.isAuthenticated()) {
            resp.sendError(403);
        } else {
            resp.sendRedirect(this.loginUriProvider.getLoginUri(URI.create(req.getRequestURL().toString())).toASCIIString());
        }
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        URI uri = this.getUri(request);
        response.sendRedirect(this.loginUriProvider.getLoginUri(uri).toASCIIString());
    }

    private URI getUri(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        if (request.getQueryString() != null) {
            requestURL.append("?").append(request.getQueryString());
        }
        return URI.create(requestURL.toString());
    }

    private void renderView(HttpServletResponse resp, Map<String, Object> params) throws IOException, ServletException {
        this.render(resp, TEMPLATE_KEY, params);
    }

    private void render(HttpServletResponse resp, String template, Map<String, Object> soyData) throws IOException, ServletException {
        try {
            this.soyTemplateRenderer.render((Appendable)resp.getWriter(), RESOURCE_KEY, template, soyData);
        }
        catch (SoyException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException)cause;
            }
            throw new ServletException((Throwable)e);
        }
    }
}

