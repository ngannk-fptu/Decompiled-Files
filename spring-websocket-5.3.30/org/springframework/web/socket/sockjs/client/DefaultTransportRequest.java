/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.http.HttpHeaders
 *  org.springframework.lang.Nullable
 *  org.springframework.scheduling.TaskScheduler
 *  org.springframework.util.Assert
 *  org.springframework.util.concurrent.ListenableFutureCallback
 *  org.springframework.util.concurrent.SettableListenableFuture
 */
package org.springframework.web.socket.sockjs.client;

import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SettableListenableFuture;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.sockjs.SockJsTransportFailureException;
import org.springframework.web.socket.sockjs.client.SockJsUrlInfo;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.TransportRequest;
import org.springframework.web.socket.sockjs.frame.SockJsMessageCodec;
import org.springframework.web.socket.sockjs.transport.TransportType;

class DefaultTransportRequest
implements TransportRequest {
    private static final Log logger = LogFactory.getLog(DefaultTransportRequest.class);
    private final SockJsUrlInfo sockJsUrlInfo;
    private final HttpHeaders handshakeHeaders;
    private final HttpHeaders httpRequestHeaders;
    private final Transport transport;
    private final TransportType serverTransportType;
    private SockJsMessageCodec codec;
    @Nullable
    private Principal user;
    private long timeoutValue;
    @Nullable
    private TaskScheduler timeoutScheduler;
    private final List<Runnable> timeoutTasks = new ArrayList<Runnable>();
    @Nullable
    private DefaultTransportRequest fallbackRequest;

    public DefaultTransportRequest(SockJsUrlInfo sockJsUrlInfo, @Nullable HttpHeaders handshakeHeaders, @Nullable HttpHeaders httpRequestHeaders, Transport transport, TransportType serverTransportType, SockJsMessageCodec codec) {
        Assert.notNull((Object)sockJsUrlInfo, (String)"SockJsUrlInfo is required");
        Assert.notNull((Object)transport, (String)"Transport is required");
        Assert.notNull((Object)((Object)serverTransportType), (String)"TransportType is required");
        Assert.notNull((Object)codec, (String)"SockJsMessageCodec is required");
        this.sockJsUrlInfo = sockJsUrlInfo;
        this.handshakeHeaders = handshakeHeaders != null ? handshakeHeaders : new HttpHeaders();
        this.httpRequestHeaders = httpRequestHeaders != null ? httpRequestHeaders : new HttpHeaders();
        this.transport = transport;
        this.serverTransportType = serverTransportType;
        this.codec = codec;
    }

    @Override
    public SockJsUrlInfo getSockJsUrlInfo() {
        return this.sockJsUrlInfo;
    }

    @Override
    public HttpHeaders getHandshakeHeaders() {
        return this.handshakeHeaders;
    }

    @Override
    public HttpHeaders getHttpRequestHeaders() {
        return this.httpRequestHeaders;
    }

    @Override
    public URI getTransportUrl() {
        return this.sockJsUrlInfo.getTransportUrl(this.serverTransportType);
    }

    public void setUser(Principal user) {
        this.user = user;
    }

    @Override
    @Nullable
    public Principal getUser() {
        return this.user;
    }

    @Override
    public SockJsMessageCodec getMessageCodec() {
        return this.codec;
    }

    public void setTimeoutValue(long timeoutValue) {
        this.timeoutValue = timeoutValue;
    }

    public void setTimeoutScheduler(TaskScheduler scheduler) {
        this.timeoutScheduler = scheduler;
    }

    @Override
    public void addTimeoutTask(Runnable runnable) {
        this.timeoutTasks.add(runnable);
    }

    public void setFallbackRequest(DefaultTransportRequest fallbackRequest) {
        this.fallbackRequest = fallbackRequest;
    }

    public void connect(WebSocketHandler handler, SettableListenableFuture<WebSocketSession> future) {
        if (logger.isTraceEnabled()) {
            logger.trace((Object)("Starting " + this));
        }
        ConnectCallback connectCallback = new ConnectCallback(handler, future);
        this.scheduleConnectTimeoutTask(connectCallback);
        this.transport.connect(this, handler).addCallback((ListenableFutureCallback)connectCallback);
    }

    private void scheduleConnectTimeoutTask(ConnectCallback connectHandler) {
        if (this.timeoutScheduler != null) {
            if (logger.isTraceEnabled()) {
                logger.trace((Object)("Scheduling connect to time out after " + this.timeoutValue + " ms."));
            }
            Date timeoutDate = new Date(System.currentTimeMillis() + this.timeoutValue);
            this.timeoutScheduler.schedule((Runnable)connectHandler, timeoutDate);
        } else if (logger.isTraceEnabled()) {
            logger.trace((Object)"Connect timeout task not scheduled (no TaskScheduler configured).");
        }
    }

    public String toString() {
        return "TransportRequest[url=" + this.getTransportUrl() + "]";
    }

    private class ConnectCallback
    implements ListenableFutureCallback<WebSocketSession>,
    Runnable {
        private final WebSocketHandler handler;
        private final SettableListenableFuture<WebSocketSession> future;
        private final AtomicBoolean handled = new AtomicBoolean();

        public ConnectCallback(WebSocketHandler handler, SettableListenableFuture<WebSocketSession> future) {
            this.handler = handler;
            this.future = future;
        }

        public void onSuccess(@Nullable WebSocketSession session) {
            if (this.handled.compareAndSet(false, true)) {
                this.future.set((Object)session);
            } else if (logger.isErrorEnabled()) {
                logger.error((Object)("Connect success/failure already handled for " + DefaultTransportRequest.this));
            }
        }

        public void onFailure(Throwable ex) {
            this.handleFailure(ex, false);
        }

        @Override
        public void run() {
            this.handleFailure(null, true);
        }

        private void handleFailure(@Nullable Throwable ex, boolean isTimeoutFailure) {
            if (this.handled.compareAndSet(false, true)) {
                if (isTimeoutFailure) {
                    String message = "Connect timed out for " + DefaultTransportRequest.this;
                    logger.error((Object)message);
                    ex = new SockJsTransportFailureException(message, DefaultTransportRequest.this.getSockJsUrlInfo().getSessionId(), (Throwable)ex);
                }
                if (DefaultTransportRequest.this.fallbackRequest != null) {
                    logger.error((Object)(DefaultTransportRequest.this + " failed. Falling back on next transport."), ex);
                    DefaultTransportRequest.this.fallbackRequest.connect(this.handler, this.future);
                } else {
                    logger.error((Object)("No more fallback transports after " + DefaultTransportRequest.this), ex);
                    if (ex != null) {
                        this.future.setException(ex);
                    }
                }
                if (isTimeoutFailure) {
                    try {
                        for (Runnable runnable : DefaultTransportRequest.this.timeoutTasks) {
                            runnable.run();
                        }
                    }
                    catch (Throwable ex2) {
                        logger.error((Object)("Transport failed to run timeout tasks for " + DefaultTransportRequest.this), ex2);
                    }
                }
            } else {
                logger.error((Object)("Connect success/failure events already took place for " + DefaultTransportRequest.this + ". Ignoring this additional failure event."), ex);
            }
        }
    }
}

