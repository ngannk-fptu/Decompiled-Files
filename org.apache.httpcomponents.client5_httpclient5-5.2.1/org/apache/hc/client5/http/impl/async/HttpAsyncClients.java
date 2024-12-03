/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.concurrent.DefaultThreadFactory
 *  org.apache.hc.core5.http.ConnectionReuseStrategy
 *  org.apache.hc.core5.http.HttpRequestInterceptor
 *  org.apache.hc.core5.http.config.CharCodingConfig
 *  org.apache.hc.core5.http.config.Http1Config
 *  org.apache.hc.core5.http.nio.AsyncPushConsumer
 *  org.apache.hc.core5.http.nio.HandlerFactory
 *  org.apache.hc.core5.http.nio.ssl.TlsStrategy
 *  org.apache.hc.core5.http.protocol.DefaultHttpProcessor
 *  org.apache.hc.core5.http.protocol.HttpProcessor
 *  org.apache.hc.core5.http.protocol.RequestUserAgent
 *  org.apache.hc.core5.http2.HttpVersionPolicy
 *  org.apache.hc.core5.http2.config.H2Config
 *  org.apache.hc.core5.http2.protocol.H2RequestConnControl
 *  org.apache.hc.core5.http2.protocol.H2RequestContent
 *  org.apache.hc.core5.http2.protocol.H2RequestTargetHost
 *  org.apache.hc.core5.reactor.IOEventHandlerFactory
 *  org.apache.hc.core5.reactor.IOReactorConfig
 *  org.apache.hc.core5.util.VersionInfo
 */
package org.apache.hc.client5.http.impl.async;

import java.util.concurrent.ThreadFactory;
import org.apache.hc.client5.http.DnsResolver;
import org.apache.hc.client5.http.SchemePortResolver;
import org.apache.hc.client5.http.SystemDefaultDnsResolver;
import org.apache.hc.client5.http.config.TlsConfig;
import org.apache.hc.client5.http.impl.DefaultClientConnectionReuseStrategy;
import org.apache.hc.client5.http.impl.DefaultSchemePortResolver;
import org.apache.hc.client5.http.impl.async.AsyncPushConsumerRegistry;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.H2AsyncClientBuilder;
import org.apache.hc.client5.http.impl.async.H2AsyncClientProtocolStarter;
import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder;
import org.apache.hc.client5.http.impl.async.HttpAsyncClientProtocolNegotiationStarter;
import org.apache.hc.client5.http.impl.async.MinimalH2AsyncClient;
import org.apache.hc.client5.http.impl.async.MinimalHttpAsyncClient;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.nio.AsyncClientConnectionManager;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.core5.concurrent.DefaultThreadFactory;
import org.apache.hc.core5.http.ConnectionReuseStrategy;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.config.CharCodingConfig;
import org.apache.hc.core5.http.config.Http1Config;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.http.protocol.DefaultHttpProcessor;
import org.apache.hc.core5.http.protocol.HttpProcessor;
import org.apache.hc.core5.http.protocol.RequestUserAgent;
import org.apache.hc.core5.http2.HttpVersionPolicy;
import org.apache.hc.core5.http2.config.H2Config;
import org.apache.hc.core5.http2.protocol.H2RequestConnControl;
import org.apache.hc.core5.http2.protocol.H2RequestContent;
import org.apache.hc.core5.http2.protocol.H2RequestTargetHost;
import org.apache.hc.core5.reactor.IOEventHandlerFactory;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.VersionInfo;

public final class HttpAsyncClients {
    private HttpAsyncClients() {
    }

    public static HttpAsyncClientBuilder custom() {
        return HttpAsyncClientBuilder.create();
    }

    public static CloseableHttpAsyncClient createDefault() {
        return HttpAsyncClientBuilder.create().build();
    }

    public static CloseableHttpAsyncClient createSystem() {
        return HttpAsyncClientBuilder.create().useSystemProperties().build();
    }

    public static H2AsyncClientBuilder customHttp2() {
        return H2AsyncClientBuilder.create();
    }

    public static CloseableHttpAsyncClient createHttp2Default() {
        return H2AsyncClientBuilder.create().build();
    }

    public static CloseableHttpAsyncClient createHttp2System() {
        return H2AsyncClientBuilder.create().useSystemProperties().build();
    }

    private static HttpProcessor createMinimalProtocolProcessor() {
        return new DefaultHttpProcessor(new HttpRequestInterceptor[]{new H2RequestContent(), new H2RequestTargetHost(), new H2RequestConnControl(), new RequestUserAgent(VersionInfo.getSoftwareInfo((String)"Apache-HttpAsyncClient", (String)"org.apache.hc.client5", HttpAsyncClients.class))});
    }

    private static MinimalHttpAsyncClient createMinimalHttpAsyncClientImpl(IOEventHandlerFactory eventHandlerFactory, AsyncPushConsumerRegistry pushConsumerRegistry, IOReactorConfig ioReactorConfig, AsyncClientConnectionManager connmgr, SchemePortResolver schemePortResolver, TlsConfig tlsConfig) {
        return new MinimalHttpAsyncClient(eventHandlerFactory, pushConsumerRegistry, ioReactorConfig, (ThreadFactory)new DefaultThreadFactory("httpclient-main", true), (ThreadFactory)new DefaultThreadFactory("httpclient-dispatch", true), connmgr, schemePortResolver, tlsConfig);
    }

