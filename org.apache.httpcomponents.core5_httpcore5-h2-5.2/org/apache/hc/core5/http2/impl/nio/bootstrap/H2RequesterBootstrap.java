/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Experimental
 *  org.apache.hc.core5.function.Callback
 *  org.apache.hc.core5.function.Decorator
 *  org.apache.hc.core5.function.Supplier
 *  org.apache.hc.core5.http.ConnectionReuseStrategy
 *  org.apache.hc.core5.http.ContentLengthStrategy
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.http.HttpRequestMapper
 *  org.apache.hc.core5.http.config.CharCodingConfig
 *  org.apache.hc.core5.http.config.Http1Config
 *  org.apache.hc.core5.http.impl.DefaultConnectionReuseStrategy
 *  org.apache.hc.core5.http.impl.DefaultContentLengthStrategy
 *  org.apache.hc.core5.http.impl.Http1StreamListener
 *  org.apache.hc.core5.http.impl.HttpProcessors
 *  org.apache.hc.core5.http.impl.nio.ClientHttp1StreamDuplexerFactory
 *  org.apache.hc.core5.http.impl.nio.DefaultHttpRequestWriterFactory
 *  org.apache.hc.core5.http.impl.nio.DefaultHttpResponseParserFactory
 *  org.apache.hc.core5.http.nio.AsyncPushConsumer
 *  org.apache.hc.core5.http.nio.NHttpMessageParserFactory
 *  org.apache.hc.core5.http.nio.NHttpMessageWriterFactory
 *  org.apache.hc.core5.http.nio.ssl.TlsStrategy
 *  org.apache.hc.core5.http.protocol.HttpProcessor
 *  org.apache.hc.core5.http.protocol.RequestHandlerRegistry
 *  org.apache.hc.core5.http.protocol.UriPatternType
 *  org.apache.hc.core5.pool.ConnPoolListener
 *  org.apache.hc.core5.pool.DefaultDisposalCallback
 *  org.apache.hc.core5.pool.DisposalCallback
 *  org.apache.hc.core5.pool.LaxConnPool
 *  org.apache.hc.core5.pool.ManagedConnPool
 *  org.apache.hc.core5.pool.PoolConcurrencyPolicy
 *  org.apache.hc.core5.pool.PoolReusePolicy
 *  org.apache.hc.core5.pool.StrictConnPool
 *  org.apache.hc.core5.reactor.IOReactorConfig
 *  org.apache.hc.core5.reactor.IOSession
 *  org.apache.hc.core5.reactor.IOSessionListener
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.TimeValue
 *  org.apache.hc.core5.util.Timeout
 */
package org.apache.hc.core5.http2.impl.nio.bootstrap;

import java.util.ArrayList;
import java.util.List;
import org.apache.hc.core5.annotation.Experimental;
import org.apache.hc.core5.function.Callback;
import org.apache.hc.core5.function.Decorator;
import org.apache.hc.core5.function.Supplier;
import org.apache.hc.core5.http.ConnectionReuseStrategy;
import org.apache.hc.core5.http.ContentLengthStrategy;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequestMapper;
import org.apache.hc.core5.http.config.CharCodingConfig;
import org.apache.hc.core5.http.config.Http1Config;
import org.apache.hc.core5.http.impl.DefaultConnectionReuseStrategy;
import org.apache.hc.core5.http.impl.DefaultContentLengthStrategy;
import org.apache.hc.core5.http.impl.Http1StreamListener;
import org.apache.hc.core5.http.impl.HttpProcessors;
import org.apache.hc.core5.http.impl.nio.ClientHttp1StreamDuplexerFactory;
import org.apache.hc.core5.http.impl.nio.DefaultHttpRequestWriterFactory;
import org.apache.hc.core5.http.impl.nio.DefaultHttpResponseParserFactory;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.NHttpMessageParserFactory;
import org.apache.hc.core5.http.nio.NHttpMessageWriterFactory;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.http.protocol.HttpProcessor;
import org.apache.hc.core5.http.protocol.RequestHandlerRegistry;
import org.apache.hc.core5.http.protocol.UriPatternType;
import org.apache.hc.core5.http2.HttpVersionPolicy;
import org.apache.hc.core5.http2.config.H2Config;
import org.apache.hc.core5.http2.impl.H2Processors;
import org.apache.hc.core5.http2.impl.nio.ClientH2StreamMultiplexerFactory;
import org.apache.hc.core5.http2.impl.nio.ClientHttpProtocolNegotiationStarter;
import org.apache.hc.core5.http2.impl.nio.H2StreamListener;
import org.apache.hc.core5.http2.impl.nio.bootstrap.H2AsyncRequester;
import org.apache.hc.core5.http2.impl.nio.bootstrap.HandlerEntry;
import org.apache.hc.core5.http2.nio.support.DefaultAsyncPushConsumerFactory;
import org.apache.hc.core5.http2.ssl.H2ClientTlsStrategy;
import org.apache.hc.core5.pool.ConnPoolListener;
import org.apache.hc.core5.pool.DefaultDisposalCallback;
import org.apache.hc.core5.pool.DisposalCallback;
import org.apache.hc.core5.pool.LaxConnPool;
import org.apache.hc.core5.pool.ManagedConnPool;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.pool.StrictConnPool;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.reactor.IOSessionListener;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

