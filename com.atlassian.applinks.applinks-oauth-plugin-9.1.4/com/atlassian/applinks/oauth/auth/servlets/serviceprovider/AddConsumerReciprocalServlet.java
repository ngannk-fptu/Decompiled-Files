/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider
 *  com.atlassian.applinks.core.RedirectController
 *  com.atlassian.applinks.core.ServletPathConstants
 *  com.atlassian.applinks.core.util.MessageFactory
 *  com.atlassian.applinks.core.util.URIUtil
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService
 *  com.atlassian.applinks.internal.common.docs.DocumentationLinker
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.applinks.ui.AbstractApplinksServlet$NotFoundException
 *  com.atlassian.applinks.ui.auth.AdminUIAuthenticator
 *  com.atlassian.applinks.ui.validators.CallbackParameterValidator
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
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.oauth.auth.servlets.serviceprovider;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider;
import com.atlassian.applinks.core.RedirectController;
import com.atlassian.applinks.core.ServletPathConstants;
import com.atlassian.applinks.core.util.MessageFactory;
import com.atlassian.applinks.core.util.URIUtil;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService;
import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.applinks.oauth.auth.servlets.AbstractOAuthConfigServlet;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.applinks.ui.AbstractApplinksServlet;
import com.atlassian.applinks.ui.auth.AdminUIAuthenticator;
import com.atlassian.applinks.ui.validators.CallbackParameterValidator;
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
import java.util.Collections;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddConsumerReciprocalServlet
extends AbstractOAuthConfigServlet {
    private final AuthenticationConfigurationManager authenticationConfigurationManager;
    private final ConsumerTokenStoreService consumerTokenStoreService;
    private final WebSudoManager webSudoManager;
    private final CallbackParameterValidator callbackParameterValidator;
    private final RedirectController redirectController;
    public static final String ENABLE_OAUTH_AUTHENTICATION_PARAMETER = "enable-oauth";
    public static final String SUCCESS_PARAM = "success";
    public static final String CALLBACK_PARAM = "callback";
    private static final Logger LOG = LoggerFactory.getLogger(AddConsumerReciprocalServlet.class);

    public AddConsumerReciprocalServlet(I18nResolver i18nResolver, MessageFactory messageFactory, TemplateRenderer templateRenderer, WebResourceManager webResourceManager, ApplicationLinkService applicationLinkService, AdminUIAuthenticator adminUIAuthenticator, AuthenticationConfigurationManager authenticationConfigurationManager, ConsumerTokenStoreService consumerTokenStoreService, InternalHostApplication internalHostApplication, LoginUriProvider loginUriProvider, DocumentationLinker documentationLinker, WebSudoManager webSudoManager, XsrfTokenAccessor xsrfTokenAccessor, XsrfTokenValidator xsrfTokenValidator, CallbackParameterValidator callbackParameterValidator, RedirectController redirectController) {
        super(i18nResolver, messageFactory, templateRenderer, webResourceManager, applicationLinkService, adminUIAuthenticator, documentationLinker, loginUriProvider, internalHostApplication, xsrfTokenAccessor, xsrfTokenValidator);
        this.authenticationConfigurationManager = authenticationConfigurationManager;
        this.consumerTokenStoreService = consumerTokenStoreService;
        this.webSudoManager = webSudoManager;
        this.callbackParameterValidator = callbackParameterValidator;
        this.redirectController = redirectController;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        block8: {
            try {
                ApplicationLink applicationLink;
                this.webSudoManager.willExecuteWebSudoRequest(request);
                try {
                    applicationLink = this.getRequiredApplicationLink(request);
                }
                catch (AbstractApplinksServlet.NotFoundException ex) {
                    resp.sendRedirect(this.createAndValidateRedirectUrl(request, true, null));
                    return;
                }
                boolean enable = Boolean.parseBoolean(request.getParameter(ENABLE_OAUTH_AUTHENTICATION_PARAMETER));
                try {
                    if (enable) {
                        this.authenticationConfigurationManager.registerProvider(applicationLink.getId(), OAuthAuthenticationProvider.class, Collections.emptyMap());
                        this.redirectOrPrintRedirectionWarning(request, resp, this.i18nResolver.getText("auth.oauth.config.serviceprovider.consumer.enabled"));
                        break block8;
                    }
                    if (this.authenticationConfigurationManager.isConfigured(applicationLink.getId(), OAuthAuthenticationProvider.class)) {
                        this.consumerTokenStoreService.removeAllConsumerTokens(applicationLink);
                    }
                    this.authenticationConfigurationManager.unregisterProvider(applicationLink.getId(), OAuthAuthenticationProvider.class);
                    this.redirectOrPrintRedirectionWarning(request, resp, this.i18nResolver.getText("auth.oauth.config.serviceprovider.consumer.disabled"));
                }
                catch (Exception e) {
                    LOG.error("Error occurred when trying to " + (enable ? "enable" : "disable") + " OAuth authentication configuration for application link '" + applicationLink + "'", (Throwable)e);
                    String message = enable ? this.i18nResolver.getText("auth.oauth.config.error.reciprocal.config.enable") : this.i18nResolver.getText("auth.oauth.config.error.reciprocal.config.disable");
                    resp.sendRedirect(this.createAndValidateRedirectUrl(request, false, message));
                }
            }
            catch (WebSudoSessionException wse) {
                this.webSudoManager.enforceWebSudoProtection(request, resp);
            }
        }
    }

    public static String getReciprocalServletUrl(URI baseUrl, ApplicationId applicationId, String callbackUrl, String actionParamValue) {
        URI enableOAuthURL = URIUtil.uncheckedConcatenate((URI)baseUrl, (String[])new String[]{ServletPathConstants.APPLINKS_CONFIG_SERVLET_PATH + "/oauth/outbound/apl/" + applicationId + "?callback=" + callbackUrl + "&" + ENABLE_OAUTH_AUTHENTICATION_PARAMETER + "=" + actionParamValue});
        return enableOAuthURL.toString();
    }

    private void redirectOrPrintRedirectionWarning(HttpServletRequest request, HttpServletResponse response, String message) throws IOException {
        String redirectUrl = this.createRedirectUrl(request, true, message);
        this.redirectController.redirectOrPrintRedirectionWarning(response, redirectUrl);
    }

    private String createAndValidateRedirectUrl(HttpServletRequest req, boolean success, String message) {
        String redirectUrl = this.createRedirectUrl(req, success, message);
        this.callbackParameterValidator.validate(redirectUrl);
        return redirectUrl;
    }

    private String createRedirectUrl(HttpServletRequest req, boolean success, String message) {
        String callbackUrl = this.getRequiredParameter(req, CALLBACK_PARAM);
        if (callbackUrl.indexOf("?") == -1) {
            callbackUrl = callbackUrl + "?";
        }
        String redirectUrl = String.format("%s&success=%s", callbackUrl, success);
        if (!StringUtils.isBlank((CharSequence)message)) {
            redirectUrl = redirectUrl + "&message=" + URIUtil.utf8Encode((String)message);
        }
        return redirectUrl;
    }
}

