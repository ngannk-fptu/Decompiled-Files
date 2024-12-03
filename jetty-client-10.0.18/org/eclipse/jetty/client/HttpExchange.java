/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.io.CyclicTimeouts$Expirable
 *  org.eclipse.jetty.util.thread.AutoLock
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client;

import java.util.List;
import org.eclipse.jetty.client.HttpChannel;
import org.eclipse.jetty.client.HttpConversation;
import org.eclipse.jetty.client.HttpDestination;
import org.eclipse.jetty.client.HttpRequest;
import org.eclipse.jetty.client.HttpResponse;
import org.eclipse.jetty.client.ResponseNotifier;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.io.CyclicTimeouts;
import org.eclipse.jetty.util.thread.AutoLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpExchange
implements CyclicTimeouts.Expirable {
    private static final Logger LOG = LoggerFactory.getLogger(HttpExchange.class);
    private final AutoLock lock = new AutoLock();
    private final HttpDestination destination;
    private final HttpRequest request;
    private final List<Response.ResponseListener> listeners;
    private final HttpResponse response;
    private State requestState = State.PENDING;
    private State responseState = State.PENDING;
    private HttpChannel _channel;
    private Throwable requestFailure;
    private Throwable responseFailure;

    public HttpExchange(HttpDestination destination, HttpRequest request, List<Response.ResponseListener> listeners) {
        this.destination = destination;
        this.request = request;
        this.listeners = listeners;
        this.response = new HttpResponse(request, listeners);
        HttpConversation conversation = request.getConversation();
        conversation.getExchanges().offer(this);
        conversation.updateResponseListeners(null);
    }

    public HttpDestination getHttpDestination() {
        return this.destination;
    }

    public HttpConversation getConversation() {
        return this.request.getConversation();
    }

    public HttpRequest getRequest() {
        return this.request;
    }

    public Throwable getRequestFailure() {
        try (AutoLock l = this.lock.lock();){
            Throwable throwable = this.requestFailure;
            return throwable;
        }
    }

    public List<Response.ResponseListener> getResponseListeners() {
        return this.listeners;
    }

    public HttpResponse getResponse() {
        return this.response;
    }

    public Throwable getResponseFailure() {
        try (AutoLock l = this.lock.lock();){
            Throwable throwable = this.responseFailure;
            return throwable;
        }
    }

    public long getExpireNanoTime() {
        return this.request.getTimeoutNanoTime();
    }

    boolean associate(HttpChannel channel) {
        boolean result = false;
        boolean abort = false;
        try (AutoLock l = this.lock.lock();){
            if (this.requestState == State.PENDING && this.responseState == State.PENDING) {
                boolean bl = abort = this._channel != null;
                if (!abort) {
                    this._channel = channel;
                    result = true;
                }
            }
        }
        if (abort) {
            this.request.abort(new IllegalStateException(this.toString()));
        }
        return result;
    }

    void disassociate(HttpChannel channel) {
        boolean abort = false;
        try (AutoLock l = this.lock.lock();){
            if (this._channel != channel || this.requestState != State.TERMINATED || this.responseState != State.TERMINATED) {
                abort = true;
            }
            this._channel = null;
        }
        if (abort) {
            this.request.abort(new IllegalStateException(this.toString()));
        }
    }

    private HttpChannel getHttpChannel() {
        try (AutoLock l = this.lock.lock();){
            HttpChannel httpChannel = this._channel;
            return httpChannel;
        }
    }

    public boolean requestComplete(Throwable failure) {
        try (AutoLock l = this.lock.lock();){
            boolean bl = this.completeRequest(failure);
            return bl;
        }
    }

    private boolean completeRequest(Throwable failure) {
        if (this.requestState == State.PENDING) {
            this.requestState = State.COMPLETED;
            this.requestFailure = failure;
            return true;
        }
        return false;
    }

    public boolean responseComplete(Throwable failure) {
        try (AutoLock l = this.lock.lock();){
            boolean bl = this.completeResponse(failure);
            return bl;
        }
    }

    private boolean completeResponse(Throwable failure) {
        if (this.responseState == State.PENDING) {
            this.responseState = State.COMPLETED;
            this.responseFailure = failure;
            return true;
        }
        return false;
    }

    public Result terminateRequest() {
        Result result = null;
        try (AutoLock l = this.lock.lock();){
            if (this.requestState == State.COMPLETED) {
                this.requestState = State.TERMINATED;
            }
            if (this.requestState == State.TERMINATED && this.responseState == State.TERMINATED) {
                result = new Result(this.getRequest(), this.requestFailure, this.getResponse(), this.responseFailure);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Terminated request for {}, result: {}", (Object)this, result);
        }
        return result;
    }

    public Result terminateResponse() {
        Result result = null;
        try (AutoLock l = this.lock.lock();){
            if (this.responseState == State.COMPLETED) {
                this.responseState = State.TERMINATED;
            }
            if (this.requestState == State.TERMINATED && this.responseState == State.TERMINATED) {
                result = new Result(this.getRequest(), this.requestFailure, this.getResponse(), this.responseFailure);
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Terminated response for {}, result: {}", (Object)this, result);
        }
        return result;
    }

    public boolean abort(Throwable failure) {
        boolean abortResponse;
        boolean abortRequest;
        try (AutoLock l = this.lock.lock();){
            abortRequest = this.completeRequest(failure);
            abortResponse = this.completeResponse(failure);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Failed {}: req={}/rsp={} {}", new Object[]{this, abortRequest, abortResponse, failure});
        }
        if (!abortRequest && !abortResponse) {
            return false;
        }
        Request.Content body = this.request.getBody();
        if (abortRequest && body != null) {
            body.fail(failure);
        }
        if (this.destination.remove(this)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Aborting while queued {}: {}", (Object)this, (Object)failure);
            }
            this.notifyFailureComplete(failure);
            return true;
        }
        HttpChannel channel = this.getHttpChannel();
        if (channel == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Aborted before association {}: {}", (Object)this, (Object)failure);
            }
            this.notifyFailureComplete(failure);
            return true;
        }
        boolean aborted = channel.abort(this, abortRequest ? failure : null, abortResponse ? failure : null);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Aborted ({}) while active {}: {}", new Object[]{aborted, this, failure});
        }
        return aborted;
    }

    private void notifyFailureComplete(Throwable failure) {
        this.destination.getRequestNotifier().notifyFailure(this.request, failure);
        List<Response.ResponseListener> listeners = this.getConversation().getResponseListeners();
        ResponseNotifier responseNotifier = this.destination.getResponseNotifier();
        responseNotifier.notifyFailure(listeners, (Response)this.response, failure);
        responseNotifier.notifyComplete(listeners, new Result(this.request, failure, this.response, failure));
    }

    public void resetResponse() {
        try (AutoLock l = this.lock.lock();){
            this.responseState = State.PENDING;
            this.responseFailure = null;
            this.response.clearHeaders();
        }
    }

    public void proceed(Throwable failure) {
        HttpChannel channel = this.getHttpChannel();
        if (channel != null) {
            channel.proceed(this, failure);
        }
    }

    public String toString() {
        try (AutoLock l = this.lock.lock();){
            String string = String.format("%s@%x{req=%s[%s/%s] res=%s[%s/%s]}", new Object[]{HttpExchange.class.getSimpleName(), this.hashCode(), this.request, this.requestState, this.requestFailure, this.response, this.responseState, this.responseFailure});
            return string;
        }
    }

    private static enum State {
        PENDING,
        COMPLETED,
        TERMINATED;

    }
}

