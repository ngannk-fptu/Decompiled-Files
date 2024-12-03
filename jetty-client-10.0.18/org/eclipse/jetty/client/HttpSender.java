/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.http.HttpHeader
 *  org.eclipse.jetty.http.HttpHeaderValue
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.thread.Invocable$InvocationType
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.client.HttpChannel;
import org.eclipse.jetty.client.HttpConversation;
import org.eclipse.jetty.client.HttpDestination;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.client.HttpRequest;
import org.eclipse.jetty.client.RequestNotifier;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpHeaderValue;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.thread.Invocable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HttpSender {
    private static final Logger LOG = LoggerFactory.getLogger(HttpSender.class);
    private final ContentConsumer consumer = new ContentConsumer();
    private final AtomicReference<RequestState> requestState = new AtomicReference<RequestState>(RequestState.QUEUED);
    private final AtomicReference<Throwable> failure = new AtomicReference();
    private final HttpChannel channel;
    private Request.Content.Subscription subscription;

    protected HttpSender(HttpChannel channel) {
        this.channel = channel;
    }

    protected HttpChannel getHttpChannel() {
        return this.channel;
    }

    protected HttpExchange getHttpExchange() {
        return this.channel.getHttpExchange();
    }

    public boolean isFailed() {
        return this.requestState.get() == RequestState.FAILURE;
    }

    public void send(HttpExchange exchange) {
        if (!this.queuedToBegin(exchange)) {
            return;
        }
        if (!this.beginToHeaders(exchange)) {
            return;
        }
        this.demand();
    }

    protected boolean expects100Continue(Request request) {
        return request.getHeaders().contains(HttpHeader.EXPECT, HttpHeaderValue.CONTINUE.asString());
    }

    protected boolean queuedToBegin(HttpExchange exchange) {
        if (!this.updateRequestState(RequestState.QUEUED, RequestState.TRANSIENT)) {
            return false;
        }
        HttpRequest request = exchange.getRequest();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Request begin {}", (Object)request);
        }
        RequestNotifier notifier = this.getHttpChannel().getHttpDestination().getRequestNotifier();
        notifier.notifyBegin(request);
        Request.Content body = request.getBody();
        this.consumer.exchange = exchange;
        this.consumer.expect100 = this.expects100Continue(request);
        this.subscription = body.subscribe(this.consumer, !this.consumer.expect100);
        if (this.updateRequestState(RequestState.TRANSIENT, RequestState.BEGIN)) {
            return true;
        }
        this.abortRequest(exchange);
        return false;
    }

    protected boolean beginToHeaders(HttpExchange exchange) {
        if (!this.updateRequestState(RequestState.BEGIN, RequestState.TRANSIENT)) {
            return false;
        }
        HttpRequest request = exchange.getRequest();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Request headers {}{}{}", new Object[]{request, System.lineSeparator(), request.getHeaders().toString().trim()});
        }
        RequestNotifier notifier = this.getHttpChannel().getHttpDestination().getRequestNotifier();
        notifier.notifyHeaders(request);
        if (this.updateRequestState(RequestState.TRANSIENT, RequestState.HEADERS)) {
            return true;
        }
        this.abortRequest(exchange);
        return false;
    }

    protected boolean headersToCommit(HttpExchange exchange) {
        if (!this.updateRequestState(RequestState.HEADERS, RequestState.TRANSIENT)) {
            return false;
        }
        HttpRequest request = exchange.getRequest();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Request committed {}", (Object)request);
        }
        RequestNotifier notifier = this.getHttpChannel().getHttpDestination().getRequestNotifier();
        notifier.notifyCommit(request);
        if (this.updateRequestState(RequestState.TRANSIENT, RequestState.COMMIT)) {
            return true;
        }
        this.abortRequest(exchange);
        return false;
    }

    protected boolean someToContent(HttpExchange exchange, ByteBuffer content) {
        RequestState current = this.requestState.get();
        switch (current) {
            case COMMIT: 
            case CONTENT: {
                if (!this.updateRequestState(current, RequestState.TRANSIENT)) {
                    return false;
                }
                HttpRequest request = exchange.getRequest();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Request content {}{}{}", new Object[]{request, System.lineSeparator(), BufferUtil.toDetailString((ByteBuffer)content)});
                }
                RequestNotifier notifier = this.getHttpChannel().getHttpDestination().getRequestNotifier();
                notifier.notifyContent(request, content);
                if (this.updateRequestState(RequestState.TRANSIENT, RequestState.CONTENT)) {
                    return true;
                }
                this.abortRequest(exchange);
                return false;
            }
        }
        return false;
    }

    protected boolean someToSuccess(HttpExchange exchange) {
        RequestState current = this.requestState.get();
        switch (current) {
            case COMMIT: 
            case CONTENT: {
                if (!exchange.requestComplete(null)) {
                    return false;
                }
                this.requestState.set(RequestState.QUEUED);
                this.reset();
                HttpRequest request = exchange.getRequest();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Request success {}", (Object)request);
                }
                HttpDestination destination = this.getHttpChannel().getHttpDestination();
                destination.getRequestNotifier().notifySuccess(exchange.getRequest());
                Result result = exchange.terminateRequest();
                this.terminateRequest(exchange, null, result);
                return true;
            }
        }
        return false;
    }

    private void anyToFailure(Throwable failure) {
        HttpExchange exchange = this.getHttpExchange();
        if (exchange == null) {
            return;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Request failure {}", (Object)exchange.getRequest(), (Object)failure);
        }
        if (exchange.requestComplete(failure)) {
            this.executeAbort(exchange, failure);
        }
    }

    private void demand() {
        try {
            this.subscription.demand();
        }
        catch (Throwable x) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Failure invoking demand()", x);
            }
            this.anyToFailure(x);
        }
    }

    private void executeAbort(HttpExchange exchange, Throwable failure) {
        try {
            Executor executor = this.getHttpChannel().getHttpDestination().getHttpClient().getExecutor();
            executor.execute(() -> this.abort(exchange, failure));
        }
        catch (RejectedExecutionException x) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Exchange aborted {}", (Object)exchange, (Object)x);
            }
            this.abort(exchange, failure);
        }
    }

    private void abortRequest(HttpExchange exchange) {
        Throwable failure = this.failure.get();
        if (this.subscription != null) {
            this.subscription.fail(failure);
        }
        this.dispose();
        HttpRequest request = exchange.getRequest();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Request abort {} {} on {}: {}", new Object[]{request, exchange, this.getHttpChannel(), failure});
        }
        HttpDestination destination = this.getHttpChannel().getHttpDestination();
        destination.getRequestNotifier().notifyFailure(request, failure);
        Result result = exchange.terminateRequest();
        this.terminateRequest(exchange, failure, result);
    }

    private void terminateRequest(HttpExchange exchange, Throwable failure, Result result) {
        HttpRequest request = exchange.getRequest();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Terminating request {}", (Object)request);
        }
        if (result == null) {
            if (failure != null && exchange.responseComplete(failure)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Response failure from request {} {}", (Object)request, (Object)exchange);
                }
                this.getHttpChannel().abortResponse(exchange, failure);
            }
        } else {
            result = this.channel.exchangeTerminating(exchange, result);
            HttpDestination destination = this.getHttpChannel().getHttpDestination();
            boolean ordered = destination.getHttpClient().isStrictEventOrdering();
            if (!ordered) {
                this.channel.exchangeTerminated(exchange, result);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Request/Response {}: {}", (Object)(failure == null ? "succeeded" : "failed"), (Object)result);
            }
            HttpConversation conversation = exchange.getConversation();
            destination.getResponseNotifier().notifyComplete(conversation.getResponseListeners(), result);
            if (ordered) {
                this.channel.exchangeTerminated(exchange, result);
            }
        }
    }

    protected abstract void sendHeaders(HttpExchange var1, ByteBuffer var2, boolean var3, Callback var4);

    protected abstract void sendContent(HttpExchange var1, ByteBuffer var2, boolean var3, Callback var4);

    protected void reset() {
        this.consumer.reset();
    }

    protected void dispose() {
    }

    public void proceed(HttpExchange exchange, Throwable failure) {
        this.consumer.expect100 = false;
        if (failure == null) {
            this.demand();
        } else {
            this.anyToFailure(failure);
        }
    }

    public boolean abort(HttpExchange exchange, Throwable failure) {
        RequestState current;
        this.failure.compareAndSet(null, failure);
        do {
            if ((current = this.requestState.get()) != RequestState.FAILURE) continue;
            return false;
        } while (!this.updateRequestState(current, RequestState.FAILURE));
        boolean abort = current != RequestState.TRANSIENT;
        if (abort) {
            this.abortRequest(exchange);
            return true;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Concurrent failure: request termination skipped, performed by helpers");
        }
        return false;
    }

    private boolean updateRequestState(RequestState from, RequestState to) {
        boolean updated = this.requestState.compareAndSet(from, to);
        if (!updated && LOG.isDebugEnabled()) {
            LOG.debug("RequestState update failed: {} -> {}: {}", new Object[]{from, to, this.requestState.get()});
        }
        return updated;
    }

    protected String relativize(String path) {
        try {
            String result = path;
            URI uri = URI.create(result);
            if (uri.isAbsolute()) {
                result = uri.getPath();
            }
            return result.isEmpty() ? "/" : result;
        }
        catch (Throwable x) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Could not relativize {}", (Object)path);
            }
            return path;
        }
    }

    public String toString() {
        return String.format("%s@%x(req=%s,failure=%s)", this.getClass().getSimpleName(), this.hashCode(), this.requestState, this.failure);
    }

    private class ContentConsumer
    implements Request.Content.Consumer,
    Callback {
        private HttpExchange exchange;
        private boolean expect100;
        private ByteBuffer contentBuffer;
        private boolean lastContent;
        private Callback callback;
        private boolean committed;

        private ContentConsumer() {
        }

        private void reset() {
            this.exchange = null;
            this.contentBuffer = null;
            this.lastContent = false;
            this.callback = null;
            this.committed = false;
        }

        @Override
        public void onContent(ByteBuffer buffer, boolean last, Callback callback) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Content {} last={} for {}", new Object[]{BufferUtil.toDetailString((ByteBuffer)buffer), last, this.exchange.getRequest()});
            }
            this.contentBuffer = buffer.slice();
            this.lastContent = last;
            this.callback = callback;
            if (this.committed) {
                HttpSender.this.sendContent(this.exchange, buffer, last, this);
            } else {
                HttpSender.this.sendHeaders(this.exchange, buffer, last, this);
            }
        }

        @Override
        public void onFailure(Throwable failure) {
            this.failed(failure);
        }

        public void succeeded() {
            boolean proceed = false;
            if (this.committed) {
                proceed = HttpSender.this.someToContent(this.exchange, this.contentBuffer);
            } else {
                this.committed = true;
                if (HttpSender.this.headersToCommit(this.exchange)) {
                    proceed = true;
                    if (this.contentBuffer.hasRemaining()) {
                        proceed = HttpSender.this.someToContent(this.exchange, this.contentBuffer);
                    }
                }
            }
            this.callback.succeeded();
            if (!proceed) {
                return;
            }
            if (this.lastContent) {
                HttpSender.this.someToSuccess(this.exchange);
            } else if (this.expect100) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Expecting 100 Continue for {}", (Object)this.exchange.getRequest());
                }
            } else {
                HttpSender.this.demand();
            }
        }

        public void failed(Throwable x) {
            if (this.callback != null) {
                this.callback.failed(x);
            }
            HttpSender.this.anyToFailure(x);
        }

        public Invocable.InvocationType getInvocationType() {
            return Invocable.InvocationType.NON_BLOCKING;
        }
    }

    private static enum RequestState {
        TRANSIENT,
        QUEUED,
        BEGIN,
        HEADERS,
        COMMIT,
        CONTENT,
        FAILURE;

    }
}

