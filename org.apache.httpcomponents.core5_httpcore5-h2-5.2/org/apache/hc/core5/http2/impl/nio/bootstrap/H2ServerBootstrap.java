/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.function.Callback
 *  org.apache.hc.core5.function.Decorator
 *  org.apache.hc.core5.function.Supplier
 *  org.apache.hc.core5.http.ConnectionReuseStrategy
 *  org.apache.hc.core5.http.ContentLengthStrategy
 *  org.apache.hc.core5.http.HttpRequestMapper
 *  org.apache.hc.core5.http.config.CharCodingConfig
 *  org.apache.hc.core5.http.config.Http1Config
 *  org.apache.hc.core5.http.config.NamedElementChain
 *  org.apache.hc.core5.http.config.NamedElementChain$Node
 *  org.apache.hc.core5.http.impl.DefaultConnectionReuseStrategy
 *  org.apache.hc.core5.http.impl.DefaultContentLengthStrategy
 *  org.apache.hc.core5.http.impl.Http1StreamListener
 *  org.apache.hc.core5.http.impl.HttpProcessors
 *  org.apache.hc.core5.http.impl.bootstrap.HttpAsyncServer
 *  org.apache.hc.core5.http.impl.bootstrap.StandardFilter
 *  org.apache.hc.core5.http.impl.nio.DefaultHttpRequestParserFactory
 *  org.apache.hc.core5.http.impl.nio.DefaultHttpResponseWriterFactory
 *  org.apache.hc.core5.http.impl.nio.ServerHttp1StreamDuplexerFactory
 *  org.apache.hc.core5.http.nio.AsyncFilterHandler
 *  org.apache.hc.core5.http.nio.AsyncServerExchangeHandler
 *  org.apache.hc.core5.http.nio.AsyncServerRequestHandler
 *  org.apache.hc.core5.http.nio.HandlerFactory
 *  org.apache.hc.core5.http.nio.NHttpMessageParserFactory
 *  org.apache.hc.core5.http.nio.NHttpMessageWriterFactory
 *  org.apache.hc.core5.http.nio.ssl.TlsStrategy
 *  org.apache.hc.core5.http.nio.support.AsyncServerExpectationFilter
 *  org.apache.hc.core5.http.nio.support.AsyncServerFilterChainElement
 *  org.apache.hc.core5.http.nio.support.AsyncServerFilterChainExchangeHandlerFactory
 *  org.apache.hc.core5.http.nio.support.BasicAsyncServerExpectationDecorator
 *  org.apache.hc.core5.http.nio.support.BasicServerExchangeHandler
 *  org.apache.hc.core5.http.nio.support.DefaultAsyncResponseExchangeHandlerFactory
 *  org.apache.hc.core5.http.nio.support.TerminalAsyncServerFilter
 *  org.apache.hc.core5.http.protocol.HttpProcessor
 *  org.apache.hc.core5.http.protocol.LookupRegistry
 *  org.apache.hc.core5.http.protocol.RequestHandlerRegistry
 *  org.apache.hc.core5.http.protocol.UriPatternType
 *  org.apache.hc.core5.net.InetAddressUtils
 *  org.apache.hc.core5.reactor.IOEventHandlerFactory
 *  org.apache.hc.core5.reactor.IOReactorConfig
 *  org.apache.hc.core5.reactor.IOSession
 *  org.apache.hc.core5.reactor.IOSessionListener
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.Timeout
 */
package org.apache.hc.core5.http2.impl.nio.bootstrap;

