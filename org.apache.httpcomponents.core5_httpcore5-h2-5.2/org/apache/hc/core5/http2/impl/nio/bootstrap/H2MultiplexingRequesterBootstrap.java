/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.function.Callback
 *  org.apache.hc.core5.function.Decorator
 *  org.apache.hc.core5.function.Resolver
 *  org.apache.hc.core5.function.Supplier
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.http.HttpRequestMapper
 *  org.apache.hc.core5.http.config.CharCodingConfig
 *  org.apache.hc.core5.http.impl.DefaultAddressResolver
 *  org.apache.hc.core5.http.nio.AsyncPushConsumer
 *  org.apache.hc.core5.http.nio.ssl.TlsStrategy
 *  org.apache.hc.core5.http.protocol.HttpProcessor
 *  org.apache.hc.core5.http.protocol.RequestHandlerRegistry
 *  org.apache.hc.core5.http.protocol.UriPatternType
 *  org.apache.hc.core5.reactor.IOReactorConfig
 *  org.apache.hc.core5.reactor.IOSession
 *  org.apache.hc.core5.reactor.IOSessionListener
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.core5.http2.impl.nio.bootstrap;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import org.apache.hc.core5.function.Callback;
import org.apache.hc.core5.function.Decorator;
import org.apache.hc.core5.function.Resolver;
import org.apache.hc.core5.function.Supplier;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequestMapper;
import org.apache.hc.core5.http.config.CharCodingConfig;
import org.apache.hc.core5.http.impl.DefaultAddressResolver;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.http.protocol.HttpProcessor;
import org.apache.hc.core5.http.protocol.RequestHandlerRegistry;
import org.apache.hc.core5.http.protocol.UriPatternType;
import org.apache.hc.core5.http2.config.H2Config;
import org.apache.hc.core5.http2.impl.H2Processors;
import org.apache.hc.core5.http2.impl.nio.ClientH2PrefaceHandler;
import org.apache.hc.core5.http2.impl.nio.ClientH2StreamMultiplexerFactory;
import org.apache.hc.core5.http2.impl.nio.H2StreamListener;
import org.apache.hc.core5.http2.impl.nio.bootstrap.H2MultiplexingRequester;
import org.apache.hc.core5.http2.impl.nio.bootstrap.HandlerEntry;
import org.apache.hc.core5.http2.nio.support.DefaultAsyncPushConsumerFactory;
import org.apache.hc.core5.http2.ssl.H2ClientTlsStrategy;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.reactor.IOSessionListener;
import org.apache.hc.core5.util.Args;

public class H2MultiplexingRequesterBootstrap {
    private final List<HandlerEntry<Supplier<AsyncPushConsumer>>> pushConsumerList = new ArrayList<HandlerEntry<Supplier<AsyncPushConsumer>>>();
    private UriPatternType uriPatternType;
    private IOReactorConfig ioReactorConfig;
    private HttpProcessor httpProcessor;
    private CharCodingConfig charCodingConfig;
    private H2Config h2Config;
    private TlsStrategy tlsStrategy;
    private boolean strictALPNHandshake;
    private Decorator<IOSession> ioSessionDecorator;
    private Callback<Exception> exceptionCallback;
    private IOSessionListener sessionListener;
    private H2StreamListener streamListener;

    private H2MultiplexingRequesterBootstrap() {
    }

    public static H2MultiplexingRequesterBootstrap bootstrap() {
        return new H2MultiplexingRequesterBootstrap();
    }

    public final H2MultiplexingRequesterBootstrap setIOReactorConfig(IOReactorConfig ioReactorConfig) {
        this.ioReactorConfig = ioReactorConfig;
        return this;
    }

    public final H2MultiplexingRequesterBootstrap setHttpProcessor(HttpProcessor httpProcessor) {
        this.httpProcessor = httpProcessor;
        return this;
    }

