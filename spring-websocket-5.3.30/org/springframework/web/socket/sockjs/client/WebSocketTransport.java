/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.context.Lifecycle
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.concurrent.ListenableFuture
 *  org.springframework.util.concurrent.ListenableFutureCallback
 *  org.springframework.util.concurrent.SettableListenableFuture
 */
package org.springframework.web.socket.sockjs.client;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.Lifecycle;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SettableListenableFuture;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.TransportRequest;
import org.springframework.web.socket.sockjs.client.WebSocketClientSockJsSession;
import org.springframework.web.socket.sockjs.transport.TransportType;

public class WebSocketTransport
implements Transport,
Lifecycle {
    private static final Log logger = LogFactory.getLog(WebSocketTransport.class);
    private final WebSocketClient webSocketClient;
    private volatile boolean running;

    public WebSocketTransport(WebSocketClient webSocketClient) {
        Assert.notNull((Object)webSocketClient, (String)"WebSocketClient is required");
        this.webSocketClient = webSocketClient;
    }

    public WebSocketClient getWebSocketClient() {
        return this.webSocketClient;
    }

    @Override
    public List<TransportType> getTransportTypes() {
        return Collections.singletonList(TransportType.WEBSOCKET);
    }

    @Override
    public ListenableFuture<WebSocketSession> connect(TransportRequest request, WebSocketHandler handler) {
        final SettableListenableFuture future = new SettableListenableFuture();
        WebSocketClientSockJsSession session = new WebSocketClientSockJsSession(request, handler, (SettableListenableFuture<WebSocketSession>)future);
        handler = new ClientSockJsWebSocketHandler(session);
        request.addTimeoutTask(session.getTimeoutTask());
        URI url = request.getTransportUrl();
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders(request.getHandshakeHeaders());
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("Starting WebSocket session on " + url));
        }
        this.webSocketClient.doHandshake(handler, headers, url).addCallback((ListenableFutureCallback)new ListenableFutureCallback<WebSocketSession>(){

            public void onSuccess(@Nullable WebSocketSession webSocketSession) {
            }

            public void onFailure(Throwable ex) {
                future.setException(ex);
            }
        });
        return future;
    }

    public void start() {
        if (!this.isRunning()) {
            if (this.webSocketClient instanceof Lifecycle) {
                ((Lifecycle)this.webSocketClient).start();
            } else {
                this.running = true;
            }
        }
    }

    public void stop() {
        if (this.isRunning()) {
            if (this.webSocketClient instanceof Lifecycle) {
                ((Lifecycle)this.webSocketClient).stop();
            } else {
                this.running = false;
            }
        }
    }

    public boolean isRunning() {
        if (this.webSocketClient instanceof Lifecycle) {
            return ((Lifecycle)this.webSocketClient).isRunning();
        }
        return this.running;
    }

    public String toString() {
        return "WebSocketTransport[client=" + this.webSocketClient + "]";
    }

    private static class ClientSockJsWebSocketHandler
    extends TextWebSocketHandler {
        private final WebSocketClientSockJsSession sockJsSession;
        private final AtomicBoolean connected = new AtomicBoolean();

        public ClientSockJsWebSocketHandler(WebSocketClientSockJsSession session) {
            Assert.notNull((Object)session, (String)"Session must not be null");
            this.sockJsSession = session;
        }

        @Override
        public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
            Assert.state((boolean)this.connected.compareAndSet(false, true), (String)"Already connected");
            this.sockJsSession.initializeDelegateSession(webSocketSession);
        }

        @Override
        public void handleTextMessage(WebSocketSession webSocketSession, TextMessage message) throws Exception {
            this.sockJsSession.handleFrame((String)message.getPayload());
        }

        @Override
        public void handleTransportError(WebSocketSession webSocketSession, Throwable ex) throws Exception {
            this.sockJsSession.handleTransportError(ex);
        }

        @Override
        public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus status) throws Exception {
            this.sockJsSession.afterTransportClosed(status);
        }
    }
}

