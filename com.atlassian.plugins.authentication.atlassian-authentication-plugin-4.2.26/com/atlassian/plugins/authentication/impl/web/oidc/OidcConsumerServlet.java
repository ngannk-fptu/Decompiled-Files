/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.AuthenticationListener
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  javax.annotation.Nonnull
 *  javax.inject.Inject
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.web.oidc;

import com.atlassian.plugins.authentication.api.config.IdpConfigService;
import com.atlassian.plugins.authentication.api.config.JustInTimeConfig;
import com.atlassian.plugins.authentication.api.config.SsoType;
import com.atlassian.plugins.authentication.api.config.oidc.OidcConfig;
import com.atlassian.plugins.authentication.impl.util.ApplicationStateValidator;
import com.atlassian.plugins.authentication.impl.web.AbstractConsumerServlet;
import com.atlassian.plugins.authentication.impl.web.AuthenticationHandler;
import com.atlassian.plugins.authentication.impl.web.AuthenticationHandlerNotConfiguredException;
import com.atlassian.plugins.authentication.impl.web.AuthenticationHandlerProvider;
import com.atlassian.plugins.authentication.impl.web.SessionData;
import com.atlassian.plugins.authentication.impl.web.SessionDataService;
import com.atlassian.plugins.authentication.impl.web.oidc.OidcAuthenticationRequest;
import com.atlassian.plugins.authentication.impl.web.oidc.OidcTimeouts;
import com.atlassian.plugins.authentication.impl.web.usercontext.AuthenticationFailedException;
import com.atlassian.plugins.authentication.impl.web.usercontext.PrincipalResolver;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.ProvisioningService;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.mapping.MappingExpression;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.mapping.OidcUserDataFromIdpMapper;
import com.atlassian.plugins.authentication.impl.web.usercontext.rememberme.RememberMeCookieHandler;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.AuthenticationListener;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.TokenErrorResponse;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.Tokens;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import com.nimbusds.openid.connect.sdk.UserInfoSuccessResponse;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import com.nimbusds.openid.connect.sdk.validators.IDTokenClaimsVerifier;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.text.ParseException;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.minidev.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OidcConsumerServlet
extends AbstractConsumerServlet {
    private static final Logger log = LoggerFactory.getLogger(OidcConsumerServlet.class);
    public static final String CALLBACK_URL = "/plugins/servlet/oidc/callback";
    private static final String MAX_CLOCK_SKEW_PROPERTY_NAME = "com.atlassian.plugins.authentication.impl.web.oidc.OidcConsumerServlet.maxClockSkewSeconds";
    private static final Duration MAX_CLOCK_SKEW = Optional.ofNullable(System.getProperty("com.atlassian.plugins.authentication.impl.web.oidc.OidcConsumerServlet.maxClockSkewSeconds")).map(Integer::parseInt).map(Duration::ofSeconds).orElse(Duration.ofSeconds(60L));
    private final AuthenticationHandlerProvider authenticationHandlerProvider;
    private final OidcUserDataFromIdpMapper mapper;
    private final OidcTimeouts oidcTimeouts;

    @Inject
    public OidcConsumerServlet(ApplicationProperties applicationProperties, IdpConfigService idpConfigService, AuthenticationHandlerProvider authenticationHandlerProvider, PrincipalResolver principalResolver, SessionDataService sessionDataService, AuthenticationListener authenticationListener, I18nResolver i18nResolver, RememberMeCookieHandler rememberMeCookieHandler, ApplicationStateValidator applicationStateValidator, ProvisioningService provisioningService, OidcUserDataFromIdpMapper mapper, OidcTimeouts oidcTimeouts) {
        super(applicationProperties, principalResolver, sessionDataService, authenticationListener, i18nResolver, rememberMeCookieHandler, applicationStateValidator, idpConfigService, provisioningService);
        this.authenticationHandlerProvider = authenticationHandlerProvider;
        this.mapper = mapper;
        this.oidcTimeouts = oidcTimeouts;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Principal resolvedPrincipal;
        AuthenticationSuccessResponse successResponse = this.parseResponse(request);
        SessionData sessionData = this.sessionDataService.getSessionData(request, response, successResponse.getState().getValue()).orElseThrow(() -> new AuthenticationFailedException("Unknown state in response"));
        OidcConfig oidcConfig = this.fetchOidcConfigFromSession(sessionData);
        this.applicationStateValidator.checkCanProcessAuthenticationRequest(oidcConfig);
        OIDCTokens tokens = this.getOidcTokens(successResponse, oidcConfig);
        String username = this.getUsername(tokens, sessionData, oidcConfig);
        JustInTimeConfig jitConfig = oidcConfig.getJustInTimeConfig();
        Optional<Principal> principal = Optional.empty();
        if (jitConfig.isEnabled().orElse(false).booleanValue()) {
            this.provisioningService.handleJustInTimeProvisioning(this.mapper.mapUser(tokens, username, oidcConfig), request);
        }
        if (!this.principalResolver.isAllowedToAuthenticate(resolvedPrincipal = principal.orElseGet(() -> this.principalResolver.resolvePrincipal(username, request).orElseThrow(() -> new AuthenticationFailedException("Received SSO request for user " + username + ", but the user does not exist"))), request)) {
            throw new AuthenticationFailedException("Received SSO request for user " + username + ", but the user is not permitted to log in");
        }
        this.authenticationSuccess(request, response, resolvedPrincipal, "oidc.authentication.successful");
        String redirectUrl = this.sessionDataService.extractTargetUrlOrReturnBaseUrl(Optional.of(sessionData));
        log.debug("Authenticated user {} from IDP with ID '{}', redirecting to {}", new Object[]{resolvedPrincipal.getName(), oidcConfig.getId(), redirectUrl});
        this.refreshRememberMeCookieIfNeeded(oidcConfig, request, response, resolvedPrincipal);
        response.sendRedirect(redirectUrl);
    }

    private String getUsername(OIDCTokens tokens, SessionData sessionData, OidcConfig oidcConfig) {
        Preconditions.checkState((boolean)(sessionData.getAuthenticationRequest() instanceof OidcAuthenticationRequest));
        OidcAuthenticationRequest request = (OidcAuthenticationRequest)sessionData.getAuthenticationRequest();
        try {
            JWTClaimsSet jwtClaimsSet = tokens.getIDToken().getJWTClaimsSet();
            new IDTokenClaimsVerifier(new Issuer(oidcConfig.getIssuer()), new ClientID(oidcConfig.getClientId()), Nonce.parse(request.getNonce()), (int)MAX_CLOCK_SKEW.getSeconds()).verify(jwtClaimsSet, null);
            String rawExpression = Strings.isNullOrEmpty((String)oidcConfig.getUsernameClaim()) ? "${sub}" : oidcConfig.getUsernameClaim();
            MappingExpression usernameClaimExpression = new MappingExpression(rawExpression);
            return usernameClaimExpression.evaluateWithValues(varName -> this.getUsernameFromCustomClaim(oidcConfig, tokens, jwtClaimsSet, (String)varName));
        }
        catch (BadJWTException | ParseException e) {
            throw new AuthenticationFailedException("ID token parsing failed", e);
        }
    }

    @NotNull
    private OIDCTokens getOidcTokens(AuthenticationSuccessResponse successResponse, OidcConfig oidcConfig) {
        TokenRequest tokenRequest = this.prepareTokenRequest(successResponse.getAuthorizationCode(), oidcConfig);
        return this.exchangeAuthorizationCodeForTokens(tokenRequest);
    }

    private AuthenticationSuccessResponse parseResponse(HttpServletRequest request) {
        try {
            AuthenticationResponse authResp = AuthenticationResponseParser.parse(URI.create(request.getRequestURL().toString()), Maps.transformValues((Map)request.getParameterMap(), ImmutableList::copyOf));
            if (authResp.indicatesSuccess()) {
                return authResp.toSuccessResponse();
            }
            throw this.toException("Error when fetching authorization response", authResp.toErrorResponse().getErrorObject());
        }
        catch (com.nimbusds.oauth2.sdk.ParseException e) {
            throw new AuthenticationFailedException("Parsing authentication response failed", e);
        }
    }

    private OidcConfig fetchOidcConfigFromSession(SessionData sessionData) {
        return OidcConfig.from(this.idpConfigService.getIdpConfig(sessionData.getIdpConfigId())).orElseThrow(() -> new AuthenticationHandlerNotConfiguredException("Session IDP Config is not OIDC in OIDC callback"));
    }

    @Nonnull
    @VisibleForTesting
    OIDCTokens exchangeAuthorizationCodeForTokens(TokenRequest tokenReq) {
        try {
            HTTPRequest httpRequest = tokenReq.toHTTPRequest();
            httpRequest.setConnectTimeout(this.oidcTimeouts.getConnectTimeoutInMillis());
            httpRequest.setReadTimeout(this.oidcTimeouts.getReadTimeoutInMillis());
            HTTPResponse tokenHTTPResp = httpRequest.send();
            if (tokenHTTPResp.getStatusCode() == 200) {
                return OIDCTokenResponse.parse(tokenHTTPResp).getOIDCTokens();
            }
            ErrorObject oidcErrorObject = TokenErrorResponse.parse(tokenHTTPResp).getErrorObject();
            if (oidcErrorObject.getCode() == null && oidcErrorObject.getDescription() == null) {
                log.debug("Received invalid response when exchanging authorization tokens: {}", (Object)tokenHTTPResp.getContent());
            }
            throw this.toException("Exchanging authorization tokens failed", oidcErrorObject);
        }
        catch (com.nimbusds.oauth2.sdk.ParseException | SerializeException | IOException e) {
            throw new AuthenticationFailedException("Exchanging authorization tokens failed.", e);
        }
    }

    private String getUsernameFromCustomClaim(OidcConfig oidcConfig, OIDCTokens tokens, JWTClaimsSet jwtClaimsSet, String usernameClaim) {
        String usernameFromIdToken;
        try {
            log.debug("Looking for a username in ID token by checking custom claim [{}]", (Object)usernameClaim);
            usernameFromIdToken = jwtClaimsSet.getStringClaim(usernameClaim);
        }
        catch (ParseException e) {
            throw new AuthenticationFailedException("ID token parsing failed", e);
        }
        if (Strings.isNullOrEmpty((String)usernameFromIdToken)) {
            log.debug("Custom claim with a username in ID token not found. Request to the userinfo endpoint will be sent.");
            return this.getUsernameFromUserInfoEndpoint(tokens, usernameClaim, oidcConfig.getUserInfoEndpoint());
        }
        return usernameFromIdToken;
    }

    @Nonnull
    private String getUsernameFromUserInfoEndpoint(Tokens tokens, String usernameClaim, String userInfoEndpoint) {
        URI userInfoUri = URI.create(userInfoEndpoint);
        UserInfoRequest userInfoReq = new UserInfoRequest(userInfoUri, (BearerAccessToken)tokens.getAccessToken());
        UserInfoSuccessResponse userInfoResponse = this.getUserInfoResponse(userInfoReq);
        JSONObject claims = userInfoResponse.getUserInfo().toJSONObject();
        String username = claims.getAsString(usernameClaim);
        if (Strings.isNullOrEmpty((String)username)) {
            log.debug("Couldn't find claim representing username [{}] within the set of claims returned from userinfo endpoint: {}", (Object)usernameClaim, (Object)claims.keySet().toString());
            throw new AuthenticationFailedException("Couldn't find claim representing username");
        }
        return username;
    }

    @Nonnull
    @VisibleForTesting
    UserInfoSuccessResponse getUserInfoResponse(UserInfoRequest userInfoReq) {
        try {
            HTTPRequest httpRequest = userInfoReq.toHTTPRequest();
            httpRequest.setReadTimeout(this.oidcTimeouts.getReadTimeoutInMillis());
            httpRequest.setConnectTimeout(this.oidcTimeouts.getConnectTimeoutInMillis());
            HTTPResponse userInfoHTTPResp = httpRequest.send();
            UserInfoResponse userInfoResponse = UserInfoResponse.parse(userInfoHTTPResp);
            if (userInfoResponse.indicatesSuccess()) {
                return userInfoResponse.toSuccessResponse();
            }
            throw this.toException("Error when fetching data from userinfo endpoint", userInfoResponse.toErrorResponse().getErrorObject());
        }
        catch (com.nimbusds.oauth2.sdk.ParseException | SerializeException | IOException e) {
            throw new AuthenticationFailedException("Error when fetching data from userinfo endpoint");
        }
    }

    private AuthenticationFailedException toException(String message, ErrorObject errorObject) {
        return new AuthenticationFailedException(message + ". Error: " + errorObject.toJSONObject().toString());
    }

    @Nonnull
    private TokenRequest prepareTokenRequest(AuthorizationCode authCode, OidcConfig ssoConfig) {
        URI tokenEndpoint = URI.create(ssoConfig.getTokenEndpoint());
        ClientID clientID = new ClientID(ssoConfig.getClientId());
        Secret clientSecret = new Secret(ssoConfig.getClientSecret());
        ClientSecretBasic clientInfo = new ClientSecretBasic(clientID, clientSecret);
        AuthorizationCodeGrant authorizationCodeGrant = new AuthorizationCodeGrant(authCode, this.getRedirectUri());
        return new TokenRequest(tokenEndpoint, clientInfo, (AuthorizationGrant)authorizationCodeGrant);
    }

    @Nonnull
    private URI getRedirectUri() {
        AuthenticationHandler authenticationHandler = this.authenticationHandlerProvider.getAuthenticationHandler(SsoType.OIDC);
        return URI.create(authenticationHandler.getConsumerServletUrl());
    }

    private void refreshRememberMeCookieIfNeeded(OidcConfig oidcConfig, HttpServletRequest request, HttpServletResponse response, Principal principal) {
        if (oidcConfig.isEnableRememberMe()) {
            this.rememberMeCookieHandler.refreshRememberMeCookie(request, response, principal);
            log.debug("Refreshed 'remember me' cookie for {}", (Object)principal.getName());
        }
    }
}