public class H2RequesterBootstrap {
    private final List<HandlerEntry<Supplier<AsyncPushConsumer>>> pushConsumerList = new ArrayList<HandlerEntry<Supplier<AsyncPushConsumer>>>();
    private UriPatternType uriPatternType;
    private IOReactorConfig ioReactorConfig;
    private HttpProcessor httpProcessor;
    private CharCodingConfig charCodingConfig;
    private HttpVersionPolicy versionPolicy;
    private H2Config h2Config;
    private Http1Config http1Config;
    private int defaultMaxPerRoute;
    private int maxTotal;
    private TimeValue timeToLive;
    private PoolReusePolicy poolReusePolicy;
    private PoolConcurrencyPolicy poolConcurrencyPolicy;
    private TlsStrategy tlsStrategy;
    private Timeout handshakeTimeout;
    private Decorator<IOSession> ioSessionDecorator;
    private Callback<Exception> exceptionCallback;
    private IOSessionListener sessionListener;
    private H2StreamListener streamListener;
    private Http1StreamListener http1StreamListener;
    private ConnPoolListener<HttpHost> connPoolListener;

    private H2RequesterBootstrap() {
    }

    public static H2RequesterBootstrap bootstrap() {
        return new H2RequesterBootstrap();
    }

    public final H2RequesterBootstrap setIOReactorConfig(IOReactorConfig ioReactorConfig) {
        this.ioReactorConfig = ioReactorConfig;
        return this;
    }

    public final H2RequesterBootstrap setHttpProcessor(HttpProcessor httpProcessor) {
        this.httpProcessor = httpProcessor;
        return this;
    }

    public final H2RequesterBootstrap setVersionPolicy(HttpVersionPolicy versionPolicy) {
        this.versionPolicy = versionPolicy;
        return this;
    }

    public final H2RequesterBootstrap setH2Config(H2Config h2Config) {
        this.h2Config = h2Config;
        return this;
    }

    public final H2RequesterBootstrap setHttp1Config(Http1Config http1Config) {
        this.http1Config = http1Config;
        return this;
    }

    public final H2RequesterBootstrap setCharCodingConfig(CharCodingConfig charCodingConfig) {
        this.charCodingConfig = charCodingConfig;
        return this;
    }

    public final H2RequesterBootstrap setDefaultMaxPerRoute(int defaultMaxPerRoute) {
        this.defaultMaxPerRoute = defaultMaxPerRoute;
        return this;
    }

