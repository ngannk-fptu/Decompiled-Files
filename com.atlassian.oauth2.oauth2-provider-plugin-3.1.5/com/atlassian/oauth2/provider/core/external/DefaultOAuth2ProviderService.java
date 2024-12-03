/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.provider.api.client.Client
 *  com.atlassian.oauth2.provider.api.client.ClientService
 *  com.atlassian.oauth2.provider.api.external.OAuth2AuthorizationServerMetadata
 *  com.atlassian.oauth2.provider.api.external.OAuth2ProviderService
 *  com.atlassian.oauth2.provider.api.token.TokenService
 *  com.atlassian.oauth2.provider.api.token.access.AccessToken
 *  com.atlassian.oauth2.provider.api.token.refresh.RefreshToken
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.core.external;

import com.atlassian.oauth2.provider.api.client.Client;
import com.atlassian.oauth2.provider.api.client.ClientService;
import com.atlassian.oauth2.provider.api.external.OAuth2AuthorizationServerMetadata;
import com.atlassian.oauth2.provider.api.external.OAuth2ProviderService;
import com.atlassian.oauth2.provider.api.token.TokenService;
import com.atlassian.oauth2.provider.api.token.access.AccessToken;
import com.atlassian.oauth2.provider.api.token.refresh.RefreshToken;
import com.atlassian.oauth2.provider.core.external.OAuth2AuthorizationServerMetadataFactory;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultOAuth2ProviderService
implements OAuth2ProviderService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultOAuth2ProviderService.class);
    private final ClientService clientService;
    private final TokenService tokenService;
    private final UserManager userManager;
    private final OAuth2AuthorizationServerMetadataFactory oAuth2AuthorizationServerMetadataFactory;

    public DefaultOAuth2ProviderService(ClientService clientService, TokenService tokenService, UserManager userManager, OAuth2AuthorizationServerMetadataFactory oAuth2AuthorizationServerMetadataFactory) {
        this.clientService = clientService;
        this.tokenService = tokenService;
        this.userManager = userManager;
        this.oAuth2AuthorizationServerMetadataFactory = oAuth2AuthorizationServerMetadataFactory;
    }

    public List<Client> listClients() {
        logger.debug("Retrieving all clients.");
        return this.clientService.list();
    }

    public Optional<Client> findClient(String clientId) {
        logger.debug("Retrieving client associated with client id [{}].", (Object)clientId);
        return this.clientService.getByClientId(clientId);
    }

    public List<AccessToken> listCurrentUsersAccessTokens() {
        UserKey currentUserKey = this.userManager.getRemoteUserKey();
        if (currentUserKey == null) {
            logger.debug("User key is null. There are no access tokens to retrieve.");
            return Collections.emptyList();
        }
        logger.debug("Retrieving access tokens associated with user key {}", (Object)currentUserKey);
        return this.tokenService.findAccessTokensByUserKey(currentUserKey);
    }

    public List<RefreshToken> listCurrentUsersRefreshTokens() {
        UserKey currentUserKey = this.userManager.getRemoteUserKey();
        if (currentUserKey == null) {
            logger.debug("User key is null. There are no refresh tokens to retrieve.");
            return Collections.emptyList();
        }
        logger.debug("Retrieving refresh tokens associated with user key [{}].", (Object)currentUserKey);
        return this.tokenService.findRefreshTokensByUserKey(currentUserKey);
    }

    public OAuth2AuthorizationServerMetadata getOAuth2AuthorizationServerMetadata() {
        return this.oAuth2AuthorizationServerMetadataFactory.create();
    }
}

