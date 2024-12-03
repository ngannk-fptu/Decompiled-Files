/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.permission.AuthorisationException
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.atlassian.sal.api.user.UserRole
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.pats.servlet;

import com.atlassian.pats.utils.LicenseChecker;
import com.atlassian.pats.utils.ProductHelper;
import com.atlassian.pats.web.loopsprevention.RedirectsLoopPreventer;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.permission.AuthorisationException;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.sal.api.user.UserRole;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminPersonalTokensServlet
extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AdminPersonalTokensServlet.class);
    static final String URL = "/plugins/servlet/personal-tokens";
    public static final String MODULE_KEY = "com.atlassian.pats.pats-plugin:personal-tokens-admin-templates";
    private final LoginUriProvider loginUriProvider;
    private final SoyTemplateRenderer renderer;
    private final WebSudoManager webSudoManager;
    private final RedirectsLoopPreventer loopPreventer;
    private final ProductHelper productHelper;
    private final LicenseChecker licenseChecker;
    private final I18nResolver i18nResolver;
    private final PermissionEnforcer permissionEnforcer;

    public AdminPersonalTokensServlet(LoginUriProvider loginUriProvider, SoyTemplateRenderer renderer, WebSudoManager webSudoManager, RedirectsLoopPreventer loopPreventer, ProductHelper productHelper, LicenseChecker licenseChecker, I18nResolver i18nResolver, PermissionEnforcer permissionEnforcer) {
        this.loginUriProvider = loginUriProvider;
        this.renderer = renderer;
        this.webSudoManager = webSudoManager;
        this.loopPreventer = loopPreventer;
        this.productHelper = productHelper;
        this.licenseChecker = licenseChecker;
        this.i18nResolver = i18nResolver;
        this.permissionEnforcer = permissionEnforcer;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!this.licenseChecker.isDataCenterProduct()) {
            response.sendError(Response.Status.FORBIDDEN.getStatusCode(), this.i18nResolver.getText("personal.access.tokens.error.invalid.license"));
        } else {
            try {
                this.webSudoManager.willExecuteWebSudoRequest(request);
                if (this.permissionEnforcer.isSystemAdmin()) {
                    response.setContentType("text/html");
                    this.renderer.render((Appendable)response.getWriter(), MODULE_KEY, this.productHelper.getAdminViewTemplateName(), Collections.emptyMap());
                } else {
                    if (this.permissionEnforcer.isAuthenticated()) {
                        throw new AuthorisationException();
                    }
                    String target = this.loginUriProvider.getLoginUriForRole(URI.create(URL), UserRole.SYSADMIN).toString();
                    logger.debug("Redirecting to {}", (Object)target);
                    this.loopPreventer.preventRedirectsLoop(request, target);
                    response.sendRedirect(target);
                }
            }
            catch (WebSudoSessionException e) {
                this.webSudoManager.enforceWebSudoProtection(request, response);
            }
        }
    }
}

