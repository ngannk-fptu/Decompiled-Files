/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.provider.api.authorization.Authorization
 *  com.atlassian.oauth2.provider.api.authorization.AuthorizationService
 *  com.atlassian.oauth2.provider.api.authorization.TokenResponseErrorDescription
 *  com.atlassian.oauth2.provider.api.token.TokenService
 *  com.atlassian.oauth2.provider.api.token.access.AccessToken
 *  com.atlassian.oauth2.provider.api.token.refresh.RefreshToken
 *  com.atlassian.sal.api.message.I18nResolver
 */
package com.atlassian.oauth2.provider.rest.service.grant;

import com.atlassian.oauth2.provider.api.authorization.Authorization;
import com.atlassian.oauth2.provider.api.authorization.AuthorizationService;
import com.atlassian.oauth2.provider.api.authorization.TokenResponseErrorDescription;
import com.atlassian.oauth2.provider.api.token.TokenService;
import com.atlassian.oauth2.provider.api.token.access.AccessToken;
import com.atlassian.oauth2.provider.api.token.refresh.RefreshToken;
import com.atlassian.oauth2.provider.core.token.JwtService;
import com.atlassian.oauth2.provider.rest.exception.InvalidRequestException;
import com.atlassian.oauth2.provider.rest.model.RestToken;
import com.atlassian.oauth2.provider.rest.model.TokenRequestFormParams;
import com.atlassian.oauth2.provider.rest.service.TokenRestService;
import com.atlassian.oauth2.provider.rest.service.grant.GrantProcessor;
import com.atlassian.oauth2.provider.rest.validation.grant.GrantValidator;
import com.atlassian.sal.api.message.I18nResolver;

public class AuthorizationCodeGrantProcessor
implements GrantProcessor {
    private final AuthorizationService authorizationService;
    private final I18nResolver i18nResolver;
    private final TokenService tokenService;
    private final GrantValidator grantValidator;
    private final JwtService jwtService;

    public AuthorizationCodeGrantProcessor(AuthorizationService authorizationService, I18nResolver i18nResolver, TokenService tokenService, GrantValidator grantValidator, JwtService jwtService) {
        this.authorizationService = authorizationService;
        this.i18nResolver = i18nResolver;
        this.tokenService = tokenService;
        this.grantValidator = grantValidator;
        this.jwtService = jwtService;
    }

    @Override
    public RestToken execute(TokenRequestFormParams formParams) {
        this.grantValidator.validate(formParams);
        return (RestToken)this.authorizationService.completeAuthorizationFlow(formParams.getClientId(), formParams.getRedirectUri(), formParams.getCode()).fold(authorization -> this.hasAuthorization((Authorization)authorization, formParams), this::missingAuthorization);
    }

    private RestToken hasAuthorization(Authorization authorization, TokenRequestFormParams formParams) {
        AccessToken accessToken = this.tokenService.createAccessToken(formParams.getClientId(), authorization.getUserKey(), formParams.getCode(), authorization.getCreatedAt().longValue(), authorization.getScope());
        RefreshToken refreshToken = this.tokenService.createRefreshToken(formParams.getClientId(), authorization.getUserKey(), authorization.getCreatedAt().longValue(), accessToken.getId(), authorization.getScope(), formParams.getCode(), 0);
        return TokenRestService.tokenEntityToRestTokenWithRefresh(this.jwtService.createToken(accessToken.getId()), this.jwtService.createToken(refreshToken.getId()), accessToken.getScope().toString());
    }

    private RestToken missingAuthorization(TokenResponseErrorDescription tokenResponseErrorDescription) {
        throw new InvalidRequestException(this.i18nResolver.getText("oauth2.rest.error.authorization.code.invalid"));
    }
}

