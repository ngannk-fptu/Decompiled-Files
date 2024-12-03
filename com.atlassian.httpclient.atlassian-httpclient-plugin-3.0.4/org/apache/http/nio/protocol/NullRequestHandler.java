/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.nio.protocol;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.nio.protocol.ErrorResponseProducer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.nio.protocol.HttpAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.nio.protocol.NullRequestConsumer;
import org.apache.http.protocol.HttpContext;

class NullRequestHandler
implements HttpAsyncRequestHandler<Object> {
    public static final NullRequestHandler INSTANCE = new NullRequestHandler();

    @Override
    public HttpAsyncRequestConsumer<Object> processRequest(HttpRequest request, HttpContext context) {
        return new NullRequestConsumer();
    }

    @Override
    public void handle(Object obj, HttpAsyncExchange httpexchange, HttpContext context) {
        HttpResponse response = httpexchange.getResponse();
        response.setStatusCode(501);
        httpexchange.submitResponse(new ErrorResponseProducer(response, new NStringEntity("Service not implemented", ContentType.TEXT_PLAIN), true));
    }
}

