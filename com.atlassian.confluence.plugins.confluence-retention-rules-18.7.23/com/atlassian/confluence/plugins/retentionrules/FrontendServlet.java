/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.retention.RetentionFeatureChecker
 *  com.atlassian.confluence.pages.TrashManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.soy.renderer.SoyException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.retentionrules;

import com.atlassian.confluence.api.service.retention.RetentionFeatureChecker;
import com.atlassian.confluence.pages.TrashManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class FrontendServlet
extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(FrontendServlet.class);
    protected static final String RESOURCE_KEY = "com.atlassian.confluence.plugins.confluence-retention-rules:confluence-retention-rules-soy-resources";
    protected static final String GLOBAL_TEMPLATE_KEY = "confluence.retention.rules.global";
    protected static final String UNAUTHORISED_TEMPLATE_KEY = "confluence.retention.rules.unauthorised";
    protected static final String VIEW_GENERAL_CONFIG = "/admin/viewgeneralconfig.action";
    private final SoyTemplateRenderer soyTemplateRenderer;
    private final UserManager userManager;
    private final RetentionFeatureChecker retentionFeatureChecker;
    private final LoginUriProvider loginUriProvider;
    private final WebSudoManager webSudoManager;
    private final TrashManager trashManager;

    @Autowired
    public FrontendServlet(@ComponentImport SoyTemplateRenderer soyTemplateRenderer, @ComponentImport RetentionFeatureChecker retentionFeatureChecker, @ComponentImport UserManager userManager, @ComponentImport LoginUriProvider loginUriProvider, @ComponentImport WebSudoManager webSudoManager, @ComponentImport TrashManager trashManager) {
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.retentionFeatureChecker = retentionFeatureChecker;
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.webSudoManager = webSudoManager;
        this.trashManager = trashManager;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (this.retentionRulesFeatureNotAvailable()) {
            try {
                response.sendRedirect(request.getContextPath() + VIEW_GENERAL_CONFIG);
            }
            catch (IOException e) {
                log.warn("Error redirecting to General config page", (Throwable)e);
            }
            return;
        }
        UserProfile user = this.userManager.getRemoteUser(request);
        if (user == null) {
            try {
                this.redirectToLogin(request, response);
            }
            catch (IOException e) {
                log.warn("Error redirecting to login page", (Throwable)e);
            }
            return;
        }
        try {
            if (!this.userManager.isSystemAdmin(user.getUserKey())) {
                this.soyTemplateRenderer.render((Appendable)response.getWriter(), RESOURCE_KEY, UNAUTHORISED_TEMPLATE_KEY, Collections.emptyMap());
                return;
            }
            try {
                this.webSudoManager.willExecuteWebSudoRequest(request);
                HashMap data = new HashMap();
                this.trashManager.getTrashDateMigrationTime().ifPresent(instant -> data.put("trashDateMigrationTime", Date.from(instant)));
                this.soyTemplateRenderer.render((Appendable)response.getWriter(), RESOURCE_KEY, GLOBAL_TEMPLATE_KEY, data);
            }
            catch (WebSudoSessionException wes) {
                this.webSudoManager.enforceWebSudoProtection(request, response);
            }
        }
        catch (SoyException e) {
            log.warn("Error rendering Soy template", (Throwable)e);
        }
    }

    protected SoyTemplateRenderer getSoyTemplateRenderer() {
        return this.soyTemplateRenderer;
    }

    protected UserManager getUserManager() {
        return this.userManager;
    }

    protected WebSudoManager getWebSudoManager() {
        return this.webSudoManager;
    }

    protected TrashManager getTrashManager() {
        return this.trashManager;
    }

    protected boolean retentionRulesFeatureNotAvailable() {
        return !this.retentionFeatureChecker.isFeatureAvailable();
    }

    protected void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

