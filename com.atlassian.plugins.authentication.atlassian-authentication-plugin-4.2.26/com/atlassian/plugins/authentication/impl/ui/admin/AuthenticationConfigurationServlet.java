/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
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
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.ui.admin;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.impl.web.loopsprevention.RedirectsLoopPreventer;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationConfigurationServlet
extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationConfigurationServlet.class);
    static final String URL = "/plugins/servlet/authentication-config";
    static final String TEMPLATE_NAME = "AuthenticationPlugin.Configuration.display";
    private final LoginUriProvider loginUriProvider;
    private final SoyTemplateRenderer renderer;
    private final UserManager userManager;
    private final WebSudoManager webSudoManager;
    private final ApplicationProperties applicationProperties;
    private final RedirectsLoopPreventer loopPreventer;

    public AuthenticationConfigurationServlet(@ComponentImport LoginUriProvider loginUriProvider, @ComponentImport SoyTemplateRenderer renderer, @ComponentImport UserManager userManager, @ComponentImport WebSudoManager webSudoManager, @ComponentImport ApplicationProperties applicationProperties, RedirectsLoopPreventer loopPreventer) {
        this.loginUriProvider = loginUriProvider;
        this.renderer = renderer;
        this.userManager = userManager;
        this.webSudoManager = webSudoManager;
        this.applicationProperties = applicationProperties;
        this.loopPreventer = loopPreventer;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(request);
            if (this.userManager.isSystemAdmin(this.userManager.getRemoteUserKey())) {
                response.setContentType("text/html");
                this.renderer.render((Appendable)response.getWriter(), "com.atlassian.plugins.authentication.atlassian-authentication-plugin:templates", TEMPLATE_NAME, (Map)ImmutableMap.of((Object)"product", (Object)this.applicationProperties.getPlatformId()));
            } else {
                String target = this.loginUriProvider.getLoginUriForRole(URI.create(URL), UserRole.SYSADMIN).toString();
                logger.info("Redirecting to " + target);
                this.loopPreventer.preventRedirectsLoop(request, target);
                response.sendRedirect(target);
            }
        }
        catch (WebSudoSessionException e) {
            this.webSudoManager.enforceWebSudoProtection(request, response);
        }
    }
}

