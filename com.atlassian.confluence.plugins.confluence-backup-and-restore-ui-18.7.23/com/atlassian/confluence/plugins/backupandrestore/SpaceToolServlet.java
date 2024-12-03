/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.service.content.SpaceService
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.user.User
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.backupandrestore;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.user.User;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

public class SpaceToolServlet
extends HttpServlet {
    private static final String RESOURCE_KEY = "com.atlassian.confluence.plugins.confluence-backup-and-restore-ui:backup-and-restore-ui-soy-resources";
    private static final String TEMPLATE_KEY = "confluence.backup.restore.ui.space.soy";
    private static final String DARK_FEATURE_KEY = "confluence.fast-xml-backup-restore";
    private final TemplateRenderer templateRenderer;
    private final LoginUriProvider loginUriProvider;
    private final SpaceService spaceService;
    private final UserManager userManager;
    private final DarkFeatureManager darkFeatureManager;
    private final SpacePermissionManager spacePermissionManager;

    @Autowired
    public SpaceToolServlet(@ComponentImport TemplateRenderer templateRenderer, @ComponentImport LoginUriProvider loginUriProvider, @ComponentImport SpaceService spaceService, @ComponentImport UserManager userManager, @ComponentImport DarkFeatureManager darkFeatureManager, @ComponentImport SpacePermissionManager spacePermissionManager) {
        this.templateRenderer = templateRenderer;
        this.loginUriProvider = loginUriProvider;
        this.spaceService = spaceService;
        this.userManager = userManager;
        this.darkFeatureManager = darkFeatureManager;
        this.spacePermissionManager = spacePermissionManager;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!this.darkFeatureManager.isEnabledForCurrentUser(DARK_FEATURE_KEY).orElse(false).booleanValue()) {
            response.sendRedirect(this.getFullUrl(request) + "/fourohfour.action");
            return;
        }
        String path = request.getPathInfo();
        if (path == null) {
            response.sendRedirect(this.getFullUrl(request) + "/fourohfour.action");
            return;
        }
        String[] spaceArray = request.getPathInfo().split("/");
        if (spaceArray.length < 2) {
            response.sendRedirect(this.getFullUrl(request) + "/fourohfour.action");
            return;
        }
        String spaceKey = spaceArray[1];
        Optional space = this.spaceService.find(new Expansion[0]).withKeys(new String[]{spaceKey}).fetch();
        if (space.isEmpty()) {
            response.sendRedirect(this.getFullUrl(request) + "/fourohfour.action");
            return;
        }
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        Space newSpace = new Space(((com.atlassian.confluence.api.model.content.Space)space.get()).getKey());
        newSpace.setId(((com.atlassian.confluence.api.model.content.Space)space.get()).getId());
        if (!this.spacePermissionManager.hasAllPermissions(List.of("SETSPACEPERMISSIONS", "EXPORTSPACE"), newSpace, (User)user)) {
            response.sendRedirect(this.getFullUrl(request) + "/notpermitted.action");
            return;
        }
        this.templateRenderer.renderTo((Appendable)response.getWriter(), RESOURCE_KEY, TEMPLATE_KEY, Map.of("spaceId", newSpace.getId(), "ttl", Integer.getInteger("confluence.backuprestore.backup.ttl-in-hours", 72)));
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

    private String getFullUrl(HttpServletRequest request) {
        StringBuffer url = request.getRequestURL();
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        return url.substring(0, url.indexOf(uri)) + contextPath;
    }
}

