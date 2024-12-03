/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.nio.command;

import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hc.core5.annotation.Internal;
import org.apache.hc.core5.concurrent.CancellableDependency;
import org.apache.hc.core5.http.RequestNotExecutedException;
import org.apache.hc.core5.http.nio.AsyncClientExchangeHandler;
import org.apache.hc.core5.http.nio.AsyncPushConsumer;
import org.apache.hc.core5.http.nio.HandlerFactory;
import org.apache.hc.core5.http.nio.command.ExecutableCommand;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.Args;

@Internal
public final class RequestExecutionCommand
extends ExecutableCommand {
    private final AsyncClientExchangeHandler exchangeHandler;
    private final HandlerFactory<AsyncPushConsumer> pushHandlerFactory;
    private final CancellableDependency cancellableDependency;
    private final HttpContext context;
    private final AtomicBoolean failed;

    public RequestExecutionCommand(AsyncClientExchangeHandler exchangeHandler, HandlerFactory<AsyncPushConsumer> pushHandlerFactory, CancellableDependency cancellableDependency, HttpContext context) {
        this.exchangeHandler = Args.notNull(exchangeHandler, "Handler");
        this.pushHandlerFactory = pushHandlerFactory;
        this.cancellableDependency = cancellableDependency;
        this.context = context;
        this.failed = new AtomicBoolean();
    }

    public RequestExecutionCommand(AsyncClientExchangeHandler exchangeHandler, HandlerFactory<AsyncPushConsumer> pushHandlerFactory, HttpContext context) {
        this(exchangeHandler, pushHandlerFactory, null, context);
    }

    public RequestExecutionCommand(AsyncClientExchangeHandler exchangeHandler, HttpContext context) {
        this(exchangeHandler, null, null, context);
    }

    public AsyncClientExchangeHandler getExchangeHandler() {
        return this.exchangeHandler;
    }

    public HandlerFactory<AsyncPushConsumer> getPushHandlerFactory() {
        return this.pushHandlerFactory;
    }

    @Override
    public CancellableDependency getCancellableDependency() {
        return this.cancellableDependency;
    }

    public HttpContext getContext() {
        return this.context;
    }

    @Override
    public void failed(Exception ex) {
        if (this.failed.compareAndSet(false, true)) {
            try {
                this.exchangeHandler.failed(ex);
            }
            finally {
                this.exchangeHandler.releaseResources();
            }
        }
    }

    @Override
    public boolean cancel() {
        if (this.failed.compareAndSet(false, true)) {
            try {
                this.exchangeHandler.failed(new RequestNotExecutedException());
                boolean bl = true;
                return bl;
            }
            finally {
                this.exchangeHandler.releaseResources();
            }
        }
        return false;
    }
}

