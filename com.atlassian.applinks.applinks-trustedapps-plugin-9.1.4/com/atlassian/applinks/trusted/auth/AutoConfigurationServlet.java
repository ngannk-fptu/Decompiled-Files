/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.core.util.MessageFactory
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.internal.common.docs.DocumentationLinker
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.applinks.ui.auth.AdminUIAuthenticator
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
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
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.applinks.trusted.auth.AbstractTrustedAppsServlet;
import com.atlassian.applinks.trusted.auth.TrustConfigurator;
import com.atlassian.applinks.ui.auth.AdminUIAuthenticator;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsConfigurationManager;
import com.atlassian.security.auth.trustedapps.TrustedApplicationsManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AutoConfigurationServlet
extends AbstractTrustedAppsServlet {
    public AutoConfigurationServlet(I18nResolver i18nResolver, InternalHostApplication host, MessageFactory messageFactory, TemplateRenderer templateRenderer, WebResourceManager webResourceManager, AdminUIAuthenticator adminUIAuthenticator, ApplicationLinkService applicationLinkService, TrustedApplicationsManager trustedApplicationsManager, AuthenticationConfigurationManager configurationManager, TrustedApplicationsConfigurationManager trustedAppsManager, TrustConfigurator trustConfigurator, LoginUriProvider loginUriProvider, DocumentationLinker documentationLinker, XsrfTokenAccessor xsrfTokenAccessor, XsrfTokenValidator xsrfTokenValidator, UserManager userManager) {
        super(i18nResolver, messageFactory, templateRenderer, webResourceManager, adminUIAuthenticator, applicationLinkService, host, trustedApplicationsManager, configurationManager, trustedAppsManager, trustConfigurator, loginUriProvider, documentationLinker, xsrfTokenAccessor, xsrfTokenValidator, userManager);
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ApplicationLink link = this.getRequiredApplicationLink(request);
        try {
            this.trustConfigurator.issueInboundTrust(link);
            this.trustConfigurator.issueOutboundTrust(link);
        }
        catch (TrustConfigurator.ConfigurationException ce) {
            response.sendError(500, "Unable to configure Trusted Applications: " + ce.getMessage());
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ApplicationLink link = this.getRequiredApplicationLink(request);
        this.trustConfigurator.revokeInboundTrust(link);
        this.trustConfigurator.revokeOutboundTrust(link);
    }
}

