/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.provider.api.token.TokenService
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.rest.service;

import com.atlassian.oauth2.provider.api.token.TokenService;
import com.atlassian.oauth2.provider.core.event.OAuth2ProviderEventPublisher;
import com.atlassian.oauth2.provider.core.token.JwtService;
import com.atlassian.oauth2.provider.rest.exception.InvalidClientException;
import com.atlassian.oauth2.provider.rest.exception.InvalidRequestException;
import com.atlassian.oauth2.provider.rest.exception.UnsupportedTokenTypeException;
import com.atlassian.oauth2.provider.rest.model.RevokeRequestFormParams;
import com.atlassian.oauth2.provider.rest.validation.RevokeTokenValidator;
import com.atlassian.sal.api.message.I18nResolver;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RevokeTokenService {
    private static final Logger logger = LoggerFactory.getLogger(RevokeTokenService.class);
    private final TokenService tokenService;
    private final RevokeTokenValidator revokeTokenValidator;
    private final JwtService jwtService;
    private final I18nResolver i18nResolver;

    public RevokeTokenService(TokenService tokenService, RevokeTokenValidator revokeTokenValidator, JwtService jwtService, I18nResolver i18nResolver, OAuth2ProviderEventPublisher oAuth2ProviderEventPublisher) {
        this.tokenService = tokenService;
        this.revokeTokenValidator = revokeTokenValidator;
        this.jwtService = jwtService;
        this.i18nResolver = i18nResolver;
    }

    public void revokeToken(RevokeRequestFormParams formParams) throws InvalidClientException, InvalidRequestException, UnsupportedTokenTypeException {
        if (this.revokeTokenValidator.isRevokeRequestValid(formParams)) {
            logger.debug("Revoke request valid, proceeding to revoke token");
            String tokenId = this.jwtService.extractTokenId(formParams.getToken());
            if ("access_token".equals(formParams.getTokenTypeHint())) {
                logger.debug("Token type hint provided (access_token). Revoking token using access token id");
                this.revokeUsingAccessToken(tokenId);
            } else if ("refresh_token".equals(formParams.getTokenTypeHint())) {
                logger.debug("Token type hint provided (refresh_token). Revoking token using access token id");
                this.revokeUsingRefreshToken(tokenId);
            } else if (StringUtils.isBlank((CharSequence)formParams.getTokenTypeHint())) {
                logger.debug("No token type hint provided");
                this.revokeUsingAccessToken(tokenId);
                this.revokeUsingRefreshToken(tokenId);
            } else {
                throw new UnsupportedTokenTypeException("oauth2.rest.error.unsupported.token.type");
            }
        }
    }

    public void revokeToken(String tokenId) throws InvalidRequestException {
        if (!this.tokenService.removeTokensById(tokenId)) {
            throw new InvalidRequestException(this.i18nResolver.getText("oauth2.rest.error.revoke.token"));
        }
    }

    private void revokeUsingAccessToken(String accessTokenId) {
        this.tokenService.removeAccessTokenById(accessTokenId);
        this.tokenService.removeRefreshTokenAssociatedWith(accessTokenId);
    }

    private void revokeUsingRefreshToken(String refreshTokenId) {
        this.tokenService.removeAccessTokenAssociatedWith(refreshTokenId);
        this.tokenService.removeRefreshToken(refreshTokenId);
    }
}

