/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.handler.codec.http.HttpResponseStatus
 *  org.apache.commons.logging.Log
 *  org.springframework.core.io.buffer.DataBufferFactory
 *  org.springframework.core.io.buffer.NettyDataBufferFactory
 *  org.springframework.util.Assert
 *  reactor.core.publisher.Mono
 *  reactor.netty.http.server.HttpServerRequest
 *  reactor.netty.http.server.HttpServerResponse
 */
package org.springframework.http.server.reactive;

import io.netty.handler.codec.http.HttpResponseStatus;
import java.net.URISyntaxException;
import java.util.function.BiFunction;
import org.apache.commons.logging.Log;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpLogging;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.HttpHeadResponseDecorator;
import org.springframework.http.server.reactive.ReactorServerHttpRequest;
import org.springframework.http.server.reactive.ReactorServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

public class ReactorHttpHandlerAdapter
implements BiFunction<HttpServerRequest, HttpServerResponse, Mono<Void>> {
    private static final Log logger = HttpLogging.forLogName(ReactorHttpHandlerAdapter.class);
    private final HttpHandler httpHandler;

    public ReactorHttpHandlerAdapter(HttpHandler httpHandler) {
        Assert.notNull((Object)httpHandler, (String)"HttpHandler must not be null");
        this.httpHandler = httpHandler;
    }

    @Override
    public Mono<Void> apply(HttpServerRequest reactorRequest, HttpServerResponse reactorResponse) {
        NettyDataBufferFactory bufferFactory = new NettyDataBufferFactory(reactorResponse.alloc());
        try {
            ReactorServerHttpRequest request = new ReactorServerHttpRequest(reactorRequest, bufferFactory);
            ServerHttpResponse response = new ReactorServerHttpResponse(reactorResponse, (DataBufferFactory)bufferFactory);
            if (request.getMethod() == HttpMethod.HEAD) {
                response = new HttpHeadResponseDecorator(response);
            }
            return this.httpHandler.handle(request, response).doOnError(ex -> logger.trace((Object)(request.getLogPrefix() + "Failed to complete: " + ex.getMessage()))).doOnSuccess(aVoid -> logger.trace((Object)(request.getLogPrefix() + "Handling completed")));
        }
        catch (URISyntaxException ex2) {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Failed to get request URI: " + ex2.getMessage()));
            }
            reactorResponse.status(HttpResponseStatus.BAD_REQUEST);
            return Mono.empty();
        }
    }
}

