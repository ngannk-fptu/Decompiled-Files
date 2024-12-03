/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkRequestFactory
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.AuthorisationAdminURIGenerator
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider
 *  com.atlassian.applinks.core.RedirectController
 *  com.atlassian.applinks.core.ServletPathConstants
 *  com.atlassian.applinks.core.util.RequestUtil
 *  com.atlassian.applinks.core.util.WebResources
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService
 *  com.atlassian.applinks.internal.common.auth.oauth.OAuthMessageProblemException
 *  com.atlassian.applinks.internal.common.net.ResponseHeaderUtil
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.applinks.ui.validators.CallbackParameterValidator
 *  com.atlassian.oauth.ServiceProvider
 *  com.atlassian.oauth.consumer.ConsumerService
 *  com.atlassian.oauth.consumer.ConsumerToken
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.net.ResponseException
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  javax.annotation.Nonnull
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  net.oauth.OAuth
 *  net.oauth.OAuthProblemException
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.oauth.auth;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.AuthorisationAdminURIGenerator;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider;
import com.atlassian.applinks.core.RedirectController;
import com.atlassian.applinks.core.ServletPathConstants;
import com.atlassian.applinks.core.util.RequestUtil;
import com.atlassian.applinks.core.util.WebResources;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService;
import com.atlassian.applinks.internal.common.auth.oauth.OAuthMessageProblemException;
import com.atlassian.applinks.internal.common.net.ResponseHeaderUtil;
import com.atlassian.applinks.oauth.auth.OAuthPermissionDeniedException;
import com.atlassian.applinks.oauth.auth.OAuthTokenRetriever;
import com.atlassian.applinks.oauth.auth.ServiceProviderUtil;
import com.atlassian.applinks.oauth.auth.servlets.consumer.AddServiceProviderManuallyServlet;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.applinks.ui.validators.CallbackParameterValidator;
import com.atlassian.oauth.ServiceProvider;
import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.oauth.consumer.ConsumerToken;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.oauth.OAuth;
import net.oauth.OAuthProblemException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuthApplinksServlet
extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(OAuthApplinksServlet.class);
    public static final String AUTHORIZE_PATH = "authorize";
    public static final String ACCESS_PATH = "access";
    @VisibleForTesting
    protected static final String APPLICATION_LINK_ID_PARAM = "applicationLinkID";
    private static final String REDIRECT_URL_PARAM = "redirectUrl";
    private static final String TEMPLATE = "com/atlassian/applinks/oauth/auth/oauth_dance.vm";
    private static final String ADMIN_ERROR_CHECK_CONFIG = "applinks.admin.error.message.check.for.misconfig";
    private static final String ADMIN_ERROR_CHECK_LINK = "applinks.admin.error.message.check.link";
    private static final String USER_ERROR_NOT_LOGGED_IN = "applinks.user.error.message.not.logged.in";
    private static final String USER_ERROR_ACCESS_DENIED = "applinks.user.error.message.access.denied";
    private static final String ERROR_NOT_LOGGED_IN = "auth.oauth.config.error.not.loggedin";
    private static final String ERROR_APPLINK_ID_REQUIRED = "auth.oauth.config.error.link.id.empty";
    private static final String ERROR_TYPE_NOT_LOADED = "auth.oauth.config.error.link.type.not.loaded";
    private static final String ERROR_NO_LINK_FOUND_FOR_ID = "auth.oauth.config.error.link.id";
    private static final String ERROR_OAUTH_DANCE = "auth.oauth.config.error.dance";
    private static final String ERROR_OATH_NOT_CONFIGURED = "auth.oauth.config.error.not.configured";
    private static final String ERROR_CONSUMER_UNKNOWN = "auth.oauth.config.error.dance.oauth.problem.consumer.unknown";
    private static final String ERROR_TOKEN_REJECTED = "auth.oauth.config.error.dance.oauth.problem.token.rejected";
    private static final String ERRROR_OAUTH_DANCE_PROBLEM = "auth.oauth.config.error.dance.oauth.problem";
    private static final String WARNING_TITLE_ACCESS_DENIED = "auth.oauth.config.dance.denied.title";
    private static final String WARNING_MESSAGE_ACCESS_DENIED = "auth.oauth.config.dance.denied.message";
    private final ConsumerTokenStoreService consumerTokenStoreService;
    private final OAuthTokenRetriever oAuthTokenRetriever;
    private final UserManager userManager;
    private final I18nResolver i18nResolver;
    private final WebResourceManager webResourceManager;
    private final TemplateRenderer templateRenderer;
    private final AuthenticationConfigurationManager authenticationConfigurationManager;
    private final ConsumerService consumerService;
    private final InternalHostApplication internalHostApplication;
    private final CallbackParameterValidator callbackParameterValidator;
    private final ApplicationLinkService applicationLinkService;
    private final RedirectController redirectController;

    public OAuthApplinksServlet(ConsumerTokenStoreService consumerTokenStoreService, OAuthTokenRetriever oAuthTokenRetriever, UserManager userManager, I18nResolver i18nResolver, WebResourceManager webResourceManager, TemplateRenderer templateRenderer, AuthenticationConfigurationManager authenticationConfigurationManager, ConsumerService consumerService, InternalHostApplication internalHostApplication, CallbackParameterValidator callbackParameterValidator, ApplicationLinkService applicationLinkService, RedirectController redirectController) {
        this.consumerTokenStoreService = consumerTokenStoreService;
        this.oAuthTokenRetriever = oAuthTokenRetriever;
        this.userManager = userManager;
        this.i18nResolver = i18nResolver;
        this.webResourceManager = webResourceManager;
        this.templateRenderer = templateRenderer;
        this.authenticationConfigurationManager = authenticationConfigurationManager;
        this.consumerService = consumerService;
        this.internalHostApplication = internalHostApplication;
        this.callbackParameterValidator = callbackParameterValidator;
        this.applicationLinkService = applicationLinkService;
        this.redirectController = redirectController;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ApplicationLink applicationLink;
        Map<String, Object> context = this.createVelocityContext(resp);
        String applicationLinkId = this.getApplicationLinkId(req);
        context.put(TemplateVariable.APPLINK_ID.key, StringEscapeUtils.escapeHtml4((String)applicationLinkId));
        ResponseHeaderUtil.preventCrossFrameClickJacking((HttpServletResponse)resp);
        if (this.userManager.getRemoteUser(req) == null) {
            this.addNotLoggedInUserErrorToContext(context);
            this.addErrorToContext(context, ERROR_NOT_LOGGED_IN, new String[0]);
            this.render(context, resp);
            return;
        }
        if (StringUtils.isBlank((CharSequence)applicationLinkId)) {
            this.addErrorToContext(context, ERROR_APPLINK_ID_REQUIRED, new String[0]);
            this.addCheckConfigAdminErrorToContext(context);
            this.render(context, resp);
            return;
        }
        try {
            applicationLink = this.applicationLinkService.getApplicationLink(new ApplicationId(applicationLinkId));
        }
        catch (TypeNotInstalledException e) {
            LOG.error("Failed to get application link", (Throwable)e);
            this.addErrorToContext(context, ERROR_TYPE_NOT_LOADED, applicationLinkId, e.getType());
            this.addCheckConfigAdminErrorToContext(context);
            this.render(context, resp);
            return;
        }
        if (applicationLink == null) {
            this.addErrorToContext(context, ERROR_NO_LINK_FOUND_FOR_ID, applicationLinkId);
            this.addCheckConfigAdminErrorToContext(context);
            this.render(context, resp);
            return;
        }
        context.put(TemplateVariable.APPLINK_ID.key, applicationLink.getId().toString());
        if (!this.authenticationConfigurationManager.isConfigured(applicationLink.getId(), OAuthAuthenticationProvider.class)) {
            this.addErrorToContext(context, ERROR_OATH_NOT_CONFIGURED, applicationLink.toString());
            this.addCheckLinkAdminErrorToContext(context, applicationLink.getName());
            this.render(context, resp);
            return;
        }
        String token = this.getToken(req);
        try {
            if (StringUtils.isBlank((CharSequence)token) || req.getPathInfo().endsWith(AUTHORIZE_PATH)) {
                this.obtainAndAuthorizeRequestToken(applicationLink, resp, req);
            } else if (req.getPathInfo().endsWith(ACCESS_PATH)) {
                this.getAccessToken(token, applicationLink, req);
                String redirectUrl = this.getRedirectUrl(req);
                if (StringUtils.isBlank((CharSequence)redirectUrl)) {
                    URI authAdminUri = null;
                    ApplicationLinkRequestFactory requestFactory = applicationLink.createAuthenticatedRequestFactory();
                    if (requestFactory instanceof AuthorisationAdminURIGenerator) {
                        authAdminUri = ((AuthorisationAdminURIGenerator)requestFactory).getAuthorisationAdminURI();
                    }
                    context.put(TemplateVariable.AUTH_ADMIN_URI.key, authAdminUri == null ? "" : authAdminUri.toString());
                    this.render(context, resp);
                } else {
                    this.redirectController.redirectOrPrintRedirectionWarning(resp, redirectUrl);
                }
            }
        }
        catch (Exception e) {
            this.handleExceptionThrownDuringTokenRequest(req, resp, applicationLink, e, context);
        }
    }

    private void handleExceptionThrownDuringTokenRequest(HttpServletRequest req, HttpServletResponse resp, @Nonnull ApplicationLink applicationLink, @Nonnull Exception e, @Nonnull Map<String, Object> context) throws IOException {
        LOG.error("An error occurred when performing the oauth 'dance' for application link '" + applicationLink + "'", (Throwable)e);
        this.addRedirectUrlToContext(req, context);
        if (e.getCause() instanceof OAuthProblemException) {
            OAuthProblemException oAuthProblem = (OAuthProblemException)e.getCause();
            String problem = oAuthProblem.getProblem();
            if (problem.equals("consumer_key_unknown")) {
                this.addErrorToContext(context, ERROR_CONSUMER_UNKNOWN, this.internalHostApplication.getName(), applicationLink.getName());
                this.addCheckLinkAdminErrorToContext(context, applicationLink.getName());
                this.render(context, resp);
            } else if (problem.equals("token_rejected")) {
                this.addErrorToContext(context, ERROR_TOKEN_REJECTED, new String[0]);
                this.addCheckLinkAdminErrorToContext(context, applicationLink.getName());
                this.render(context, resp);
            } else {
                this.addErrorToContext(context, ERRROR_OAUTH_DANCE_PROBLEM, applicationLink.toString(), oAuthProblem.getProblem());
                this.addCheckLinkAdminErrorToContext(context, applicationLink.getName());
                this.render(context, resp);
            }
        } else if (e instanceof OAuthPermissionDeniedException) {
            this.addWarningToContext(context, WARNING_TITLE_ACCESS_DENIED, WARNING_MESSAGE_ACCESS_DENIED, applicationLink.getName());
            this.addAccessDeniedUserErrorToContext(context, applicationLink.getName());
            this.render(context, resp);
        } else if (e instanceof OAuthMessageProblemException) {
            List<String> errorDetails = this.getDetailsFromOAuthMessageProblemException((OAuthMessageProblemException)e);
            LOG.error("Error during OAuth Dance, OAuth Parameters '{}'", (Object)StringUtils.join(errorDetails, (String)","));
            this.addErrorDetailsToContext(context, errorDetails);
            this.addErrorToContext(context, ERROR_OAUTH_DANCE, applicationLink.toString());
            this.addCheckLinkAdminErrorToContext(context, applicationLink.getName());
            this.render(context, resp);
        } else if (e instanceof ResponseException) {
            this.addErrorToContext(context, ERROR_OAUTH_DANCE, applicationLink.toString());
            this.addErrorDetailsToContext(context, Lists.newArrayList((Object[])new String[]{e.getLocalizedMessage()}));
            this.addCheckLinkAdminErrorToContext(context, applicationLink.getName());
            this.render(context, resp);
        } else {
            this.addErrorToContext(context, ERROR_OAUTH_DANCE, applicationLink.toString());
            this.addCheckLinkAdminErrorToContext(context, applicationLink.getName());
            this.render(context, resp);
        }
    }

    private void addErrorToContext(@Nonnull Map<String, Object> context, @Nonnull String errorKey, String ... args) {
        this.addVariableToContext(context, TemplateVariable.ERROR, errorKey, args);
    }

    private void addErrorDetailsToContext(@Nonnull Map<String, Object> context, @Nonnull List<String> errorDetails) {
        context.put(TemplateVariable.ERROR_DETAILS.key, errorDetails);
    }

    private void addWarningToContext(@Nonnull Map<String, Object> context, @Nonnull String warningTitleKey, @Nonnull String warningMessageKey, String ... args) {
        this.addVariableToContext(context, TemplateVariable.WARNING_TITLE, warningTitleKey, new String[0]);
        this.addVariableToContext(context, TemplateVariable.WARNING_MESSAGE, warningMessageKey, args);
    }

    private void addCheckConfigAdminErrorToContext(@Nonnull Map<String, Object> context) {
        this.addVariableToContext(context, TemplateVariable.ADMIN_ERROR_MESSAGE, ADMIN_ERROR_CHECK_CONFIG, this.internalHostApplication.getName());
    }

    private void addCheckLinkAdminErrorToContext(@Nonnull Map<String, Object> context, @Nonnull String remoteAppName) {
        this.addVariableToContext(context, TemplateVariable.ADMIN_ERROR_MESSAGE, ADMIN_ERROR_CHECK_LINK, this.internalHostApplication.getName(), remoteAppName);
    }

    private void addNotLoggedInUserErrorToContext(@Nonnull Map<String, Object> context) {
        this.addVariableToContext(context, TemplateVariable.USER_ERROR_MESSAGE, USER_ERROR_NOT_LOGGED_IN, this.internalHostApplication.getName());
    }

    private void addAccessDeniedUserErrorToContext(@Nonnull Map<String, Object> context, @Nonnull String remoteAppName) {
        this.addVariableToContext(context, TemplateVariable.USER_ERROR_MESSAGE, USER_ERROR_ACCESS_DENIED, this.internalHostApplication.getName(), remoteAppName);
    }

    private void addVariableToContext(@Nonnull Map<String, Object> context, @Nonnull TemplateVariable templateVariable, @Nonnull String errorKey, String ... args) {
        String error = args.length > 0 ? this.i18nResolver.getText(errorKey, (Serializable[])args) : this.i18nResolver.getText(errorKey);
        context.put(templateVariable.key, error);
    }

    private void render(@Nonnull Map<String, Object> context, @Nonnull HttpServletResponse resp) throws IOException {
        this.templateRenderer.render(TEMPLATE, context, (Writer)resp.getWriter());
    }

    private List<String> getDetailsFromOAuthMessageProblemException(@Nonnull OAuthMessageProblemException e) {
        ArrayList errorDetails = Lists.newArrayList();
        for (Map.Entry param : e.getParameters().entrySet()) {
            String oAuthParam = (String)param.getKey() + ": '" + (String)param.getValue() + "'";
            errorDetails.add(oAuthParam);
        }
        return errorDetails;
    }

    private void addRedirectUrlToContext(@Nonnull HttpServletRequest req, @Nonnull Map<String, Object> context) {
        String redirectUrl = this.getValidatedRedirectUrl(req);
        if (redirectUrl == null) {
            redirectUrl = "#";
        }
        context.put(TemplateVariable.REDIRECT_URL.key, redirectUrl);
    }

    private Map<String, Object> createVelocityContext(HttpServletResponse resp) {
        HashMap<String, Object> context = new HashMap<String, Object>();
        context.put("i18n", this.i18nResolver);
        resp.setContentType("text/html");
        this.webResourceManager.requireResource("com.atlassian.applinks.applinks-oauth-plugin:oauth-dance");
        StringWriter stringWriter = new StringWriter();
        this.webResourceManager.includeResources((Writer)stringWriter, UrlMode.RELATIVE);
        WebResources webResources = new WebResources();
        webResources.setIncludedResources(stringWriter.getBuffer().toString());
        context.put(TemplateVariable.WEB_RESOURCES.key, webResources);
        return context;
    }

    private String getToken(HttpServletRequest req) {
        return req.getParameter("oauth_token");
    }

    private void getAccessToken(String requestToken, ApplicationLink applicationLink, HttpServletRequest request) throws ResponseException {
        String username = this.getRemoteUsername(request);
        ConsumerToken requestTokenPair = this.consumerTokenStoreService.getConsumerToken(applicationLink, username);
        if (requestTokenPair == null) {
            throw new ResponseException("Cannot get access token as no request token pair can be found");
        }
        if (requestTokenPair.isAccessToken()) {
            return;
        }
        if (!requestToken.equals(requestTokenPair.getToken())) {
            throw new ResponseException("The oauth_token in the request is not the same as the token persisted in the system.");
        }
        Map config = this.authenticationConfigurationManager.getConfiguration(applicationLink.getId(), OAuthAuthenticationProvider.class);
        ServiceProvider serviceProvider = ServiceProviderUtil.getServiceProvider(config, applicationLink);
        String requestVerifier = request.getParameter("oauth_verifier");
        String consumerKey = this.getConsumerKey(applicationLink);
        ConsumerToken accessToken = this.oAuthTokenRetriever.getAccessToken(serviceProvider, requestTokenPair, requestVerifier, consumerKey);
        this.consumerTokenStoreService.removeConsumerToken(applicationLink.getId(), username);
        this.consumerTokenStoreService.addConsumerToken(applicationLink, username, accessToken);
    }

    private void obtainAndAuthorizeRequestToken(ApplicationLink applicationLink, HttpServletResponse resp, HttpServletRequest req) throws ResponseException, IOException {
        Map config = this.authenticationConfigurationManager.getConfiguration(applicationLink.getId(), OAuthAuthenticationProvider.class);
        ServiceProvider serviceProvider = ServiceProviderUtil.getServiceProvider(config, applicationLink);
        String consumerKey = this.getConsumerKey(applicationLink);
        String redirectUrl = this.getRedirectUrl(req);
        URI baseUrl = RequestUtil.getBaseURLFromRequest((HttpServletRequest)req, (URI)this.internalHostApplication.getBaseUrl());
        String redirectToMeUrl = baseUrl + ServletPathConstants.APPLINKS_SERVLETS_PATH + "/oauth/login-dance/" + ACCESS_PATH + "?" + APPLICATION_LINK_ID_PARAM + "=" + applicationLink.getId() + (redirectUrl != null ? "&redirectUrl=" + URLEncoder.encode(redirectUrl, "UTF-8") : "");
        ConsumerToken requestToken = this.oAuthTokenRetriever.getRequestToken(serviceProvider, consumerKey, redirectToMeUrl);
        this.consumerTokenStoreService.addConsumerToken(applicationLink, this.getRemoteUsername(req), requestToken);
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("oauth_token", requestToken.getToken());
        parameters.put("oauth_callback", redirectToMeUrl);
        resp.sendRedirect(serviceProvider.getAuthorizeUri() + "?" + OAuth.formEncode(parameters.entrySet()));
    }

    private String getRemoteUsername(HttpServletRequest request) {
        UserProfile remoteUser = this.userManager.getRemoteUser(request);
        return remoteUser == null ? null : remoteUser.getUsername();
    }

    private String getConsumerKey(ApplicationLink applicationLink) {
        Map config = this.authenticationConfigurationManager.getConfiguration(applicationLink.getId(), OAuthAuthenticationProvider.class);
        if (config.containsKey(AddServiceProviderManuallyServlet.CONSUMER_KEY_OUTBOUND)) {
            return (String)config.get(AddServiceProviderManuallyServlet.CONSUMER_KEY_OUTBOUND);
        }
        return this.consumerService.getConsumer().getKey();
    }

    private String getApplicationLinkId(HttpServletRequest req) {
        return req.getParameter(APPLICATION_LINK_ID_PARAM);
    }

    private String getRedirectUrl(HttpServletRequest req) {
        return req.getParameter(REDIRECT_URL_PARAM);
    }

    private String getValidatedRedirectUrl(HttpServletRequest req) {
        String redirectUrl = req.getParameter(REDIRECT_URL_PARAM);
        if (redirectUrl != null) {
            this.callbackParameterValidator.validate(redirectUrl);
        }
        return redirectUrl;
    }

    @VisibleForTesting
    static enum TemplateVariable {
        ADMIN_ERROR_MESSAGE("adminError"),
        APPLINK_ID("applinkId"),
        AUTH_ADMIN_URI("authAdminUri"),
        ERROR("error"),
        ERROR_DETAILS("errorDetails"),
        REDIRECT_URL("redirectUrl"),
        USER_ERROR_MESSAGE("userError"),
        WARNING_MESSAGE("warningMessage"),
        WARNING_TITLE("warningTitle"),
        WEB_RESOURCES("webResources");

        final String key;

        private TemplateVariable(String key) {
            this.key = key;
        }
    }
}

