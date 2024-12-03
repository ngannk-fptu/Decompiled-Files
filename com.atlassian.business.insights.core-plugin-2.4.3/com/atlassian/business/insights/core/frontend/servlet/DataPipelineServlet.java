/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.soy.renderer.SoyException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.business.insights.core.frontend.servlet;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataPipelineServlet
extends HttpServlet {
    static final String RESOURCE_KEY = "com.atlassian.business.insights.core-plugin:bi-frontend-base-resources";
    static final String TEMPLATE_KEY_PREFIX = "atlassian.business.insights.";
    static final String ENCODING = StandardCharsets.UTF_8.name();
    private final SoyTemplateRenderer soyTemplateRenderer;
    private final UserManager userManager;
    private final LoginUriProvider loginUriProvider;
    private final WebSudoManager webSudoManager;
    private final Logger logger;

    public DataPipelineServlet(SoyTemplateRenderer soyTemplateRenderer, UserManager userManager, LoginUriProvider loginUriProvider, WebSudoManager webSudoManager) {
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.webSudoManager = webSudoManager;
        this.logger = LoggerFactory.getLogger(DataPipelineServlet.class);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        UserProfile user = this.userManager.getRemoteUser(req);
        if (user == null) {
            this.redirectToLogin(req, res);
            return;
        }
        res.setContentType("text/html;charset=" + ENCODING);
        try {
            this.webSudoManager.willExecuteWebSudoRequest(req);
            if (!this.userManager.isSystemAdmin(user.getUserKey())) {
                this.soyTemplateRenderer.render((Appendable)res.getWriter(), RESOURCE_KEY, "atlassian.business.insights.unauthorised", null);
            } else {
                this.soyTemplateRenderer.render((Appendable)res.getWriter(), RESOURCE_KEY, "atlassian.business.insights.authorised", null);
            }
        }
        catch (SoyException e) {
            this.logger.error(String.format("Error rendering Soy templates: %s; %s", e.toString(), e.getCause()));
            throw new ServletException(String.format("Error rendering Soy templates: %s", new Object[]{e}));
        }
        catch (WebSudoSessionException e) {
            this.webSudoManager.enforceWebSudoProtection(req, res);
        }
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        URI uri = URI.create(request.getRequestURL().toString());
        response.sendRedirect(this.loginUriProvider.getLoginUri(uri).toASCIIString());
    }
}

