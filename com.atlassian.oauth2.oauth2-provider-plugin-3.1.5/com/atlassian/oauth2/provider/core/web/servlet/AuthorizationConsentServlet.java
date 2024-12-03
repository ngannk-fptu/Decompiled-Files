/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.provider.api.client.Client
 *  com.atlassian.oauth2.provider.api.client.ClientService
 *  com.atlassian.oauth2.provider.api.xsrf.OAuth2XsrfTokenGenerator
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.validator.routines.UrlValidator
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.core.web.servlet;

import com.atlassian.oauth2.common.validator.HttpsValidator;
import com.atlassian.oauth2.common.web.loopsprevention.RedirectsLoopPreventer;
import com.atlassian.oauth2.provider.api.client.Client;
import com.atlassian.oauth2.provider.api.client.ClientService;
import com.atlassian.oauth2.provider.api.xsrf.OAuth2XsrfTokenGenerator;
import com.atlassian.oauth2.provider.core.web.servlet.AuthorizationConsentServletConfiguration;
import com.atlassian.oauth2.provider.core.web.servlet.AuthorizationServlet;
import com.atlassian.oauth2.provider.core.web.validator.InvalidUrlParameterException;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationConsentServlet
extends AuthorizationServlet {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationConsentServlet.class);
    private static final String CODE_CHALLENGE_METHOD = "code_challenge_method";
    private static final String CODE_CHALLENGE = "code_challenge";
    private static final String REDIRECT_URI = "redirect_uri";
    private static final UrlValidator URL_VALIDATOR = new UrlValidator(8L);
    private final AuthorizationConsentServletConfiguration authorizationConsentServletConfiguration;
    private final OAuth2XsrfTokenGenerator oAuth2XsrfTokenGenerator;
    private final ClientService clientService;

    public AuthorizationConsentServlet(LoginUriProvider loginUriProvider, UserManager userManager, SoyTemplateRenderer templateRenderer, RedirectsLoopPreventer loopPreventer, I18nResolver i18nResolver, AuthorizationConsentServletConfiguration authorizationConsentServletConfiguration, HttpsValidator httpsValidator, OAuth2XsrfTokenGenerator oAuth2XsrfTokenGenerator, ClientService clientService) {
        super(loginUriProvider, userManager, templateRenderer, loopPreventer, i18nResolver, httpsValidator);
        this.authorizationConsentServletConfiguration = authorizationConsentServletConfiguration;
        this.oAuth2XsrfTokenGenerator = oAuth2XsrfTokenGenerator;
        this.clientService = clientService;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            this.validateParameters(request);
            super.doGet(request, response);
        }
        catch (InvalidUrlParameterException e) {
            logger.warn("Invalid '{}' URL parameter provided", (Object)e.getUrlParameterKey());
            response.sendError(412, this.i18nResolver.getText("oauth2.servlet.error.invalid.url.parameter", new Serializable[]{e.getUrlParameterKey()}));
        }
    }

    @Override
    void render(HttpServletRequest request, HttpServletResponse response, SoyTemplateRenderer soyTemplateRenderer) throws IOException {
        soyTemplateRenderer.render((Appendable)response.getWriter(), this.authorizationConsentServletConfiguration.moduleKey(), this.authorizationConsentServletConfiguration.templateName(), this.getParameters(request, response));
    }

    private Map<String, Object> getParameters(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        String clientId = request.getParameter("client_id");
        parameters.put("xsrfTokenName", this.oAuth2XsrfTokenGenerator.getXsrfTokenName());
        parameters.put("xsrfTokenValue", StringUtils.defaultString((String)this.oAuth2XsrfTokenGenerator.generateToken(request)));
        parameters.put("client_id", clientId);
        parameters.put(REDIRECT_URI, request.getParameter(REDIRECT_URI));
        parameters.put("response_type", request.getParameter("response_type"));
        parameters.put("scope", StringUtils.defaultString((String)request.getParameter("scope")));
        parameters.put("state", StringUtils.defaultString((String)request.getParameter("state")));
        parameters.put("product_name", this.authorizationConsentServletConfiguration.productName());
        Optional client = this.clientService.getByClientId(clientId);
        if (!client.isPresent()) {
            logger.debug("Invalid client id provided, unable to resolve client information.");
            response.sendError(400, this.i18nResolver.getText("oauth2.rest.error.client.does.not.exist"));
            return Collections.emptyMap();
        }
        parameters.put("client_name", ((Client)client.get()).getName());
        if (this.userManager.getRemoteUser() != null) {
            parameters.put("username", this.userManager.getRemoteUser().getUsername());
            parameters.put("full_name", this.userManager.getRemoteUser().getFullName());
            if (this.userManager.getRemoteUser().getProfilePictureUri() != null) {
                parameters.put("profile_picture_uri", this.userManager.getRemoteUser().getProfilePictureUri().toString());
            }
        } else {
            logger.debug("Unable to resolve remote user.");
        }
        if (StringUtils.isNotBlank((CharSequence)request.getParameter(CODE_CHALLENGE_METHOD))) {
            parameters.put(CODE_CHALLENGE_METHOD, request.getParameter(CODE_CHALLENGE_METHOD));
        }
        if (StringUtils.isNotBlank((CharSequence)request.getParameter(CODE_CHALLENGE))) {
            parameters.put(CODE_CHALLENGE, request.getParameter(CODE_CHALLENGE));
        }
        return parameters;
    }

    private void validateParameters(HttpServletRequest request) throws InvalidUrlParameterException {
        if (!this.isURL(request.getParameter(REDIRECT_URI))) {
            throw new InvalidUrlParameterException(REDIRECT_URI);
        }
    }

    private boolean isURL(String inputString) {
        return URL_VALIDATOR.isValid(inputString);
    }
}

