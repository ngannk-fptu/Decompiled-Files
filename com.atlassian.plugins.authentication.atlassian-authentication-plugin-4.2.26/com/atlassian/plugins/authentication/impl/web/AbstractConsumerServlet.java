/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.AuthenticationListener
 *  com.atlassian.sal.api.auth.Authenticator$Result
 *  com.atlassian.sal.api.auth.Authenticator$Result$Success
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.plugins.authentication.impl.web;

import com.atlassian.plugins.authentication.api.config.IdpConfigService;
import com.atlassian.plugins.authentication.impl.util.ApplicationStateValidator;
import com.atlassian.plugins.authentication.impl.web.SessionDataService;
import com.atlassian.plugins.authentication.impl.web.usercontext.PrincipalResolver;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.ProvisioningService;
import com.atlassian.plugins.authentication.impl.web.usercontext.rememberme.RememberMeCookieHandler;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.AuthenticationListener;
import com.atlassian.sal.api.auth.Authenticator;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.Serializable;
import java.security.Principal;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractConsumerServlet
extends HttpServlet {
    protected final ApplicationProperties applicationProperties;
    protected final PrincipalResolver principalResolver;
    protected final SessionDataService sessionDataService;
    protected final AuthenticationListener authenticationListener;
    protected final I18nResolver i18nResolver;
    protected final RememberMeCookieHandler rememberMeCookieHandler;
    protected final ApplicationStateValidator applicationStateValidator;
    protected final IdpConfigService idpConfigService;
    protected final ProvisioningService provisioningService;

    protected AbstractConsumerServlet(ApplicationProperties applicationProperties, PrincipalResolver principalResolver, SessionDataService sessionDataService, AuthenticationListener authenticationListener, I18nResolver i18nResolver, RememberMeCookieHandler rememberMeCookieHandler, ApplicationStateValidator applicationStateValidator, IdpConfigService idpConfigService, ProvisioningService provisioningService) {
        this.applicationProperties = applicationProperties;
        this.principalResolver = principalResolver;
        this.sessionDataService = sessionDataService;
        this.authenticationListener = authenticationListener;
        this.i18nResolver = i18nResolver;
        this.rememberMeCookieHandler = rememberMeCookieHandler;
        this.applicationStateValidator = applicationStateValidator;
        this.idpConfigService = idpConfigService;
        this.provisioningService = provisioningService;
    }

    protected void authenticationSuccess(HttpServletRequest request, HttpServletResponse response, Principal principal, String messageKey) {
        this.sessionDataService.requireNewSession(request);
        this.sessionDataService.setUserLoggedInWithSso(request);
        this.authenticationListener.authenticationSuccess((Authenticator.Result)new Authenticator.Result.Success(this.i18nResolver.createMessage(messageKey, new Serializable[]{principal.getName()}), principal), request, response);
        this.productSpecificWorkarounds(request);
    }

    private void productSpecificWorkarounds(HttpServletRequest request) {
        if ("jira".equals(this.applicationProperties.getPlatformId())) {
            request.setAttribute("com.atlassian.web.servlet.plugin.request.RedirectInterceptingResponse.sendRedirect", (Object)Boolean.TRUE);
            request.getSession().setAttribute("com.atlassian.labs.botkiller.BotKiller", (Object)request.getSession().getMaxInactiveInterval());
        }
    }
}