import java.util.ArrayList;
import java.util.List;
import org.apache.hc.core5.function.Callback;
import org.apache.hc.core5.function.Decorator;
import org.apache.hc.core5.function.Supplier;
import org.apache.hc.core5.http.ConnectionReuseStrategy;
import org.apache.hc.core5.http.ContentLengthStrategy;
import org.apache.hc.core5.http.HttpRequestMapper;
import org.apache.hc.core5.http.config.CharCodingConfig;
import org.apache.hc.core5.http.config.Http1Config;
import org.apache.hc.core5.http.config.NamedElementChain;
import org.apache.hc.core5.http.impl.DefaultConnectionReuseStrategy;
import org.apache.hc.core5.http.impl.DefaultContentLengthStrategy;
import org.apache.hc.core5.http.impl.Http1StreamListener;
import org.apache.hc.core5.http.impl.HttpProcessors;
import org.apache.hc.core5.http.impl.bootstrap.HttpAsyncServer;
import org.apache.hc.core5.http.impl.bootstrap.StandardFilter;
import org.apache.hc.core5.http.impl.nio.DefaultHttpRequestParserFactory;
import org.apache.hc.core5.http.impl.nio.DefaultHttpResponseWriterFactory;
import org.apache.hc.core5.http.impl.nio.ServerHttp1StreamDuplexerFactory;
import org.apache.hc.core5.http.nio.AsyncFilterHandler;
import org.apache.hc.core5.http.nio.AsyncServerExchangeHandler;
import org.apache.hc.core5.http.nio.AsyncServerRequestHandler;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.nio.NHttpMessageParserFactory;
import org.apache.hc.core5.http.nio.NHttpMessageWriterFactory;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.http.nio.support.AsyncServerExpectationFilter;
import org.apache.hc.core5.http.nio.support.AsyncServerFilterChainElement;
import org.apache.hc.core5.http.nio.support.AsyncServerFilterChainExchangeHandlerFactory;
import org.apache.hc.core5.http.nio.support.BasicAsyncServerExpectationDecorator;
import org.apache.hc.core5.http.nio.support.BasicServerExchangeHandler;
import org.apache.hc.core5.http.nio.support.DefaultAsyncResponseExchangeHandlerFactory;
import org.apache.hc.core5.http.nio.support.TerminalAsyncServerFilter;
import org.apache.hc.core5.http.protocol.HttpProcessor;
import org.apache.hc.core5.http.protocol.LookupRegistry;
import org.apache.hc.core5.http.protocol.RequestHandlerRegistry;
import org.apache.hc.core5.http.protocol.UriPatternType;
import org.apache.hc.core5.http2.HttpVersionPolicy;
import org.apache.hc.core5.http2.config.H2Config;
import org.apache.hc.core5.http2.impl.H2Processors;
import org.apache.hc.core5.http2.impl.nio.H2StreamListener;
import org.apache.hc.core5.http2.impl.nio.ServerH2StreamMultiplexerFactory;
import org.apache.hc.core5.http2.impl.nio.ServerHttpProtocolNegotiationStarter;
import org.apache.hc.core5.http2.impl.nio.bootstrap.FilterEntry;
import org.apache.hc.core5.http2.impl.nio.bootstrap.HandlerEntry;
import org.apache.hc.core5.http2.ssl.H2ServerTlsStrategy;
import org.apache.hc.core5.net.InetAddressUtils;
import org.apache.hc.core5.reactor.IOEventHandlerFactory;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.reactor.IOSessionListener;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.Timeout;

public class H2ServerBootstrap {
    private final List<HandlerEntry<Supplier<AsyncServerExchangeHandler>>> handlerList = new ArrayList<HandlerEntry<Supplier<AsyncServerExchangeHandler>>>();
    private final List<FilterEntry<AsyncFilterHandler>> filters = new ArrayList<FilterEntry<AsyncFilterHandler>>();
    private String canonicalHostName;
    private LookupRegistry<Supplier<AsyncServerExchangeHandler>> lookupRegistry;
    private IOReactorConfig ioReactorConfig;
    private HttpProcessor httpProcessor;
    private CharCodingConfig charCodingConfig;
    private HttpVersionPolicy versionPolicy;
    private H2Config h2Config;
    private Http1Config http1Config;
    private TlsStrategy tlsStrategy;
    private Timeout handshakeTimeout;
    private Decorator<IOSession> ioSessionDecorator;
    private Callback<Exception> exceptionCallback;
    private IOSessionListener sessionListener;
    private H2StreamListener h2StreamListener;
    private Http1StreamListener http1StreamListener;

    private H2ServerBootstrap() {
    }

    public static H2ServerBootstrap bootstrap() {
        return new H2ServerBootstrap();
    }

    public final H2ServerBootstrap setCanonicalHostName(String canonicalHostName) {
        this.canonicalHostName = canonicalHostName;
        return this;
    }

    public final H2ServerBootstrap setIOReactorConfig(IOReactorConfig ioReactorConfig) {
        this.ioReactorConfig = ioReactorConfig;
        return this;
    }

    public final H2ServerBootstrap setHttpProcessor(HttpProcessor httpProcessor) {
        this.httpProcessor = httpProcessor;
        return this;
    }

    public final H2ServerBootstrap setVersionPolicy(HttpVersionPolicy versionPolicy) {
        this.versionPolicy = versionPolicy;
        return this;
    }

    public final H2ServerBootstrap setH2Config(H2Config h2Config) {
        this.h2Config = h2Config;
        return this;
    }

    public final H2ServerBootstrap setHttp1Config(Http1Config http1Config) {
        this.http1Config = http1Config;
        return this;
    }

    public final H2ServerBootstrap setCharset(CharCodingConfig charCodingConfig) {
        this.charCodingConfig = charCodingConfig;
        return this;
    }

    public final H2ServerBootstrap setTlsStrategy(TlsStrategy tlsStrategy) {
        this.tlsStrategy = tlsStrategy;
        return this;
    }

    public final H2ServerBootstrap setHandshakeTimeout(Timeout handshakeTimeout) {
        this.handshakeTimeout = handshakeTimeout;
        return this;
    }

