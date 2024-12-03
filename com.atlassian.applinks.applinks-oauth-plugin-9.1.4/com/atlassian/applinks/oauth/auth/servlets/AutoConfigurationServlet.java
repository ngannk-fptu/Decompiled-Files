/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.TwoLeggedOAuthAuthenticationProvider
 *  com.atlassian.applinks.core.util.MessageFactory
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService
 *  com.atlassian.applinks.internal.common.auth.oauth.ServiceProviderStoreService
 *  com.atlassian.applinks.internal.common.docs.DocumentationLinker
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.applinks.ui.auth.AdminUIAuthenticator
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.sal.api.xsrf.XsrfTokenAccessor
 *  com.atlassian.sal.api.xsrf.XsrfTokenValidator
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.oauth.auth.servlets;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider;
import com.atlassian.applinks.api.auth.types.TwoLeggedOAuthAuthenticationProvider;
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService;
import com.atlassian.applinks.internal.common.auth.oauth.ServiceProviderStoreService;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.applinks.oauth.auth.OAuthHelper;
import com.atlassian.applinks.oauth.auth.servlets.AbstractOAuthConfigServlet;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.applinks.ui.auth.AdminUIAuthenticator;
import com.atlassian.oauth.Consumer;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import java.util.Collections;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoConfigurationServlet
extends AbstractOAuthConfigServlet {
    private static final Logger LOG = LoggerFactory.getLogger((String)AutoConfigurationServlet.class.getName());
    private final ServiceProviderStoreService serviceProviderStoreService;
    private final ConsumerTokenStoreService consumerTokenStoreService;
    private final WebSudoManager webSudoManager;
    private final AuthenticationConfigurationManager configurationManager;

    protected AutoConfigurationServlet(I18nResolver i18nResolver, MessageFactory messageFactory, TemplateRenderer templateRenderer, WebResourceManager webResourceManager, ApplicationLinkService applicationLinkService, AdminUIAuthenticator adminUIAuthenticator, ServiceProviderStoreService serviceProviderStoreService, ConsumerTokenStoreService consumerTokenStoreService, AuthenticationConfigurationManager configurationManager, DocumentationLinker documentationLinker, LoginUriProvider loginUriProvider, InternalHostApplication internalHostApplication, WebSudoManager webSudoManager, XsrfTokenAccessor xsrfTokenAccessor, XsrfTokenValidator xsrfTokenValidator) {
        super(i18nResolver, messageFactory, templateRenderer, webResourceManager, applicationLinkService, adminUIAuthenticator, documentationLinker, loginUriProvider, internalHostApplication, xsrfTokenAccessor, xsrfTokenValidator);
        this.serviceProviderStoreService = serviceProviderStoreService;
        this.configurationManager = configurationManager;
        this.consumerTokenStoreService = consumerTokenStoreService;
        this.webSudoManager = webSudoManager;
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        ApplicationLink applicationLink = this.getRequiredApplicationLink(request);
        try {
            Consumer consumer = OAuthHelper.fetchConsumerInformation(applicationLink);
            this.serviceProviderStoreService.addConsumer(consumer, applicationLink);
            this.configurationManager.registerProvider(applicationLink.getId(), OAuthAuthenticationProvider.class, Collections.emptyMap());
            this.configurationManager.registerProvider(applicationLink.getId(), TwoLeggedOAuthAuthenticationProvider.class, Collections.emptyMap());
            resp.setStatus(200);
        }
        catch (Exception e) {
            LOG.error("Failed to auto-configure OAuth authentication for application link '" + applicationLink + "'", (Throwable)e);
            resp.sendError(500, e.getMessage());
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(request);
            ApplicationLink applicationLink = this.getRequiredApplicationLink(request);
            this.consumerTokenStoreService.removeAllConsumerTokens(applicationLink);
            this.configurationManager.unregisterProvider(applicationLink.getId(), OAuthAuthenticationProvider.class);
            this.serviceProviderStoreService.removeConsumer(applicationLink);
        }
        catch (WebSudoSessionException wse) {
            this.webSudoManager.enforceWebSudoProtection(request, resp);
        }
    }
}

