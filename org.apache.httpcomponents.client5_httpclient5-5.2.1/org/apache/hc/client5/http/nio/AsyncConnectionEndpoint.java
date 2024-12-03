/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.concurrent.BasicFuture
 *  org.apache.hc.core5.concurrent.FutureCallback
 *  org.apache.hc.core5.http.nio.AsyncClientExchangeHandler
 *  org.apache.hc.core5.http.nio.AsyncPushConsumer
 *  org.apache.hc.core5.http.nio.AsyncRequestProducer
 *  org.apache.hc.core5.http.nio.AsyncResponseConsumer
 *  org.apache.hc.core5.http.nio.HandlerFactory
 *  org.apache.hc.core5.http.nio.support.BasicClientExchangeHandler
 *  org.apache.hc.core5.http.protocol.HttpContext
 *  org.apache.hc.core5.http.protocol.HttpCoreContext
 *  org.apache.hc.core5.io.CloseMode
 *  org.apache.hc.core5.io.ModalCloseable
 *  org.apache.hc.core5.util.Timeout
 */
package org.apache.hc.client5.http.nio;

import java.io.IOException;
import java.util.concurrent.Future;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.concurrent.BasicFuture;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.nio.AsyncClientExchangeHandler;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.AsyncResponseConsumer;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.nio.support.BasicClientExchangeHandler;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.http.protocol.HttpCoreContext;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.io.ModalCloseable;
import org.apache.hc.core5.util.Timeout;

@Contract(threading=ThreadingBehavior.SAFE)
public abstract class AsyncConnectionEndpoint
implements ModalCloseable {
    public abstract void execute(String var1, AsyncClientExchangeHandler var2, HandlerFactory<AsyncPushConsumer> var3, HttpContext var4);

    public abstract boolean isConnected();

    public abstract void setSocketTimeout(Timeout var1);

    public final void close() throws IOException {
        this.close(CloseMode.GRACEFUL);
    }

    public void execute(String id, AsyncClientExchangeHandler exchangeHandler, HttpContext context) {
        this.execute(id, exchangeHandler, null, context);
    }

    public final <T> Future<T> execute(String id, AsyncRequestProducer requestProducer, AsyncResponseConsumer<T> responseConsumer, HandlerFactory<AsyncPushConsumer> pushHandlerFactory, HttpContext context, FutureCallback<T> callback) {
        final BasicFuture future = new BasicFuture(callback);
        this.execute(id, (AsyncClientExchangeHandler)new BasicClientExchangeHandler(requestProducer, responseConsumer, new FutureCallback<T>(){

            public void completed(T result) {
                future.completed(result);
            }

            public void failed(Exception ex) {
                future.failed(ex);
            }

            public void cancelled() {
                future.cancel();
            }
        }), pushHandlerFactory, (HttpContext)(context != null ? context : HttpCoreContext.create()));
        return future;
    }

    public final <T> Future<T> execute(String id, AsyncRequestProducer requestProducer, AsyncResponseConsumer<T> responseConsumer, HttpContext context, FutureCallback<T> callback) {
        return this.execute(id, requestProducer, responseConsumer, null, context, callback);
    }

    public final <T> Future<T> execute(String id, AsyncRequestProducer requestProducer, AsyncResponseConsumer<T> responseConsumer, HandlerFactory<AsyncPushConsumer> pushHandlerFactory, FutureCallback<T> callback) {
        return this.execute(id, requestProducer, responseConsumer, pushHandlerFactory, null, callback);
    }

    public final <T> Future<T> execute(String id, AsyncRequestProducer requestProducer, AsyncResponseConsumer<T> responseConsumer, FutureCallback<T> callback) {
        return this.execute(id, requestProducer, responseConsumer, null, null, callback);
    }
}

