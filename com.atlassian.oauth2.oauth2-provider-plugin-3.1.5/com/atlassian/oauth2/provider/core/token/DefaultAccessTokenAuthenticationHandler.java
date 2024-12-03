/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.provider.api.token.AccessTokenAuthenticationHandler
 *  com.atlassian.oauth2.provider.api.token.AuthenticationResult
 *  com.atlassian.oauth2.provider.api.token.TokenService
 *  com.atlassian.oauth2.provider.api.token.access.AccessToken
 *  com.atlassian.oauth2.provider.api.token.exception.access.ExpiredTokenException
 *  com.atlassian.oauth2.provider.api.token.exception.access.UnrecognisedTokenException
 *  com.atlassian.oauth2.provider.api.token.exception.access.UnrecognisedUserKeyException
 *  com.atlassian.oauth2.scopes.api.Scope
 *  com.atlassian.sal.api.auth.AuthenticationListener
 *  com.atlassian.sal.api.auth.Authenticator$Result
 *  com.atlassian.sal.api.auth.Authenticator$Result$Success
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.user.UserKey
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.oauth2.provider.core.token;

import com.atlassian.oauth2.provider.api.token.AccessTokenAuthenticationHandler;
import com.atlassian.oauth2.provider.api.token.AuthenticationResult;
import com.atlassian.oauth2.provider.api.token.TokenService;
import com.atlassian.oauth2.provider.api.token.access.AccessToken;
import com.atlassian.oauth2.provider.api.token.exception.access.ExpiredTokenException;
import com.atlassian.oauth2.provider.api.token.exception.access.UnrecognisedTokenException;
import com.atlassian.oauth2.provider.api.token.exception.access.UnrecognisedUserKeyException;
import com.atlassian.oauth2.provider.core.properties.SystemProperty;
import com.atlassian.oauth2.provider.core.token.JwtService;
import com.atlassian.oauth2.provider.core.user.ProductUserProvider;
import com.atlassian.oauth2.scopes.api.Scope;
import com.atlassian.sal.api.auth.AuthenticationListener;
import com.atlassian.sal.api.auth.Authenticator;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.user.UserKey;
import java.io.Serializable;
import java.security.Principal;
import java.time.Clock;
import java.time.Instant;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultAccessTokenAuthenticationHandler
implements AccessTokenAuthenticationHandler {
    private final TokenService tokenService;
    private final AuthenticationListener authenticationListener;
    private final I18nResolver i18nResolver;
    private final ProductUserProvider productUserProvider;
    private final Clock clock;
    private final JwtService jwtService;

    public DefaultAccessTokenAuthenticationHandler(TokenService tokenService, Clock clock, AuthenticationListener authenticationListener, I18nResolver i18nResolver, ProductUserProvider productUserProvider, JwtService jwtService) {
        this.tokenService = tokenService;
        this.authenticationListener = authenticationListener;
        this.i18nResolver = i18nResolver;
        this.productUserProvider = productUserProvider;
        this.clock = clock;
        this.jwtService = jwtService;
    }

    public AuthenticationResult authenticate(HttpServletRequest request, HttpServletResponse response, String bearerToken) {
        AccessToken accessToken = this.lookupAccessToken(bearerToken);
        this.validateAccessToken(accessToken);
        this.attemptAuthentication(request, response, bearerToken, accessToken);
        return AuthenticationResult.authenticated((Scope)accessToken.getScope(), (String)accessToken.getClientId());
    }

    private AccessToken lookupAccessToken(String bearerToken) {
        return (AccessToken)this.tokenService.findByAccessTokenId(this.jwtService.extractTokenId(bearerToken)).orElseThrow(() -> new UnrecognisedTokenException(this.i18nResolver.getText("oauth2.authentication.token.not.recognized")));
    }

    private void validateAccessToken(AccessToken accessToken) {
        if (this.hasExpired(accessToken)) {
            throw new ExpiredTokenException(this.i18nResolver.getText("oauth2.authentication.token.has.expired"));
        }
    }

    private boolean hasExpired(AccessToken token) {
        Instant tokenCreatedAt = Instant.ofEpochMilli(token.getCreatedAt());
        Instant tokenExpiryTime = tokenCreatedAt.plus(SystemProperty.MAX_ACCESS_TOKEN_LIFETIME.getValue());
        Instant now = Instant.ofEpochMilli(this.clock.millis());
        return now.isAfter(tokenExpiryTime);
    }

    private void attemptAuthentication(HttpServletRequest request, HttpServletResponse response, String bearerToken, AccessToken accessToken) {
        Principal principal = this.productUserProvider.getActiveUserByKey(new UserKey(accessToken.getUserKey())).orElseThrow(() -> new UnrecognisedUserKeyException(this.i18nResolver.getText("oauth2.authentication.token.user.not.recognized")));
        Message successMessage = this.i18nResolver.createMessage("oauth2.authentication.success", new Serializable[0]);
        this.authenticationListener.authenticationSuccess((Authenticator.Result)new Authenticator.Result.Success(successMessage, principal), request, response);
        this.tokenService.updateAccessTokenLastAccessed(bearerToken);
    }
}

