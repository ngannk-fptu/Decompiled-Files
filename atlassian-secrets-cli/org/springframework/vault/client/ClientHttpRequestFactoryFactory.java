/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  okhttp3.ConnectionSpec
 *  okhttp3.ConnectionSpec$Builder
 *  okhttp3.OkHttpClient$Builder
 *  org.apache.http.client.HttpClient
 *  org.apache.http.client.RedirectStrategy
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.conn.SchemePortResolver
 *  org.apache.http.conn.routing.HttpRoutePlanner
 *  org.apache.http.conn.socket.LayeredConnectionSocketFactory
 *  org.apache.http.conn.ssl.SSLConnectionSocketFactory
 *  org.apache.http.impl.client.HttpClientBuilder
 *  org.apache.http.impl.client.HttpClients
 *  org.apache.http.impl.client.LaxRedirectStrategy
 *  org.apache.http.impl.conn.DefaultSchemePortResolver
 *  org.apache.http.impl.conn.SystemDefaultRoutePlanner
 */
package org.springframework.vault.client;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import java.io.IOException;
import java.io.InputStream;
import java.net.ProxySelector;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyManagerFactorySpi;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509TrustManager;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.vault.support.ClientOptions;
import org.springframework.vault.support.PemObject;
import org.springframework.vault.support.SslConfiguration;

public class ClientHttpRequestFactoryFactory {
    private static Log logger = LogFactory.getLog(ClientHttpRequestFactoryFactory.class);
    private static final boolean HTTP_COMPONENTS_PRESENT = ClientHttpRequestFactoryFactory.isPresent("org.apache.http.client.HttpClient");
    private static final boolean OKHTTP3_PRESENT = ClientHttpRequestFactoryFactory.isPresent("okhttp3.OkHttpClient");
    private static final boolean NETTY_PRESENT = ClientHttpRequestFactoryFactory.isPresent("io.netty.channel.nio.NioEventLoopGroup", "io.netty.handler.ssl.SslContext", "io.netty.handler.codec.http.HttpClientCodec");

    private static boolean isPresent(String ... classNames) {
        for (String className : classNames) {
            if (ClassUtils.isPresent(className, ClientHttpRequestFactoryFactory.class.getClassLoader())) continue;
            return false;
        }
        return true;
    }

    public static ClientHttpRequestFactory create(ClientOptions options, SslConfiguration sslConfiguration) {
        Assert.notNull((Object)options, "ClientOptions must not be null");
        Assert.notNull((Object)sslConfiguration, "SslConfiguration must not be null");
        try {
            if (HTTP_COMPONENTS_PRESENT) {
                return HttpComponents.usingHttpComponents(options, sslConfiguration);
            }
            if (OKHTTP3_PRESENT) {
                return OkHttp3.usingOkHttp3(options, sslConfiguration);
            }
            if (NETTY_PRESENT) {
                return Netty.usingNetty(options, sslConfiguration);
            }
        }
        catch (IOException | GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
        if (ClientHttpRequestFactoryFactory.hasSslConfiguration(sslConfiguration)) {
            logger.warn("VaultProperties has SSL configured but the SSL configuration must be applied outside the Vault Client to use the JDK HTTP client");
        }
        return new SimpleClientHttpRequestFactory();
    }

    private static SSLContext getSSLContext(SslConfiguration sslConfiguration) throws GeneralSecurityException, IOException {
        return ClientHttpRequestFactoryFactory.getSSLContext(sslConfiguration.getKeyStoreConfiguration(), sslConfiguration.getKeyConfiguration(), ClientHttpRequestFactoryFactory.getTrustManagers(sslConfiguration));
    }

    static SSLContext getSSLContext(SslConfiguration.KeyStoreConfiguration keyStoreConfiguration, SslConfiguration.KeyConfiguration keyConfiguration, @Nullable TrustManager[] trustManagers) throws GeneralSecurityException, IOException {
        KeyManager[] keyManagers = keyStoreConfiguration.isPresent() ? ClientHttpRequestFactoryFactory.createKeyManagerFactory(keyStoreConfiguration, keyConfiguration).getKeyManagers() : null;
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, null);
        return sslContext;
    }

