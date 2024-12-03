/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.client.HttpClient
 *  org.eclipse.jetty.client.api.Request
 *  org.eclipse.jetty.reactive.client.ContentChunk
 *  org.eclipse.jetty.reactive.client.ReactiveResponse
 *  org.reactivestreams.Publisher
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.client.reactive;

import java.net.URI;
import java.util.function.Consumer;
import java.util.function.Function;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.reactive.client.ContentChunk;
import org.eclipse.jetty.reactive.client.ReactiveResponse;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.client.reactive.JettyClientHttpRequest;
import org.springframework.http.client.reactive.JettyClientHttpResponse;
import org.springframework.http.client.reactive.JettyResourceFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class JettyClientHttpConnector
implements ClientHttpConnector {
    private final HttpClient httpClient;
    private DataBufferFactory bufferFactory = DefaultDataBufferFactory.sharedInstance;

    public JettyClientHttpConnector() {
        this(new HttpClient());
    }

    public JettyClientHttpConnector(HttpClient httpClient) {
        this(httpClient, null);
    }

    public JettyClientHttpConnector(HttpClient httpClient, @Nullable JettyResourceFactory resourceFactory) {
        Assert.notNull((Object)httpClient, "HttpClient is required");
        if (resourceFactory != null) {
            httpClient.setExecutor(resourceFactory.getExecutor());
            httpClient.setByteBufferPool(resourceFactory.getByteBufferPool());
            httpClient.setScheduler(resourceFactory.getScheduler());
        }
        this.httpClient = httpClient;
    }

    @Deprecated
    public JettyClientHttpConnector(JettyResourceFactory resourceFactory, @Nullable Consumer<HttpClient> customizer2) {
        this(new HttpClient(), resourceFactory);
        if (customizer2 != null) {
            customizer2.accept(this.httpClient);
        }
    }

    public void setBufferFactory(DataBufferFactory bufferFactory) {
        this.bufferFactory = bufferFactory;
    }

    @Override
    public Mono<ClientHttpResponse> connect(HttpMethod method, URI uri, Function<? super ClientHttpRequest, Mono<Void>> requestCallback) {
        if (!uri.isAbsolute()) {
            return Mono.error((Throwable)new IllegalArgumentException("URI is not absolute: " + uri));
        }
        if (!this.httpClient.isStarted()) {
            try {
                this.httpClient.start();
            }
            catch (Exception ex) {
                return Mono.error((Throwable)ex);
            }
        }
        Request jettyRequest = this.httpClient.newRequest(uri).method(method.toString());
        JettyClientHttpRequest request = new JettyClientHttpRequest(jettyRequest, this.bufferFactory);
        return requestCallback.apply(request).then(this.execute(request));
    }

    private Mono<ClientHttpResponse> execute(JettyClientHttpRequest request) {
        return Mono.fromDirect((Publisher)request.toReactiveRequest().response((reactiveResponse, chunkPublisher) -> {
            Flux content = Flux.from((Publisher)chunkPublisher).map(this::toDataBuffer);
            return Mono.just((Object)new JettyClientHttpResponse((ReactiveResponse)reactiveResponse, (Publisher<DataBuffer>)content));
        }));
    }

    private DataBuffer toDataBuffer(ContentChunk chunk) {
        DataBuffer buffer = this.bufferFactory.allocateBuffer(chunk.buffer.capacity());
        buffer.write(chunk.buffer);
        chunk.callback.succeeded();
        return buffer;
    }
}

