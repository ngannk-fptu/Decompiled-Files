/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.support;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class SslConfiguration {
    public static final String PEM_KEYSTORE_TYPE = "PEM";
    public static final String DEFAULT_KEYSTORE_TYPE = KeyStore.getDefaultType();
    private final KeyStoreConfiguration keyStoreConfiguration;
    private final KeyStoreConfiguration trustStoreConfiguration;
    private final KeyConfiguration keyConfiguration;
    private final List<String> enabledProtocols;
    private final List<String> enabledCipherSuites;

    @Deprecated
    public SslConfiguration(Resource keyStore, @Nullable String keyStorePassword, Resource trustStore, @Nullable String trustStorePassword) {
        this(new KeyStoreConfiguration(keyStore, SslConfiguration.charsOrNull(keyStorePassword), DEFAULT_KEYSTORE_TYPE), new KeyStoreConfiguration(trustStore, SslConfiguration.charsOrNull(trustStorePassword), DEFAULT_KEYSTORE_TYPE));
    }

    public SslConfiguration(KeyStoreConfiguration keyStoreConfiguration, KeyStoreConfiguration trustStoreConfiguration) {
        this(keyStoreConfiguration, KeyConfiguration.unconfigured(), trustStoreConfiguration);
    }

    public SslConfiguration(KeyStoreConfiguration keyStoreConfiguration, KeyConfiguration keyConfiguration, KeyStoreConfiguration trustStoreConfiguration, List<String> enabledProtocols, List<String> enabledCipherSuites) {
        Assert.notNull((Object)keyStoreConfiguration, "KeyStore configuration must not be null");
        Assert.notNull((Object)keyConfiguration, "KeyConfiguration must not be null");
        Assert.notNull((Object)trustStoreConfiguration, "TrustStore configuration must not be null");
        this.keyStoreConfiguration = keyStoreConfiguration;
        this.keyConfiguration = keyConfiguration;
        this.trustStoreConfiguration = trustStoreConfiguration;
        this.enabledProtocols = Collections.unmodifiableList(new ArrayList<String>(enabledProtocols));
        this.enabledCipherSuites = Collections.unmodifiableList(new ArrayList<String>(enabledCipherSuites));
    }

    public SslConfiguration(KeyStoreConfiguration keyStoreConfiguration, KeyConfiguration keyConfiguration, KeyStoreConfiguration trustStoreConfiguration) {
        this(keyStoreConfiguration, keyConfiguration, trustStoreConfiguration, Collections.emptyList(), Collections.emptyList());
    }

    public SslConfiguration(KeyStoreConfiguration keyStoreConfiguration, KeyStoreConfiguration trustStoreConfiguration, List<String> enabledProtocols, List<String> enabledCipherSuites) {
        this(keyStoreConfiguration, KeyConfiguration.unconfigured(), trustStoreConfiguration, enabledProtocols, enabledCipherSuites);
    }

    @Deprecated
    public static SslConfiguration forTrustStore(Resource trustStore, @Nullable String trustStorePassword) {
        return SslConfiguration.forTrustStore(trustStore, SslConfiguration.charsOrNull(trustStorePassword));
    }

    public static SslConfiguration forTrustStore(Resource trustStore, @Nullable char[] trustStorePassword) {
        Assert.notNull((Object)trustStore, "TrustStore must not be null");
        Assert.isTrue(trustStore.exists(), () -> String.format("TrustStore %s does not exist", trustStore));
        return new SslConfiguration(KeyStoreConfiguration.unconfigured(), KeyConfiguration.unconfigured(), new KeyStoreConfiguration(trustStore, trustStorePassword, DEFAULT_KEYSTORE_TYPE));
    }

    public static SslConfiguration forTrustStore(KeyStoreConfiguration trustStore) {
        return SslConfiguration.unconfigured().withTrustStore(trustStore);
    }

    @Deprecated
    public static SslConfiguration forKeyStore(Resource keyStore, @Nullable String keyStorePassword) {
        return SslConfiguration.forKeyStore(keyStore, SslConfiguration.charsOrNull(keyStorePassword));
    }

    public static SslConfiguration forKeyStore(Resource keyStore, @Nullable char[] keyStorePassword) {
        return SslConfiguration.forKeyStore(new KeyStoreConfiguration(keyStore, keyStorePassword, DEFAULT_KEYSTORE_TYPE), KeyConfiguration.unconfigured());
    }

    public static SslConfiguration forKeyStore(KeyStoreConfiguration keyStore) {
        return SslConfiguration.forKeyStore(keyStore, KeyConfiguration.unconfigured());
    }

    public static SslConfiguration forKeyStore(KeyStoreConfiguration keyStore, KeyConfiguration keyConfiguration) {
        return SslConfiguration.unconfigured().withKeyStore(keyStore, keyConfiguration);
    }

    public static SslConfiguration forKeyStore(Resource keyStore, @Nullable char[] keyStorePassword, KeyConfiguration keyConfiguration) {
        Assert.notNull((Object)keyStore, "KeyStore must not be null");
        Assert.isTrue(keyStore.exists(), () -> String.format("KeyStore %s does not exist", keyStore));
        Assert.notNull((Object)keyConfiguration, "KeyConfiguration must not be null");
        return new SslConfiguration(new KeyStoreConfiguration(keyStore, keyStorePassword, DEFAULT_KEYSTORE_TYPE), keyConfiguration, KeyStoreConfiguration.unconfigured());
    }

    @Deprecated
    public SslConfiguration create(Resource keyStore, @Nullable String keyStorePassword, Resource trustStore, @Nullable String trustStorePassword) {
        return SslConfiguration.create(keyStore, SslConfiguration.charsOrNull(keyStorePassword), trustStore, SslConfiguration.charsOrNull(trustStorePassword));
    }

    public static SslConfiguration create(Resource keyStore, @Nullable char[] keyStorePassword, Resource trustStore, @Nullable char[] trustStorePassword) {
        Assert.notNull((Object)keyStore, "KeyStore must not be null");
        Assert.isTrue(keyStore.exists(), () -> String.format("KeyStore %s does not exist", keyStore));
        Assert.notNull((Object)trustStore, "TrustStore must not be null");
        Assert.isTrue(trustStore.exists(), String.format("TrustStore %s does not exist", trustStore));
        return new SslConfiguration(new KeyStoreConfiguration(keyStore, keyStorePassword, DEFAULT_KEYSTORE_TYPE), new KeyStoreConfiguration(trustStore, trustStorePassword, DEFAULT_KEYSTORE_TYPE));
    }

    public static SslConfiguration unconfigured() {
        return new SslConfiguration(KeyStoreConfiguration.unconfigured(), KeyStoreConfiguration.unconfigured());
    }

    public List<String> getEnabledProtocols() {
        return this.enabledProtocols;
    }

    public SslConfiguration withEnabledProtocols(String ... enabledProtocols) {
        Assert.notNull((Object)enabledProtocols, "Enabled protocols must not be null");
        return this.withEnabledProtocols(Arrays.asList(enabledProtocols));
    }

    public SslConfiguration withEnabledProtocols(List<String> enabledProtocols) {
        Assert.notNull(enabledProtocols, "Enabled protocols must not be null");
        return new SslConfiguration(this.keyStoreConfiguration, this.keyConfiguration, this.trustStoreConfiguration, enabledProtocols, this.enabledCipherSuites);
    }

    public List<String> getEnabledCipherSuites() {
        return this.enabledCipherSuites;
    }

    public SslConfiguration withEnabledCipherSuites(String ... enabledCipherSuites) {
        Assert.notNull(this.enabledProtocols, "Enabled cipher suites must not be null");
        return this.withEnabledCipherSuites(Arrays.asList(enabledCipherSuites));
    }

    public SslConfiguration withEnabledCipherSuites(List<String> enabledCipherSuites) {
        Assert.notNull(this.enabledProtocols, "Enabled cipher suites must not be null");
        return new SslConfiguration(this.keyStoreConfiguration, this.keyConfiguration, this.trustStoreConfiguration, this.enabledProtocols, enabledCipherSuites);
    }

    public Resource getKeyStore() {
        return this.keyStoreConfiguration.getResource();
    }

    @Deprecated
    @Nullable
    public String getKeyStorePassword() {
        return SslConfiguration.stringOrNull(this.keyStoreConfiguration.getStorePassword());
    }

    public KeyStoreConfiguration getKeyStoreConfiguration() {
        return this.keyStoreConfiguration;
    }

    public KeyConfiguration getKeyConfiguration() {
        return this.keyConfiguration;
    }

    public SslConfiguration withKeyStore(KeyStoreConfiguration configuration) {
        return this.withKeyStore(configuration, KeyConfiguration.unconfigured());
    }

    public SslConfiguration withKeyStore(KeyStoreConfiguration configuration, KeyConfiguration keyConfiguration) {
        return new SslConfiguration(configuration, keyConfiguration, this.trustStoreConfiguration);
    }

    public Resource getTrustStore() {
        return this.trustStoreConfiguration.getResource();
    }

    @Deprecated
    @Nullable
    public String getTrustStorePassword() {
        return SslConfiguration.stringOrNull(this.trustStoreConfiguration.getStorePassword());
    }

    public KeyStoreConfiguration getTrustStoreConfiguration() {
        return this.trustStoreConfiguration;
    }

    public SslConfiguration withTrustStore(KeyStoreConfiguration configuration) {
        return new SslConfiguration(this.keyStoreConfiguration, this.keyConfiguration, configuration);
    }

    @Nullable
    private static String stringOrNull(@Nullable char[] storePassword) {
        return storePassword != null ? new String(storePassword) : null;
    }

    @Nullable
    private static char[] charsOrNull(@Nullable String trustStorePassword) {
        return trustStorePassword != null ? trustStorePassword.toCharArray() : null;
    }

    static class AbsentResource
    extends AbstractResource {
        static final AbsentResource INSTANCE = new AbsentResource();

        private AbsentResource() {
        }

        @Override
        public String getDescription() {
            return this.getClass().getSimpleName();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            throw new UnsupportedOperationException("Empty resource");
        }
    }

    public static class KeyConfiguration {
        private static final KeyConfiguration UNCONFIGURED = new KeyConfiguration(null, null);
        @Nullable
        private final char[] keyPassword;
        @Nullable
        private final String keyAlias;

        private KeyConfiguration(@Nullable char[] keyPassword, @Nullable String keyAlias) {
            this.keyPassword = (char[])(keyPassword == null ? null : Arrays.copyOf(keyPassword, keyPassword.length));
            this.keyAlias = keyAlias;
        }

        public static KeyConfiguration unconfigured() {
            return UNCONFIGURED;
        }

        public static KeyConfiguration of(@Nullable char[] keyPassword, @Nullable String keyAlias) {
            return new KeyConfiguration(keyPassword, keyAlias);
        }

        @Nullable
        public char[] getKeyPassword() {
            return this.keyPassword;
        }

        @Nullable
        public String getKeyAlias() {
            return this.keyAlias;
        }
    }

    public static class KeyStoreConfiguration {
        private static final KeyStoreConfiguration UNCONFIGURED = new KeyStoreConfiguration(AbsentResource.INSTANCE, null, DEFAULT_KEYSTORE_TYPE);
        private final Resource resource;
        @Nullable
        private final char[] storePassword;
        private final String storeType;

        public KeyStoreConfiguration(Resource resource, @Nullable char[] storePassword, String storeType) {
            Assert.notNull((Object)resource, "Resource must not be null");
            Assert.isTrue(resource instanceof AbsentResource || resource.exists(), () -> String.format("Resource %s does not exist", resource));
            Assert.notNull((Object)storeType, "Keystore type must not be null");
            this.resource = resource;
            this.storeType = storeType;
            this.storePassword = (char[])(storePassword == null ? null : Arrays.copyOf(storePassword, storePassword.length));
        }

        public static KeyStoreConfiguration of(Resource resource) {
            return new KeyStoreConfiguration(resource, null, DEFAULT_KEYSTORE_TYPE);
        }

        public static KeyStoreConfiguration of(Resource resource, @Nullable char[] storePassword) {
            return KeyStoreConfiguration.of(resource, storePassword, DEFAULT_KEYSTORE_TYPE);
        }

        public static KeyStoreConfiguration of(Resource resource, @Nullable char[] storePassword, String keyStoreType) {
            return new KeyStoreConfiguration(resource, storePassword, keyStoreType);
        }

        public static KeyStoreConfiguration unconfigured() {
            return UNCONFIGURED;
        }

        public boolean isPresent() {
            return !(this.resource instanceof AbsentResource);
        }

        public Resource getResource() {
            return this.resource;
        }

        @Nullable
        public char[] getStorePassword() {
            return this.storePassword;
        }

        public String getStoreType() {
            return this.storeType;
        }

        public KeyStoreConfiguration withStoreType(String storeType) {
            Assert.notNull((Object)storeType, "Key store type must not be null");
            return new KeyStoreConfiguration(this.resource, this.storePassword, storeType);
        }
    }
}