    public final H2ServerBootstrap setIOSessionDecorator(Decorator<IOSession> ioSessionDecorator) {
        this.ioSessionDecorator = ioSessionDecorator;
        return this;
    }

    public final H2ServerBootstrap setExceptionCallback(Callback<Exception> exceptionCallback) {
        this.exceptionCallback = exceptionCallback;
        return this;
    }

    public final H2ServerBootstrap setIOSessionListener(IOSessionListener sessionListener) {
        this.sessionListener = sessionListener;
        return this;
    }

    public final H2ServerBootstrap setStreamListener(H2StreamListener h2StreamListener) {
        this.h2StreamListener = h2StreamListener;
        return this;
    }

    public final H2ServerBootstrap setStreamListener(Http1StreamListener http1StreamListener) {
        this.http1StreamListener = http1StreamListener;
        return this;
    }

    public final H2ServerBootstrap setLookupRegistry(LookupRegistry<Supplier<AsyncServerExchangeHandler>> lookupRegistry) {
        this.lookupRegistry = lookupRegistry;
        return this;
    }

    public final H2ServerBootstrap register(String uriPattern, Supplier<AsyncServerExchangeHandler> supplier) {
        Args.notBlank((CharSequence)uriPattern, (String)"URI pattern");
        Args.notNull(supplier, (String)"Supplier");
        this.handlerList.add(new HandlerEntry<Supplier<AsyncServerExchangeHandler>>(null, uriPattern, supplier));
        return this;
    }

    public final H2ServerBootstrap registerVirtual(String hostname, String uriPattern, Supplier<AsyncServerExchangeHandler> supplier) {
        Args.notBlank((CharSequence)hostname, (String)"Hostname");
        Args.notBlank((CharSequence)uriPattern, (String)"URI pattern");
        Args.notNull(supplier, (String)"Supplier");
        this.handlerList.add(new HandlerEntry<Supplier<AsyncServerExchangeHandler>>(hostname, uriPattern, supplier));
        return this;
    }

    public final <T> H2ServerBootstrap register(String uriPattern, AsyncServerRequestHandler<T> requestHandler) {
        this.register(uriPattern, (Supplier<AsyncServerExchangeHandler>)((Supplier)() -> new BasicServerExchangeHandler(requestHandler)));
        return this;
    }

    public final <T> H2ServerBootstrap registerVirtual(String hostname, String uriPattern, AsyncServerRequestHandler<T> requestHandler) {
        this.registerVirtual(hostname, uriPattern, (Supplier<AsyncServerExchangeHandler>)((Supplier)() -> new BasicServerExchangeHandler(requestHandler)));
        return this;
    }

    public final H2ServerBootstrap addFilterBefore(String existing, String name, AsyncFilterHandler filterHandler) {
        Args.notBlank((CharSequence)existing, (String)"Existing");
        Args.notBlank((CharSequence)name, (String)"Name");
        Args.notNull((Object)filterHandler, (String)"Filter handler");
        this.filters.add(new FilterEntry<AsyncFilterHandler>(FilterEntry.Position.BEFORE, name, filterHandler, existing));
        return this;
    }

    public final H2ServerBootstrap addFilterAfter(String existing, String name, AsyncFilterHandler filterHandler) {
        Args.notBlank((CharSequence)existing, (String)"Existing");
        Args.notBlank((CharSequence)name, (String)"Name");
        Args.notNull((Object)filterHandler, (String)"Filter handler");
        this.filters.add(new FilterEntry<AsyncFilterHandler>(FilterEntry.Position.AFTER, name, filterHandler, existing));
        return this;
    }

    public final H2ServerBootstrap replaceFilter(String existing, AsyncFilterHandler filterHandler) {
        Args.notBlank((CharSequence)existing, (String)"Existing");
        Args.notNull((Object)filterHandler, (String)"Filter handler");
        this.filters.add(new FilterEntry<AsyncFilterHandler>(FilterEntry.Position.REPLACE, existing, filterHandler, existing));
        return this;
    }

    public final H2ServerBootstrap addFilterFirst(String name, AsyncFilterHandler filterHandler) {
        Args.notNull((Object)name, (String)"Name");
        Args.notNull((Object)filterHandler, (String)"Filter handler");
        this.filters.add(new FilterEntry<AsyncFilterHandler>(FilterEntry.Position.FIRST, name, filterHandler, null));
        return this;
    }

    public final H2ServerBootstrap addFilterLast(String name, AsyncFilterHandler filterHandler) {
        Args.notNull((Object)name, (String)"Name");
        Args.notNull((Object)filterHandler, (String)"Filter handler");
        this.filters.add(new FilterEntry<AsyncFilterHandler>(FilterEntry.Position.LAST, name, filterHandler, null));
        return this;
    }

