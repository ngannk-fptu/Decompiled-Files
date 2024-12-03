/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.oauth2.provider.api.authorization.Authorization
 *  com.atlassian.oauth2.provider.api.authorization.AuthorizationService
 *  com.atlassian.oauth2.provider.api.client.ClientService
 *  com.atlassian.oauth2.provider.api.pkce.PkceService
 *  com.atlassian.oauth2.provider.api.token.TokenService
 *  com.atlassian.sal.api.message.I18nResolver
 */
package com.atlassian.oauth2.provider.rest.validation.grant;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.oauth2.provider.api.authorization.Authorization;
import com.atlassian.oauth2.provider.api.authorization.AuthorizationService;
import com.atlassian.oauth2.provider.api.client.ClientService;
import com.atlassian.oauth2.provider.api.pkce.PkceService;
import com.atlassian.oauth2.provider.api.token.TokenService;
import com.atlassian.oauth2.provider.core.properties.SystemProperty;
import com.atlassian.oauth2.provider.rest.exception.InvalidClientException;
import com.atlassian.oauth2.provider.rest.exception.InvalidGrantException;
import com.atlassian.oauth2.provider.rest.exception.InvalidRequestException;
import com.atlassian.oauth2.provider.rest.model.TokenRequestFormParams;
import com.atlassian.oauth2.provider.rest.validation.grant.GrantValidator;
import com.atlassian.sal.api.message.I18nResolver;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class AuthorizationCodeGrantValidator
extends GrantValidator {
    private static final String AUTHORIZATION_CODE_GRANT_LOCK = "com.atlassian.oauth2.provider.authorization.code.grant.lock";
    private final AuthorizationService authorizationService;
    private final TokenService tokenService;
    private final ClusterLockService clusterLockService;
    private final PkceService pkceService;

    public static AuthorizationCodeGrantValidator get(I18nResolver i18nResolver, ClientService clientService, AuthorizationService authorizationService, TokenService tokenService, ClusterLockService clusterLockService, PkceService pkceService) {
        return new AuthorizationCodeGrantValidator(i18nResolver, clientService, tokenService, authorizationService, clusterLockService, pkceService);
    }

    private AuthorizationCodeGrantValidator(I18nResolver i18nResolver, ClientService clientService, TokenService tokenService, AuthorizationService authorizationService, ClusterLockService clusterLockService, PkceService pkceService) {
        super(i18nResolver, clientService);
        this.authorizationService = authorizationService;
        this.tokenService = tokenService;
        this.clusterLockService = clusterLockService;
        this.pkceService = pkceService;
    }

    @Override
    public void validateGrantSpecificConstraints(TokenRequestFormParams formParams) {
        this.validateRequiredParams(formParams.requiredAccessTokenParams());
        this.blockReplayAttack(formParams.getCode());
        Optional authorization = this.authorizationService.getAuthorization(formParams.getCode());
        if (!authorization.isPresent()) {
            throw new InvalidRequestException(this.i18nResolver.getText("oauth2.rest.error.authorization.code.invalid"));
        }
        this.validateRedirectUri((Authorization)authorization.get(), formParams.getClientId(), formParams.getRedirectUri());
        this.validateAuthorizationCode(formParams.getClientId(), (Authorization)authorization.get());
        this.validatePkceCodeVerifier(formParams.getCode(), formParams.getCodeVerifier());
    }

    private void blockReplayAttack(String authorizationCode) throws InvalidGrantException, InterruptedException {
        block5: {
            ClusterLock lock = this.clusterLockService.getLockForName(AUTHORIZATION_CODE_GRANT_LOCK);
            if (lock.tryLock(SystemProperty.GLOBAL_CLUSTER_LOCK_TIMEOUT_SECONDS.getValue().intValue(), TimeUnit.SECONDS)) {
                try {
                    if (this.isReplayAttack(authorizationCode)) {
                        this.tokenService.removeTokensByAuthorizationCode(authorizationCode);
                        throw new InvalidGrantException(this.i18nResolver.getText("oauth2.rest.error.multiple.create.attempts.error"));
                    }
                    break block5;
                }
                finally {
                    lock.unlock();
                }
            }
            throw new IllegalMonitorStateException(this.i18nResolver.getText("oauth2.rest.error.multiple.create.attempts.error"));
        }
    }

    private boolean isReplayAttack(String authorizationCode) {
        return this.tokenService.isCodeRedeemed(authorizationCode);
    }

    private void validateRedirectUri(Authorization authorization, String clientId, String redirectUri) throws InvalidGrantException, InvalidClientException {
        if (!authorization.getRedirectUri().equals(redirectUri)) {
            throw new InvalidGrantException(this.i18nResolver.getText("oauth2.rest.error.authorization.redirect.invalid"));
        }
        if (!this.uriMatchClientId(clientId, redirectUri)) {
            throw new InvalidClientException(this.i18nResolver.getText("oauth2.rest.error.unauthenticated.client"));
        }
    }

    private boolean uriMatchClientId(String clientId, String redirectUri) {
        List redirectUris = this.clientService.findRedirectUrisByClientId(clientId);
        return redirectUris.contains(redirectUri);
    }

    private void validateAuthorizationCode(String clientId, Authorization authorization) throws InvalidGrantException {
        if (!clientId.equals(authorization.getClientId())) {
            throw new InvalidGrantException(this.i18nResolver.getText("oauth2.rest.error.unauthenticated.client"));
        }
    }

    private void validatePkceCodeVerifier(String authorizationCode, String codeVerifier) throws InvalidGrantException {
        if (!this.authorizationService.isPkceEnabledForAuthorization(authorizationCode)) {
            return;
        }
        if (codeVerifier == null) {
            throw new InvalidGrantException(this.i18nResolver.getText("oauth2.token.pkce.code.verifier.missing"));
        }
        if (!this.pkceService.isValidCode(codeVerifier)) {
            throw new InvalidGrantException(this.i18nResolver.getText("oauth2.token.pkce.code.verifier.invalid"));
        }
        if (!this.authorizationService.isPkceCodeVerifierValidAgainstAuthorization(codeVerifier, authorizationCode)) {
            throw new InvalidGrantException(this.i18nResolver.getText("oauth2.token.pkce.code.verifier.mismatch"));
        }
    }
}