    @Nullable
    static TrustManager[] getTrustManagers(SslConfiguration sslConfiguration) throws GeneralSecurityException, IOException {
        return sslConfiguration.getTrustStoreConfiguration().isPresent() ? ClientHttpRequestFactoryFactory.createTrustManagerFactory(sslConfiguration.getTrustStoreConfiguration()).getTrustManagers() : null;
    }

    static KeyManagerFactory createKeyManagerFactory(SslConfiguration.KeyStoreConfiguration keyStoreConfiguration, SslConfiguration.KeyConfiguration keyConfiguration) throws GeneralSecurityException, IOException {
        KeyStore keyStore = ClientHttpRequestFactoryFactory.getKeyStore(keyStoreConfiguration);
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        char[] keyPasswordToUse = keyConfiguration.getKeyPassword();
        if (keyPasswordToUse == null) {
            keyPasswordToUse = keyStoreConfiguration.getStorePassword() == null ? new char[]{} : keyStoreConfiguration.getStorePassword();
        }
        keyManagerFactory.init(keyStore, keyPasswordToUse);
        if (StringUtils.hasText(keyConfiguration.getKeyAlias())) {
            return new KeySelectingKeyManagerFactory(keyManagerFactory, keyConfiguration);
        }
        return keyManagerFactory;
    }

    static KeyStore getKeyStore(SslConfiguration.KeyStoreConfiguration keyStoreConfiguration) throws IOException, GeneralSecurityException {
        KeyStore keyStore = KeyStore.getInstance(ClientHttpRequestFactoryFactory.getKeyStoreType(keyStoreConfiguration));
        ClientHttpRequestFactoryFactory.loadKeyStore(keyStoreConfiguration, keyStore);
        return keyStore;
    }

    private static String getKeyStoreType(SslConfiguration.KeyStoreConfiguration keyStoreConfiguration) {
        if (StringUtils.hasText(keyStoreConfiguration.getStoreType()) && !"PEM".equalsIgnoreCase(keyStoreConfiguration.getStoreType())) {
            return keyStoreConfiguration.getStoreType();
        }
        return KeyStore.getDefaultType();
    }

    static TrustManagerFactory createTrustManagerFactory(SslConfiguration.KeyStoreConfiguration keyStoreConfiguration) throws GeneralSecurityException, IOException {
        KeyStore trustStore = ClientHttpRequestFactoryFactory.getKeyStore(keyStoreConfiguration);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
        return trustManagerFactory;
    }

