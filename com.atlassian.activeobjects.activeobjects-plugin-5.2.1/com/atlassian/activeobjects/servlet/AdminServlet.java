/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.activeobjects.servlet;

import com.atlassian.activeobjects.admin.tables.TablesController;
import com.atlassian.activeobjects.servlet.AdminUi;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminServlet
extends HttpServlet {
    private static final String TEMPLATE = "templates/list-tables.vm";
    private final AdminUi adminUi;
    private final TemplateRenderer templateRenderer;
    private final TablesController tablesController;
    private final UserManager userManager;
    private WebSudoManager webSudoManager;
    private final LoginUriProvider loginUriProvider;

    public AdminServlet(AdminUi adminUi, LoginUriProvider loginUriProvider, TablesController tablesController, TemplateRenderer templateRenderer, UserManager userManager, WebSudoManager webSudoManager) {
        this.adminUi = adminUi;
        this.loginUriProvider = loginUriProvider;
        this.tablesController = tablesController;
        this.templateRenderer = templateRenderer;
        this.userManager = userManager;
        this.webSudoManager = webSudoManager;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (Strings.isNullOrEmpty((String)pathInfo) || !pathInfo.equals("/tables/list")) {
            resp.sendError(404);
            return;
        }
        try {
            this.webSudoManager.willExecuteWebSudoRequest(req);
            UserProfile user = this.userManager.getRemoteUser(req);
            if (user == null) {
                this.sendRedirectToLogin(req, resp);
                return;
            }
            if (!this.isUserSysAdmin(user)) {
                resp.sendError(403);
                return;
            }
            if (!this.adminUi.isEnabled()) {
                if (AdminUi.isDevModeEnabled()) {
                    resp.sendError(404, "The Active Objects admin UI is disabled, see the logs for more information.");
                } else {
                    resp.sendError(404);
                }
            }
            resp.setContentType("text/html;charset=UTF-8");
            this.templateRenderer.render(TEMPLATE, (Map)ImmutableMap.of((Object)"tables", this.tablesController.list()), (Writer)resp.getWriter());
        }
        catch (WebSudoSessionException ignored) {
            this.webSudoManager.enforceWebSudoProtection(req, resp);
        }
    }

    private boolean isUserSysAdmin(UserProfile user) {
        return user != null && this.userManager.isSystemAdmin(user.getUserKey());
    }

    private void sendRedirectToLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String requestUri = req.getRequestURI();
        String contextPath = req.getContextPath();
        if (!Strings.isNullOrEmpty((String)contextPath)) {
            requestUri = requestUri.substring(contextPath.length());
        }
        resp.sendRedirect(this.loginUriProvider.getLoginUri(URI.create(requestUri)).toString());
    }
}

