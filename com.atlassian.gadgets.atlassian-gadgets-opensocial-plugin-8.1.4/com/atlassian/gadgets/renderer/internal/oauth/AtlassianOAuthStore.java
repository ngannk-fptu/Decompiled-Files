/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.util.Uri
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.oauth.bridge.Consumers
 *  com.atlassian.oauth.consumer.ConsumerService
 *  com.atlassian.oauth.consumer.ConsumerToken
 *  com.atlassian.oauth.consumer.ConsumerToken$ConsumerTokenBuilder
 *  com.atlassian.oauth.consumer.ConsumerTokenStore
 *  com.atlassian.oauth.consumer.ConsumerTokenStore$Key
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.inject.Inject
 *  net.oauth.OAuthConsumer
 *  net.oauth.OAuthServiceProvider
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.apache.shindig.auth.SecurityToken
 *  org.apache.shindig.gadgets.GadgetException
 *  org.apache.shindig.gadgets.GadgetException$Code
 *  org.apache.shindig.gadgets.oauth.OAuthStore
 *  org.apache.shindig.gadgets.oauth.OAuthStore$ConsumerInfo
 *  org.apache.shindig.gadgets.oauth.OAuthStore$TokenInfo
 */
package com.atlassian.gadgets.renderer.internal.oauth;

import com.atlassian.gadgets.util.Uri;
import com.atlassian.oauth.Consumer;
import com.atlassian.oauth.bridge.Consumers;
import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.oauth.consumer.ConsumerToken;
import com.atlassian.oauth.consumer.ConsumerTokenStore;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import java.util.Map;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthServiceProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.gadgets.GadgetException;
import org.apache.shindig.gadgets.oauth.OAuthStore;

public class AtlassianOAuthStore
implements OAuthStore {
    private static final String OAUTH_CALLBACK_SERVLET_PATH = "plugins/servlet/gadgets/oauth-callback";
    private final ConsumerService consumerService;
    private final ConsumerTokenStore tokenStore;
    private final ApplicationProperties applicationProperties;

    @Inject
    public AtlassianOAuthStore(@ComponentImport ConsumerService consumerService, @ComponentImport ConsumerTokenStore tokenStore, @ComponentImport ApplicationProperties applicationProperties) {
        this.consumerService = (ConsumerService)Preconditions.checkNotNull((Object)consumerService, (Object)"consumerService");
        this.tokenStore = (ConsumerTokenStore)Preconditions.checkNotNull((Object)tokenStore, (Object)"tokenStore");
        this.applicationProperties = (ApplicationProperties)Preconditions.checkNotNull((Object)applicationProperties, (Object)"applicationProperties");
    }

    public OAuthStore.ConsumerInfo getConsumerKeyAndSecret(SecurityToken securityToken, String service, OAuthServiceProvider provider) throws GadgetException {
        return new OAuthStore.ConsumerInfo(Consumers.asOAuthConsumer((Consumer)this.findConsumerForService(service), (OAuthServiceProvider)provider), null, this.getOAuthCallbackUrl());
    }

    public OAuthStore.TokenInfo getTokenInfo(SecurityToken securityToken, OAuthStore.ConsumerInfo consumerInfo, String serviceName, String tokenName) throws GadgetException {
        ConsumerTokenStore.Key key = this.createKey(securityToken, serviceName, tokenName);
        ConsumerToken token = this.tokenStore.get(key);
        if (token == null) {
            return null;
        }
        return new OAuthStore.TokenInfo(token.getToken(), token.getTokenSecret(), token.getProperty("org.apache.shindig.oauth.sessionHandle"), NumberUtils.toLong((String)token.getProperty("org.apache.shindig.oauth.tokenExpireMillis")));
    }

    public void setTokenInfo(SecurityToken securityToken, OAuthStore.ConsumerInfo consumerInfo, String serviceName, String tokenName, OAuthStore.TokenInfo tokenInfo) throws GadgetException {
        ConsumerToken token;
        ConsumerTokenStore.Key key = this.createKey(securityToken, serviceName, tokenName);
        ConsumerToken savedToken = this.tokenStore.put(key, token = ((ConsumerToken.ConsumerTokenBuilder)((ConsumerToken.ConsumerTokenBuilder)((ConsumerToken.ConsumerTokenBuilder)ConsumerToken.newAccessToken((String)tokenInfo.getAccessToken()).tokenSecret(tokenInfo.getTokenSecret())).consumer(Consumers.fromOAuthConsumer((OAuthConsumer)consumerInfo.getConsumer()))).properties(TokenSessionProperties.createPropertyMap(tokenInfo))).build());
        if (!(!savedToken.isRequestToken() && token.getToken().equals(savedToken.getToken()) && token.getTokenSecret().equals(savedToken.getTokenSecret()) && token.getConsumer().getKey().equals(savedToken.getConsumer().getKey()) && token.getProperties().equals(savedToken.getProperties()))) {
            throw new GadgetException(GadgetException.Code.INTERNAL_SERVER_ERROR, "Saved token is inconsistent with the actual token");
        }
    }

    private String getOAuthCallbackUrl() {
        return Uri.ensureTrailingSlash((String)this.applicationProperties.getBaseUrl()) + OAUTH_CALLBACK_SERVLET_PATH;
    }

    public void removeToken(SecurityToken securityToken, OAuthStore.ConsumerInfo consumerInfo, String serviceName, String tokenName) throws GadgetException {
        ConsumerTokenStore.Key key = this.createKey(securityToken, serviceName, tokenName);
        this.tokenStore.remove(key);
    }

    private Consumer findConsumerForService(String service) {
        Consumer consumer;
        if (StringUtils.isBlank((CharSequence)service)) {
            consumer = this.consumerService.getConsumer();
        } else {
            consumer = this.consumerService.getConsumer(service);
            if (consumer == null) {
                consumer = this.consumerService.getConsumer();
            }
        }
        return consumer;
    }

    private ConsumerTokenStore.Key createKey(SecurityToken securityToken, String serviceName, String tokenName) {
        StringBuilder sb = new StringBuilder();
        sb.append(securityToken.getModuleId());
        sb.append(':');
        sb.append(securityToken.getViewerId());
        sb.append(':');
        sb.append(serviceName);
        sb.append(':');
        sb.append(tokenName);
        return new ConsumerTokenStore.Key(sb.toString());
    }

    static final class TokenSessionProperties {
        static final String SESSION_HANDLE = "org.apache.shindig.oauth.sessionHandle";
        static final String TOKEN_EXPIRE_MILLIS = "org.apache.shindig.oauth.tokenExpireMillis";

        TokenSessionProperties() {
        }

        static Map<String, String> createPropertyMap(OAuthStore.TokenInfo tokenInfo) {
            ImmutableMap.Builder properties = ImmutableMap.builder();
            if (tokenInfo.getSessionHandle() != null) {
                properties.put((Object)SESSION_HANDLE, (Object)tokenInfo.getSessionHandle());
            }
            if (tokenInfo.getTokenExpireMillis() > 0L) {
                properties.put((Object)TOKEN_EXPIRE_MILLIS, (Object)String.valueOf(tokenInfo.getTokenExpireMillis()));
            }
            return properties.build();
        }
    }
}

