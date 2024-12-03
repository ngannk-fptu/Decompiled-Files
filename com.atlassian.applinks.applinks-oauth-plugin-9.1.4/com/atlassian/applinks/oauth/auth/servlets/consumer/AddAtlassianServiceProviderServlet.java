/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider
 *  com.atlassian.applinks.core.util.MessageFactory
 *  com.atlassian.applinks.core.util.RendererContextBuilder
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService
 *  com.atlassian.applinks.internal.common.docs.DocumentationLinker
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.applinks.ui.auth.AdminUIAuthenticator
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
 */
package com.atlassian.applinks.oauth.auth.servlets.consumer;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider;
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.core.util.RendererContextBuilder;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.applinks.oauth.auth.servlets.AbstractOAuthConfigServlet;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.applinks.ui.auth.AdminUIAuthenticator;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AddAtlassianServiceProviderServlet
extends AbstractOAuthConfigServlet {
    private final AuthenticationConfigurationManager authenticationConfigurationManager;
    private final ConsumerTokenStoreService consumerTokenStoreService;
    private final InternalHostApplication internalHostApplication;
    private final WebSudoManager webSudoManager;
    private static final String OUTGOING_ENABLED = "enabled";
    private static final String TEMPLATE = "com/atlassian/applinks/oauth/auth/outbound_oauth_plugin_installed.vm";
    private static final String OAUTH_OUTGOING_ENABLED = "outgoing-enabled";

    public AddAtlassianServiceProviderServlet(I18nResolver i18nResolver, MessageFactory messageFactory, TemplateRenderer templateRenderer, WebResourceManager webResourceManager, ApplicationLinkService applicationLinkService, AdminUIAuthenticator adminUIAuthenticator, AuthenticationConfigurationManager authenticationConfigurationManager, ConsumerTokenStoreService consumerTokenStoreService, InternalHostApplication internalHostApplication, LoginUriProvider loginUriProvider, DocumentationLinker documentationLinker, WebSudoManager webSudoManager, XsrfTokenAccessor xsrfTokenAccessor, XsrfTokenValidator xsrfTokenValidator) {
        super(i18nResolver, messageFactory, templateRenderer, webResourceManager, applicationLinkService, adminUIAuthenticator, documentationLinker, loginUriProvider, internalHostApplication, xsrfTokenAccessor, xsrfTokenValidator);
        this.authenticationConfigurationManager = authenticationConfigurationManager;
        this.consumerTokenStoreService = consumerTokenStoreService;
        this.internalHostApplication = internalHostApplication;
        this.webSudoManager = webSudoManager;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(request);
            ApplicationLink applicationLink = this.getRequiredApplicationLink(request);
            RendererContextBuilder builder = this.createContextBuilder(applicationLink);
            boolean isConfigured = this.authenticationConfigurationManager.isConfigured(applicationLink.getId(), OAuthAuthenticationProvider.class);
            builder.put(OUTGOING_ENABLED, (Object)isConfigured);
            this.render(TEMPLATE, builder.build(), request, response);
        }
        catch (WebSudoSessionException wse) {
            this.webSudoManager.enforceWebSudoProtection(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(request);
            ApplicationLink applicationLink = this.getRequiredApplicationLink(request);
            RendererContextBuilder builder = this.createContextBuilder(applicationLink);
            boolean enabled = Boolean.parseBoolean(request.getParameter(OAUTH_OUTGOING_ENABLED));
            builder.put(OUTGOING_ENABLED, (Object)enabled);
            if (enabled) {
                this.authenticationConfigurationManager.registerProvider(applicationLink.getId(), OAuthAuthenticationProvider.class, Collections.emptyMap());
                builder.put("message", (Object)this.i18nResolver.getText("auth.oauth.config.consumer.atlassian.serviceprovider.message.enabled", new Serializable[]{this.internalHostApplication.getName(), applicationLink.getName(), this.internalHostApplication.getBaseUrl()}));
            } else {
                this.consumerTokenStoreService.removeAllConsumerTokens(applicationLink);
                this.authenticationConfigurationManager.unregisterProvider(applicationLink.getId(), OAuthAuthenticationProvider.class);
                builder.put("message", (Object)this.i18nResolver.getText("auth.oauth.config.consumer.atlassian.serviceprovider.message.disabled"));
            }
            this.render(TEMPLATE, builder.build(), request, response);
        }
        catch (WebSudoSessionException wse) {
            this.webSudoManager.enforceWebSudoProtection(request, response);
        }
    }
}

