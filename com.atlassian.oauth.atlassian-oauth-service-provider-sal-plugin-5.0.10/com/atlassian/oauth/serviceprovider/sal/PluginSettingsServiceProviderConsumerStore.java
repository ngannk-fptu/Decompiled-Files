/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.oauth.serviceprovider.ServiceProviderConsumerStore
 *  com.atlassian.oauth.serviceprovider.StoreException
 *  com.atlassian.oauth.shared.sal.AbstractSettingsProperties
 *  com.atlassian.oauth.shared.sal.Functions
 *  com.atlassian.oauth.shared.sal.HashingLongPropertyKeysPluginSettings
 *  com.atlassian.oauth.shared.sal.PrefixingPluginSettings
 *  com.atlassian.oauth.util.RSAKeys
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  org.springframework.util.CollectionUtils
 */
package com.atlassian.oauth.serviceprovider.sal;

import com.atlassian.oauth.Consumer;
import com.atlassian.oauth.serviceprovider.ServiceProviderConsumerStore;
import com.atlassian.oauth.serviceprovider.StoreException;
import com.atlassian.oauth.shared.sal.AbstractSettingsProperties;
import com.atlassian.oauth.shared.sal.Functions;
import com.atlassian.oauth.shared.sal.HashingLongPropertyKeysPluginSettings;
import com.atlassian.oauth.shared.sal.PrefixingPluginSettings;
import com.atlassian.oauth.util.RSAKeys;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.util.CollectionUtils;

