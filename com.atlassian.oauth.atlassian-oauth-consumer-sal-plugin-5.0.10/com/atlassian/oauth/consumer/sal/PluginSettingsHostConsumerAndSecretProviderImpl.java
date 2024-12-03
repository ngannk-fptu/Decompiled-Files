/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.oauth.consumer.ConsumerCreationException
 *  com.atlassian.oauth.consumer.ConsumerService
 *  com.atlassian.oauth.consumer.core.ConsumerServiceStore$ConsumerAndSecret
 *  com.atlassian.oauth.consumer.core.HostConsumerAndSecretProvider
 *  com.atlassian.oauth.shared.sal.PrefixingPluginSettings
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 */
package com.atlassian.oauth.consumer.sal;

import com.atlassian.oauth.Consumer;
import com.atlassian.oauth.consumer.ConsumerCreationException;
import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.oauth.consumer.core.ConsumerServiceStore;
import com.atlassian.oauth.consumer.core.HostConsumerAndSecretProvider;
import com.atlassian.oauth.consumer.sal.ConsumerProperties;
import com.atlassian.oauth.consumer.sal.KeyPairFactory;
import com.atlassian.oauth.shared.sal.PrefixingPluginSettings;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Properties;

public class PluginSettingsHostConsumerAndSecretProviderImpl
implements HostConsumerAndSecretProvider {
    static final String HOST_SERVICENAME = "__HOST_SERVICE__";
    private static final SecureRandom random = new SecureRandom();
    private final ApplicationProperties applicationProperties;
    private final KeyPairFactory keyPairFactory;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final I18nResolver i18n;

    public PluginSettingsHostConsumerAndSecretProviderImpl(ApplicationProperties applicationProperties, PluginSettingsFactory pluginSettingsFactory, KeyPairFactory keyPairFactory, I18nResolver i18nResolver) {
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.pluginSettingsFactory = Objects.requireNonNull(pluginSettingsFactory, "pluginSettingsFactory");
        this.keyPairFactory = Objects.requireNonNull(keyPairFactory, "keyPairFactory");
        this.i18n = Objects.requireNonNull(i18nResolver, "i18nResolver");
    }

    public synchronized ConsumerServiceStore.ConsumerAndSecret get() {
        Settings settings = this.settings();
        ConsumerServiceStore.ConsumerAndSecret hostCas = settings.get();
        if (hostCas != null) {
            return hostCas;
        }
        hostCas = this.createHostConsumerAndSecret();
        settings.put(hostCas);
        return hostCas;
    }

    public ConsumerServiceStore.ConsumerAndSecret put(ConsumerServiceStore.ConsumerAndSecret hostCas) {
        Settings settings = this.settings();
        settings.put(hostCas);
        return settings.get();
    }

    private ConsumerServiceStore.ConsumerAndSecret createHostConsumerAndSecret() {
        KeyPair keyPair;
        String key = this.generateConsumerKey();
        try {
            keyPair = this.keyPairFactory.newKeyPair();
        }
        catch (GeneralSecurityException e) {
            throw new ConsumerCreationException("Could not create key pair for consumer", (Throwable)e);
        }
        Consumer consumer = Consumer.key((String)key).name(this.applicationProperties.getDisplayName()).publicKey(keyPair.getPublic()).description(this.i18n.getText("host.consumer.default.description", new Serializable[]{this.applicationProperties.getDisplayName(), this.applicationProperties.getBaseUrl(UrlMode.CANONICAL)})).build();
        return new ConsumerServiceStore.ConsumerAndSecret(HOST_SERVICENAME, consumer, keyPair.getPrivate());
    }

    private String generateConsumerKey() {
        StringBuilder stringBuilder = new StringBuilder(this.applicationProperties.getDisplayName());
        stringBuilder.append(":");
        random.ints(10L, 0, 10).forEach(stringBuilder::append);
        return stringBuilder.toString();
    }

    private Settings settings() {
        return new Settings(this.pluginSettingsFactory.createGlobalSettings());
    }

    final class Settings {
        private PluginSettings pluginSettings;

        public Settings(PluginSettings settings) {
            this.pluginSettings = new PrefixingPluginSettings(Objects.requireNonNull(settings, "settings"), ConsumerService.class.getName() + ":host");
        }

        public ConsumerServiceStore.ConsumerAndSecret get() {
            Properties props = (Properties)this.pluginSettings.get(PluginSettingsHostConsumerAndSecretProviderImpl.HOST_SERVICENAME);
            if (props == null) {
                return null;
            }
            return new ConsumerProperties(props).asConsumerAndSecret(PluginSettingsHostConsumerAndSecretProviderImpl.HOST_SERVICENAME);
        }

        public void put(ConsumerServiceStore.ConsumerAndSecret cas) {
            this.pluginSettings.put(PluginSettingsHostConsumerAndSecretProviderImpl.HOST_SERVICENAME, (Object)new ConsumerProperties(cas).asProperties());
        }
    }
}

