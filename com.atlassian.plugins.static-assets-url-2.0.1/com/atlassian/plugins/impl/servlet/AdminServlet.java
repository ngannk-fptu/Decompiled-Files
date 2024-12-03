/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.plugins.impl.servlet;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminServlet
extends HttpServlet {
    private static final String PLUGIN_KEY = "com.atlassian.plugins.static-assets-url";
    private static final String SSR_RESOURCE_KEY = "com.atlassian.plugins.static-assets-url:ssr-admin-page";
    private static final String SSR_TEMPLATE = "StaticAssetsPlugin.Templates.Admin.page";
    private static final String WR_CONTEXT = "com.atlassian.plugins.static-assets-url.cdn-admin-init";
    private LoginUriProvider loginUriProvider;
    private PageBuilderService pageBuilderService;
    private SoyTemplateRenderer soyTemplateRenderer;
    private UserManager userManager;
    private WebSudoManager webSudoManager;

    public AdminServlet(UserManager userManager, WebSudoManager webSudoManager, LoginUriProvider loginUriProvider, SoyTemplateRenderer soyTemplateRenderer, PageBuilderService pageBuilderService) {
        this.userManager = userManager;
        this.webSudoManager = webSudoManager;
        this.loginUriProvider = loginUriProvider;
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.pageBuilderService = pageBuilderService;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserKey userKey = this.userManager.getRemoteUserKey(req);
        if (userKey == null) {
            resp.sendRedirect(this.loginUriProvider.getLoginUri(URI.create(req.getRequestURL().toString())).toASCIIString());
            return;
        }
        if (!this.userManager.isSystemAdmin(userKey)) {
            resp.sendError(403);
            return;
        }
        resp.setContentType("text/html;charset=utf-8");
        try {
            this.webSudoManager.willExecuteWebSudoRequest(req);
            this.pageBuilderService.assembler().resources().requireContext(WR_CONTEXT);
            this.soyTemplateRenderer.render((Appendable)resp.getWriter(), SSR_RESOURCE_KEY, SSR_TEMPLATE, new HashMap());
        }
        catch (WebSudoSessionException ignored) {
            this.webSudoManager.enforceWebSudoProtection(req, resp);
        }
    }
}

