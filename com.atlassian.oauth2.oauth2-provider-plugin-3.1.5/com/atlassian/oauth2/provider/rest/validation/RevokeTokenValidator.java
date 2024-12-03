/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.provider.api.client.ClientService
 *  com.atlassian.oauth2.provider.api.token.TokenService
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.oauth2.provider.rest.validation;

import com.atlassian.oauth2.provider.api.client.ClientService;
import com.atlassian.oauth2.provider.api.token.TokenService;
import com.atlassian.oauth2.provider.core.token.JwtService;
import com.atlassian.oauth2.provider.rest.exception.InvalidClientException;
import com.atlassian.oauth2.provider.rest.exception.InvalidRequestException;
import com.atlassian.oauth2.provider.rest.model.RevokeRequestFormParams;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.Serializable;
import org.apache.commons.lang3.StringUtils;

public class RevokeTokenValidator {
    private final TokenService tokenService;
    private final ClientService clientService;
    private final I18nResolver i18nResolver;
    private final JwtService jwtService;

    public RevokeTokenValidator(TokenService tokenService, ClientService clientService, I18nResolver i18nResolver, JwtService jwtService) {
        this.tokenService = tokenService;
        this.clientService = clientService;
        this.i18nResolver = i18nResolver;
        this.jwtService = jwtService;
    }

    private void validateClient(String clientId, String clientSecret) throws InvalidClientException, InvalidRequestException {
        if (StringUtils.isBlank((CharSequence)clientId)) {
            throw new InvalidRequestException(this.i18nResolver.getText("oauth2.rest.error.missing.required.parameter", new Serializable[]{"client_id"}));
        }
        if (StringUtils.isBlank((CharSequence)clientSecret)) {
            throw new InvalidRequestException(this.i18nResolver.getText("oauth2.rest.error.missing.required.parameter", new Serializable[]{"client_secret"}));
        }
        if (!this.clientService.isClientSecretValid(clientId, clientSecret)) {
            throw new InvalidClientException(this.i18nResolver.getText("oauth2.rest.error.unauthenticated.client"));
        }
    }

    public boolean isRevokeRequestValid(RevokeRequestFormParams formParams) throws InvalidClientException, InvalidRequestException {
        this.validateClient(formParams.getClientId(), formParams.getClientSecret());
        return this.isTokenValid(formParams.getClientId(), formParams.getToken());
    }

    private boolean isTokenValid(String clientId, String tokenId) throws InvalidRequestException {
        if (StringUtils.isBlank((CharSequence)tokenId)) {
            throw new InvalidRequestException(this.i18nResolver.getText("oauth2.rest.error.missing.required.parameter", new Serializable[]{"token"}));
        }
        try {
            String extractedTokenId = this.jwtService.extractTokenId(tokenId);
            return this.tokenService.isRefreshTokenValid(clientId, extractedTokenId) || this.tokenService.isAccessTokenValid(clientId, extractedTokenId);
        }
        catch (Exception exception) {
            return false;
        }
    }
}

