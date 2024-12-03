/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.plugin.notifications.admin;

import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import java.net.URI;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractAdminServlet
extends HttpServlet {
    private final WebSudoManager webSudoManager;
    private final TemplateRenderer renderer;
    private final UserManager userManager;
    private final LoginUriProvider loginUriProvider;
    private final WebResourceManager webResourceManager;

    public AbstractAdminServlet(WebSudoManager webSudoManager, TemplateRenderer renderer, UserManager userManager, LoginUriProvider loginUriProvider, WebResourceManager webResourceManager) {
        this.webSudoManager = webSudoManager;
        this.renderer = renderer;
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.webResourceManager = webResourceManager;
    }

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            response.setContentType("text/html");
            this.webSudoManager.willExecuteWebSudoRequest(request);
            String remoteUsername = this.userManager.getRemoteUsername(request);
            if (!this.userManager.isSystemAdmin(remoteUsername)) {
                response.sendRedirect(this.loginUriProvider.getLoginUri(this.getUri(request)).toASCIIString());
                return;
            }
            this.requireResource(this.webResourceManager);
            this.renderResponse(this.renderer, request, response);
        }
        catch (WebSudoSessionException wes) {
            this.webSudoManager.enforceWebSudoProtection(request, response);
        }
    }

    protected abstract void requireResource(WebResourceManager var1);

    protected abstract void renderResponse(TemplateRenderer var1, HttpServletRequest var2, HttpServletResponse var3) throws IOException;

    private URI getUri(HttpServletRequest request) {
        StringBuffer builder = request.getRequestURL();
        if (request.getQueryString() != null) {
            builder.append("?");
            builder.append(request.getQueryString());
        }
        return URI.create(builder.toString());
    }
}