public class PluginSettingsServiceProviderConsumerStore
implements ServiceProviderConsumerStore {
    private final PluginSettingsFactory pluginSettingsFactory;

    public PluginSettingsServiceProviderConsumerStore(PluginSettingsFactory factory) {
        this.pluginSettingsFactory = Objects.requireNonNull(factory, "factory");
    }

    public Consumer get(String consumerKey) {
        Objects.requireNonNull(consumerKey, "consumerKey");
        Settings.ConsumerProperties props = this.settings().getConsumerProperties(consumerKey);
        if (props == null) {
            return null;
        }
        try {
            try {
                return Consumer.key((String)consumerKey).name(props.getName()).publicKey(props.getPublicKey()).description(props.getDescription()).callback(props.getCallback()).threeLOAllowed(props.getThreeLOAllowed()).twoLOAllowed(props.getTwoLOAllowed()).executingTwoLOUser(props.getExecutingTwoLOUser()).twoLOImpersonationAllowed(props.getTwoLOImpersonationAllowed()).build();
            }
            catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new StoreException((Throwable)e);
            }
        }
        catch (URISyntaxException e) {
            throw new StoreException("callback URI is not valid", (Throwable)e);
        }
    }

    public void put(Consumer consumer) {
        Objects.requireNonNull(consumer, "consumer");
        Settings settings = this.settings();
        settings.addConsumerKey(consumer.getKey());
        settings.putConsumerProperties(consumer.getKey(), new Settings.ConsumerProperties(consumer));
    }

    public void remove(String consumerKey) {
        Objects.requireNonNull(consumerKey, "consumerKey");
        Settings settings = this.settings();
        settings.removeConsumerKey(consumerKey);
        settings.removeConsumerProperties(consumerKey);
    }

    public Iterable<Consumer> getAll() {
        return StreamSupport.stream(this.getConsumerKeys().spliterator(), false).map(this::get).collect(Collectors.toList());
    }

    private Iterable<String> getConsumerKeys() {
        return this.settings().getConsumerKeys();
    }

    private Settings settings() {
        return new Settings(this.pluginSettingsFactory.createGlobalSettings());
    }

    static final class Settings {
        private final PluginSettings settings;

        Settings(PluginSettings settings) {
            this.settings = new PrefixingPluginSettings((PluginSettings)new HashingLongPropertyKeysPluginSettings(settings), ServiceProviderConsumerStore.class.getName());
        }

        ConsumerProperties getConsumerProperties(String consumerKey) {
            Properties props = (Properties)this.settings.get(this.consumerSettingKey(consumerKey));
            if (props == null) {
                return null;
            }
            return new ConsumerProperties(props);
        }

        void putConsumerProperties(String key, AbstractSettingsProperties consumerProperties) {
            this.settings.put(this.consumerSettingKey(key), (Object)consumerProperties.asProperties());
        }

        void removeConsumerProperties(String key) {
            this.settings.remove(this.consumerSettingKey(key));
        }

        private String consumerSettingKey(String key) {
            return "consumer." + key;
        }

        Set<String> getConsumerKeys() {
            String encodedKeys = (String)this.settings.get("allConsumerKeys");
            if (encodedKeys == null) {
                return new HashSet<String>();
            }
            return Arrays.stream(encodedKeys.split("/")).map(Functions.KEY_DECODER).collect(Collectors.toSet());
        }

        void putConsumerKeys(Set<String> keys) {
            if (CollectionUtils.isEmpty(keys)) {
                this.settings.remove("allConsumerKeys");
            } else {
                String consumerKeys = keys.stream().map(Functions.KEY_ENCODER).collect(Collectors.joining("/"));
                this.settings.put("allConsumerKeys", (Object)consumerKeys);
            }
        }

        void addConsumerKey(String key) {
            Set<String> consumerKeys = this.getConsumerKeys();
            consumerKeys.add(key);
            this.putConsumerKeys(consumerKeys);
        }

        void removeConsumerKey(String key) {
            Set<String> consumerKeys = this.getConsumerKeys();
            consumerKeys.remove(key);
            this.putConsumerKeys(consumerKeys);
        }

        static final class Keys {
            static final String CONSUMER_KEYS = "allConsumerKeys";

            Keys() {
            }
        }

        static final class ConsumerProperties
        extends AbstractSettingsProperties {
            static final String PUBLIC_KEY = "publicKey";
            static final String CALLBACK = "callback";
            static final String DESCRIPTION = "description";
            static final String NAME = "name";
            static final String THREE_LO_ALLOWED = "threeLOAllowed";
            static final String TWO_LO_ALLOWED = "twoLOAllowed";
            static final String EXECUTING_TWO_LO_USER = "executingTwoLOUser";
            static final String TWO_LO_IMPERSONATION_ALLOWED = "twoLOImpersonationAllowed";

            ConsumerProperties(Consumer consumer) {
                this.putName(consumer.getName());
                this.putPublicKey(consumer.getPublicKey());
                this.putDescription(consumer.getDescription());
                this.putCallback(consumer.getCallback());
                this.putThreeLOAllowed(consumer.getThreeLOAllowed());
                this.putTwoLOAllowed(consumer.getTwoLOAllowed());
                this.putExecutingTwoLOUser(consumer.getExecutingTwoLOUser());
                this.putTwoLOImpersonationAllowed(consumer.getTwoLOImpersonationAllowed());
            }

            ConsumerProperties(Properties properties) {
                super(properties);
            }

            String getName() {
                return this.get(NAME);
            }

            void putName(String name) {
                this.put(NAME, name);
            }

            PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
                return RSAKeys.fromPemEncodingToPublicKey((String)this.get(PUBLIC_KEY));
            }

            void putPublicKey(PublicKey publicKey) {
                this.put(PUBLIC_KEY, RSAKeys.toPemEncoding((Key)publicKey));
            }

            String getDescription() {
                return this.get(DESCRIPTION);
            }

            void putDescription(String description) {
                if (description == null) {
                    return;
                }
                this.put(DESCRIPTION, description);
            }

            URI getCallback() throws URISyntaxException {
                String callback = this.get(CALLBACK);
                if (callback == null) {
                    return null;
                }
                return new URI(callback);
            }

            void putCallback(URI callback) {
                if (callback == null) {
                    return;
                }
                this.put(CALLBACK, callback.toString());
            }

            boolean getThreeLOAllowed() {
                String isAllowed = this.get(THREE_LO_ALLOWED);
                if (isAllowed == null) {
                    return true;
                }
                return Boolean.parseBoolean(isAllowed);
            }

            void putThreeLOAllowed(boolean threeLOAllowed) {
                this.put(THREE_LO_ALLOWED, Boolean.toString(threeLOAllowed));
            }

            boolean getTwoLOAllowed() {
                String isAllowed = this.get(TWO_LO_ALLOWED);
                if (isAllowed == null) {
                    return false;
                }
                return Boolean.parseBoolean(isAllowed);
            }

            void putTwoLOAllowed(boolean twoLOAllowed) {
                this.put(TWO_LO_ALLOWED, Boolean.toString(twoLOAllowed));
            }

            String getExecutingTwoLOUser() {
                return this.get(EXECUTING_TWO_LO_USER);
            }

            void putExecutingTwoLOUser(String executingTwoLOUser) {
                if (executingTwoLOUser == null) {
                    return;
                }
                this.put(EXECUTING_TWO_LO_USER, executingTwoLOUser);
            }

            boolean getTwoLOImpersonationAllowed() {
                String isAllowed = this.get(TWO_LO_IMPERSONATION_ALLOWED);
                if (isAllowed == null) {
                    return false;
                }
                return Boolean.parseBoolean(isAllowed);
            }

            void putTwoLOImpersonationAllowed(boolean twoLOImpersonationAllowed) {
                this.put(TWO_LO_IMPERSONATION_ALLOWED, Boolean.toString(twoLOImpersonationAllowed));
            }
        }
    }
}

