/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.soy.renderer.SoyException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.rebuildindex;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

public class FrontendServlet
extends HttpServlet {
    private static final String RESOURCE_KEY = "com.atlassian.confluence.plugins.confluence-rebuild-index:confluence-rebuild-index-soy-resources";
    private static final String TEMPLATE_KEY = "confluence.rebuild.index.root";
    private static final String UNAUTHORISED_TEMPLATE_KEY = "confluence.rebuild.index.unauthorised";
    private static final String LEGACY_CONTENT_INDEX = "/admin/search-indexes.action";
    private final SoyTemplateRenderer soyTemplateRenderer;
    private final ClusterManager clusterManager;
    private final UserManager userManager;
    private final DarkFeatureManager darkFeatureManager;
    private final LoginUriProvider loginUriProvider;
    private final WebSudoManager webSudoManager;

    @Autowired
    public FrontendServlet(@ComponentImport SoyTemplateRenderer soyTemplateRenderer, @ComponentImport DarkFeatureManager darkFeatureManager, @ComponentImport ClusterManager clusterManager, @ComponentImport UserManager userManager, @ComponentImport LoginUriProvider loginUriProvider, @ComponentImport WebSudoManager webSudoManager) {
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.darkFeatureManager = darkFeatureManager;
        this.clusterManager = clusterManager;
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.webSudoManager = webSudoManager;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, SoyException {
        Boolean isDarkFeatureEnabled = this.darkFeatureManager.isEnabledForAllUsers("confluence.reindex.improvements").orElse(false);
        if (!isDarkFeatureEnabled.booleanValue()) {
            response.sendRedirect(request.getContextPath() + LEGACY_CONTENT_INDEX);
            return;
        }
        UserProfile user = this.userManager.getRemoteUser(request);
        if (user == null) {
            this.redirectToLogin(request, response);
            return;
        }
        if (!this.userManager.isAdmin(user.getUserKey())) {
            this.soyTemplateRenderer.render((Appendable)response.getWriter(), RESOURCE_KEY, UNAUTHORISED_TEMPLATE_KEY, Collections.emptyMap());
            return;
        }
        try {
            this.webSudoManager.willExecuteWebSudoRequest(request);
            ImmutableMap data = ImmutableMap.of((Object)"isClustered", (Object)this.clusterManager.isClustered());
            this.soyTemplateRenderer.render((Appendable)response.getWriter(), RESOURCE_KEY, TEMPLATE_KEY, (Map)data);
            return;
        }
        catch (WebSudoSessionException wes) {
            this.webSudoManager.enforceWebSudoProtection(request, response);
            return;
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
}

