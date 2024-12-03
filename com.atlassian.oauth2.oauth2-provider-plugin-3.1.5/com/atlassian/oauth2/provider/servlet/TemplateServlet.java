/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserRole
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.google.common.collect.ImmutableMap
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.oauth2.provider.servlet;

import com.atlassian.oauth2.common.web.loopsprevention.RedirectsLoopPreventer;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserRole;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TemplateServlet
extends HttpServlet {
    static final String URL = "/plugins/servlet/oauth2/provider";
    private final SoyTemplateRenderer templateRenderer;
    private final LoginUriProvider loginUriProvider;
    private final RedirectsLoopPreventer loopPreventer;
    private final UserManager userManager;
    private final WebSudoManager webSudoManager;
    private final ApplicationProperties applicationProperties;

    public TemplateServlet(SoyTemplateRenderer templateRenderer, LoginUriProvider loginUriProvider, RedirectsLoopPreventer loopPreventer, UserManager userManager, WebSudoManager webSudoManager, ApplicationProperties applicationProperties) {
        this.templateRenderer = templateRenderer;
        this.loginUriProvider = loginUriProvider;
        this.loopPreventer = loopPreventer;
        this.userManager = userManager;
        this.webSudoManager = webSudoManager;
        this.applicationProperties = applicationProperties;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(request);
            if (this.userManager.isSystemAdmin(this.userManager.getRemoteUserKey())) {
                response.setContentType("text/html;charset=UTF-8");
                this.templateRenderer.render((Appendable)response.getWriter(), "com.atlassian.oauth2.oauth2-provider-plugin:oauth2-provider-plugin", "OAuth.Configuration.init", (Map)ImmutableMap.of((Object)"product", (Object)this.applicationProperties.getDisplayName()));
            } else {
                String target = this.loginUriProvider.getLoginUriForRole(URI.create(URL), UserRole.SYSADMIN).toString();
                this.loopPreventer.preventRedirectsLoop(request, target);
                response.sendRedirect(target);
            }
        }
        catch (WebSudoSessionException e) {
            this.webSudoManager.enforceWebSudoProtection(request, response);
        }
    }
}

