/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.web.socket.handler;

import org.springframework.util.Assert;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

public class WebSocketHandlerDecorator
implements WebSocketHandler {
    private final WebSocketHandler delegate;

    public WebSocketHandlerDecorator(WebSocketHandler delegate) {
        Assert.notNull((Object)delegate, (String)"Delegate must not be null");
        this.delegate = delegate;
    }

    public WebSocketHandler getDelegate() {
        return this.delegate;
    }

    public WebSocketHandler getLastHandler() {
        WebSocketHandler result = this.delegate;
        while (result instanceof WebSocketHandlerDecorator) {
            result = ((WebSocketHandlerDecorator)result).getDelegate();
        }
        return result;
    }

    public static WebSocketHandler unwrap(WebSocketHandler handler) {
        if (handler instanceof WebSocketHandlerDecorator) {
            return ((WebSocketHandlerDecorator)handler).getLastHandler();
        }
        return handler;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.delegate.afterConnectionEstablished(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        this.delegate.handleMessage(session, message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        this.delegate.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        this.delegate.afterConnectionClosed(session, closeStatus);
    }

    @Override
    public boolean supportsPartialMessages() {
        return this.delegate.supportsPartialMessages();
    }

    public String toString() {
        return this.getClass().getSimpleName() + " [delegate=" + this.delegate + "]";
    }
}

