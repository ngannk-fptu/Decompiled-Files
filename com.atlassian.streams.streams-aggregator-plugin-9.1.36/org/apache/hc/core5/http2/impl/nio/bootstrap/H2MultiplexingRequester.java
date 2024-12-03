/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.impl.nio.bootstrap;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import org.apache.hc.core5.annotation.Internal;
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
import org.apache.hc.core5.http.HttpRequest;
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
import org.apache.hc.core5.net.URIAuthority;
import org.apache.hc.core5.reactor.Command;
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
        super(eventHandlerFactory, ioReactorConfig, ioSessionDecorator, exceptionCallback, sessionListener, ShutdownCommand.GRACEFUL_IMMEDIATE_CALLBACK, DefaultAddressResolver.INSTANCE);
        this.connPool = new H2ConnPool(this, addressResolver, tlsStrategy);
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
        Args.notNull(exchangeHandler, "Exchange handler");
        Args.notNull(timeout, "Timeout");
        Args.notNull(context, "Context");
        CancellableExecution cancellableExecution = new CancellableExecution();
        this.execute(exchangeHandler, pushHandlerFactory, cancellableExecution, timeout, context);
        return cancellableExecution;
    }

    public Cancellable execute(AsyncClientExchangeHandler exchangeHandler, Timeout timeout, HttpContext context) {
        return this.execute(exchangeHandler, null, timeout, context);
    }

    private void execute(final AsyncClientExchangeHandler exchangeHandler, final HandlerFactory<AsyncPushConsumer> pushHandlerFactory, final CancellableDependency cancellableDependency, final Timeout timeout, final HttpContext context) {
        Args.notNull(exchangeHandler, "Exchange handler");
        Args.notNull(timeout, "Timeout");
        Args.notNull(context, "Context");
        try {
            exchangeHandler.produceRequest(new RequestChannel(){

                @Override
                public void sendRequest(final HttpRequest request, final EntityDetails entityDetails, HttpContext httpContext) throws HttpException, IOException {
                    String scheme = request.getScheme();
                    URIAuthority authority = request.getAuthority();
                    if (authority == null) {
                        throw new ProtocolException("Request authority not specified");
                    }
                    HttpHost target = new HttpHost(scheme, authority);
                    H2MultiplexingRequester.this.connPool.getSession(target, timeout, new FutureCallback<IOSession>(){

                        @Override
                        public void completed(IOSession ioSession) {
                            ioSession.enqueue(new RequestExecutionCommand(new AsyncClientExchangeHandler(){

                                @Override
                                public void releaseResources() {
                                    exchangeHandler.releaseResources();
                                }

                                @Override
                                public void produceRequest(RequestChannel channel, HttpContext httpContext) throws HttpException, IOException {
                                    channel.sendRequest(request, entityDetails, httpContext);
                                }

                                @Override
                                public int available() {
                                    return exchangeHandler.available();
                                }

                                @Override
                                public void produce(DataStreamChannel channel) throws IOException {
                                    exchangeHandler.produce(channel);
                                }

                                @Override
                                public void consumeInformation(HttpResponse response, HttpContext httpContext) throws HttpException, IOException {
                                    exchangeHandler.consumeInformation(response, httpContext);
                                }

                                @Override
                                public void consumeResponse(HttpResponse response, EntityDetails entityDetails, HttpContext httpContext) throws HttpException, IOException {
                                    exchangeHandler.consumeResponse(response, entityDetails, httpContext);
                                }

                                @Override
                                public void updateCapacity(CapacityChannel capacityChannel) throws IOException {
                                    exchangeHandler.updateCapacity(capacityChannel);
                                }

                                @Override
                                public void consume(ByteBuffer src) throws IOException {
                                    exchangeHandler.consume(src);
                                }

                                @Override
                                public void streamEnd(List<? extends Header> trailers) throws HttpException, IOException {
                                    exchangeHandler.streamEnd(trailers);
                                }

                                @Override
                                public void cancel() {
                                    exchangeHandler.cancel();
                                }

                                @Override
                                public void failed(Exception cause) {
                                    exchangeHandler.failed(cause);
                                }
                            }, pushHandlerFactory, cancellableDependency, context), Command.Priority.NORMAL);
                            if (!ioSession.isOpen()) {
                                exchangeHandler.failed(new ConnectionClosedException());
                            }
                        }

                        @Override
                        public void failed(Exception ex) {
                            exchangeHandler.failed(ex);
                        }

                        @Override
                        public void cancelled() {
                            exchangeHandler.cancel();
                        }
                    });
                }
            }, context);
        }
        catch (IOException | HttpException ex) {
            exchangeHandler.failed(ex);
        }
    }

    public final <T> Future<T> execute(AsyncRequestProducer requestProducer, AsyncResponseConsumer<T> responseConsumer, HandlerFactory<AsyncPushConsumer> pushHandlerFactory, Timeout timeout, HttpContext context, FutureCallback<T> callback) {
        Args.notNull(requestProducer, "Request producer");
        Args.notNull(responseConsumer, "Response consumer");
        Args.notNull(timeout, "Timeout");
        final ComplexFuture<T> future = new ComplexFuture<T>(callback);
        BasicClientExchangeHandler<T> exchangeHandler = new BasicClientExchangeHandler<T>(requestProducer, responseConsumer, new FutureContribution<T>(future){

            @Override
            public void completed(T result) {
                future.completed(result);
            }
        });
        this.execute(exchangeHandler, pushHandlerFactory, future, timeout, context != null ? context : HttpCoreContext.create());
        return future;
    }

    public final <T> Future<T> execute(AsyncRequestProducer requestProducer, AsyncResponseConsumer<T> responseConsumer, Timeout timeout, HttpContext context, FutureCallback<T> callback) {
        return this.execute(requestProducer, responseConsumer, null, timeout, context, callback);
    }

    public final <T> Future<T> execute(AsyncRequestProducer requestProducer, AsyncResponseConsumer<T> responseConsumer, Timeout timeout, FutureCallback<T> callback) {
        return this.execute(requestProducer, responseConsumer, null, timeout, null, callback);
    }
}

