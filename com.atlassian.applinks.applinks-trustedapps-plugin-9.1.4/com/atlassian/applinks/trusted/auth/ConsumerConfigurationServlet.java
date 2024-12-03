/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.auth.types.TrustedAppsAuthenticationProvider
 *  com.atlassian.applinks.core.RedirectController
 *  com.atlassian.applinks.core.util.MessageFactory
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.internal.common.docs.DocumentationLinker
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.applinks.ui.AbstractApplinksServlet$NotFoundException
 *  com.atlassian.applinks.ui.auth.AdminUIAuthenticator
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.sal.api.xsrf.XsrfTokenAccessor
 *  com.atlassian.sal.api.xsrf.XsrfTokenValidator
 *  com.atlassian.security.auth.trustedapps.TrustedApplicationsConfigurationManager
 *  com.atlassian.security.auth.trustedapps.TrustedApplicationsManager
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.applinks.trusted.auth;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.auth.types.TrustedAppsAuthenticationProvider;
import com.atlassian.applinks.core.RedirectController;
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.applinks.trusted.auth.AbstractTrustedAppsServlet;
import com.atlassian.applinks.trusted.auth.Action;
import com.atlassian.applinks.trusted.auth.TrustConfigurator;
import com.atlassian.applinks.ui.AbstractApplinksServlet;
import com.atlassian.applinks.ui.auth.AdminUIAuthenticator;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsConfigurationManager;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ConsumerConfigurationServlet
extends AbstractTrustedAppsServlet {
    public static final String CALLBACK_URL = "callbackUrl";
    private final WebSudoManager webSudoManager;
    private final RedirectController redirectController;

    public ConsumerConfigurationServlet(I18nResolver i18nResolver, TemplateRenderer templateRenderer, AdminUIAuthenticator adminUIAuthenticator, WebResourceManager webResourceManager, AuthenticationConfigurationManager configurationManager, ApplicationLinkService applicationLinkService, MessageFactory messageFactory, TrustedApplicationsManager trustedApplicationsManager, TrustedApplicationsConfigurationManager trustedAppsManager, InternalHostApplication hostApplication, TrustConfigurator trustConfigurator, LoginUriProvider loginUriProvider, DocumentationLinker documentationLinker, WebSudoManager webSudoManager, XsrfTokenAccessor xsrfTokenAccessor, XsrfTokenValidator xsrfTokenValidator, UserManager userManager, RedirectController redirectController) {
        super(i18nResolver, messageFactory, templateRenderer, webResourceManager, adminUIAuthenticator, applicationLinkService, hostApplication, trustedApplicationsManager, configurationManager, trustedAppsManager, trustConfigurator, loginUriProvider, documentationLinker, xsrfTokenAccessor, xsrfTokenValidator, userManager);
        this.webSudoManager = webSudoManager;
        this.redirectController = redirectController;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(request);
            ApplicationLink link = this.getRequiredApplicationLink(request);
            this.trustConfigurator.configureOutboundTrust(link, this.getAction(request));
            this.render(link, request, response);
        }
        catch (WebSudoSessionException wse) {
            this.webSudoManager.enforceWebSudoProtection(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(request);
            if (this.peerHasUAL(request)) {
                this.configureTrustAndRedirect(request, response);
            } else {
                this.render(this.getRequiredApplicationLink(request), request, response);
            }
        }
        catch (WebSudoSessionException wse) {
            this.webSudoManager.enforceWebSudoProtection(request, response);
        }
    }

    private void configureTrustAndRedirect(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ApplicationLink applicationLink;
        String callbackUrl = this.getRequiredParameter(request, CALLBACK_URL);
        Action action = this.getAction(request);
        String redirectUrl = this.buildCallBackUrl(callbackUrl, action, true);
        try {
            applicationLink = this.getRequiredApplicationLink(request);
        }
        catch (AbstractApplinksServlet.NotFoundException ex) {
            this.redirectController.redirectOrPrintRedirectionWarning(response, redirectUrl);
            return;
        }
        this.trustConfigurator.configureOutboundTrust(applicationLink, action);
        this.redirectController.redirectOrPrintRedirectionWarning(response, redirectUrl);
    }

    private String buildCallBackUrl(String callbackUrlBase, Action action, boolean success) {
        StringBuilder buf = new StringBuilder(callbackUrlBase).append(callbackUrlBase.contains("?") ? (char)'&' : '?').append("action=").append(action.name()).append('&').append("result=").append(success ? "success" : "failure");
        return buf.toString();
    }

    private void render(ApplicationLink appLink, HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean enabled = this.configurationManager.isConfigured(appLink.getId(), TrustedAppsAuthenticationProvider.class);
        String consumer = this.internalHostApplication.getName();
        String consumerAppType = this.i18nResolver.getText(this.internalHostApplication.getType().getI18nKey());
        String provider = appLink.getName();
        String providerAppType = this.i18nResolver.getText(appLink.getType().getI18nKey());
        this.render(request, response, consumer, consumerAppType, provider, providerAppType, enabled, this.emptyContext());
    }
}

