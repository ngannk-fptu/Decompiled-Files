/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.client5.http.cookie.BasicCookieStore
 *  org.apache.hc.client5.http.cookie.CookieStore
 *  org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient
 *  org.apache.hc.client5.http.impl.async.HttpAsyncClients
 *  org.apache.hc.client5.http.protocol.HttpClientContext
 *  org.apache.hc.core5.concurrent.FutureCallback
 *  org.apache.hc.core5.http.HttpResponse
 *  org.apache.hc.core5.http.HttpStreamResetException
 *  org.apache.hc.core5.http.Message
 *  org.apache.hc.core5.http.nio.AsyncRequestProducer
 *  org.apache.hc.core5.http.nio.AsyncResponseConsumer
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.reactive.ReactiveResponseConsumer
 *  org.reactivestreams.Publisher
 *  reactor.core.publisher.Mono
 *  reactor.core.publisher.MonoSink
 */
package org.springframework.http.client.reactive;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStreamResetException;
import org.apache.hc.core5.http.Message;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.AsyncResponseConsumer;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.reactive.ReactiveResponseConsumer;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.client.reactive.HttpComponentsClientHttpRequest;
import org.springframework.http.client.reactive.HttpComponentsClientHttpResponse;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

public class HttpComponentsClientHttpConnector
implements ClientHttpConnector,
Closeable {
    private final CloseableHttpAsyncClient client;
    private final BiFunction<HttpMethod, URI, ? extends HttpClientContext> contextProvider;
    private DataBufferFactory dataBufferFactory = DefaultDataBufferFactory.sharedInstance;

    public HttpComponentsClientHttpConnector() {
        this(HttpAsyncClients.createDefault());
    }

    public HttpComponentsClientHttpConnector(CloseableHttpAsyncClient client) {
        this(client, (method, uri) -> HttpClientContext.create());
    }

    public HttpComponentsClientHttpConnector(CloseableHttpAsyncClient client, BiFunction<HttpMethod, URI, ? extends HttpClientContext> contextProvider) {
        Assert.notNull((Object)client, "Client must not be null");
        Assert.notNull(contextProvider, "ContextProvider must not be null");
        this.contextProvider = contextProvider;
        this.client = client;
        this.client.start();
    }

    public void setBufferFactory(DataBufferFactory bufferFactory) {
        this.dataBufferFactory = bufferFactory;
    }

    @Override
    public Mono<ClientHttpResponse> connect(HttpMethod method, URI uri, Function<? super ClientHttpRequest, Mono<Void>> requestCallback) {
        HttpClientContext context = this.contextProvider.apply(method, uri);
        if (context.getCookieStore() == null) {
            context.setCookieStore((CookieStore)new BasicCookieStore());
        }
        HttpComponentsClientHttpRequest request = new HttpComponentsClientHttpRequest(method, uri, context, this.dataBufferFactory);
        return requestCallback.apply(request).then(Mono.defer(() -> this.execute(request, context)));
    }

    private Mono<ClientHttpResponse> execute(HttpComponentsClientHttpRequest request, HttpClientContext context) {
        AsyncRequestProducer requestProducer = request.toRequestProducer();
        return Mono.create(sink -> {
            ReactiveResponseConsumer reactiveResponseConsumer = new ReactiveResponseConsumer((FutureCallback)new MonoFutureCallbackAdapter((MonoSink<ClientHttpResponse>)sink, this.dataBufferFactory, context));
            this.client.execute(requestProducer, (AsyncResponseConsumer)reactiveResponseConsumer, (HttpContext)context, null);
        });
    }

    @Override
    public void close() throws IOException {
        this.client.close();
    }

    private static class MonoFutureCallbackAdapter
    implements FutureCallback<Message<HttpResponse, Publisher<ByteBuffer>>> {
        private final MonoSink<ClientHttpResponse> sink;
        private final DataBufferFactory dataBufferFactory;
        private final HttpClientContext context;

        public MonoFutureCallbackAdapter(MonoSink<ClientHttpResponse> sink, DataBufferFactory dataBufferFactory, HttpClientContext context) {
            this.sink = sink;
            this.dataBufferFactory = dataBufferFactory;
            this.context = context;
        }

        public void completed(Message<HttpResponse, Publisher<ByteBuffer>> result) {
            this.sink.success((Object)new HttpComponentsClientHttpResponse(this.dataBufferFactory, result, this.context));
        }

        public void failed(Exception ex) {
            this.sink.error(ex instanceof HttpStreamResetException && ex.getCause() != null ? ex.getCause() : ex);
        }

        public void cancelled() {
        }
    }
}

