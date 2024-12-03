/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http2.nio.support;

import org.apache.hc.core5.function.Supplier;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestMapper;
import org.apache.hc.core5.http.MisdirectedRequestException;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.Args;

public final class DefaultAsyncPushConsumerFactory
implements HandlerFactory<AsyncPushConsumer> {
    private final HttpRequestMapper<Supplier<AsyncPushConsumer>> mapper;

    public DefaultAsyncPushConsumerFactory(HttpRequestMapper<Supplier<AsyncPushConsumer>> mapper) {
        this.mapper = Args.notNull(mapper, "Request handler mapper");
    }

    @Override
    public AsyncPushConsumer create(HttpRequest request, HttpContext context) throws HttpException {
        try {
            Supplier<AsyncPushConsumer> supplier = this.mapper.resolve(request, context);
            return supplier != null ? supplier.get() : null;
        }
        catch (MisdirectedRequestException ex) {
            return null;
        }
    }
}