    @Deprecated
    public static MinimalHttpAsyncClient createMinimal(HttpVersionPolicy versionPolicy, H2Config h2Config, Http1Config h1Config, IOReactorConfig ioReactorConfig, AsyncClientConnectionManager connmgr) {
        AsyncPushConsumerRegistry pushConsumerRegistry = new AsyncPushConsumerRegistry();
        return HttpAsyncClients.createMinimalHttpAsyncClientImpl(new HttpAsyncClientProtocolNegotiationStarter(HttpAsyncClients.createMinimalProtocolProcessor(), (HandlerFactory<AsyncPushConsumer>)((HandlerFactory)(request, context) -> pushConsumerRegistry.get(request)), h2Config, h1Config, CharCodingConfig.DEFAULT, (ConnectionReuseStrategy)DefaultClientConnectionReuseStrategy.INSTANCE), pushConsumerRegistry, ioReactorConfig, connmgr, DefaultSchemePortResolver.INSTANCE, versionPolicy != null ? TlsConfig.custom().setVersionPolicy(versionPolicy).build() : null);
    }

    public static MinimalHttpAsyncClient createMinimal(H2Config h2Config, Http1Config h1Config, IOReactorConfig ioReactorConfig, AsyncClientConnectionManager connmgr) {
        AsyncPushConsumerRegistry pushConsumerRegistry = new AsyncPushConsumerRegistry();
        return HttpAsyncClients.createMinimalHttpAsyncClientImpl(new HttpAsyncClientProtocolNegotiationStarter(HttpAsyncClients.createMinimalProtocolProcessor(), (HandlerFactory<AsyncPushConsumer>)((HandlerFactory)(request, context) -> pushConsumerRegistry.get(request)), h2Config, h1Config, CharCodingConfig.DEFAULT, (ConnectionReuseStrategy)DefaultClientConnectionReuseStrategy.INSTANCE), pushConsumerRegistry, ioReactorConfig, connmgr, DefaultSchemePortResolver.INSTANCE, null);
    }

    @Deprecated
    public static MinimalHttpAsyncClient createMinimal(HttpVersionPolicy versionPolicy, H2Config h2Config, Http1Config h1Config, IOReactorConfig ioReactorConfig) {
        return HttpAsyncClients.createMinimal(versionPolicy, h2Config, h1Config, ioReactorConfig, PoolingAsyncClientConnectionManagerBuilder.create().build());
    }

    public static MinimalHttpAsyncClient createMinimal(H2Config h2Config, Http1Config h1Config, IOReactorConfig ioReactorConfig) {
        return HttpAsyncClients.createMinimal(h2Config, h1Config, ioReactorConfig, PoolingAsyncClientConnectionManagerBuilder.create().build());
    }

    public static MinimalHttpAsyncClient createMinimal(H2Config h2Config, Http1Config h1Config) {
        return HttpAsyncClients.createMinimal(HttpVersionPolicy.NEGOTIATE, h2Config, h1Config, IOReactorConfig.DEFAULT);
    }

    public static MinimalHttpAsyncClient createMinimal() {
        return HttpAsyncClients.createMinimal(H2Config.DEFAULT, Http1Config.DEFAULT);
    }

    public static MinimalHttpAsyncClient createMinimal(AsyncClientConnectionManager connManager) {
        return HttpAsyncClients.createMinimal(HttpVersionPolicy.NEGOTIATE, H2Config.DEFAULT, Http1Config.DEFAULT, IOReactorConfig.DEFAULT, connManager);
    }

    private static MinimalH2AsyncClient createMinimalHttp2AsyncClientImpl(IOEventHandlerFactory eventHandlerFactory, AsyncPushConsumerRegistry pushConsumerRegistry, IOReactorConfig ioReactorConfig, DnsResolver dnsResolver, TlsStrategy tlsStrategy) {
        return new MinimalH2AsyncClient(eventHandlerFactory, pushConsumerRegistry, ioReactorConfig, (ThreadFactory)new DefaultThreadFactory("httpclient-main", true), (ThreadFactory)new DefaultThreadFactory("httpclient-dispatch", true), dnsResolver, tlsStrategy);
    }

    public static MinimalH2AsyncClient createHttp2Minimal(H2Config h2Config, IOReactorConfig ioReactorConfig, DnsResolver dnsResolver, TlsStrategy tlsStrategy) {
        AsyncPushConsumerRegistry pushConsumerRegistry = new AsyncPushConsumerRegistry();
        return HttpAsyncClients.createMinimalHttp2AsyncClientImpl(new H2AsyncClientProtocolStarter(HttpAsyncClients.createMinimalProtocolProcessor(), (HandlerFactory<AsyncPushConsumer>)((HandlerFactory)(request, context) -> pushConsumerRegistry.get(request)), h2Config, CharCodingConfig.DEFAULT), pushConsumerRegistry, ioReactorConfig, dnsResolver, tlsStrategy);
    }

    public static MinimalH2AsyncClient createHttp2Minimal(H2Config h2Config, IOReactorConfig ioReactorConfig, TlsStrategy tlsStrategy) {
        return HttpAsyncClients.createHttp2Minimal(h2Config, ioReactorConfig, SystemDefaultDnsResolver.INSTANCE, tlsStrategy);
    }

    public static MinimalH2AsyncClient createHttp2Minimal(H2Config h2Config, IOReactorConfig ioReactorConfig) {
        return HttpAsyncClients.createHttp2Minimal(h2Config, ioReactorConfig, DefaultClientTlsStrategy.getDefault());
    }

    public static MinimalH2AsyncClient createHttp2Minimal(H2Config h2Config) {
        return HttpAsyncClients.createHttp2Minimal(h2Config, IOReactorConfig.DEFAULT);
    }

    public static MinimalH2AsyncClient createHttp2Minimal() {
        return HttpAsyncClients.createHttp2Minimal(H2Config.DEFAULT);
    }
}

