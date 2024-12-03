/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.Lifecycle
 *  org.springframework.http.HttpHeaders
 *  org.springframework.lang.Nullable
 *  org.springframework.util.concurrent.ListenableFuture
 *  org.springframework.util.concurrent.ListenableFutureCallback
 */
package org.springframework.web.socket.client;

import java.util.List;
import java.util.Map;
import org.springframework.context.Lifecycle;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.ConnectionManagerSupport;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.handler.LoggingWebSocketHandlerDecorator;

public class WebSocketConnectionManager
extends ConnectionManagerSupport {
    private final WebSocketClient client;
    private final WebSocketHandler webSocketHandler;
    @Nullable
    private WebSocketSession webSocketSession;
    private final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

    public WebSocketConnectionManager(WebSocketClient client, WebSocketHandler webSocketHandler, String uriTemplate, Object ... uriVariables) {
        super(uriTemplate, uriVariables);
        this.client = client;
        this.webSocketHandler = this.decorateWebSocketHandler(webSocketHandler);
    }

    public void setSubProtocols(List<String> protocols) {
        this.headers.setSecWebSocketProtocol(protocols);
    }

    public List<String> getSubProtocols() {
        return this.headers.getSecWebSocketProtocol();
    }

    public void setOrigin(@Nullable String origin) {
        this.headers.setOrigin(origin);
    }

    @Nullable
    public String getOrigin() {
        return this.headers.getOrigin();
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers.clear();
        this.headers.putAll((Map<? extends String, ? extends List<String>>)headers);
    }

    public HttpHeaders getHeaders() {
        return this.headers;
    }

    @Override
    public void startInternal() {
        if (this.client instanceof Lifecycle && !((Lifecycle)this.client).isRunning()) {
            ((Lifecycle)this.client).start();
        }
        super.startInternal();
    }

    @Override
    public void stopInternal() throws Exception {
        if (this.client instanceof Lifecycle && ((Lifecycle)this.client).isRunning()) {
            ((Lifecycle)this.client).stop();
        }
        super.stopInternal();
    }

    @Override
    public boolean isConnected() {
        return this.webSocketSession != null && this.webSocketSession.isOpen();
    }

    @Override
    protected void openConnection() {
        if (this.logger.isInfoEnabled()) {
            this.logger.info((Object)("Connecting to WebSocket at " + this.getUri()));
        }
        ListenableFuture<WebSocketSession> future = this.client.doHandshake(this.webSocketHandler, this.headers, this.getUri());
        future.addCallback((ListenableFutureCallback)new ListenableFutureCallback<WebSocketSession>(){

            public void onSuccess(@Nullable WebSocketSession result) {
                WebSocketConnectionManager.this.webSocketSession = result;
                WebSocketConnectionManager.this.logger.info((Object)"Successfully connected");
            }

            public void onFailure(Throwable ex) {
                WebSocketConnectionManager.this.logger.error((Object)"Failed to connect", ex);
            }
        });
    }

    @Override
    protected void closeConnection() throws Exception {
        if (this.webSocketSession != null) {
            this.webSocketSession.close();
        }
    }

    protected WebSocketHandler decorateWebSocketHandler(WebSocketHandler handler) {
        return new LoggingWebSocketHandlerDecorator(handler);
    }
}

