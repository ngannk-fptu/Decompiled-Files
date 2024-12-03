/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelOption
 *  io.netty.handler.ssl.SslContextBuilder
 *  org.eclipse.jetty.client.HttpClient
 *  org.eclipse.jetty.util.ssl.SslContextFactory
 *  org.springframework.http.client.reactive.ClientHttpConnector
 *  org.springframework.http.client.reactive.JettyClientHttpConnector
 *  org.springframework.http.client.reactive.ReactorClientHttpConnector
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  reactor.netty.http.Http11SslContextSpec
 *  reactor.netty.http.client.HttpClient
 *  reactor.netty.tcp.SslProvider$ProtocolSslContextSpec
 */
package org.springframework.vault.client;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContextBuilder;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.JettyClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.vault.client.ClientHttpRequestFactoryFactory;
import org.springframework.vault.support.ClientOptions;
import org.springframework.vault.support.SslConfiguration;
import reactor.netty.http.Http11SslContextSpec;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.SslProvider;

public class ClientHttpConnectorFactory {
    private static final boolean REACTOR_NETTY_PRESENT = ClientHttpConnectorFactory.isPresent("reactor.netty.http.client.HttpClient");
    private static final boolean JETTY_PRESENT = ClientHttpConnectorFactory.isPresent("org.eclipse.jetty.client.HttpClient");

    private static boolean isPresent(String ... classNames) {
        for (String className : classNames) {
            if (ClassUtils.isPresent((String)className, (ClassLoader)ClientHttpConnectorFactory.class.getClassLoader())) continue;
            return false;
        }
        return true;
    }

    public static ClientHttpConnector create(ClientOptions options, SslConfiguration sslConfiguration) {
        Assert.notNull((Object)options, (String)"ClientOptions must not be null");
        Assert.notNull((Object)sslConfiguration, (String)"SslConfiguration must not be null");
        try {
            if (REACTOR_NETTY_PRESENT) {
                return ReactorNetty.usingReactorNetty(options, sslConfiguration);
            }
            if (JETTY_PRESENT) {
                return JettyClient.usingJetty(options, sslConfiguration);
            }
        }
        catch (IOException | GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
        throw new IllegalStateException("No supported Reactive Http Client library available (Reactor Netty, Jetty)");
    }

    static class JettyClient {
        JettyClient() {
        }

        public static JettyClientHttpConnector usingJetty(ClientOptions options, SslConfiguration sslConfiguration) throws GeneralSecurityException, IOException {
            return new JettyClientHttpConnector(JettyClient.configureClient(JettyClient.getHttpClient(sslConfiguration), options));
        }

        public static org.eclipse.jetty.client.HttpClient configureClient(org.eclipse.jetty.client.HttpClient httpClient, ClientOptions options) {
            httpClient.setConnectTimeout(options.getConnectionTimeout().toMillis());
            httpClient.setAddressResolutionTimeout(options.getConnectionTimeout().toMillis());
            return httpClient;
        }

        public static org.eclipse.jetty.client.HttpClient getHttpClient(SslConfiguration sslConfiguration) throws IOException, GeneralSecurityException {
            if (ClientHttpRequestFactoryFactory.hasSslConfiguration(sslConfiguration)) {
                SslConfiguration.KeyConfiguration keyConfiguration;
                KeyStore keyStore;
                SslContextFactory sslContextFactory = new SslContextFactory();
                if (sslConfiguration.getKeyStoreConfiguration().isPresent()) {
                    keyStore = ClientHttpRequestFactoryFactory.getKeyStore(sslConfiguration.getKeyStoreConfiguration());
                    sslContextFactory.setKeyStore(keyStore);
                }
                if (sslConfiguration.getTrustStoreConfiguration().isPresent()) {
                    keyStore = ClientHttpRequestFactoryFactory.getKeyStore(sslConfiguration.getTrustStoreConfiguration());
                    sslContextFactory.setTrustStore(keyStore);
                }
                if ((keyConfiguration = sslConfiguration.getKeyConfiguration()).getKeyAlias() != null) {
                    sslContextFactory.setCertAlias(keyConfiguration.getKeyAlias());
                }
                if (keyConfiguration.getKeyPassword() != null) {
                    sslContextFactory.setKeyManagerPassword(new String(keyConfiguration.getKeyPassword()));
                }
                if (!sslConfiguration.getEnabledProtocols().isEmpty()) {
                    sslContextFactory.setIncludeProtocols(sslConfiguration.getEnabledProtocols().toArray(new String[0]));
                }
                if (!sslConfiguration.getEnabledCipherSuites().isEmpty()) {
                    sslContextFactory.setIncludeCipherSuites(sslConfiguration.getEnabledCipherSuites().toArray(new String[0]));
                }
                return new org.eclipse.jetty.client.HttpClient(sslContextFactory);
            }
            return new org.eclipse.jetty.client.HttpClient();
        }
    }

    public static class ReactorNetty {
        public static ReactorClientHttpConnector usingReactorNetty(ClientOptions options, SslConfiguration sslConfiguration) {
            return new ReactorClientHttpConnector(ReactorNetty.createClient(options, sslConfiguration));
        }

        public static HttpClient createClient(ClientOptions options, SslConfiguration sslConfiguration) {
            HttpClient client = HttpClient.create();
            if (ClientHttpRequestFactoryFactory.hasSslConfiguration(sslConfiguration)) {
                Http11SslContextSpec sslContextSpec = ((Http11SslContextSpec)Http11SslContextSpec.forClient().configure(it -> ReactorNetty.configureSsl(sslConfiguration, it))).get();
                client = client.secure(builder -> builder.sslContext((SslProvider.ProtocolSslContextSpec)sslContextSpec));
            }
            client = (HttpClient)((HttpClient)client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (Object)Math.toIntExact(options.getConnectionTimeout().toMillis()))).proxyWithSystemProperties();
            return client;
        }

        private static void configureSsl(SslConfiguration sslConfiguration, SslContextBuilder sslContextBuilder) {
            try {
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
            }
            catch (IOException | GeneralSecurityException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}

