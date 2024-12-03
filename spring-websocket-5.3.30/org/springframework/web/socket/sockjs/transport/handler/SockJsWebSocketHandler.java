/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.web.socket.sockjs.transport.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.util.Assert;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.sockjs.transport.SockJsServiceConfig;
import org.springframework.web.socket.sockjs.transport.session.WebSocketServerSockJsSession;

public class SockJsWebSocketHandler
extends TextWebSocketHandler
implements SubProtocolCapable {
    private final SockJsServiceConfig sockJsServiceConfig;
    private final WebSocketServerSockJsSession sockJsSession;
    private final List<String> subProtocols;
    private final AtomicInteger sessionCount = new AtomicInteger();

    public SockJsWebSocketHandler(SockJsServiceConfig serviceConfig, WebSocketHandler webSocketHandler, WebSocketServerSockJsSession sockJsSession) {
        Assert.notNull((Object)serviceConfig, (String)"serviceConfig must not be null");
        Assert.notNull((Object)webSocketHandler, (String)"webSocketHandler must not be null");
        Assert.notNull((Object)sockJsSession, (String)"session must not be null");
        this.sockJsServiceConfig = serviceConfig;
        this.sockJsSession = sockJsSession;
        webSocketHandler = WebSocketHandlerDecorator.unwrap(webSocketHandler);
        this.subProtocols = webSocketHandler instanceof SubProtocolCapable ? new ArrayList<String>(((SubProtocolCapable)((Object)webSocketHandler)).getSubProtocols()) : Collections.emptyList();
    }

    @Override
    public List<String> getSubProtocols() {
        return this.subProtocols;
    }

    protected SockJsServiceConfig getSockJsConfig() {
        return this.sockJsServiceConfig;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession wsSession) throws Exception {
        Assert.isTrue((boolean)this.sessionCount.compareAndSet(0, 1), (String)"Unexpected connection");
        this.sockJsSession.initializeDelegateSession(wsSession);
    }

    @Override
    public void handleTextMessage(WebSocketSession wsSession, TextMessage message) throws Exception {
        this.sockJsSession.handleMessage(message, wsSession);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession wsSession, CloseStatus status) throws Exception {
        this.sockJsSession.delegateConnectionClosed(status);
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable exception) throws Exception {
        this.sockJsSession.delegateError(exception);
    }
}

