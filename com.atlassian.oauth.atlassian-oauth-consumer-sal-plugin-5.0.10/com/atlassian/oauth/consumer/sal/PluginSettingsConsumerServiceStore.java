/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.oauth.consumer.ConsumerService
 *  com.atlassian.oauth.consumer.core.ConsumerServiceStore
 *  com.atlassian.oauth.consumer.core.ConsumerServiceStore$ConsumerAndSecret
 *  com.atlassian.oauth.shared.sal.Functions
 *  com.atlassian.oauth.shared.sal.HashingLongPropertyKeysPluginSettings
 *  com.atlassian.oauth.shared.sal.PrefixingPluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.oauth.consumer.sal;

import com.atlassian.oauth.Consumer;
import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.oauth.consumer.core.ConsumerServiceStore;
import com.atlassian.oauth.consumer.sal.ConsumerProperties;
import com.atlassian.oauth.shared.sal.Functions;
import com.atlassian.oauth.shared.sal.HashingLongPropertyKeysPluginSettings;
import com.atlassian.oauth.shared.sal.PrefixingPluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;

public class PluginSettingsConsumerServiceStore
implements ConsumerServiceStore {
    private final PluginSettingsFactory pluginSettingsFactory;

    public PluginSettingsConsumerServiceStore(PluginSettingsFactory factory) {
        this.pluginSettingsFactory = Objects.requireNonNull(factory, "factory");
    }

    public ConsumerServiceStore.ConsumerAndSecret get(String service) {
        Objects.requireNonNull(service, "service");
        ConsumerProperties consumerProperties = this.settings().getConsumerProperties(service);
        if (consumerProperties == null) {
            return null;
        }
        return consumerProperties.asConsumerAndSecret(service);
    }

    public ConsumerServiceStore.ConsumerAndSecret getByKey(String consumerKey) {
        Objects.requireNonNull(consumerKey, "consumerKey");
        String service = this.settings().getServiceNameForConsumerKey(consumerKey);
        if (service == null) {
            return null;
        }
        return this.get(service);
    }

    public Iterable<Consumer> getAllServiceProviders() {
        return this.settings().getServiceNames().stream().map(service -> this.get((String)service).getConsumer()).collect(Collectors.toList());
    }

    public void put(String service, ConsumerServiceStore.ConsumerAndSecret cas) {
        Objects.requireNonNull(service, "service");
        Objects.requireNonNull(cas, "cas");
        this.settings().putConsumerProperties(service, new ConsumerProperties(cas));
    }

    public void removeByKey(String consumerKey) {
        Objects.requireNonNull(consumerKey, "consumerKey");
        String service = this.settings().getServiceNameForConsumerKey(consumerKey);
        if (service == null) {
            return;
        }
        this.settings().removeConsumerProperties(service, consumerKey);
    }

    private Settings settings() {
        return new Settings(this.pluginSettingsFactory.createGlobalSettings());
    }

    static final class Settings {
        private final PluginSettings settings;

        public Settings(PluginSettings settings) {
            this.settings = new PrefixingPluginSettings((PluginSettings)new HashingLongPropertyKeysPluginSettings(settings), ConsumerService.class.getName());
        }

        public ConsumerProperties getConsumerProperties(String service) {
            Properties props = (Properties)this.settings.get(service);
            if (props == null) {
                return null;
            }
            return new ConsumerProperties(props);
        }

        private void putConsumerProperties(String service, ConsumerProperties props) {
            this.settings.put(service, (Object)props.asProperties());
            this.addService(service);
            this.putServiceNameForConsumerKey(props.getConsumerKey(), service);
        }

        private void removeConsumerProperties(String service, String consumerKey) {
            this.settings.remove(service);
            this.removeService(service);
            this.removeServiceNameForConsumerKey(consumerKey);
        }

        public String getServiceNameForConsumerKey(String consumerKey) {
            return (String)this.settings.get("consumerService." + consumerKey);
        }

        private void putServiceNameForConsumerKey(String consumerKey, String service) {
            this.settings.put("consumerService." + consumerKey, (Object)service);
        }

        private void removeServiceNameForConsumerKey(String consumerKey) {
            this.settings.remove("consumerService." + consumerKey);
        }

        public Set<String> getServiceNames() {
            String encodedKeys = (String)this.settings.get("serviceNames");
            if (StringUtils.isBlank((CharSequence)encodedKeys)) {
                return new HashSet<String>();
            }
            return Arrays.stream(encodedKeys.split("/")).map(Functions.KEY_DECODER).collect(Collectors.toSet());
        }

        private void putServiceNames(Iterable<String> keys) {
            this.settings.put("serviceNames", (Object)StreamSupport.stream(keys.spliterator(), false).map(Functions.KEY_ENCODER).collect(Collectors.joining("/")));
        }

        private void addService(String service) {
            Set<String> serviceNames = this.getServiceNames();
            serviceNames.add(service);
            this.putServiceNames(serviceNames);
        }

        private void removeService(String service) {
            Set<String> serviceNames = this.getServiceNames();
            serviceNames.remove(service);
            this.putServiceNames(serviceNames);
        }

        static final class Keys {
            static final String SERVICE_NAMES = "serviceNames";
            static final String CONSUMER_SERVICE = "consumerService";

            Keys() {
            }
        }
    }
}

