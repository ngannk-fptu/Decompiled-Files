/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.api.external;

import com.atlassian.oauth2.provider.api.client.Client;
import com.atlassian.oauth2.provider.api.external.OAuth2AuthorizationServerMetadata;
import com.atlassian.oauth2.provider.api.token.access.AccessToken;
import com.atlassian.oauth2.provider.api.token.refresh.RefreshToken;
import java.util.List;
import java.util.Optional;

public interface OAuth2ProviderService {
    public List<Client> listClients();

    public Optional<Client> findClient(String var1);

    public List<AccessToken> listCurrentUsersAccessTokens();

    public List<RefreshToken> listCurrentUsersRefreshTokens();

    public OAuth2AuthorizationServerMetadata getOAuth2AuthorizationServerMetadata();
}

