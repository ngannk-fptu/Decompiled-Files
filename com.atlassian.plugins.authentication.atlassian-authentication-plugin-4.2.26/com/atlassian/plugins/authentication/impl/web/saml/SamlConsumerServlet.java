/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.AuthenticationListener
 *  com.atlassian.sal.api.auth.Authenticator$Result
 *  com.atlassian.sal.api.auth.Authenticator$Result$Failure
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.base.Strings
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nonnull
 *  javax.inject.Inject
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.web.saml;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.api.config.IdpConfigService;
import com.atlassian.plugins.authentication.api.config.IdpSearchParameters;
import com.atlassian.plugins.authentication.api.config.JustInTimeConfig;
import com.atlassian.plugins.authentication.api.config.SsoType;
import com.atlassian.plugins.authentication.api.config.saml.SamlConfig;
import com.atlassian.plugins.authentication.impl.util.ApplicationStateValidator;
import com.atlassian.plugins.authentication.impl.web.AbstractConsumerServlet;
import com.atlassian.plugins.authentication.impl.web.AuthenticationHandler;
import com.atlassian.plugins.authentication.impl.web.AuthenticationHandlerNotConfiguredException;
import com.atlassian.plugins.authentication.impl.web.AuthenticationHandlerProvider;
import com.atlassian.plugins.authentication.impl.web.SessionData;
import com.atlassian.plugins.authentication.impl.web.SessionDataService;
import com.atlassian.plugins.authentication.impl.web.saml.SamlAssertionValidationService;
import com.atlassian.plugins.authentication.impl.web.saml.provider.InvalidSamlResponse;
import com.atlassian.plugins.authentication.impl.web.saml.provider.SamlProvider;
import com.atlassian.plugins.authentication.impl.web.saml.provider.SamlRequest;
import com.atlassian.plugins.authentication.impl.web.saml.provider.SamlResponse;
import com.atlassian.plugins.authentication.impl.web.usercontext.AuthenticationFailedException;
import com.atlassian.plugins.authentication.impl.web.usercontext.PrincipalResolver;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.ProvisioningService;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.mapping.MappingExpression;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.mapping.SamlUserDataFromIdpMapper;
import com.atlassian.plugins.authentication.impl.web.usercontext.rememberme.RememberMeCookieHandler;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.AuthenticationListener;
import com.atlassian.sal.api.auth.Authenticator;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SamlConsumerServlet
extends AbstractConsumerServlet {
    private static final Logger log = LoggerFactory.getLogger(SamlConsumerServlet.class);
    public static final String URL = "/plugins/servlet/samlconsumer";
    public static final String SAML_RESPONSE_PARAM = "SAMLResponse";
    public static final String RELAY_STATE_QUERY_PARAM = "RelayState";
    private final SamlProvider samlProvider;
    private final SamlAssertionValidationService samlAssertionValidationService;
    private final AuthenticationHandlerProvider authenticationHandlerProvider;
    private final SamlUserDataFromIdpMapper mapper;

    @Inject
    public SamlConsumerServlet(@ComponentImport ApplicationProperties applicationProperties, IdpConfigService idpConfigService, PrincipalResolver principalResolver, SamlProvider samlProvider, SessionDataService sessionDataService, SamlAssertionValidationService samlAssertionValidationService, @ComponentImport AuthenticationListener authenticationListener, @ComponentImport I18nResolver i18nResolver, RememberMeCookieHandler rememberMeCookieHandler, ApplicationStateValidator applicationStateValidator, AuthenticationHandlerProvider authenticationHandlerProvider, ProvisioningService provisioningService, SamlUserDataFromIdpMapper mapper) {
        super(applicationProperties, principalResolver, sessionDataService, authenticationListener, i18nResolver, rememberMeCookieHandler, applicationStateValidator, idpConfigService, provisioningService);
        this.samlProvider = samlProvider;
        this.samlAssertionValidationService = samlAssertionValidationService;
        this.authenticationHandlerProvider = authenticationHandlerProvider;
        this.mapper = mapper;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.debug("Received SAML callback request");
        Optional<SessionData> sessionData = this.sessionDataService.getSessionData(request, response, request.getParameter(RELAY_STATE_QUERY_PARAM));
        SamlConfig samlConfig = null;
        String username = null;
        try {
            String finalUsername;
            Principal resolvedPrincipal;
            samlConfig = sessionData.map(this::fetchSamlConfigFromSession).orElseGet(() -> this.fetchSamlConfigByIssuer(request));
            this.applicationStateValidator.checkCanProcessAuthenticationRequest(samlConfig);
            SamlRequest samlRequest = sessionData.map(data -> (SamlRequest)data.getAuthenticationRequest()).orElse(null);
            SamlResponse samlResponse = this.samlProvider.extractSamlResponse(request, response, this.getServiceProviderInfo(), samlConfig, samlRequest);
            this.applicationStateValidator.checkHasAppropriateLicenseForSamlResponse(samlResponse);
            this.samlAssertionValidationService.validateAssertionId(samlResponse);
            username = this.getUsername(samlResponse, samlConfig);
            JustInTimeConfig jitConfig = samlConfig.getJustInTimeConfig();
            if (jitConfig.isEnabled().orElse(false).booleanValue()) {
                this.provisioningService.handleJustInTimeProvisioning(this.mapper.mapUser(samlResponse, username, samlConfig), request);
            }
            if (!this.principalResolver.isAllowedToAuthenticate(resolvedPrincipal = this.principalResolver.resolvePrincipal(finalUsername = username, request).orElseThrow(() -> new AuthenticationFailedException("Received SSO request for user " + finalUsername + ", but the user does not exist")), request)) {
                throw new AuthenticationFailedException("Received SSO request for user " + username + ", but the user is not permitted to log in");
            }
            this.authenticationSuccess(request, response, resolvedPrincipal, "saml.authentication.successful");
            String redirectUrl = this.sessionDataService.extractTargetUrlOrReturnBaseUrl(sessionData);
            log.debug("Authenticated user {} from IDP with ID '{}', redirecting to {}", new Object[]{resolvedPrincipal.getName(), samlConfig.getId(), redirectUrl});
            this.refreshRememberMeCookieIfNeeded(samlConfig, request, response, samlResponse, resolvedPrincipal);
            response.sendRedirect(redirectUrl);
        }
        catch (InvalidSamlResponse e) {
            log.warn("Received an invalid SamlResponse: {}", (Object)e.toString());
            e.setTargetUrl(sessionData.flatMap(SessionData::getTargetUrl).map(URI::toString).orElse(null));
            if (samlConfig != null) {
                e.setIdpConfigId(samlConfig.getId());
            }
            this.authenticationListener.authenticationFailure((Authenticator.Result)new Authenticator.Result.Failure(this.i18nResolver.createMessage("saml.authentication.invalidsamlresponse", new Serializable[]{request.getRemoteAddr()})), request, response);
            throw e;
        }
        catch (AuthenticationFailedException e) {
            log.debug("Failed to authenticate: {}", (Object)e.toString());
            this.authenticationListener.authenticationFailure((Authenticator.Result)new Authenticator.Result.Failure(this.i18nResolver.createMessage("saml.authentication.authenticationfailed", new Serializable[]{username})), request, response);
            throw e;
        }
    }

    private SamlConfig fetchSamlConfigFromSession(SessionData sessionData) {
        return SamlConfig.from(this.idpConfigService.getIdpConfig(sessionData.getIdpConfigId())).orElseThrow(() -> new AuthenticationHandlerNotConfiguredException("SP initiated SAML flow: session IDP Config is not SAML in SAML callback"));
    }

    private SamlConfig fetchSamlConfigByIssuer(HttpServletRequest request) {
        List<String> issuers = this.samlProvider.getIssuers(request);
        List samlConfigs = this.idpConfigService.getIdpConfigs(IdpSearchParameters.allEnabledOfType(SsoType.SAML)).stream().map(idpConfig -> (SamlConfig)idpConfig).filter(samlConfig -> issuers.stream().anyMatch(issuer -> Objects.equals(issuer, samlConfig.getIssuer()))).collect(Collectors.toList());
        if (samlConfigs.size() != 1) {
            log.error("IDP initiated SAML flow: could not retrieve IDP config for issuers {}", issuers);
            throw new AuthenticationHandlerNotConfiguredException("Could not log in from Identity Provider");
        }
        return (SamlConfig)samlConfigs.get(0);
    }

    private void refreshRememberMeCookieIfNeeded(SamlConfig samlConfig, HttpServletRequest request, HttpServletResponse response, SamlResponse samlResponse, Principal principal) {
        if (samlConfig.isEnableRememberMe() || this.hasRememberMeFlagFromCrowd(samlResponse)) {
            this.rememberMeCookieHandler.refreshRememberMeCookie(request, response, principal);
            log.debug("Refreshed 'remember me' cookie for {}", (Object)principal.getName());
        }
    }

    private boolean hasRememberMeFlagFromCrowd(SamlResponse samlResponse) {
        Iterable<String> attributeValues = samlResponse.getAttribute("atl.crowd.properties.remember_me");
        return attributeValues != null && StreamSupport.stream(attributeValues.spliterator(), false).anyMatch(Boolean::parseBoolean);
    }

    private String getUsername(@Nonnull SamlResponse samlResponse, @Nonnull SamlConfig samlConfig) {
        String rawExpression = Strings.isNullOrEmpty((String)samlConfig.getUsernameAttribute()) ? "${NameID}" : samlConfig.getUsernameAttribute();
        MappingExpression expression = new MappingExpression(rawExpression);
        return expression.evaluateWithValues(varName -> this.getAttributeOrNameId(samlResponse, (String)varName));
    }

    private String getAttributeOrNameId(SamlResponse response, String key) {
        return "NameID".equalsIgnoreCase(key) ? response.getNameId() : (String)Iterables.getOnlyElement(response.getAttribute(key));
    }

    private SamlProvider.ServiceProviderInfo getServiceProviderInfo() {
        AuthenticationHandler authenticationHandler = this.authenticationHandlerProvider.getAuthenticationHandler(SsoType.SAML);
        return new SamlProvider.ServiceProviderInfo(authenticationHandler.getIssuerUrl(), authenticationHandler.getConsumerServletUrl());
    }
}

