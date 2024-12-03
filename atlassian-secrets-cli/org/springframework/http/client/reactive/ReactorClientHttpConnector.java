/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 *  reactor.ipc.netty.http.client.HttpClient
 *  reactor.ipc.netty.http.client.HttpClientOptions$Builder
 *  reactor.ipc.netty.http.client.HttpClientRequest
 *  reactor.ipc.netty.http.client.HttpClientResponse
 */
package org.springframework.http.client.reactive;

import io.netty.buffer.UnpooledByteBufAllocator;
import java.net.URI;
import java.util.function.Consumer;
import java.util.function.Function;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.client.reactive.ReactorClientHttpRequest;
import org.springframework.http.client.reactive.ReactorClientHttpResponse;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.client.HttpClient;
import reactor.ipc.netty.http.client.HttpClientOptions;
import reactor.ipc.netty.http.client.HttpClientRequest;
import reactor.ipc.netty.http.client.HttpClientResponse;

public class ReactorClientHttpConnector
implements ClientHttpConnector {
    static final NettyDataBufferFactory BUFFER_FACTORY = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false));
    private final HttpClient httpClient;

    public ReactorClientHttpConnector() {
        this.httpClient = HttpClient.builder().options(options -> options.compression(true)).build();
    }

    public ReactorClientHttpConnector(Consumer<? super HttpClientOptions.Builder> clientOptions) {
        this.httpClient = HttpClient.create(clientOptions);
    }

    @Override
    public Mono<ClientHttpResponse> connect(HttpMethod method, URI uri, Function<? super ClientHttpRequest, Mono<Void>> requestCallback) {
        if (!uri.isAbsolute()) {
            return Mono.error((Throwable)new IllegalArgumentException("URI is not absolute: " + uri));
        }
        return this.httpClient.request(this.adaptHttpMethod(method), uri.toString(), request -> (Mono)requestCallback.apply(this.adaptRequest(method, uri, (HttpClientRequest)request))).map(this::adaptResponse);
    }

    private io.netty.handler.codec.http.HttpMethod adaptHttpMethod(HttpMethod method) {
        return io.netty.handler.codec.http.HttpMethod.valueOf(method.name());
    }

    private ReactorClientHttpRequest adaptRequest(HttpMethod method, URI uri, HttpClientRequest request) {
        return new ReactorClientHttpRequest(method, uri, request);
    }

    private ClientHttpResponse adaptResponse(HttpClientResponse response) {
        return new ReactorClientHttpResponse(response);
    }
}

