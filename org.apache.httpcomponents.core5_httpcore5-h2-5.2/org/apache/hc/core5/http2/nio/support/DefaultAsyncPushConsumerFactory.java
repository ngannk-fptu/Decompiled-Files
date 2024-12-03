/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.function.Supplier
 *  org.apache.hc.core5.http.HttpException
 *  org.apache.hc.core5.http.HttpRequest
 *  org.apache.hc.core5.http.HttpRequestMapper
 *  org.apache.hc.core5.http.MisdirectedRequestException
 *  org.apache.hc.core5.http.nio.AsyncPushConsumer
 *  org.apache.hc.core5.http.nio.HandlerFactory
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.util.Args
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
        this.mapper = (HttpRequestMapper)Args.notNull(mapper, (String)"Request handler mapper");
    }

    public AsyncPushConsumer create(HttpRequest request, HttpContext context) throws HttpException {
        try {
            Supplier supplier = (Supplier)this.mapper.resolve(request, context);
            return supplier != null ? (AsyncPushConsumer)supplier.get() : null;
        }
        catch (MisdirectedRequestException ex) {
            return null;
        }
    }
}

