/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 *  reactor.ipc.netty.http.server.HttpServerRequest
 *  reactor.ipc.netty.http.server.HttpServerResponse
 */
package org.springframework.http.server.reactive;

import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.net.URISyntaxException;
import java.util.function.BiFunction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.HttpHeadResponseDecorator;
import org.springframework.http.server.reactive.ReactorServerHttpRequest;
import org.springframework.http.server.reactive.ReactorServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.server.HttpServerRequest;
import reactor.ipc.netty.http.server.HttpServerResponse;

public class ReactorHttpHandlerAdapter
implements BiFunction<HttpServerRequest, HttpServerResponse, Mono<Void>> {
    private static final NettyDataBufferFactory BUFFER_FACTORY = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false));
    private static final Log logger = LogFactory.getLog(ReactorHttpHandlerAdapter.class);
    private final HttpHandler httpHandler;

    public ReactorHttpHandlerAdapter(HttpHandler httpHandler) {
        Assert.notNull((Object)httpHandler, "HttpHandler must not be null");
        this.httpHandler = httpHandler;
    }

    @Override
    public Mono<Void> apply(HttpServerRequest request, HttpServerResponse response) {
        ServerHttpResponse adaptedResponse;
        ReactorServerHttpRequest adaptedRequest;
        try {
            adaptedRequest = new ReactorServerHttpRequest(request, BUFFER_FACTORY);
            adaptedResponse = new ReactorServerHttpResponse(response, BUFFER_FACTORY);
        }
        catch (URISyntaxException ex2) {
            if (logger.isWarnEnabled()) {
                logger.warn("Invalid URL for incoming request: " + ex2.getMessage());
            }
            response.status(HttpResponseStatus.BAD_REQUEST);
            return Mono.empty();
        }
        if (adaptedRequest.getMethod() == HttpMethod.HEAD) {
            adaptedResponse = new HttpHeadResponseDecorator(adaptedResponse);
        }
        return this.httpHandler.handle(adaptedRequest, adaptedResponse).doOnError(ex -> logger.warn("Handling completed with error: " + ex.getMessage())).doOnSuccess(aVoid -> logger.debug("Handling completed with success"));
    }
}

