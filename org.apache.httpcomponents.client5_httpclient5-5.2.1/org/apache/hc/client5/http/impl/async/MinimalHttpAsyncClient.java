/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.concurrent.BasicFuture
 *  org.apache.hc.core5.concurrent.Cancellable
 *  org.apache.hc.core5.concurrent.ComplexCancellable
 *  org.apache.hc.core5.concurrent.ComplexFuture
 *  org.apache.hc.core5.concurrent.FutureCallback
 *  org.apache.hc.core5.function.Callback
 *  org.apache.hc.core5.function.Decorator
 *  org.apache.hc.core5.http.EntityDetails
 *  org.apache.hc.core5.http.Header
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.HttpHost
 *  org.apache.hc.core5.http.HttpResponse
 *  org.apache.hc.core5.http.nio.AsyncClientEndpoint
 *  org.apache.hc.core5.http.nio.AsyncClientExchangeHandler
 *  org.apache.hc.core5.http.nio.AsyncPushConsumer
 *  org.apache.hc.core5.http.nio.CapacityChannel
 *  org.apache.hc.core5.http.nio.DataStreamChannel
 *  org.apache.hc.core5.http.nio.HandlerFactory
 *  org.apache.hc.core5.http.nio.RequestChannel
 *  org.apache.hc.core5.http.nio.command.ShutdownCommand
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.io.CloseMode
 *  org.apache.hc.core5.io.Closer
 *  org.apache.hc.core5.net.NamedEndpoint
 *  org.apache.hc.core5.reactor.Command
 *  org.apache.hc.core5.reactor.Command$Priority
 *  org.apache.hc.core5.reactor.DefaultConnectingIOReactor
 *  org.apache.hc.core5.reactor.IOEventHandlerFactory
 *  org.apache.hc.core5.reactor.IOReactorConfig
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.Asserts
 *  org.apache.hc.core5.util.TimeValue
 *  org.apache.hc.core5.util.Timeout
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.hc.client5.http.impl.async;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.SchemePortResolver;
import org.apache.hc.client5.http.config.Configurable;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.config.TlsConfig;
import org.apache.hc.client5.http.impl.ConnPoolSupport;
import org.apache.hc.client5.http.impl.DefaultSchemePortResolver;
import org.apache.hc.client5.http.impl.ExecSupport;
import org.apache.hc.client5.http.impl.async.AbstractMinimalHttpAsyncClientBase;
import org.apache.hc.client5.http.impl.async.AsyncPushConsumerRegistry;
import org.apache.hc.client5.http.impl.async.LoggingAsyncClientExchangeHandler;
import org.apache.hc.client5.http.impl.async.LoggingExceptionCallback;
import org.apache.hc.client5.http.impl.async.LoggingIOSessionDecorator;
import org.apache.hc.client5.http.impl.classic.RequestFailedException;
import org.apache.hc.client5.http.nio.AsyncClientConnectionManager;
import org.apache.hc.client5.http.nio.AsyncConnectionEndpoint;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.routing.RoutingSupport;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.concurrent.BasicFuture;
import org.apache.hc.core5.concurrent.Cancellable;
import org.apache.hc.core5.concurrent.ComplexCancellable;
import org.apache.hc.core5.concurrent.ComplexFuture;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.function.Callback;
import org.apache.hc.core5.function.Decorator;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.nio.AsyncClientEndpoint;
import org.apache.hc.core5.http.nio.AsyncClientExchangeHandler;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.CapacityChannel;
import org.apache.hc.core5.http.nio.DataStreamChannel;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.nio.RequestChannel;
import org.apache.hc.core5.http.nio.command.ShutdownCommand;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.io.Closer;
import org.apache.hc.core5.net.NamedEndpoint;
import org.apache.hc.core5.reactor.Command;
import org.apache.hc.core5.reactor.DefaultConnectingIOReactor;
import org.apache.hc.core5.reactor.IOEventHandlerFactory;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.Asserts;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Contract(threading=ThreadingBehavior.SAFE_CONDITIONAL)
public final class MinimalHttpAsyncClient
extends AbstractMinimalHttpAsyncClientBase {
    private static final Logger LOG = LoggerFactory.getLogger(MinimalHttpAsyncClient.class);
    private final AsyncClientConnectionManager manager;
    private final SchemePortResolver schemePortResolver;
    private final TlsConfig tlsConfig;

    MinimalHttpAsyncClient(IOEventHandlerFactory eventHandlerFactory, AsyncPushConsumerRegistry pushConsumerRegistry, IOReactorConfig reactorConfig, ThreadFactory threadFactory, ThreadFactory workerThreadFactory, AsyncClientConnectionManager manager, SchemePortResolver schemePortResolver, TlsConfig tlsConfig) {
        super(new DefaultConnectingIOReactor(eventHandlerFactory, reactorConfig, workerThreadFactory, (Decorator)LoggingIOSessionDecorator.INSTANCE, (Callback)LoggingExceptionCallback.INSTANCE, null, ioSession -> ioSession.enqueue((Command)new ShutdownCommand(CloseMode.GRACEFUL), Command.Priority.NORMAL)), pushConsumerRegistry, threadFactory);
        this.manager = manager;
        this.schemePortResolver = schemePortResolver != null ? schemePortResolver : DefaultSchemePortResolver.INSTANCE;
        this.tlsConfig = tlsConfig;
    }

    private Future<AsyncConnectionEndpoint> leaseEndpoint(HttpHost host, Timeout connectionRequestTimeout, final Timeout connectTimeout, final HttpClientContext clientContext, final FutureCallback<AsyncConnectionEndpoint> callback) {
        HttpRoute route = new HttpRoute(RoutingSupport.normalize(host, this.schemePortResolver));
        final ComplexFuture resultFuture = new ComplexFuture(callback);
        String exchangeId = ExecSupport.getNextExchangeId();
        clientContext.setExchangeId(exchangeId);
        Future<AsyncConnectionEndpoint> leaseFuture = this.manager.lease(exchangeId, route, null, connectionRequestTimeout, new FutureCallback<AsyncConnectionEndpoint>(){

            public void completed(final AsyncConnectionEndpoint connectionEndpoint) {
                if (connectionEndpoint.isConnected()) {
                    resultFuture.completed((Object)connectionEndpoint);
                } else {
                    Future<AsyncConnectionEndpoint> connectFuture = MinimalHttpAsyncClient.this.manager.connect(connectionEndpoint, MinimalHttpAsyncClient.this.getConnectionInitiator(), connectTimeout, MinimalHttpAsyncClient.this.tlsConfig, (HttpContext)clientContext, new FutureCallback<AsyncConnectionEndpoint>(){

                        public void completed(AsyncConnectionEndpoint result) {
                            resultFuture.completed((Object)result);
                        }

                        public void failed(Exception ex) {
                            try {
                                Closer.closeQuietly((Closeable)((Object)connectionEndpoint));
                                MinimalHttpAsyncClient.this.manager.release(connectionEndpoint, null, TimeValue.ZERO_MILLISECONDS);
                            }
                            finally {
                                resultFuture.failed(ex);
                            }
                        }

                        public void cancelled() {
                            try {
                                Closer.closeQuietly((Closeable)((Object)connectionEndpoint));
                                MinimalHttpAsyncClient.this.manager.release(connectionEndpoint, null, TimeValue.ZERO_MILLISECONDS);
                            }
                            finally {
                                resultFuture.cancel(true);
                            }
                        }
                    });
                    resultFuture.setDependency(connectFuture);
                }
            }

            public void failed(Exception ex) {
                callback.failed(ex);
            }

            public void cancelled() {
                callback.cancelled();
            }
        });
        resultFuture.setDependency(leaseFuture);
        return resultFuture;
    }

    public Future<AsyncClientEndpoint> lease(HttpHost host, FutureCallback<AsyncClientEndpoint> callback) {
        return this.lease(host, (HttpContext)HttpClientContext.create(), callback);
    }

    public Future<AsyncClientEndpoint> lease(HttpHost host, HttpContext context, FutureCallback<AsyncClientEndpoint> callback) {
        Args.notNull((Object)host, (String)"Host");
        Args.notNull((Object)context, (String)"HTTP context");
        final BasicFuture future = new BasicFuture(callback);
        if (!this.isRunning()) {
            future.failed((Exception)new CancellationException("Connection lease cancelled"));
            return future;
        }
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        RequestConfig requestConfig = clientContext.getRequestConfig();
        Timeout connectionRequestTimeout = requestConfig.getConnectionRequestTimeout();
        Timeout connectTimeout = requestConfig.getConnectTimeout();
        this.leaseEndpoint(host, connectionRequestTimeout, connectTimeout, clientContext, new FutureCallback<AsyncConnectionEndpoint>(){

            public void completed(AsyncConnectionEndpoint result) {
                future.completed((Object)new InternalAsyncClientEndpoint(result));
            }

            public void failed(Exception ex) {
                future.failed(ex);
            }

            public void cancelled() {
                future.cancel(true);
            }
        });
        return future;
    }

    @Override
    public Cancellable execute(final AsyncClientExchangeHandler exchangeHandler, final HandlerFactory<AsyncPushConsumer> pushHandlerFactory, HttpContext context) {
        ComplexCancellable cancellable = new ComplexCancellable();
        try {
            if (!this.isRunning()) {
                throw new CancellationException("Request execution cancelled");
            }
            final HttpClientContext clientContext = context != null ? HttpClientContext.adapt(context) : HttpClientContext.create();
            exchangeHandler.produceRequest((request, entityDetails, context1) -> {
                RequestConfig requestConfig = null;
                if (request instanceof Configurable) {
                    requestConfig = ((Configurable)request).getConfig();
                }
                if (requestConfig != null) {
                    clientContext.setRequestConfig(requestConfig);
                } else {
                    requestConfig = clientContext.getRequestConfig();
                }
                Timeout connectionRequestTimeout = requestConfig.getConnectionRequestTimeout();
                Timeout connectTimeout = requestConfig.getConnectTimeout();
                final Timeout responseTimeout = requestConfig.getResponseTimeout();
                HttpHost target = new HttpHost(request.getScheme(), (NamedEndpoint)request.getAuthority());
                Future<AsyncConnectionEndpoint> leaseFuture = this.leaseEndpoint(target, connectionRequestTimeout, connectTimeout, clientContext, new FutureCallback<AsyncConnectionEndpoint>(){

                    public void completed(AsyncConnectionEndpoint connectionEndpoint) {
                        final InternalAsyncClientEndpoint endpoint = new InternalAsyncClientEndpoint(connectionEndpoint);
                        final AtomicInteger messageCountDown = new AtomicInteger(2);
                        AsyncClientExchangeHandler internalExchangeHandler = new AsyncClientExchangeHandler(){

                            public void releaseResources() {
                                try {
                                    exchangeHandler.releaseResources();
                                }
                                finally {
                                    endpoint.releaseAndDiscard();
                                }
                            }

                            public void failed(Exception cause) {
                                try {
                                    exchangeHandler.failed(cause);
                                }
                                finally {
                                    endpoint.releaseAndDiscard();
                                }
                            }

                            public void cancel() {
                                this.failed(new RequestFailedException("Request aborted"));
                            }

                            public void produceRequest(RequestChannel channel, HttpContext context1) throws HttpException, IOException {
                                channel.sendRequest(request, entityDetails, context1);
                                if (entityDetails == null) {
                                    messageCountDown.decrementAndGet();
                                }
                            }

                            public int available() {
                                return exchangeHandler.available();
                            }

                            public void produce(final DataStreamChannel channel) throws IOException {
                                exchangeHandler.produce(new DataStreamChannel(){

                                    public void requestOutput() {
                                        channel.requestOutput();
                                    }

                                    public int write(ByteBuffer src) throws IOException {
                                        return channel.write(src);
                                    }

                                    public void endStream(List<? extends Header> trailers) throws IOException {
                                        channel.endStream(trailers);
                                        if (messageCountDown.decrementAndGet() <= 0) {
                                            endpoint.releaseAndReuse();
                                        }
                                    }

                                    public void endStream() throws IOException {
                                        channel.endStream();
                                        if (messageCountDown.decrementAndGet() <= 0) {
                                            endpoint.releaseAndReuse();
                                        }
                                    }
                                });
                            }

                            public void consumeInformation(HttpResponse response, HttpContext context1) throws HttpException, IOException {
                                exchangeHandler.consumeInformation(response, context1);
                            }

                            public void consumeResponse(HttpResponse response, EntityDetails entityDetails, HttpContext context1) throws HttpException, IOException {
                                exchangeHandler.consumeResponse(response, entityDetails, context1);
                                if (response.getCode() >= 400) {
                                    messageCountDown.decrementAndGet();
                                }
                                if (entityDetails == null && messageCountDown.decrementAndGet() <= 0) {
                                    endpoint.releaseAndReuse();
                                }
                            }

                            public void updateCapacity(CapacityChannel capacityChannel) throws IOException {
                                exchangeHandler.updateCapacity(capacityChannel);
                            }

                            public void consume(ByteBuffer src) throws IOException {
                                exchangeHandler.consume(src);
                            }

                            public void streamEnd(List<? extends Header> trailers) throws HttpException, IOException {
                                if (messageCountDown.decrementAndGet() <= 0) {
                                    endpoint.releaseAndReuse();
                                }
                                exchangeHandler.streamEnd(trailers);
                            }
                        };
                        if (responseTimeout != null) {
                            endpoint.setSocketTimeout(responseTimeout);
                        }
                        endpoint.execute(internalExchangeHandler, (HandlerFactory<AsyncPushConsumer>)pushHandlerFactory, (HttpContext)clientContext);
                    }

                    public void failed(Exception ex) {
                        exchangeHandler.failed(ex);
                    }

                    public void cancelled() {
                        exchangeHandler.cancel();
                    }
                });
                cancellable.setDependency(() -> leaseFuture.cancel(true));
            }, context);
        }
        catch (IOException | IllegalStateException | HttpException ex) {
            exchangeHandler.failed((Exception)ex);
        }
        return cancellable;
    }

    private class InternalAsyncClientEndpoint
    extends AsyncClientEndpoint {
        private final AsyncConnectionEndpoint connectionEndpoint;
        private final AtomicBoolean released;

        InternalAsyncClientEndpoint(AsyncConnectionEndpoint connectionEndpoint) {
            this.connectionEndpoint = connectionEndpoint;
            this.released = new AtomicBoolean(false);
        }

        boolean isReleased() {
            return this.released.get();
        }

        public boolean isConnected() {
            return !this.isReleased() && this.connectionEndpoint.isConnected();
        }

        public void execute(AsyncClientExchangeHandler exchangeHandler, HandlerFactory<AsyncPushConsumer> pushHandlerFactory, HttpContext context) {
            Asserts.check((!this.released.get() ? 1 : 0) != 0, (String)"Endpoint has already been released");
            HttpClientContext clientContext = context != null ? HttpClientContext.adapt(context) : HttpClientContext.create();
            String exchangeId = ExecSupport.getNextExchangeId();
            clientContext.setExchangeId(exchangeId);
            if (LOG.isDebugEnabled()) {
                LOG.debug("{} executing message exchange {}", (Object)exchangeId, (Object)ConnPoolSupport.getId(this.connectionEndpoint));
                this.connectionEndpoint.execute(exchangeId, new LoggingAsyncClientExchangeHandler(LOG, exchangeId, exchangeHandler), pushHandlerFactory, (HttpContext)clientContext);
            } else {
                this.connectionEndpoint.execute(exchangeId, exchangeHandler, (HttpContext)clientContext);
            }
        }

        public void setSocketTimeout(Timeout timeout) {
            this.connectionEndpoint.setSocketTimeout(timeout);
        }

        public void releaseAndReuse() {
            if (this.released.compareAndSet(false, true)) {
                MinimalHttpAsyncClient.this.manager.release(this.connectionEndpoint, null, TimeValue.NEG_ONE_MILLISECOND);
            }
        }

        public void releaseAndDiscard() {
            if (this.released.compareAndSet(false, true)) {
                Closer.closeQuietly((Closeable)((Object)this.connectionEndpoint));
                MinimalHttpAsyncClient.this.manager.release(this.connectionEndpoint, null, TimeValue.ZERO_MILLISECONDS);
            }
        }
    }
}

