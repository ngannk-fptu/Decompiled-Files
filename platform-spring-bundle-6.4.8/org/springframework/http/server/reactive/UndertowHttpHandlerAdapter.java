/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.undertow.server.HttpHandler
 *  io.undertow.server.HttpServerExchange
 *  org.apache.commons.logging.Log
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 */
package org.springframework.http.server.reactive;

import io.undertow.server.HttpServerExchange;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.commons.logging.Log;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpLogging;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.HttpHeadResponseDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.UndertowServerHttpRequest;
import org.springframework.http.server.reactive.UndertowServerHttpResponse;
import org.springframework.util.Assert;

public class UndertowHttpHandlerAdapter
implements io.undertow.server.HttpHandler {
    private static final Log logger = HttpLogging.forLogName(UndertowHttpHandlerAdapter.class);
    private final HttpHandler httpHandler;
    private DataBufferFactory bufferFactory = DefaultDataBufferFactory.sharedInstance;

    public UndertowHttpHandlerAdapter(HttpHandler httpHandler) {
        Assert.notNull((Object)httpHandler, "HttpHandler must not be null");
        this.httpHandler = httpHandler;
    }

    public void setDataBufferFactory(DataBufferFactory bufferFactory) {
        Assert.notNull((Object)bufferFactory, "DataBufferFactory must not be null");
        this.bufferFactory = bufferFactory;
    }

    public DataBufferFactory getDataBufferFactory() {
        return this.bufferFactory;
    }

    public void handleRequest(HttpServerExchange exchange2) {
        UndertowServerHttpRequest request = null;
        try {
            request = new UndertowServerHttpRequest(exchange2, this.getDataBufferFactory());
        }
        catch (URISyntaxException ex) {
            if (logger.isWarnEnabled()) {
                logger.debug((Object)("Failed to get request URI: " + ex.getMessage()));
            }
            exchange2.setStatusCode(400);
            return;
        }
        ServerHttpResponse response = new UndertowServerHttpResponse(exchange2, this.getDataBufferFactory(), request);
        if (request.getMethod() == HttpMethod.HEAD) {
            response = new HttpHeadResponseDecorator(response);
        }
        HandlerResultSubscriber resultSubscriber = new HandlerResultSubscriber(exchange2, request);
        this.httpHandler.handle(request, response).subscribe((Subscriber)resultSubscriber);
    }

    private static class HandlerResultSubscriber
    implements Subscriber<Void> {
        private final HttpServerExchange exchange;
        private final String logPrefix;

        public HandlerResultSubscriber(HttpServerExchange exchange2, UndertowServerHttpRequest request) {
            this.exchange = exchange2;
            this.logPrefix = request.getLogPrefix();
        }

        public void onSubscribe(Subscription subscription) {
            subscription.request(Long.MAX_VALUE);
        }

        public void onNext(Void aVoid) {
        }

        public void onError(Throwable ex) {
            logger.trace((Object)(this.logPrefix + "Failed to complete: " + ex.getMessage()));
            if (this.exchange.isResponseStarted()) {
                try {
                    logger.debug((Object)(this.logPrefix + "Closing connection"));
                    this.exchange.getConnection().close();
                }
                catch (IOException iOException) {}
            } else {
                logger.debug((Object)(this.logPrefix + "Setting HttpServerExchange status to 500 Server Error"));
                this.exchange.setStatusCode(500);
                this.exchange.endExchange();
            }
        }

        public void onComplete() {
            logger.trace((Object)(this.logPrefix + "Handling completed"));
            this.exchange.endExchange();
        }
    }
}

