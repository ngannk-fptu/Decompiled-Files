/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
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
package com.atlassian.plugins.custom_apps.ui.deprecated;

import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.plugins.custom_apps.security.authentication.LoginPage;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class AdminServlet
extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(AdminServlet.class);
    private final WebResourceManager webResourceManager;
    private final SoyTemplateRenderer soyTemplateRenderer;
    private final UserManager userManager;
    private final I18nResolver i18nResolver;
    private final WebSudoManager webSudoManager;
    private final LoginPage loginPage;

    public AdminServlet(WebResourceManager webResourceManager, SoyTemplateRenderer soyTemplateRenderer, UserManager userManager, I18nResolver i18nResolver, WebSudoManager webSudoManager, LoginPage loginPage) {
        this.webResourceManager = webResourceManager;
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.userManager = userManager;
        this.i18nResolver = i18nResolver;
        this.webSudoManager = webSudoManager;
        this.loginPage = loginPage;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(req);
            if (!this.userManager.isAdmin(this.userManager.getRemoteUsername(req))) {
                this.loginPage.redirect(req, resp);
            } else {
                this.webResourceManager.requireResource("com.atlassian.plugins.atlassian-nav-links-plugin:custom-apps-admin-ui-resources-old");
                resp.setContentType("text/html");
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("title", this.i18nResolver.getText("custom-apps.page.title"));
                params.put("description", this.i18nResolver.getText("custom-apps.page.description"));
                try {
                    resp.getWriter().print(this.soyTemplateRenderer.render("com.atlassian.plugins.atlassian-nav-links-plugin:custom-apps-admin-page-template", "com.atlassian.plugins.custom_apps.ui.customAppsAdminPage", params));
                }
                catch (SoyException e) {
                    log.error("Error rendering template", (Throwable)e);
                    resp.sendError(500, e.getMessage());
                }
            }
        }
        catch (WebSudoSessionException wse) {
            this.webSudoManager.enforceWebSudoProtection(req, resp);
        }
    }
}

