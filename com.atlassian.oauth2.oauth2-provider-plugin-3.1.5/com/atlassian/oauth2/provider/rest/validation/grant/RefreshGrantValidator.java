/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.provider.api.client.ClientService
 *  com.atlassian.oauth2.provider.api.token.TokenService
 *  com.atlassian.oauth2.provider.api.token.refresh.RefreshToken
 *  com.atlassian.sal.api.message.I18nResolver
 */
package com.atlassian.oauth2.provider.rest.validation.grant;

import com.atlassian.oauth2.provider.api.client.ClientService;
import com.atlassian.oauth2.provider.api.token.TokenService;
import com.atlassian.oauth2.provider.api.token.refresh.RefreshToken;
import com.atlassian.oauth2.provider.core.properties.SystemProperty;
import com.atlassian.oauth2.provider.core.token.JwtService;
import com.atlassian.oauth2.provider.rest.exception.InvalidGrantException;
import com.atlassian.oauth2.provider.rest.model.TokenRequestFormParams;
import com.atlassian.oauth2.provider.rest.validation.grant.GrantValidator;
import com.atlassian.sal.api.message.I18nResolver;
import java.time.Instant;
import java.util.Optional;

public class RefreshGrantValidator
extends GrantValidator {
    private static final String I18N_INVALID_REFRESH_TOKEN = "oauth2.rest.error.invalid.refresh.token";
    private final TokenService tokenService;
    private final JwtService jwtService;

    public static RefreshGrantValidator get(I18nResolver i18nResolver, ClientService clientService, TokenService tokenService, JwtService jwtService) {
        return new RefreshGrantValidator(i18nResolver, clientService, tokenService, jwtService);
    }

    private RefreshGrantValidator(I18nResolver i18nResolver, ClientService clientService, TokenService tokenService, JwtService jwtService) {
        super(i18nResolver, clientService);
        this.tokenService = tokenService;
        this.jwtService = jwtService;
    }

    @Override
    public void validateGrantSpecificConstraints(TokenRequestFormParams formParams) {
        this.validateRequiredParams(formParams.requiredRefreshTokenParams());
        Optional refreshToken = this.tokenService.findByRefreshTokenId(this.jwtService.extractTokenId(formParams.getRefreshToken()));
        if (!refreshToken.isPresent()) {
            throw new InvalidGrantException(this.i18nResolver.getText(I18N_INVALID_REFRESH_TOKEN));
        }
        this.validateRefreshToken(formParams.getClientId(), (RefreshToken)refreshToken.get());
    }

    private void validateRefreshToken(String clientId, RefreshToken refreshToken) throws InvalidGrantException {
        if (!refreshToken.getClientId().equals(clientId)) {
            throw new InvalidGrantException(this.i18nResolver.getText(I18N_INVALID_REFRESH_TOKEN));
        }
        this.validateRefreshTokenExpiration(refreshToken);
    }

    private void validateRefreshTokenExpiration(RefreshToken refreshToken) throws InvalidGrantException {
        Instant expirationDate = Instant.ofEpochMilli(refreshToken.getCreatedAt()).plus(SystemProperty.MAX_REFRESH_TOKEN_LIFETIME.getValue());
        if (Instant.now().isAfter(expirationDate)) {
            throw new InvalidGrantException(this.i18nResolver.getText(I18N_INVALID_REFRESH_TOKEN));
        }
    }
}

