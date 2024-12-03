/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.oauth2.client.api.ClientConfiguration
 *  com.atlassian.oauth2.client.api.ClientToken
 *  com.atlassian.oauth2.client.api.lib.token.TokenService
 *  com.atlassian.oauth2.client.api.lib.token.TokenServiceException
 *  com.atlassian.oauth2.client.api.storage.config.ProviderType
 *  com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.client.lib.token;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.oauth2.client.api.ClientConfiguration;
import com.atlassian.oauth2.client.api.ClientToken;
import com.atlassian.oauth2.client.api.lib.token.TokenService;
import com.atlassian.oauth2.client.api.lib.token.TokenServiceException;
import com.atlassian.oauth2.client.api.storage.config.ProviderType;
import com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity;
import com.atlassian.oauth2.client.lib.ClientTokenImpl;
import com.atlassian.oauth2.client.lib.token.InternalTokenService;
import com.atlassian.oauth2.client.lib.token.RefreshTokenExpirationHandler;
import com.atlassian.oauth2.client.lib.web.AuthorizationCodeFlowUrlsProvider;
import com.atlassian.oauth2.client.properties.SystemProperty;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.RefreshTokenGrant;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.auth.ClientSecretPost;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.oauth2.sdk.token.Tokens;
import java.net.URI;
import java.time.Clock;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultTokenService
implements TokenService,
InternalTokenService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultTokenService.class);
    private final Clock clock;
    private final RefreshTokenExpirationHandler refreshTokenExpirationHandler;
    private final AuthorizationCodeFlowUrlsProvider authorizationCodeFlowUrlsProvider;
    private final Duration minimumAccessTokenLifetimeWhenNoRefreshToken;
    private final Duration requestConfigurationTimeout;
    private final Set<ProviderType> providersWithRequiredRefreshToken;

    public DefaultTokenService(Clock clock, RefreshTokenExpirationHandler refreshTokenExpirationHandler, AuthorizationCodeFlowUrlsProvider authorizationCodeFlowUrlsProvider, Duration minimumAccessTokenLifetimeWhenNoRefreshToken, Duration requestConfigurationTimeout, Collection<ProviderType> providersWithRequiredRefreshToken) {
        this.clock = clock;
        this.refreshTokenExpirationHandler = refreshTokenExpirationHandler;
        this.authorizationCodeFlowUrlsProvider = authorizationCodeFlowUrlsProvider;
        this.minimumAccessTokenLifetimeWhenNoRefreshToken = minimumAccessTokenLifetimeWhenNoRefreshToken;
        this.requestConfigurationTimeout = requestConfigurationTimeout;
        this.providersWithRequiredRefreshToken = ImmutableSet.copyOf(providersWithRequiredRefreshToken);
    }

    public ClientToken forceRefresh(ClientConfiguration config, ClientToken token) throws TokenServiceException {
        Preconditions.checkArgument((token.getRefreshToken() != null ? 1 : 0) != 0);
        return this.getToken(ClientTokenImpl.builder(token), config, this.getRefreshRequest(config, token));
    }

    public ClientToken refreshIfNeeded(ClientConfiguration config, ClientToken token, Duration margin) throws TokenServiceException {
        if (this.isRefreshNeeded(token, margin)) {
            return this.forceRefresh(config, token);
        }
        return token;
    }

    public boolean isRefreshNeeded(ClientToken token, Duration margin) {
        try {
            if (token.getRefreshToken() == null) {
                return false;
            }
            Instant nowWithMargin = this.clock.instant().plus(SystemProperty.MAX_CLOCK_SKEW.getValue()).plus(margin);
            return token.getAccessTokenExpiration().isBefore(nowWithMargin);
        }
        catch (ArithmeticException | DateTimeException e) {
            logger.debug("Margin exceeds limits of Instant. Refresh required", (Throwable)e);
            return true;
        }
    }

    @Override
    public ClientToken getAccessTokenFromAuthorizationCode(ClientConfiguration config, String authorizationCode) throws TokenServiceException {
        ClientToken token = this.getToken(ClientTokenImpl.builder(), config, this.getAuthorizationCodeExchangeRequest(config, authorizationCode));
        this.validateRefreshTokenPresenceForNewToken(config, token);
        return token;
    }

    @VisibleForTesting
    protected void validateRefreshTokenPresenceForNewToken(ClientConfiguration config, ClientToken token) {
        if (token.getRefreshToken() == null) {
            Preconditions.checkArgument((!this.providersWithRequiredRefreshToken.contains(config.getProviderType()) ? 1 : 0) != 0, (Object)("Refresh token is missing but required for the provider: " + config.getProviderType()));
            Instant minimumExpiry = this.clock.instant().plus(this.minimumAccessTokenLifetimeWhenNoRefreshToken);
            Preconditions.checkArgument((!token.getAccessTokenExpiration().isBefore(minimumExpiry) ? 1 : 0) != 0, (Object)("Refresh token is not present and access token lifetime is too short " + token.getAccessTokenExpiration()));
        }
    }

    @VisibleForTesting
    protected HTTPRequest getRefreshRequest(ClientConfiguration config, ClientToken token) {
        return this.getTokenRequestInternal(config, new RefreshTokenGrant(new RefreshToken(token.getRefreshToken())));
    }

    @VisibleForTesting
    protected HTTPRequest getAuthorizationCodeExchangeRequest(ClientConfiguration config, String authorizationCode) {
        return this.getTokenRequestInternal(config, new AuthorizationCodeGrant(new AuthorizationCode(authorizationCode), this.authorizationCodeFlowUrlsProvider.getRedirectUri(config)));
    }

    @VisibleForTesting
    protected ClientToken getToken(ClientTokenImpl.Builder builder, ClientConfiguration config, HTTPRequest request) throws TokenServiceException {
        TokenResponse response;
        Instant now = this.clock.instant();
        try {
            HTTPResponse httpResponse = request.send();
            logger.trace("Got OAuth Provider response: [{}], [{}]", (Object)httpResponse.getStatusCode(), (Object)httpResponse.getContent());
            response = TokenResponse.parse(httpResponse);
        }
        catch (Exception e) {
            throw new TokenServiceException((Throwable)e);
        }
        if (!response.indicatesSuccess()) {
            throw new TokenServiceException(response.toErrorResponse().getErrorObject().getDescription());
        }
        return this.buildToken(builder, config, response.toSuccessResponse().getTokens(), now);
    }

    private HTTPRequest getTokenRequestInternal(ClientConfiguration config, AuthorizationGrant grant) {
        logger.debug("Trying to get tokens for an integration with a client id - {}", (Object)config.getClientId());
        ClientSecretPost clientAuth = new ClientSecretPost(new ClientID(config.getClientId()), new Secret(config.getClientSecret()));
        URI tokenEndpoint = URI.create(config.getTokenEndpoint());
        HashMap<String, List<String>> customParams = new HashMap<String, List<String>>();
        Scope scopes = Scope.parse(config.getScopes());
        if (config.getProviderType() == ProviderType.MICROSOFT) {
            scopes.add("offline_access");
            String redirectUri = this.authorizationCodeFlowUrlsProvider.getRedirectUri(config).toString();
            customParams.put("redirect_uri", (List<String>)ImmutableList.of((Object)redirectUri));
        }
        HTTPRequest request = new TokenRequest(tokenEndpoint, clientAuth, grant, scopes, null, customParams).toHTTPRequest();
        request.setAccept("application/json");
        request.setHeader("x-atlassian-token", new String[]{"no-check"});
        if (SystemProperty.ADD_EMPTY_USER_AGENT_FOR_TOKEN_REQUESTS.getValue().booleanValue()) {
            request.setHeader("User-Agent", new String[]{""});
        }
        request.setConnectTimeout((int)this.requestConfigurationTimeout.toMillis());
        return request;
    }

    @VisibleForTesting
    ClientToken buildToken(ClientTokenImpl.Builder builder, ClientConfiguration config, Tokens tokens, Instant now) {
        if (tokens.getRefreshToken() != null) {
            builder.refreshToken(tokens.getRefreshToken().getValue());
        }
        long lifetimeSeconds = tokens.getAccessToken().getLifetime();
        return builder.accessToken(tokens.getAccessToken().getValue()).accessTokenExpiration(lifetimeSeconds == 0L ? ClientTokenEntity.MAX_TIMESTAMP : now.plusSeconds(lifetimeSeconds)).refreshTokenExpiration(this.refreshTokenExpirationHandler.getExpirationTimeForToken(config, now, tokens.getRefreshToken())).build();
    }
}