    public final H2MultiplexingRequesterBootstrap setH2Config(H2Config h2Config) {
        this.h2Config = h2Config;
        return this;
    }

    public final H2MultiplexingRequesterBootstrap setCharCodingConfig(CharCodingConfig charCodingConfig) {
        this.charCodingConfig = charCodingConfig;
        return this;
    }

    public final H2MultiplexingRequesterBootstrap setTlsStrategy(TlsStrategy tlsStrategy) {
        this.tlsStrategy = tlsStrategy;
        return this;
    }

    public final H2MultiplexingRequesterBootstrap setStrictALPNHandshake(boolean strictALPNHandshake) {
        this.strictALPNHandshake = strictALPNHandshake;
        return this;
    }

    public final H2MultiplexingRequesterBootstrap setIOSessionDecorator(Decorator<IOSession> ioSessionDecorator) {
        this.ioSessionDecorator = ioSessionDecorator;
        return this;
    }

    public final H2MultiplexingRequesterBootstrap setExceptionCallback(Callback<Exception> exceptionCallback) {
        this.exceptionCallback = exceptionCallback;
        return this;
    }

    public final H2MultiplexingRequesterBootstrap setIOSessionListener(IOSessionListener sessionListener) {
        this.sessionListener = sessionListener;
        return this;
    }

    public final H2MultiplexingRequesterBootstrap setStreamListener(H2StreamListener streamListener) {
        this.streamListener = streamListener;
        return this;
    }

    public final H2MultiplexingRequesterBootstrap setUriPatternType(UriPatternType uriPatternType) {
        this.uriPatternType = uriPatternType;
        return this;
    }

    public final H2MultiplexingRequesterBootstrap register(String uriPattern, Supplier<AsyncPushConsumer> supplier) {
        Args.notBlank((CharSequence)uriPattern, (String)"URI pattern");
        Args.notNull(supplier, (String)"Supplier");
        this.pushConsumerList.add(new HandlerEntry<Supplier<AsyncPushConsumer>>(null, uriPattern, supplier));
        return this;
    }

    public final H2MultiplexingRequesterBootstrap registerVirtual(String hostname, String uriPattern, Supplier<AsyncPushConsumer> supplier) {
        Args.notBlank((CharSequence)hostname, (String)"Hostname");
        Args.notBlank((CharSequence)uriPattern, (String)"URI pattern");
        Args.notNull(supplier, (String)"Supplier");
        this.pushConsumerList.add(new HandlerEntry<Supplier<AsyncPushConsumer>>(hostname, uriPattern, supplier));
        return this;
    }

    public H2MultiplexingRequester create() {
        RequestHandlerRegistry registry = new RequestHandlerRegistry(this.uriPatternType);
        for (HandlerEntry<Supplier<AsyncPushConsumer>> entry : this.pushConsumerList) {
            registry.register(entry.hostname, entry.uriPattern, entry.handler);
        }
        ClientH2StreamMultiplexerFactory http2StreamHandlerFactory = new ClientH2StreamMultiplexerFactory(this.httpProcessor != null ? this.httpProcessor : H2Processors.client(), new DefaultAsyncPushConsumerFactory((HttpRequestMapper<Supplier<AsyncPushConsumer>>)registry), this.h2Config != null ? this.h2Config : H2Config.DEFAULT, this.charCodingConfig != null ? this.charCodingConfig : CharCodingConfig.DEFAULT, this.streamListener);
        return new H2MultiplexingRequester(this.ioReactorConfig, (ioSession, attachment) -> new ClientH2PrefaceHandler(ioSession, http2StreamHandlerFactory, this.strictALPNHandshake), this.ioSessionDecorator, this.exceptionCallback, this.sessionListener, (Resolver<HttpHost, InetSocketAddress>)DefaultAddressResolver.INSTANCE, (TlsStrategy)(this.tlsStrategy != null ? this.tlsStrategy : new H2ClientTlsStrategy()));
    }
}

