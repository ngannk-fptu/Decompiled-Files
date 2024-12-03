/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.permission.AuthorisationException
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.soy.renderer.SoyException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.denormalisedpermissions;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.permission.AuthorisationException;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

public class FrontendServlet
extends HttpServlet {
    private static final String RESOURCE_KEY = "com.atlassian.confluence.plugins.confluence-denormalised-permissions:confluence-denormalised-permissions-soy-resources";
    private static final String TEMPLATE_KEY = "confluence.denormalised.permissions.root";
    private final SoyTemplateRenderer soyTemplateRenderer;
    private final UserManager userManager;
    private final LoginUriProvider loginUriProvider;
    private final WebSudoManager webSudoManager;

    @Autowired
    public FrontendServlet(@ComponentImport SoyTemplateRenderer soyTemplateRenderer, @ComponentImport UserManager userManager, @ComponentImport LoginUriProvider loginUriProvider, @ComponentImport WebSudoManager webSudoManager) {
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.webSudoManager = webSudoManager;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, SoyException {
        UserProfile user = this.userManager.getRemoteUser(request);
        if (user == null) {
            this.redirectToLogin(request, response);
            return;
        }
        if (!this.userManager.isAdmin(user.getUserKey())) {
            throw new AuthorisationException();
        }
        try {
            this.webSudoManager.willExecuteWebSudoRequest(request);
            this.soyTemplateRenderer.render((Appendable)response.getWriter(), RESOURCE_KEY, TEMPLATE_KEY, Collections.emptyMap());
        }
        catch (WebSudoSessionException wes) {
            this.webSudoManager.enforceWebSudoProtection(request, response);
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

