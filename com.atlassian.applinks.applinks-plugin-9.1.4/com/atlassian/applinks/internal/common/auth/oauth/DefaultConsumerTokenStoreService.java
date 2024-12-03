/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider
 *  com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager
 *  com.atlassian.oauth.consumer.ConsumerService
 *  com.atlassian.oauth.consumer.ConsumerToken
 *  com.atlassian.oauth.consumer.ConsumerToken$ConsumerTokenBuilder
 *  com.atlassian.oauth.consumer.ConsumerTokenStore
 *  com.atlassian.oauth.consumer.ConsumerTokenStore$Key
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.applinks.internal.common.auth.oauth;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.auth.types.OAuthAuthenticationProvider;
import com.atlassian.applinks.internal.common.auth.oauth.ApplinksOAuth;
import com.atlassian.applinks.internal.common.auth.oauth.ConsumerTokenStoreService;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationManager;
import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.oauth.consumer.ConsumerToken;
import com.atlassian.oauth.consumer.ConsumerTokenStore;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultConsumerTokenStoreService
implements ConsumerTokenStoreService {
    private final ConsumerTokenStore consumerTokenStore;
    private final AuthenticationConfigurationManager configurationManager;
    private final ConsumerService consumerService;
    private static final String APPLINKS_APPLICATION_LINK_ID = "applinks.oauth.applicationLinkId";

    @Autowired
    public DefaultConsumerTokenStoreService(ConsumerTokenStore consumerTokenStore, AuthenticationConfigurationManager configurationManager, ConsumerService consumerService) {
        this.consumerTokenStore = consumerTokenStore;
        this.configurationManager = configurationManager;
        this.consumerService = consumerService;
    }

    @Override
    public void addConsumerToken(ApplicationLink applicationLink, String username, ConsumerToken consumerToken) {
        Objects.requireNonNull(applicationLink, "applicationLink");
        Objects.requireNonNull(username, "username");
        Objects.requireNonNull(consumerToken, "consumerToken");
        this.verifyOAuthOutgoingEnabled(applicationLink.getId());
        HashMap<String, String> tokenProperties = new HashMap<String, String>();
        tokenProperties.put(APPLINKS_APPLICATION_LINK_ID, applicationLink.getId().get());
        ConsumerTokenStore.Key key = DefaultConsumerTokenStoreService.makeOAuthApplinksConsumerKey(username, applicationLink.getId().get());
        ConsumerToken.ConsumerTokenBuilder tokenBuilder = consumerToken.isAccessToken() ? ConsumerToken.newAccessToken((String)consumerToken.getToken()) : ConsumerToken.newRequestToken((String)consumerToken.getToken());
        ((ConsumerToken.ConsumerTokenBuilder)((ConsumerToken.ConsumerTokenBuilder)tokenBuilder.tokenSecret(consumerToken.getTokenSecret())).consumer(consumerToken.getConsumer())).properties(tokenProperties);
        this.consumerTokenStore.put(key, tokenBuilder.build());
    }

    @Override
    public void removeAllConsumerTokens(ApplicationLink applicationLink) {
        Objects.requireNonNull(applicationLink, "applicationLink");
        Map configuration = this.configurationManager.getConfiguration(applicationLink.getId(), OAuthAuthenticationProvider.class);
        this.verifyOAuthOutgoingEnabled(applicationLink.getId());
        if (configuration.containsKey(ApplinksOAuth.AUTH_CONFIG_CONSUMER_KEY_OUTBOUND)) {
            String consumerKey = (String)configuration.get(ApplinksOAuth.AUTH_CONFIG_CONSUMER_KEY_OUTBOUND);
            this.consumerTokenStore.removeTokensForConsumer(consumerKey);
        } else {
            String consumerKey = this.consumerService.getConsumer().getKey();
            Map consumerTokens = this.consumerTokenStore.getConsumerTokens(consumerKey);
            for (ConsumerTokenStore.Key key : consumerTokens.keySet()) {
                Map tokenProperties = ((ConsumerToken)consumerTokens.get(key)).getProperties();
                if (!tokenProperties.containsKey(APPLINKS_APPLICATION_LINK_ID) || !((String)tokenProperties.get(APPLINKS_APPLICATION_LINK_ID)).equals(applicationLink.getId().get())) continue;
                this.consumerTokenStore.remove(key);
            }
        }
    }

    @Override
    public boolean removeConsumerToken(ApplicationId applicationId, String username) {
        Objects.requireNonNull(applicationId, "applicationLink");
        this.verifyOAuthOutgoingEnabled(applicationId);
        ConsumerTokenStore.Key key = DefaultConsumerTokenStoreService.makeOAuthApplinksConsumerKey(username, applicationId.get());
        if (this.consumerTokenStore.get(key) != null) {
            this.consumerTokenStore.remove(key);
            return true;
        }
        return false;
    }

    @Override
    public ConsumerToken getConsumerToken(ApplicationLink applicationLink, String username) {
        Objects.requireNonNull(username, "Username cannot be null!");
        Objects.requireNonNull(applicationLink, "Application Link cannot be null!");
        this.verifyOAuthOutgoingEnabled(applicationLink.getId());
        return this.consumerTokenStore.get(DefaultConsumerTokenStoreService.makeOAuthApplinksConsumerKey(username, applicationLink.getId().get()));
    }

    public static ConsumerTokenStore.Key makeOAuthApplinksConsumerKey(String username, String applicationLinkId) {
        Objects.requireNonNull(username, "Username cannot be null!");
        Objects.requireNonNull(applicationLinkId, "Application Link Id cannot be null!");
        return new ConsumerTokenStore.Key(applicationLinkId + ":" + username);
    }

    @Override
    public boolean isOAuthOutgoingEnabled(ApplicationId applicationId) {
        return this.configurationManager.isConfigured(applicationId, OAuthAuthenticationProvider.class);
    }

    private void verifyOAuthOutgoingEnabled(ApplicationId applicationId) {
        if (!this.isOAuthOutgoingEnabled(applicationId)) {
            throw new IllegalStateException("OAuth not enabled for outgoing authentication!");
        }
    }
}