    public HttpAsyncServer create() {
        DefaultAsyncResponseExchangeHandlerFactory handlerFactory;
        String actualCanonicalHostName = this.canonicalHostName != null ? this.canonicalHostName : InetAddressUtils.getCanonicalLocalHostName();
        RequestHandlerRegistry registry = new RequestHandlerRegistry(actualCanonicalHostName, () -> this.lookupRegistry != null ? this.lookupRegistry : UriPatternType.newMatcher((UriPatternType)UriPatternType.URI_PATTERN));
        for (HandlerEntry<Supplier<AsyncServerExchangeHandler>> entry : this.handlerList) {
            registry.register(entry.hostname, entry.uriPattern, entry.handler);
        }
        if (!this.filters.isEmpty()) {
            NamedElementChain filterChainDefinition = new NamedElementChain();
            filterChainDefinition.addLast((Object)new TerminalAsyncServerFilter((HandlerFactory)new DefaultAsyncResponseExchangeHandlerFactory((HttpRequestMapper)registry)), StandardFilter.MAIN_HANDLER.name());
            filterChainDefinition.addFirst((Object)new AsyncServerExpectationFilter(), StandardFilter.EXPECT_CONTINUE.name());
            for (FilterEntry<AsyncFilterHandler> entry : this.filters) {
                switch (entry.position) {
                    case AFTER: {
                        filterChainDefinition.addAfter(entry.existing, entry.filterHandler, entry.name);
                        break;
                    }
                    case BEFORE: {
                        filterChainDefinition.addBefore(entry.existing, entry.filterHandler, entry.name);
                        break;
                    }
                    case REPLACE: {
                        filterChainDefinition.replace(entry.existing, entry.filterHandler);
                        break;
                    }
                    case FIRST: {
                        filterChainDefinition.addFirst(entry.filterHandler, entry.name);
                        break;
                    }
                    case LAST: {
                        filterChainDefinition.addBefore(StandardFilter.MAIN_HANDLER.name(), entry.filterHandler, entry.name);
                    }
                }
            }
            AsyncServerFilterChainElement execChain = null;
            for (NamedElementChain.Node current = filterChainDefinition.getLast(); current != null; current = current.getPrevious()) {
                execChain = new AsyncServerFilterChainElement((AsyncFilterHandler)current.getValue(), execChain);
            }
            handlerFactory = new AsyncServerFilterChainExchangeHandlerFactory(execChain, this.exceptionCallback);
        } else {
            handlerFactory = new DefaultAsyncResponseExchangeHandlerFactory((HttpRequestMapper)registry, handler -> new BasicAsyncServerExpectationDecorator(handler, this.exceptionCallback));
        }
        ServerH2StreamMultiplexerFactory http2StreamHandlerFactory = new ServerH2StreamMultiplexerFactory(this.httpProcessor != null ? this.httpProcessor : H2Processors.server(), (HandlerFactory<AsyncServerExchangeHandler>)handlerFactory, this.h2Config != null ? this.h2Config : H2Config.DEFAULT, this.charCodingConfig != null ? this.charCodingConfig : CharCodingConfig.DEFAULT, this.h2StreamListener);
        TlsStrategy actualTlsStrategy = this.tlsStrategy != null ? this.tlsStrategy : new H2ServerTlsStrategy();
        ServerHttp1StreamDuplexerFactory http1StreamHandlerFactory = new ServerHttp1StreamDuplexerFactory(this.httpProcessor != null ? this.httpProcessor : HttpProcessors.server(), (HandlerFactory)handlerFactory, this.http1Config != null ? this.http1Config : Http1Config.DEFAULT, this.charCodingConfig != null ? this.charCodingConfig : CharCodingConfig.DEFAULT, (ConnectionReuseStrategy)DefaultConnectionReuseStrategy.INSTANCE, (NHttpMessageParserFactory)DefaultHttpRequestParserFactory.INSTANCE, (NHttpMessageWriterFactory)DefaultHttpResponseWriterFactory.INSTANCE, (ContentLengthStrategy)DefaultContentLengthStrategy.INSTANCE, (ContentLengthStrategy)DefaultContentLengthStrategy.INSTANCE, this.http1StreamListener);
        ServerHttpProtocolNegotiationStarter ioEventHandlerFactory = new ServerHttpProtocolNegotiationStarter(http1StreamHandlerFactory, http2StreamHandlerFactory, this.versionPolicy != null ? this.versionPolicy : HttpVersionPolicy.NEGOTIATE, actualTlsStrategy, this.handshakeTimeout);
        return new HttpAsyncServer((IOEventHandlerFactory)ioEventHandlerFactory, this.ioReactorConfig, this.ioSessionDecorator, this.exceptionCallback, this.sessionListener, actualCanonicalHostName);
    }
}

