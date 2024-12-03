/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.confluence.setup.BuildInformation
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.permission.AuthorisationException
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.backupandrestore;

import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.setup.BuildInformation;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.permission.AuthorisationException;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class AdminServlet
extends HttpServlet {
    private static final String RESOURCE_KEY = "com.atlassian.confluence.plugins.confluence-backup-and-restore-ui:backup-and-restore-ui-soy-resources";
    private static final String TEMPLATE_KEY = "confluence.backup.restore.ui.admin.soy";
    private static final String DARK_FEATURE_KEY = "confluence.fast-xml-backup-restore";
    private static final Logger logger = LoggerFactory.getLogger(AdminServlet.class);
    private final TemplateRenderer templateRenderer;
    private final PermissionEnforcer permissionEnforcer;
    private final WebSudoManager webSudoManager;
    private final LoginUriProvider loginUriProvider;
    private final DarkFeatureManager darkFeatureManager;
    private final ApplicationProperties applicationProperties;

    @Autowired
    public AdminServlet(@ComponentImport TemplateRenderer templateRenderer, @ComponentImport PermissionEnforcer permissionEnforcer, @ComponentImport WebSudoManager webSudoManager, @ComponentImport LoginUriProvider loginUriProvider, @ComponentImport DarkFeatureManager darkFeatureManager, @ComponentImport ApplicationProperties applicationProperties) {
        this.templateRenderer = templateRenderer;
        this.permissionEnforcer = permissionEnforcer;
        this.webSudoManager = webSudoManager;
        this.loginUriProvider = loginUriProvider;
        this.darkFeatureManager = darkFeatureManager;
        this.applicationProperties = applicationProperties;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!this.darkFeatureManager.isEnabledForCurrentUser(DARK_FEATURE_KEY).orElse(false).booleanValue()) {
            response.sendRedirect(this.getFullUrl(request) + "/fourohfour.action");
            return;
        }
        if (this.permissionEnforcer.isSystemAdmin()) {
            try {
                this.webSudoManager.willExecuteWebSudoRequest(request);
                this.renderTemplate(response);
            }
            catch (WebSudoSessionException e) {
                this.webSudoManager.enforceWebSudoProtection(request, response);
            }
        } else {
            if (this.permissionEnforcer.isAuthenticated()) {
                throw new AuthorisationException();
            }
            this.redirectToLogin(request, response);
        }
    }

    private void renderTemplate(HttpServletResponse response) throws IOException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("ttl", Integer.getInteger("confluence.backuprestore.backup.ttl-in-hours", 72));
        map.put("currentVersion", BuildInformation.INSTANCE.getVersionNumber());
        map.put("minVersion", "6.0.5");
        if (this.applicationProperties.getSharedHomeDirectory().isPresent()) {
            this.setPaths(map, (Path)this.applicationProperties.getSharedHomeDirectory().get());
        } else if (this.applicationProperties.getLocalHomeDirectory().isPresent()) {
            this.setPaths(map, (Path)this.applicationProperties.getLocalHomeDirectory().get());
        } else {
            logger.error("no application home directories found");
        }
        this.templateRenderer.renderTo((Appendable)response.getWriter(), RESOURCE_KEY, TEMPLATE_KEY, map);
    }

    private void setPaths(Map<String, Object> map, Path path) {
        map.put("sitePath", path.resolve("restore").resolve("site").toString());
        map.put("spacePath", path.resolve("restore").resolve("space").toString());
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

