/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.provider.api.authorization.AuthorizationService
 *  com.atlassian.oauth2.provider.api.client.Client
 *  com.atlassian.oauth2.provider.api.client.ClientService
 *  com.atlassian.oauth2.provider.api.pkce.CodeChallengeMethod
 *  com.atlassian.oauth2.scopes.api.ScopeResolver
 *  javax.annotation.Nonnull
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.core.UriBuilder
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.rest.service;

import com.atlassian.oauth2.provider.api.authorization.AuthorizationService;
import com.atlassian.oauth2.provider.api.client.Client;
import com.atlassian.oauth2.provider.api.client.ClientService;
import com.atlassian.oauth2.provider.api.pkce.CodeChallengeMethod;
import com.atlassian.oauth2.provider.core.web.servlet.AuthorizationConsentServletConfiguration;
import com.atlassian.oauth2.provider.core.web.servlet.AuthorizationErrorServletConfiguration;
import com.atlassian.oauth2.provider.core.xsrf.XsrfTokenValidationException;
import com.atlassian.oauth2.provider.core.xsrf.XsrfValidator;
import com.atlassian.oauth2.provider.rest.exception.BadRequestException;
import com.atlassian.oauth2.provider.rest.exception.InvalidRequestException;
import com.atlassian.oauth2.provider.rest.model.RestAuthorizationRequest;
import com.atlassian.oauth2.provider.rest.service.ScopeRestService;
import com.atlassian.oauth2.provider.rest.validation.AuthorizationValidator;
import com.atlassian.oauth2.scopes.api.ScopeResolver;
import java.net.URI;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationRestService {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationRestService.class);
    private final AuthorizationService authorizationService;
    private final ClientService clientService;
    private final AuthorizationValidator authorizationValidator;
    private final XsrfValidator xsrfValidator;
    private final ScopeRestService scopeRestService;
    private final ScopeResolver scopeResolver;
    private final AuthorizationConsentServletConfiguration authorizationConsentServletConfiguration;
    private final AuthorizationErrorServletConfiguration authorizationErrorServletConfiguration;

    public AuthorizationRestService(AuthorizationService authorizationService, ClientService clientService, AuthorizationValidator authorizationValidator, XsrfValidator xsrfValidator, ScopeRestService scopeRestService, ScopeResolver scopeResolver, AuthorizationConsentServletConfiguration authorizationConsentServletConfiguration, AuthorizationErrorServletConfiguration authorizationErrorServletConfiguration) {
        this.authorizationService = authorizationService;
        this.clientService = clientService;
        this.authorizationValidator = authorizationValidator;
        this.xsrfValidator = xsrfValidator;
        this.scopeRestService = scopeRestService;
        this.scopeResolver = scopeResolver;
        this.authorizationConsentServletConfiguration = authorizationConsentServletConfiguration;
        this.authorizationErrorServletConfiguration = authorizationErrorServletConfiguration;
    }

    public URI getRedirectUri(HttpServletRequest httpServletRequest, RestAuthorizationRequest request) {
        Optional<String> scopeErrorMessage = this.authorizationValidator.validateScope(request.getClientId(), request.getScope());
        if (scopeErrorMessage.isPresent()) {
            return this.redirectUriWithErrorQueryParameters(request.getRedirectUri(), request.getState(), "invalid_scope", scopeErrorMessage.get());
        }
        if (this.hasConsent(httpServletRequest)) {
            return this.validateAndStartAuthorizationFlow(httpServletRequest, request);
        }
        return this.redirectToConsentScreen(request);
    }

    private boolean hasConsent(@Nonnull HttpServletRequest httpServletRequest) {
        String referrer = httpServletRequest.getHeader("Referer");
        return referrer != null && referrer.startsWith(this.authorizationConsentServletConfiguration.consentServletUri());
    }

    private URI validateAndStartAuthorizationFlow(HttpServletRequest httpServletRequest, RestAuthorizationRequest request) {
        Optional client = this.clientService.getByClientId(request.getClientId());
        try {
            this.xsrfValidator.validateXsrf(httpServletRequest);
            this.authorizationValidator.validateForResourceOwner(client, request);
            this.ensureCodeChallengeMethodSetIfRequired(request);
            this.authorizationValidator.validatePkceFields(request.getCodeChallengeMethod(), request.getCodeChallenge());
        }
        catch (InvalidRequestException e) {
            return this.getErrorPageUri(e.getError(), e.getDescription());
        }
        catch (XsrfTokenValidationException xsrfTokenValidationException) {
            return this.getErrorPageUri("invalid_request", xsrfTokenValidationException.getMessage());
        }
        try {
            this.authorizationValidator.validateForClient(request);
            this.updateScope(request, (Client)client.get());
            this.authorizationValidator.validateScope((Client)client.get(), request);
        }
        catch (BadRequestException exception) {
            return this.redirectUriWithErrorQueryParameters(request.getRedirectUri(), request.getState(), exception.getError(), exception.getDescription());
        }
        return this.startAuthorizationFlow(request);
    }

    private void ensureCodeChallengeMethodSetIfRequired(RestAuthorizationRequest request) {
        boolean challengeProvided = StringUtils.isNotBlank((CharSequence)request.getCodeChallenge());
        boolean methodMissing = StringUtils.isBlank((CharSequence)request.getCodeChallengeMethod());
        if (methodMissing && challengeProvided) {
            request.setCodeChallengeMethod("plain");
        }
    }

    private void updateScope(RestAuthorizationRequest request, Client client) {
        request.setScope(this.scopeRestService.getOrClientDefault(request.getScope(), client.getScope()).toString());
    }

    private URI startAuthorizationFlow(@Nonnull RestAuthorizationRequest restAuthorizationRequest) {
        logger.debug("Starting authorization flow");
        String authorizationCode = this.authorizationService.startAuthorizationFlow(restAuthorizationRequest.getClientId(), restAuthorizationRequest.getRedirectUri(), this.scopeResolver.getScope(restAuthorizationRequest.getScope()), CodeChallengeMethod.fromString((String)restAuthorizationRequest.getCodeChallengeMethod()), restAuthorizationRequest.getCodeChallenge());
        return this.redirectUriWithParameters(restAuthorizationRequest.getRedirectUri(), authorizationCode, restAuthorizationRequest.getState());
    }

    private URI redirectUriWithErrorQueryParameters(String redirectUri, String state, String errorName, String errorDescription) {
        UriBuilder uriBuilder = UriBuilder.fromUri((String)redirectUri).queryParam("error", new Object[]{errorName}).queryParam("error_description", new Object[]{errorDescription});
        if (StringUtils.isNotBlank((CharSequence)state)) {
            uriBuilder.queryParam("state", new Object[]{state});
        }
        return uriBuilder.build(new Object[0]);
    }

    private URI redirectUriWithParameters(String redirectUri, String authenticationCode, String state) {
        UriBuilder uriBuilder = UriBuilder.fromUri((String)redirectUri).queryParam("code", new Object[]{authenticationCode});
        if (StringUtils.isNotBlank((CharSequence)state)) {
            uriBuilder.queryParam("state", new Object[]{state});
        }
        return uriBuilder.build(new Object[0]);
    }

    private URI redirectToConsentScreen(RestAuthorizationRequest request) {
        try {
            return this.getConsentPageUri(request);
        }
        catch (InvalidRequestException e) {
            return this.getErrorPageUri(e.getError(), e.getDescription());
        }
    }

    private URI getConsentPageUri(RestAuthorizationRequest request) throws InvalidRequestException {
        this.authorizationValidator.validateRedirectToConsentScreen(request);
        UriBuilder builder = UriBuilder.fromUri((String)this.authorizationConsentServletConfiguration.consentServletUri()).queryParam(AuthorizationConsentServletConfiguration.QueryParameter.CLIENT_ID_PARAMETER.name, new Object[]{request.getClientId()}).queryParam(AuthorizationConsentServletConfiguration.QueryParameter.REDIRECT_URI_PARAMETER.name, new Object[]{request.getRedirectUri()}).queryParam(AuthorizationConsentServletConfiguration.QueryParameter.RESPONSE_TYPE.name, new Object[]{request.getResponseType()}).queryParam(AuthorizationConsentServletConfiguration.QueryParameter.SCOPE.name, new Object[]{request.getScope()});
        if (StringUtils.isNotBlank((CharSequence)request.getState())) {
            builder.queryParam(AuthorizationConsentServletConfiguration.QueryParameter.STATE.name, new Object[]{request.getState()});
        }
        if (StringUtils.isNotBlank((CharSequence)request.getCodeChallengeMethod())) {
            builder.queryParam(AuthorizationConsentServletConfiguration.QueryParameter.CODE_CHALLENGE_METHOD.name, new Object[]{request.getCodeChallengeMethod()});
        }
        if (StringUtils.isNotBlank((CharSequence)request.getCodeChallenge())) {
            builder.queryParam(AuthorizationConsentServletConfiguration.QueryParameter.CODE_CHALLENGE.name, new Object[]{request.getCodeChallenge()});
        }
        return builder.build(new Object[0]);
    }

    private URI getErrorPageUri(String errorName, String errorDescription) {
        return UriBuilder.fromUri((String)this.authorizationErrorServletConfiguration.errorServletUri()).queryParam(AuthorizationErrorServletConfiguration.QueryParameter.ERROR_NAME.name, new Object[]{errorName}).queryParam(AuthorizationErrorServletConfiguration.QueryParameter.ERROR_DESCRIPTION.name, new Object[]{errorDescription}).build(new Object[0]);
    }
}

