/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.oauth.Token
 *  com.atlassian.oauth.consumer.ConsumerToken
 *  com.atlassian.oauth.consumer.ConsumerToken$ConsumerTokenBuilder
 *  com.atlassian.oauth.consumer.ConsumerTokenStore
 *  com.atlassian.oauth.consumer.ConsumerTokenStore$Key
 *  com.atlassian.oauth.consumer.core.ConsumerServiceStore
 *  com.atlassian.oauth.consumer.core.ConsumerServiceStore$ConsumerAndSecret
 *  com.atlassian.oauth.consumer.core.HostConsumerAndSecretProvider
 *  com.atlassian.oauth.shared.sal.Functions
 *  com.atlassian.oauth.shared.sal.HashingLongPropertyKeysPluginSettings
 *  com.atlassian.oauth.shared.sal.PrefixingPluginSettings
 *  com.atlassian.oauth.shared.sal.TokenProperties
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  org.apache.commons.codec.digest.DigestUtils
 */
package com.atlassian.oauth.consumer.sal;

import com.atlassian.oauth.Consumer;
import com.atlassian.oauth.Token;
import com.atlassian.oauth.consumer.ConsumerToken;
import com.atlassian.oauth.consumer.ConsumerTokenStore;
import com.atlassian.oauth.consumer.core.ConsumerServiceStore;
import com.atlassian.oauth.consumer.core.HostConsumerAndSecretProvider;
import com.atlassian.oauth.shared.sal.Functions;
import com.atlassian.oauth.shared.sal.HashingLongPropertyKeysPluginSettings;
import com.atlassian.oauth.shared.sal.PrefixingPluginSettings;
import com.atlassian.oauth.shared.sal.TokenProperties;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.codec.digest.DigestUtils;

