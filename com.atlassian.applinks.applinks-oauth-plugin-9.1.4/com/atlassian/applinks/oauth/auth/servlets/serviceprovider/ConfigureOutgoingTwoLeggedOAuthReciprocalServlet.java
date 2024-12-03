/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.auth.AuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.TwoLeggedOAuthAuthenticationProvider
 *  com.atlassian.applinks.api.auth.types.TwoLeggedOAuthWithImpersonationAuthenticationProvider
 *  com.atlassian.applinks.core.ServletPathConstants
 *  com.atlassian.applinks.core.util.MessageFactory
 *  com.atlassian.applinks.core.util.URIUtil
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.internal.common.docs.DocumentationLinker
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.applinks.ui.AbstractApplinksServlet$NotFoundException
 *  com.atlassian.applinks.ui.auth.AdminUIAuthenticator
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.sal.api.websudo.WebSudoSessionException
 *  com.atlassian.sal.api.xsrf.XsrfTokenAccessor
 *  com.atlassian.sal.api.xsrf.XsrfTokenValidator
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.collect.ImmutableSet
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.oauth.auth.servlets.serviceprovider;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.auth.AuthenticationProvider;
import com.atlassian.applinks.api.auth.types.TwoLeggedOAuthAuthenticationProvider;
import com.atlassian.applinks.api.auth.types.TwoLeggedOAuthWithImpersonationAuthenticationProvider;
import com.atlassian.applinks.core.ServletPathConstants;
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.core.util.URIUtil;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.applinks.oauth.auth.servlets.AbstractOAuthConfigServlet;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.applinks.ui.AbstractApplinksServlet;
import com.atlassian.applinks.ui.auth.AdminUIAuthenticator;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.sal.api.xsrf.XsrfTokenAccessor;
import com.atlassian.sal.api.xsrf.XsrfTokenValidator;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigureOutgoingTwoLeggedOAuthReciprocalServlet
extends AbstractOAuthConfigServlet {
    public static final String ENABLE_OUTGOING_2LO_AUTHENTICATION_PARAMETER = "enable-outgoing-2lo";
    public static final String ENABLE_OUTGOING_2LOI_AUTHENTICATION_PARAMETER = "enable-outgoing-2loi";
    public static final String OUTGOING_2LO_SUCCESS_PARAM = "outgoing_2lo_success";
    public static final String CALLBACK_PARAM = "callback";
    private static final Iterable<Class<? extends AuthenticationProvider>> TWO_LEGGED_OAUTH_AUTHENTICATION_PROVIDERS = ImmutableSet.of(TwoLeggedOAuthAuthenticationProvider.class, TwoLeggedOAuthWithImpersonationAuthenticationProvider.class);
    private final AuthenticationConfigurationManager authenticationConfigurationManager;
    private final WebSudoManager webSudoManager;
    private static final Logger LOG = LoggerFactory.getLogger(ConfigureOutgoingTwoLeggedOAuthReciprocalServlet.class);

    protected ConfigureOutgoingTwoLeggedOAuthReciprocalServlet(I18nResolver i18nResolver, MessageFactory messageFactory, TemplateRenderer templateRenderer, WebResourceManager webResourceManager, ApplicationLinkService applicationLinkService, AdminUIAuthenticator adminUIAuthenticator, DocumentationLinker documentationLinker, LoginUriProvider loginUriProvider, InternalHostApplication internalHostApplication, XsrfTokenAccessor xsrfTokenAccessor, XsrfTokenValidator xsrfTokenValidator, AuthenticationConfigurationManager authenticationConfigurationManager, WebSudoManager webSudoManager) {
        super(i18nResolver, messageFactory, templateRenderer, webResourceManager, applicationLinkService, adminUIAuthenticator, documentationLinker, loginUriProvider, internalHostApplication, xsrfTokenAccessor, xsrfTokenValidator);
        this.authenticationConfigurationManager = authenticationConfigurationManager;
        this.webSudoManager = webSudoManager;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        try {
            ApplicationLink applicationLink;
            this.webSudoManager.willExecuteWebSudoRequest(request);
            try {
                applicationLink = this.getRequiredApplicationLink(request);
            }
            catch (AbstractApplinksServlet.NotFoundException ex) {
                resp.sendRedirect(this.createRedirectUrl(request, true, null));
                return;
            }
            if (StringUtils.isEmpty((CharSequence)request.getParameter(ENABLE_OUTGOING_2LOI_AUTHENTICATION_PARAMETER))) {
                LOG.debug("Remote instance for link [{}] does not support independent configuration of 2LO/2LOi.", (Object)applicationLink.getId());
                this.Reconfigure2LOAnd2LOiInTandem(request, resp, applicationLink);
            } else {
                this.Reconfigure2LOAnd2LOiIndependently(request, resp, applicationLink);
            }
        }
        catch (WebSudoSessionException wse) {
            this.webSudoManager.enforceWebSudoProtection(request, resp);
        }
    }

    private void Reconfigure2LOAnd2LOiInTandem(HttpServletRequest request, HttpServletResponse resp, ApplicationLink applicationLink) throws IOException {
        boolean enable = Boolean.parseBoolean(request.getParameter(ENABLE_OUTGOING_2LO_AUTHENTICATION_PARAMETER));
        try {
            if (enable) {
                for (Class<? extends AuthenticationProvider> authenticationProvider : TWO_LEGGED_OAUTH_AUTHENTICATION_PROVIDERS) {
                    this.authenticationConfigurationManager.registerProvider(applicationLink.getId(), authenticationProvider, Collections.emptyMap());
                }
                resp.sendRedirect(this.createRedirectUrl(request, true, this.i18nResolver.getText("auth.oauth.config.serviceprovider.outgoing.2lo.enabled")));
            } else {
                for (Class<? extends AuthenticationProvider> authenticationProvider : TWO_LEGGED_OAUTH_AUTHENTICATION_PROVIDERS) {
                    this.authenticationConfigurationManager.unregisterProvider(applicationLink.getId(), authenticationProvider);
                }
                resp.sendRedirect(this.createRedirectUrl(request, true, this.i18nResolver.getText("auth.oauth.config.serviceprovider.outgoing.2lo.disabled")));
            }
        }
        catch (Exception e) {
            LOG.error("Error occurred when trying to " + (enable ? "enable" : "disable") + " outgoing 2-Legged OAuth authentication configuration for application link '" + applicationLink + "'", (Throwable)e);
            String message = enable ? this.i18nResolver.getText("auth.oauth.config.error.reciprocal.outgoing.2lo.config.enable") : this.i18nResolver.getText("auth.oauth.config.error.reciprocal.outgoing.2lo.config.disable");
            resp.sendRedirect(this.createRedirectUrl(request, false, message));
        }
    }

    private void Reconfigure2LOAnd2LOiIndependently(HttpServletRequest request, HttpServletResponse resp, ApplicationLink applicationLink) throws IOException {
        boolean enable2LO = Boolean.parseBoolean(request.getParameter(ENABLE_OUTGOING_2LO_AUTHENTICATION_PARAMETER));
        boolean enable2LOi = Boolean.parseBoolean(request.getParameter(ENABLE_OUTGOING_2LOI_AUTHENTICATION_PARAMETER));
        try {
            String message = "";
            if (enable2LO) {
                this.authenticationConfigurationManager.registerProvider(applicationLink.getId(), TwoLeggedOAuthAuthenticationProvider.class, Collections.emptyMap());
                message = message + this.i18nResolver.getText("auth.oauth.config.serviceprovider.outgoing.2lo.enabled");
            } else {
                this.authenticationConfigurationManager.unregisterProvider(applicationLink.getId(), TwoLeggedOAuthAuthenticationProvider.class);
                message = message + this.i18nResolver.getText("auth.oauth.config.serviceprovider.outgoing.2lo.disabled");
            }
            if (enable2LOi) {
                this.authenticationConfigurationManager.registerProvider(applicationLink.getId(), TwoLeggedOAuthWithImpersonationAuthenticationProvider.class, Collections.emptyMap());
                message = message + this.i18nResolver.getText("auth.oauth.config.serviceprovider.outgoing.2loi.enabled");
            } else {
                this.authenticationConfigurationManager.unregisterProvider(applicationLink.getId(), TwoLeggedOAuthWithImpersonationAuthenticationProvider.class);
                message = message + this.i18nResolver.getText("auth.oauth.config.serviceprovider.outgoing.2loi.disabled");
            }
            resp.sendRedirect(this.createRedirectUrl(request, true, message));
        }
        catch (Exception e) {
            LOG.error("Error occurred when trying to " + (enable2LO ? "enable" : "disable") + " outgoing 2LO and " + (enable2LOi ? "enable" : "disable") + " outgoing 2LOi authentication configuration for application link '" + applicationLink + "'", (Throwable)e);
            String message = (enable2LO ? this.i18nResolver.getText("auth.oauth.config.error.reciprocal.outgoing.2lo.config.enable") : this.i18nResolver.getText("auth.oauth.config.error.reciprocal.outgoing.2lo.config.disable")) + (enable2LOi ? this.i18nResolver.getText("auth.oauth.config.error.reciprocal.outgoing.2loi.config.enable") : this.i18nResolver.getText("auth.oauth.config.error.reciprocal.outgoing.2loi.config.disable"));
            resp.sendRedirect(this.createRedirectUrl(request, false, message));
        }
    }

    public static String getReciprocalServletUrl(URI baseUrl, ApplicationId applicationId, String callbackUrl, String actionParamValue, String actionParamValue2) {
        URI enableOAuthURL = URIUtil.uncheckedConcatenate((URI)baseUrl, (String[])new String[]{ServletPathConstants.APPLINKS_CONFIG_SERVLET_PATH + "/oauth/outbound/apl-2lo/" + applicationId + "?callback=" + callbackUrl + "&" + ENABLE_OUTGOING_2LO_AUTHENTICATION_PARAMETER + "=" + actionParamValue + "&" + ENABLE_OUTGOING_2LOI_AUTHENTICATION_PARAMETER + "=" + actionParamValue2});
        return enableOAuthURL.toString();
    }

    private String createRedirectUrl(HttpServletRequest req, boolean success, String message) {
        String callbackUrl = this.getRequiredParameter(req, CALLBACK_PARAM);
        if (callbackUrl.indexOf("?") == -1) {
            callbackUrl = callbackUrl + "?";
        }
        String redirectUrl = String.format("%s&outgoing_2lo_success=%s", callbackUrl, success);
        if (!StringUtils.isBlank((CharSequence)message)) {
            redirectUrl = redirectUrl + "&message=" + URIUtil.utf8Encode((String)message);
        }
        return redirectUrl;
    }
}