    public final H2RequesterBootstrap setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
        return this;
    }

    public final H2RequesterBootstrap setTimeToLive(TimeValue timeToLive) {
        this.timeToLive = timeToLive;
        return this;
    }

    public final H2RequesterBootstrap setPoolReusePolicy(PoolReusePolicy poolReusePolicy) {
        this.poolReusePolicy = poolReusePolicy;
        return this;
    }

    @Experimental
    public final H2RequesterBootstrap setPoolConcurrencyPolicy(PoolConcurrencyPolicy poolConcurrencyPolicy) {
        this.poolConcurrencyPolicy = poolConcurrencyPolicy;
        return this;
    }

    public final H2RequesterBootstrap setTlsStrategy(TlsStrategy tlsStrategy) {
        this.tlsStrategy = tlsStrategy;
        return this;
    }

    public final H2RequesterBootstrap setHandshakeTimeout(Timeout handshakeTimeout) {
        this.handshakeTimeout = handshakeTimeout;
        return this;
    }

    public final H2RequesterBootstrap setIOSessionDecorator(Decorator<IOSession> ioSessionDecorator) {
        this.ioSessionDecorator = ioSessionDecorator;
        return this;
    }

    public final H2RequesterBootstrap setExceptionCallback(Callback<Exception> exceptionCallback) {
        this.exceptionCallback = exceptionCallback;
        return this;
    }

    public final H2RequesterBootstrap setIOSessionListener(IOSessionListener sessionListener) {
        this.sessionListener = sessionListener;
        return this;
    }

    public final H2RequesterBootstrap setStreamListener(H2StreamListener streamListener) {
        this.streamListener = streamListener;
        return this;
    }

    public final H2RequesterBootstrap setStreamListener(Http1StreamListener http1StreamListener) {
        this.http1StreamListener = http1StreamListener;
        return this;
    }

    public final H2RequesterBootstrap setConnPoolListener(ConnPoolListener<HttpHost> connPoolListener) {
        this.connPoolListener = connPoolListener;
        return this;
    }

    public final H2RequesterBootstrap setUriPatternType(UriPatternType uriPatternType) {
        this.uriPatternType = uriPatternType;
        return this;
    }

    public final H2RequesterBootstrap register(String uriPattern, Supplier<AsyncPushConsumer> supplier) {
        Args.notBlank((CharSequence)uriPattern, (String)"URI pattern");
        Args.notNull(supplier, (String)"Supplier");
        this.pushConsumerList.add(new HandlerEntry<Supplier<AsyncPushConsumer>>(null, uriPattern, supplier));
        return this;
    }

    public final H2RequesterBootstrap registerVirtual(String hostname, String uriPattern, Supplier<AsyncPushConsumer> supplier) {
        Args.notBlank((CharSequence)hostname, (String)"Hostname");
        Args.notBlank((CharSequence)uriPattern, (String)"URI pattern");
        Args.notNull(supplier, (String)"Supplier");
        this.pushConsumerList.add(new HandlerEntry<Supplier<AsyncPushConsumer>>(hostname, uriPattern, supplier));
        return this;
    }

    public H2AsyncRequester create() {
        LaxConnPool connPool;
        switch (this.poolConcurrencyPolicy != null ? this.poolConcurrencyPolicy : PoolConcurrencyPolicy.STRICT) {
            case LAX: {
                connPool = new LaxConnPool(this.defaultMaxPerRoute > 0 ? this.defaultMaxPerRoute : 20, this.timeToLive, this.poolReusePolicy, (DisposalCallback)new DefaultDisposalCallback(), this.connPoolListener);
                break;
            }
            default: {
                connPool = new StrictConnPool(this.defaultMaxPerRoute > 0 ? this.defaultMaxPerRoute : 20, this.maxTotal > 0 ? this.maxTotal : 50, this.timeToLive, this.poolReusePolicy, (DisposalCallback)new DefaultDisposalCallback(), this.connPoolListener);
            }
        }
        RequestHandlerRegistry registry = new RequestHandlerRegistry(this.uriPatternType);
        for (HandlerEntry<Supplier<AsyncPushConsumer>> entry : this.pushConsumerList) {
            registry.register(entry.hostname, entry.uriPattern, entry.handler);
        }
        ClientH2StreamMultiplexerFactory http2StreamHandlerFactory = new ClientH2StreamMultiplexerFactory(this.httpProcessor != null ? this.httpProcessor : H2Processors.client(), new DefaultAsyncPushConsumerFactory((HttpRequestMapper<Supplier<AsyncPushConsumer>>)registry), this.h2Config != null ? this.h2Config : H2Config.DEFAULT, this.charCodingConfig != null ? this.charCodingConfig : CharCodingConfig.DEFAULT, this.streamListener);
        TlsStrategy actualTlsStrategy = this.tlsStrategy != null ? this.tlsStrategy : new H2ClientTlsStrategy();
        ClientHttp1StreamDuplexerFactory http1StreamHandlerFactory = new ClientHttp1StreamDuplexerFactory(this.httpProcessor != null ? this.httpProcessor : HttpProcessors.client(), this.http1Config != null ? this.http1Config : Http1Config.DEFAULT, this.charCodingConfig != null ? this.charCodingConfig : CharCodingConfig.DEFAULT, (ConnectionReuseStrategy)DefaultConnectionReuseStrategy.INSTANCE, (NHttpMessageParserFactory)new DefaultHttpResponseParserFactory(this.http1Config), (NHttpMessageWriterFactory)DefaultHttpRequestWriterFactory.INSTANCE, (ContentLengthStrategy)DefaultContentLengthStrategy.INSTANCE, (ContentLengthStrategy)DefaultContentLengthStrategy.INSTANCE, this.http1StreamListener);
        ClientHttpProtocolNegotiationStarter ioEventHandlerFactory = new ClientHttpProtocolNegotiationStarter(http1StreamHandlerFactory, http2StreamHandlerFactory, this.versionPolicy != null ? this.versionPolicy : HttpVersionPolicy.NEGOTIATE, actualTlsStrategy, this.handshakeTimeout);
        return new H2AsyncRequester(this.versionPolicy != null ? this.versionPolicy : HttpVersionPolicy.NEGOTIATE, this.ioReactorConfig, ioEventHandlerFactory, this.ioSessionDecorator, this.exceptionCallback, this.sessionListener, (ManagedConnPool<HttpHost, IOSession>)connPool, actualTlsStrategy, this.handshakeTimeout);
    }
}

