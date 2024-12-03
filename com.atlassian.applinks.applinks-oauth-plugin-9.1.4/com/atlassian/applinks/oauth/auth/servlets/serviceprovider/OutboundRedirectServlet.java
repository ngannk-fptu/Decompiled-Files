/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.TwoLeggedOAuthAuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.TwoLeggedOAuthWithImpersonationAuthenticationProvider
 *  com.atlassian.applinks.core.auth.AbstractAdminOnlyAuthServlet
 *  com.atlassian.applinks.core.util.MessageFactory
 *  com.atlassian.applinks.core.util.RequestUtil
 *  com.atlassian.applinks.core.util.URIUtil
 *  com.atlassian.applinks.host.spi.HostApplication
 *  com.atlassian.applinks.host.spi.InternalHostApplication
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
package com.atlassian.applinks.oauth.auth.servlets.serviceprovider;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider;
import com.atlassian.applinks.api.auth.types.TwoLeggedOAuthAuthenticationProvider;
import com.atlassian.applinks.api.auth.types.TwoLeggedOAuthWithImpersonationAuthenticationProvider;
import com.atlassian.applinks.core.auth.AbstractAdminOnlyAuthServlet;
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.core.util.RequestUtil;
import com.atlassian.applinks.core.util.URIUtil;
import com.atlassian.applinks.host.spi.HostApplication;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.applinks.oauth.auth.OAuthAuthenticatorProviderPluginModule;
import com.atlassian.applinks.oauth.auth.OAuthHelper;
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
import java.net.URI;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OutboundRedirectServlet
extends AbstractOAuthConfigServlet {
    public static final String SUPPORT_APPLINK_PARAM = "supportsAppLinks";
    private final AuthenticationConfigurationManager authenticationConfigurationManager;
    private final WebSudoManager webSudoManager;
    private final HostApplication hostApplication;

    protected OutboundRedirectServlet(I18nResolver i18nResolver, MessageFactory messageFactory, TemplateRenderer templateRenderer, WebResourceManager webResourceManager, ApplicationLinkService applicationLinkService, AdminUIAuthenticator adminUIAuthenticator, DocumentationLinker documentationLinker, LoginUriProvider loginUriProvider, InternalHostApplication internalHostApplication, XsrfTokenAccessor xsrfTokenAccessor, XsrfTokenValidator xsrfTokenValidator, AuthenticationConfigurationManager authenticationConfigurationManager, WebSudoManager webSudoManager, HostApplication hostApplication) {
        super(i18nResolver, messageFactory, templateRenderer, webResourceManager, applicationLinkService, adminUIAuthenticator, documentationLinker, loginUriProvider, internalHostApplication, xsrfTokenAccessor, xsrfTokenValidator);
        this.authenticationConfigurationManager = authenticationConfigurationManager;
        this.webSudoManager = webSudoManager;
        this.hostApplication = hostApplication;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.webSudoManager.willExecuteWebSudoRequest(request);
            ApplicationLink link = this.getRequiredApplicationLink(request);
            boolean supportsAppLinks = Boolean.parseBoolean(request.getParameter(SUPPORT_APPLINK_PARAM));
            String uri = this.getOutgoingConfigUrl(link, supportsAppLinks, request);
            resp.sendRedirect(uri);
        }
        catch (WebSudoSessionException wse) {
            this.webSudoManager.enforceWebSudoProtection(request, resp);
        }
    }

    public String getOutgoingConfigUrl(ApplicationLink link, boolean supportsAppLinks, HttpServletRequest request) {
        boolean oAuthPluginInstalled = OAuthHelper.isOAuthPluginInstalled(link);
        String configUri = supportsAppLinks ? link.getDisplayUrl() + OAuthAuthenticatorProviderPluginModule.ADD_CONSUMER_BY_URL_SERVLET_LOCATION + this.hostApplication.getId() + "?" + "uiposition" + "=remote&" + AbstractAdminOnlyAuthServlet.HOST_URL_PARAM + "=" + URIUtil.utf8Encode((URI)RequestUtil.getBaseURLFromRequest((HttpServletRequest)request, (URI)this.hostApplication.getBaseUrl())).toString() + "&" + "outgoing2LOEnabled" + "=" + this.authenticationConfigurationManager.isConfigured(link.getId(), TwoLeggedOAuthAuthenticationProvider.class) + "&" + "outgoing2LOiEnabled" + "=" + this.authenticationConfigurationManager.isConfigured(link.getId(), TwoLeggedOAuthWithImpersonationAuthenticationProvider.class) + "&" + "oauth-outgoing-enabled" + "=" + this.authenticationConfigurationManager.isConfigured(link.getId(), OAuthAuthenticationProvider.class) : (oAuthPluginInstalled ? RequestUtil.getBaseURLFromRequest((HttpServletRequest)request, (URI)this.hostApplication.getBaseUrl()) + OAuthAuthenticatorProviderPluginModule.OUTBOUND_ATLASSIAN_SERVLET_LOCATION + link.getId().toString() : RequestUtil.getBaseURLFromRequest((HttpServletRequest)request, (URI)this.hostApplication.getBaseUrl()) + OAuthAuthenticatorProviderPluginModule.OUTBOUND_NON_APPLINKS_SERVLET_LOCATION + link.getId().toString());
        return configUri;
    }
}