    private static void loadKeyStore(SslConfiguration.KeyStoreConfiguration keyStoreConfiguration, KeyStore keyStore) throws IOException, GeneralSecurityException {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Loading keystore from %s", keyStoreConfiguration.getResource()));
        }
        try (InputStream inputStream = null;){
            inputStream = keyStoreConfiguration.getResource().getInputStream();
            if ("PEM".equalsIgnoreCase(keyStoreConfiguration.getStoreType())) {
                keyStore.load(null);
                ClientHttpRequestFactoryFactory.loadFromPem(keyStore, inputStream);
            } else {
                keyStore.load(inputStream, keyStoreConfiguration.getStorePassword());
            }
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Keystore loaded with %d entries", keyStore.size()));
            }
        }
    }

    private static void loadFromPem(KeyStore keyStore, InputStream inputStream) throws IOException, KeyStoreException {
        List<PemObject> pemObjects = PemObject.parse(new String(FileCopyUtils.copyToByteArray(inputStream)));
        for (PemObject pemObject : pemObjects) {
            if (!pemObject.isCertificate()) continue;
            X509Certificate cert = pemObject.getCertificate();
            String alias = cert.getSubjectX500Principal().getName();
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Adding certificate with alias %s", alias));
            }
            keyStore.setCertificateEntry(alias, cert);
        }
    }

    static boolean hasSslConfiguration(SslConfiguration sslConfiguration) {
        return sslConfiguration.getTrustStoreConfiguration().isPresent() || sslConfiguration.getKeyStoreConfiguration().isPresent();
    }

    private static class KeySelectingX509KeyManager
    extends X509ExtendedKeyManager {
        private final X509ExtendedKeyManager delegate;
        private final SslConfiguration.KeyConfiguration keyConfiguration;

        KeySelectingX509KeyManager(X509ExtendedKeyManager delegate, SslConfiguration.KeyConfiguration keyConfiguration) {
            this.delegate = delegate;
            this.keyConfiguration = keyConfiguration;
        }

        @Override
        public String[] getClientAliases(String keyType, Principal[] issuers) {
            return this.delegate.getClientAliases(keyType, issuers);
        }

        @Override
        public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
            return this.keyConfiguration.getKeyAlias();
        }

        @Override
        public String chooseEngineClientAlias(String[] keyType, Principal[] issuers, SSLEngine engine) {
            return this.keyConfiguration.getKeyAlias();
        }

        @Override
        public String[] getServerAliases(String keyType, Principal[] issuers) {
            return this.delegate.getServerAliases(keyType, issuers);
        }

        @Override
        public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
            return this.delegate.chooseServerAlias(keyType, issuers, socket);
        }

        @Override
        public X509Certificate[] getCertificateChain(String alias) {
            return this.delegate.getCertificateChain(alias);
        }

        @Override
        public PrivateKey getPrivateKey(String alias) {
            return this.delegate.getPrivateKey(alias);
        }
    }

    static class KeySelectingKeyManagerFactory
    extends KeyManagerFactory {
        KeySelectingKeyManagerFactory(final KeyManagerFactory factory, final SslConfiguration.KeyConfiguration keyConfiguration) {
            super(new KeyManagerFactorySpi(){

                @Override
                protected void engineInit(KeyStore keyStore, char[] chars) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
                    factory.init(keyStore, chars);
                }

                @Override
                protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException {
                    factory.init(managerFactoryParameters);
                }

                @Override
                protected KeyManager[] engineGetKeyManagers() {
                    KeyManager[] keyManagers = factory.getKeyManagers();
                    if (keyManagers.length == 1 && keyManagers[0] instanceof X509ExtendedKeyManager) {
                        return new KeyManager[]{new KeySelectingX509KeyManager((X509ExtendedKeyManager)keyManagers[0], keyConfiguration)};
                    }
                    return keyManagers;
                }
            }, factory.getProvider(), factory.getAlgorithm());
        }
    }

    static class Netty {
        Netty() {
        }

        static ClientHttpRequestFactory usingNetty(ClientOptions options, SslConfiguration sslConfiguration) throws GeneralSecurityException, IOException {
            Netty4ClientHttpRequestFactory requestFactory = new Netty4ClientHttpRequestFactory();
            if (ClientHttpRequestFactoryFactory.hasSslConfiguration(sslConfiguration)) {
                SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();
                if (sslConfiguration.getTrustStoreConfiguration().isPresent()) {
                    sslContextBuilder.trustManager(ClientHttpRequestFactoryFactory.createTrustManagerFactory(sslConfiguration.getTrustStoreConfiguration()));
                }
                if (sslConfiguration.getKeyStoreConfiguration().isPresent()) {
                    sslContextBuilder.keyManager(ClientHttpRequestFactoryFactory.createKeyManagerFactory(sslConfiguration.getKeyStoreConfiguration(), sslConfiguration.getKeyConfiguration()));
                }
                if (!sslConfiguration.getEnabledProtocols().isEmpty()) {
                    sslContextBuilder.protocols(sslConfiguration.getEnabledProtocols());
                }
                if (!sslConfiguration.getEnabledCipherSuites().isEmpty()) {
                    sslContextBuilder.ciphers(sslConfiguration.getEnabledCipherSuites());
                }
                requestFactory.setSslContext(sslContextBuilder.sslProvider(SslProvider.JDK).build());
            }
            requestFactory.setConnectTimeout(Math.toIntExact(options.getConnectionTimeout().toMillis()));
            requestFactory.setReadTimeout(Math.toIntExact(options.getReadTimeout().toMillis()));
            requestFactory.afterPropertiesSet();
            return requestFactory;
        }
    }

    public static class OkHttp3 {
        public static OkHttp3ClientHttpRequestFactory usingOkHttp3(ClientOptions options, SslConfiguration sslConfiguration) throws GeneralSecurityException, IOException {
            OkHttpClient.Builder builder = OkHttp3.getBuilder(options, sslConfiguration);
            return new OkHttp3ClientHttpRequestFactory(builder.build());
        }

        public static OkHttpClient.Builder getBuilder(ClientOptions options, SslConfiguration sslConfiguration) throws GeneralSecurityException, IOException {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            ConnectionSpec sslConnectionSpec = ConnectionSpec.MODERN_TLS;
            if (ClientHttpRequestFactoryFactory.hasSslConfiguration(sslConfiguration)) {
                Object[] trustManagers = ClientHttpRequestFactoryFactory.getTrustManagers(sslConfiguration);
                if (trustManagers == null || trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                    throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
                }
                SSLContext sslContext = ClientHttpRequestFactoryFactory.getSSLContext(sslConfiguration.getKeyStoreConfiguration(), sslConfiguration.getKeyConfiguration(), (TrustManager[])trustManagers);
                ConnectionSpec.Builder sslConnectionSpecBuilder = new ConnectionSpec.Builder(sslConnectionSpec);
                if (!sslConfiguration.getEnabledProtocols().isEmpty()) {
                    sslConnectionSpecBuilder.tlsVersions(sslConfiguration.getEnabledProtocols().toArray(new String[0]));
                }
                if (!sslConfiguration.getEnabledCipherSuites().isEmpty()) {
                    sslConnectionSpecBuilder.cipherSuites(sslConfiguration.getEnabledCipherSuites().toArray(new String[0]));
                }
                sslConnectionSpec = sslConnectionSpecBuilder.build();
                builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager)trustManagers[0]);
            }
            builder.connectionSpecs(Arrays.asList(sslConnectionSpec, ConnectionSpec.CLEARTEXT));
            builder.connectTimeout(options.getConnectionTimeout().toMillis(), TimeUnit.MILLISECONDS).readTimeout(options.getReadTimeout().toMillis(), TimeUnit.MILLISECONDS);
            return builder;
        }
    }

    public static class HttpComponents {
        public static HttpComponentsClientHttpRequestFactory usingHttpComponents(ClientOptions options, SslConfiguration sslConfiguration) throws GeneralSecurityException, IOException {
            HttpClientBuilder httpClientBuilder = HttpComponents.getHttpClientBuilder(options, sslConfiguration);
            return new HttpComponentsClientHttpRequestFactory((HttpClient)httpClientBuilder.build());
        }

        public static HttpClientBuilder getHttpClientBuilder(ClientOptions options, SslConfiguration sslConfiguration) throws GeneralSecurityException, IOException {
            HttpClientBuilder httpClientBuilder = HttpClients.custom();
            httpClientBuilder.setRoutePlanner((HttpRoutePlanner)new SystemDefaultRoutePlanner((SchemePortResolver)DefaultSchemePortResolver.INSTANCE, ProxySelector.getDefault()));
            if (ClientHttpRequestFactoryFactory.hasSslConfiguration(sslConfiguration)) {
                SSLContext sslContext = ClientHttpRequestFactoryFactory.getSSLContext(sslConfiguration);
                String[] enabledProtocols = null;
                if (!sslConfiguration.getEnabledProtocols().isEmpty()) {
                    enabledProtocols = sslConfiguration.getEnabledProtocols().toArray(new String[0]);
                }
                String[] enabledCipherSuites = null;
                if (!sslConfiguration.getEnabledCipherSuites().isEmpty()) {
                    enabledCipherSuites = sslConfiguration.getEnabledCipherSuites().toArray(new String[0]);
                }
                SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, enabledProtocols, enabledCipherSuites, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
                httpClientBuilder.setSSLSocketFactory((LayeredConnectionSocketFactory)sslSocketFactory);
                httpClientBuilder.setSSLContext(sslContext);
            }
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(Math.toIntExact(options.getConnectionTimeout().toMillis())).setSocketTimeout(Math.toIntExact(options.getReadTimeout().toMillis())).setAuthenticationEnabled(true).build();
            httpClientBuilder.setDefaultRequestConfig(requestConfig);
            httpClientBuilder.setRedirectStrategy((RedirectStrategy)new LaxRedirectStrategy());
            return httpClientBuilder;
        }
    }
}

