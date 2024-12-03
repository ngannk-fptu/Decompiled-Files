/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.oauth2.provider.api.authorization.AuthorizationService
 *  com.atlassian.oauth2.provider.api.client.ClientService
 *  com.atlassian.oauth2.provider.api.pkce.PkceService
 *  com.atlassian.oauth2.provider.api.token.TokenService
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.rest.service.grant;

import com.atlassian.beehive.ClusterLockService;
import com.atlassian.oauth2.provider.api.authorization.AuthorizationService;
import com.atlassian.oauth2.provider.api.client.ClientService;
import com.atlassian.oauth2.provider.api.pkce.PkceService;
import com.atlassian.oauth2.provider.api.token.TokenService;
import com.atlassian.oauth2.provider.core.token.JwtService;
import com.atlassian.oauth2.provider.rest.exception.InvalidRequestException;
import com.atlassian.oauth2.provider.rest.exception.UnsupportedGrantTypeException;
import com.atlassian.oauth2.provider.rest.service.grant.AuthorizationCodeGrantProcessor;
import com.atlassian.oauth2.provider.rest.service.grant.GrantProcessor;
import com.atlassian.oauth2.provider.rest.service.grant.RefreshTokenGrantProcessor;
import com.atlassian.oauth2.provider.rest.validation.grant.AuthorizationCodeGrantValidator;
import com.atlassian.oauth2.provider.rest.validation.grant.RefreshGrantValidator;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.Serializable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrantProcessorFactory {
    private static final Logger logger = LoggerFactory.getLogger(GrantProcessorFactory.class);
    private final AuthorizationService authorizationService;
    private final TokenService tokenService;
    private final ClientService clientService;
    private final I18nResolver i18nResolver;
    private final ClusterLockService clusterLockService;
    private final JwtService jwtService;
    private final PkceService pkceService;

    public GrantProcessorFactory(AuthorizationService authorizationService, TokenService tokenService, ClientService clientService, I18nResolver i18nResolver, ClusterLockService clusterLockService, JwtService jwtService, PkceService pkceService) {
        this.authorizationService = authorizationService;
        this.tokenService = tokenService;
        this.clientService = clientService;
        this.i18nResolver = i18nResolver;
        this.clusterLockService = clusterLockService;
        this.jwtService = jwtService;
        this.pkceService = pkceService;
    }

    public GrantProcessor createGrantProcessor(String grantType) {
        if (StringUtils.isBlank((CharSequence)grantType)) {
            throw new InvalidRequestException(this.i18nResolver.getText("oauth2.rest.error.missing.required.parameter", new Serializable[]{"grant_type"}));
        }
        if (grantType.equals("refresh_token")) {
            logger.debug("Valid grant type (refresh_token), processing refresh grant");
            return new RefreshTokenGrantProcessor(this.tokenService, this.i18nResolver, RefreshGrantValidator.get(this.i18nResolver, this.clientService, this.tokenService, this.jwtService), this.clusterLockService, this.jwtService);
        }
        if (grantType.equals("authorization_code")) {
            logger.debug("Valid grant type (authorization_code), processing authorization code grant");
            return new AuthorizationCodeGrantProcessor(this.authorizationService, this.i18nResolver, this.tokenService, AuthorizationCodeGrantValidator.get(this.i18nResolver, this.clientService, this.authorizationService, this.tokenService, this.clusterLockService, this.pkceService), this.jwtService);
        }
        throw new UnsupportedGrantTypeException(this.i18nResolver.getText("oauth2.rest.error.unsupported.grant.type"));
    }
}

