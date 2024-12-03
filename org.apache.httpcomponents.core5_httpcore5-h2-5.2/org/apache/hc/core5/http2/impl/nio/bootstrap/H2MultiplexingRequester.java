/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Internal
 *  org.apache.hc.core5.concurrent.BasicFuture
 *  org.apache.hc.core5.concurrent.Cancellable
 *  org.apache.hc.core5.concurrent.CancellableDependency
 *  org.apache.hc.core5.concurrent.ComplexFuture
 *  org.apache.hc.core5.concurrent.FutureCallback
 *  org.apache.hc.core5.concurrent.FutureContribution
 *  org.apache.hc.core5.function.Callback
 *  org.apache.hc.core5.function.Decorator
 *  org.apache.hc.core5.function.Resolver
 *  org.apache.hc.core5.http.ConnectionClosedException
 *  org.apache.hc.core5.http.EntityDetails
 *  org.apache.hc.core5.http.Header
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.http.HttpResponse
 *  org.apache.hc.core5.http.ProtocolException
 *  org.apache.hc.core5.http.impl.DefaultAddressResolver
 *  org.apache.hc.core5.http.impl.bootstrap.AsyncRequester
 *  org.apache.hc.core5.http.nio.AsyncClientExchangeHandler
 *  org.apache.hc.core5.http.nio.AsyncPushConsumer
 *  org.apache.hc.core5.http.nio.AsyncRequestProducer
 *  org.apache.hc.core5.http.nio.AsyncResponseConsumer
 *  org.apache.hc.core5.http.nio.CapacityChannel
 *  org.apache.hc.core5.http.nio.DataStreamChannel
 *  org.apache.hc.core5.http.nio.HandlerFactory
 *  org.apache.hc.core5.http.nio.RequestChannel
 *  org.apache.hc.core5.http.nio.command.RequestExecutionCommand
 *  org.apache.hc.core5.http.nio.command.ShutdownCommand
 *  org.apache.hc.core5.http.nio.ssl.TlsStrategy
 *  org.apache.hc.core5.http.nio.support.BasicClientExchangeHandler
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.http.protocol.HttpCoreContext
 *  org.apache.hc.core5.net.NamedEndpoint
 *  org.apache.hc.core5.net.URIAuthority
 *  org.apache.hc.core5.reactor.Command
 *  org.apache.hc.core5.reactor.Command$Priority
 *  org.apache.hc.core5.reactor.ConnectionInitiator
 *  org.apache.hc.core5.reactor.IOEventHandlerFactory
 *  org.apache.hc.core5.reactor.IOReactorConfig
 *  org.apache.hc.core5.reactor.IOSession
 *  org.apache.hc.core5.reactor.IOSessionListener
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.TimeValue
 *  org.apache.hc.core5.util.Timeout
 */
package org.apache.hc.core5.http2.impl.nio.bootstrap;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.concurrent.BasicFuture;
import org.apache.hc.core5.concurrent.Cancellable;
import org.apache.hc.core5.concurrent.CancellableDependency;
import org.apache.hc.core5.concurrent.ComplexFuture;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.concurrent.FutureContribution;
import org.apache.hc.core5.function.Callback;
import org.apache.hc.core5.function.Decorator;
import org.apache.hc.core5.function.Resolver;
import org.apache.hc.core5.http.ConnectionClosedException;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.impl.DefaultAddressResolver;
import org.apache.hc.core5.http.impl.bootstrap.AsyncRequester;
import org.apache.hc.core5.http.nio.AsyncClientExchangeHandler;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.AsyncResponseConsumer;
import org.apache.hc.core5.http.nio.CapacityChannel;
import org.apache.hc.core5.http.nio.DataStreamChannel;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.nio.RequestChannel;
import org.apache.hc.core5.http.nio.command.RequestExecutionCommand;
import org.apache.hc.core5.http.nio.command.ShutdownCommand;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.http.nio.support.BasicClientExchangeHandler;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.protocol.HttpCoreContext;
import org.apache.hc.core5.http2.impl.nio.bootstrap.CancellableExecution;
import org.apache.hc.core5.http2.nio.pool.H2ConnPool;
import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.net.URIAuthority;
import org.apache.hc.core5.reactor.Command;
import org.apache.hc.core5.reactor.ConnectionInitiator;
import org.apache.hc.core5.reactor.IOEventHandlerFactory;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.reactor.IOSession;
import org.apache.hc.core5.reactor.IOSessionListener;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

