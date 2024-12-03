/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.undertow.server.HttpHandler
 *  io.undertow.server.HttpServerExchange
 */
package org.springframework.http.server.reactive;

import io.undertow.server.HttpServerExchange;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.HttpHeadResponseDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.UndertowServerHttpRequest;
import org.springframework.http.server.reactive.UndertowServerHttpResponse;
import org.springframework.util.Assert;

public class UndertowHttpHandlerAdapter
implements io.undertow.server.HttpHandler {
    private static final Log logger = LogFactory.getLog(UndertowHttpHandlerAdapter.class);
    private final HttpHandler httpHandler;
    private DataBufferFactory bufferFactory = new DefaultDataBufferFactory(false);

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
                logger.warn("Invalid URL for incoming request: " + ex.getMessage());
            }
            exchange2.setStatusCode(400);
            return;
        }
        ServerHttpResponse response = new UndertowServerHttpResponse(exchange2, this.getDataBufferFactory());
        if (request.getMethod() == HttpMethod.HEAD) {
            response = new HttpHeadResponseDecorator(response);
        }
        HandlerResultSubscriber resultSubscriber = new HandlerResultSubscriber(exchange2);
        this.httpHandler.handle(request, response).subscribe((Subscriber)resultSubscriber);
    }

    private class HandlerResultSubscriber
    implements Subscriber<Void> {
        private final HttpServerExchange exchange;

        public HandlerResultSubscriber(HttpServerExchange exchange2) {
            this.exchange = exchange2;
        }

        @Override
        public void onSubscribe(Subscription subscription) {
            subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(Void aVoid) {
        }

        @Override
        public void onError(Throwable ex) {
            logger.warn("Handling completed with error: " + ex.getMessage());
            if (this.exchange.isResponseStarted()) {
                try {
                    logger.debug("Closing connection");
                    this.exchange.getConnection().close();
                }
                catch (IOException iOException) {}
            } else {
                logger.debug("Setting response status code to 500");
                this.exchange.setStatusCode(500);
                this.exchange.endExchange();
            }
        }

        @Override
        public void onComplete() {
            logger.debug("Handling completed with success");
            this.exchange.endExchange();
        }
    }
}