public class PluginSettingsConsumerTokenStore
implements ConsumerTokenStore {
    private final PluginSettingsFactory pluginSettingsFactory;
    private final ConsumerServiceStore consumerServiceStore;
    private final HostConsumerAndSecretProvider hostCasProvider;

    public PluginSettingsConsumerTokenStore(PluginSettingsFactory factory, ConsumerServiceStore consumerServiceStore, HostConsumerAndSecretProvider hostCasProvider) {
        this.pluginSettingsFactory = Objects.requireNonNull(factory, "factory");
        this.consumerServiceStore = Objects.requireNonNull(consumerServiceStore, "consumerServiceStore");
        this.hostCasProvider = Objects.requireNonNull(hostCasProvider, "hostCasProvider");
    }

    public ConsumerToken get(ConsumerTokenStore.Key key) {
        Objects.requireNonNull(key, "key");
        TokenProperties props = this.settings().get(key);
        if (props == null) {
            return null;
        }
        ConsumerServiceStore.ConsumerAndSecret cas = this.hostCasProvider.get();
        if (cas == null || !cas.getConsumer().getKey().equals(props.getConsumerKey())) {
            cas = this.consumerServiceStore.getByKey(props.getConsumerKey());
        }
        Objects.requireNonNull(cas, "consumerAndSecret");
        Consumer consumer = cas.getConsumer();
        if (props.isAccessToken()) {
            return ((ConsumerToken.ConsumerTokenBuilder)((ConsumerToken.ConsumerTokenBuilder)((ConsumerToken.ConsumerTokenBuilder)ConsumerToken.newAccessToken((String)props.getToken()).tokenSecret(props.getTokenSecret())).consumer(consumer)).properties(props.getProperties())).build();
        }
        return ((ConsumerToken.ConsumerTokenBuilder)((ConsumerToken.ConsumerTokenBuilder)((ConsumerToken.ConsumerTokenBuilder)ConsumerToken.newRequestToken((String)props.getToken()).tokenSecret(props.getTokenSecret())).consumer(consumer)).properties(props.getProperties())).build();
    }

    public Map<ConsumerTokenStore.Key, ConsumerToken> getConsumerTokens(String consumerKey) {
        Objects.requireNonNull(consumerKey, "consumerKey");
        HashMap<ConsumerTokenStore.Key, ConsumerToken> consumerTokens = new HashMap<ConsumerTokenStore.Key, ConsumerToken>();
        for (String tokenStr : this.settings().getTokensForConsumer(consumerKey)) {
            ConsumerTokenStore.Key key = new ConsumerTokenStore.Key(tokenStr);
            ConsumerToken token = this.get(key);
            if (token == null) continue;
            consumerTokens.put(key, token);
        }
        return Collections.unmodifiableMap(consumerTokens);
    }

    public ConsumerToken put(ConsumerTokenStore.Key key, ConsumerToken token) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(token, "token");
        Settings settings = this.settings();
        settings.put(key, new ConsumerTokenProperties(token));
        settings.addTokenForConsumer(token.getConsumer().getKey(), key);
        return token;
    }

    public void remove(ConsumerTokenStore.Key key) {
        Objects.requireNonNull(key, "key");
        Settings settings = this.settings();
        TokenProperties properties = settings.get(key);
        if (properties == null) {
            return;
        }
        settings.remove(key);
        settings.removeTokenForConsumer(properties.getConsumerKey(), key);
    }

    public void removeTokensForConsumer(String consumerKey) {
        Objects.requireNonNull(consumerKey, "consumerKey");
        Settings settings = this.settings();
        Set<String> tokens = settings.getTokensForConsumer(consumerKey);
        for (String token : tokens) {
            settings.remove(new ConsumerTokenStore.Key(token));
        }
        settings.removeTokensForConsumer(consumerKey);
    }

    private Settings settings() {
        return new Settings(this.pluginSettingsFactory.createGlobalSettings());
    }

    static final class ConsumerTokenProperties
    extends TokenProperties {
        public ConsumerTokenProperties(Properties properties) {
            super(properties);
        }

        public ConsumerTokenProperties(ConsumerToken token) {
            super((Token)token);
        }
    }

    static final class Settings {
        private final PluginSettings settings;

        Settings(PluginSettings settings) {
            this.settings = new PrefixingPluginSettings((PluginSettings)new HashingLongPropertyKeysPluginSettings(settings), ConsumerTokenStore.class.getName());
        }

        TokenProperties get(ConsumerTokenStore.Key key) {
            Properties props = (Properties)this.settings.get(this.tokenSettingKey(key));
            if (props == null) {
                return null;
            }
            return new ConsumerTokenProperties(props);
        }

        void put(ConsumerTokenStore.Key key, TokenProperties tokenProperties) {
            this.settings.put(this.tokenSettingKey(key), (Object)tokenProperties.asProperties());
        }

        void remove(ConsumerTokenStore.Key key) {
            this.settings.remove(this.tokenSettingKey(key));
        }

        private String tokenSettingKey(ConsumerTokenStore.Key key) {
            return "keys." + DigestUtils.sha1Hex((String)key.toString());
        }

        public Set<String> getTokensForConsumer(String consumerKey) {
            String tokenKeys = (String)this.settings.get(this.consumerSettingKey(consumerKey));
            if (tokenKeys == null) {
                return new HashSet<String>();
            }
            return Arrays.stream(tokenKeys.split("/")).map(Functions.KEY_DECODER).collect(Collectors.toSet());
        }

        private void putTokensForConsumer(String consumerKey, Set<String> tokens) {
            if (!tokens.isEmpty()) {
                this.settings.put(this.consumerSettingKey(consumerKey), (Object)tokens.stream().map(Functions.KEY_ENCODER).collect(Collectors.joining("/")));
            } else {
                this.settings.put(this.consumerSettingKey(consumerKey), null);
            }
        }

        public void removeTokenForConsumer(String consumerKey, ConsumerTokenStore.Key tokenKey) {
            Set<String> tokens = this.getTokensForConsumer(consumerKey);
            tokens.remove(tokenKey.toString());
            this.putTokensForConsumer(consumerKey, tokens);
        }

        public void addTokenForConsumer(String consumerKey, ConsumerTokenStore.Key tokenKey) {
            Set<String> tokensForConsumer = this.getTokensForConsumer(consumerKey);
            tokensForConsumer.add(tokenKey.toString());
            this.putTokensForConsumer(consumerKey, tokensForConsumer);
        }

        public void removeTokensForConsumer(String consumerKey) {
            this.settings.remove(this.consumerSettingKey(consumerKey));
        }

        private String consumerSettingKey(String consumerKey) {
            return "consumerKeys." + consumerKey;
        }
    }
}