public class H2MultiplexingRequester
extends AsyncRequester {
    private final H2ConnPool connPool;

    @Internal
    public H2MultiplexingRequester(IOReactorConfig ioReactorConfig, IOEventHandlerFactory eventHandlerFactory, Decorator<IOSession> ioSessionDecorator, Callback<Exception> exceptionCallback, IOSessionListener sessionListener, Resolver<HttpHost, InetSocketAddress> addressResolver, TlsStrategy tlsStrategy) {
        super(eventHandlerFactory, ioReactorConfig, ioSessionDecorator, exceptionCallback, sessionListener, ShutdownCommand.GRACEFUL_IMMEDIATE_CALLBACK, (Resolver)DefaultAddressResolver.INSTANCE);
        this.connPool = new H2ConnPool((ConnectionInitiator)this, addressResolver, tlsStrategy);
    }

    public void closeIdle(TimeValue idleTime) {
        this.connPool.closeIdle(idleTime);
    }

    public Set<HttpHost> getRoutes() {
        return this.connPool.getRoutes();
    }

    public TimeValue getValidateAfterInactivity() {
        return this.connPool.getValidateAfterInactivity();
    }

    public void setValidateAfterInactivity(TimeValue timeValue) {
        this.connPool.setValidateAfterInactivity(timeValue);
    }

    public Cancellable execute(AsyncClientExchangeHandler exchangeHandler, HandlerFactory<AsyncPushConsumer> pushHandlerFactory, Timeout timeout, HttpContext context) {
        Args.notNull((Object)exchangeHandler, (String)"Exchange handler");
        Args.notNull((Object)timeout, (String)"Timeout");
        Args.notNull((Object)context, (String)"Context");
        CancellableExecution cancellableExecution = new CancellableExecution();
        this.execute(exchangeHandler, pushHandlerFactory, cancellableExecution, timeout, context);
        return cancellableExecution;
    }

    public Cancellable execute(AsyncClientExchangeHandler exchangeHandler, Timeout timeout, HttpContext context) {
        return this.execute(exchangeHandler, null, timeout, context);
    }

    private void execute(final AsyncClientExchangeHandler exchangeHandler, final HandlerFactory<AsyncPushConsumer> pushHandlerFactory, final CancellableDependency cancellableDependency, Timeout timeout, final HttpContext context) {
        Args.notNull((Object)exchangeHandler, (String)"Exchange handler");
        Args.notNull((Object)timeout, (String)"Timeout");
        Args.notNull((Object)context, (String)"Context");
        try {
            exchangeHandler.produceRequest((request, entityDetails, httpContext) -> {
                String scheme = request.getScheme();
                URIAuthority authority = request.getAuthority();
                if (authority == null) {
                    throw new ProtocolException("Request authority not specified");
                }
                HttpHost target = new HttpHost(scheme, (NamedEndpoint)authority);
                this.connPool.getSession(target, timeout, (FutureCallback)new FutureCallback<IOSession>(){

                    public void completed(IOSession ioSession) {
                        ioSession.enqueue((Command)new RequestExecutionCommand(new AsyncClientExchangeHandler(){

                            public void releaseResources() {
                                exchangeHandler.releaseResources();
                            }

                            public void produceRequest(RequestChannel channel, HttpContext httpContext) throws HttpException, IOException {
                                channel.sendRequest(request, entityDetails, httpContext);
                            }

                            public int available() {
                                return exchangeHandler.available();
                            }

                            public void produce(DataStreamChannel channel) throws IOException {
                                exchangeHandler.produce(channel);
                            }

                            public void consumeInformation(HttpResponse response, HttpContext httpContext) throws HttpException, IOException {
                                exchangeHandler.consumeInformation(response, httpContext);
                            }

                            public void consumeResponse(HttpResponse response, EntityDetails entityDetails, HttpContext httpContext) throws HttpException, IOException {
                                exchangeHandler.consumeResponse(response, entityDetails, httpContext);
                            }

                            public void updateCapacity(CapacityChannel capacityChannel) throws IOException {
                                exchangeHandler.updateCapacity(capacityChannel);
                            }

                            public void consume(ByteBuffer src) throws IOException {
                                exchangeHandler.consume(src);
                            }

                            public void streamEnd(List<? extends Header> trailers) throws HttpException, IOException {
                                exchangeHandler.streamEnd(trailers);
                            }

                            public void cancel() {
                                exchangeHandler.cancel();
                            }

                            public void failed(Exception cause) {
                                exchangeHandler.failed(cause);
                            }
                        }, pushHandlerFactory, cancellableDependency, context), Command.Priority.NORMAL);
                        if (!ioSession.isOpen()) {
                            exchangeHandler.failed((Exception)new ConnectionClosedException());
                        }
                    }

                    public void failed(Exception ex) {
                        exchangeHandler.failed(ex);
                    }

                    public void cancelled() {
                        exchangeHandler.cancel();
                    }
                });
            }, context);
        }
        catch (IOException | HttpException ex) {
            exchangeHandler.failed((Exception)ex);
        }
    }

    public final <T> Future<T> execute(AsyncRequestProducer requestProducer, AsyncResponseConsumer<T> responseConsumer, HandlerFactory<AsyncPushConsumer> pushHandlerFactory, Timeout timeout, HttpContext context, FutureCallback<T> callback) {
        Args.notNull((Object)requestProducer, (String)"Request producer");
        Args.notNull(responseConsumer, (String)"Response consumer");
        Args.notNull((Object)timeout, (String)"Timeout");
        final ComplexFuture future = new ComplexFuture(callback);
        BasicClientExchangeHandler exchangeHandler = new BasicClientExchangeHandler(requestProducer, responseConsumer, (FutureCallback)new FutureContribution<T>((BasicFuture)future){

            public void completed(T result) {
                future.completed(result);
            }
        });
        this.execute((AsyncClientExchangeHandler)exchangeHandler, pushHandlerFactory, (CancellableDependency)future, timeout, (HttpContext)(context != null ? context : HttpCoreContext.create()));
        return future;
    }

    public final <T> Future<T> execute(AsyncRequestProducer requestProducer, AsyncResponseConsumer<T> responseConsumer, Timeout timeout, HttpContext context, FutureCallback<T> callback) {
        return this.execute(requestProducer, responseConsumer, null, timeout, context, callback);
    }

    public final <T> Future<T> execute(AsyncRequestProducer requestProducer, AsyncResponseConsumer<T> responseConsumer, Timeout timeout, FutureCallback<T> callback) {
        return this.execute(requestProducer, responseConsumer, null, timeout, null, callback);
    }
}

