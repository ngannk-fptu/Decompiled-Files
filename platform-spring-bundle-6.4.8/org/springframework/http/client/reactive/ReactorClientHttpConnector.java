/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.handler.codec.http.HttpMethod
 *  reactor.core.publisher.Mono
 *  reactor.netty.Connection
 *  reactor.netty.NettyOutbound
 *  reactor.netty.http.client.HttpClient
 *  reactor.netty.http.client.HttpClient$RequestSender
 *  reactor.netty.http.client.HttpClientRequest
 *  reactor.netty.http.client.HttpClientResponse
 *  reactor.netty.resources.ConnectionProvider
 *  reactor.netty.resources.LoopResources
 */
package org.springframework.http.client.reactive;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.client.reactive.ReactorClientHttpRequest;
import org.springframework.http.client.reactive.ReactorClientHttpResponse;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.NettyOutbound;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientRequest;
import reactor.netty.http.client.HttpClientResponse;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

public class ReactorClientHttpConnector
implements ClientHttpConnector {
    private static final Function<HttpClient, HttpClient> defaultInitializer = client -> client.compress(true);
    private final HttpClient httpClient;

    public ReactorClientHttpConnector() {
        this.httpClient = defaultInitializer.apply(HttpClient.create());
    }

    public ReactorClientHttpConnector(ReactorResourceFactory factory, Function<HttpClient, HttpClient> mapper) {
        ConnectionProvider provider = factory.getConnectionProvider();
        Assert.notNull((Object)provider, "No ConnectionProvider: is ReactorResourceFactory not initialized yet?");
        this.httpClient = defaultInitializer.andThen(mapper).andThen(ReactorClientHttpConnector.applyLoopResources(factory)).apply(HttpClient.create((ConnectionProvider)provider));
    }

    private static Function<HttpClient, HttpClient> applyLoopResources(ReactorResourceFactory factory) {
        return httpClient -> {
            LoopResources resources2 = factory.getLoopResources();
            Assert.notNull((Object)resources2, "No LoopResources: is ReactorResourceFactory not initialized yet?");
            return (HttpClient)httpClient.runOn(resources2);
        };
    }

    public ReactorClientHttpConnector(HttpClient httpClient) {
        Assert.notNull((Object)httpClient, "HttpClient is required");
        this.httpClient = httpClient;
    }

    @Override
    public Mono<ClientHttpResponse> connect(HttpMethod method, URI uri, Function<? super ClientHttpRequest, Mono<Void>> requestCallback) {
        AtomicReference responseRef = new AtomicReference();
        return ((HttpClient.RequestSender)this.httpClient.request(io.netty.handler.codec.http.HttpMethod.valueOf((String)method.name())).uri(uri.toString())).send((request, outbound) -> (Mono)requestCallback.apply(this.adaptRequest(method, uri, (HttpClientRequest)request, (NettyOutbound)outbound))).responseConnection((response, connection) -> {
            responseRef.set(new ReactorClientHttpResponse((HttpClientResponse)response, (Connection)connection));
            return Mono.just((Object)((ClientHttpResponse)responseRef.get()));
        }).next().doOnCancel(() -> {
            ReactorClientHttpResponse response = (ReactorClientHttpResponse)responseRef.get();
            if (response != null) {
                response.releaseAfterCancel(method);
            }
        });
    }

    private ReactorClientHttpRequest adaptRequest(HttpMethod method, URI uri, HttpClientRequest request, NettyOutbound nettyOutbound) {
        return new ReactorClientHttpRequest(method, uri, request, nettyOutbound);
    }
}

