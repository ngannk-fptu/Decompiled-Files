/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.socket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

public interface WebSocketHandler {
    public void afterConnectionEstablished(WebSocketSession var1) throws Exception;

    public void handleMessage(WebSocketSession var1, WebSocketMessage<?> var2) throws Exception;

    public void handleTransportError(WebSocketSession var1, Throwable var2) throws Exception;

    public void afterConnectionClosed(WebSocketSession var1, CloseStatus var2) throws Exception;

    public boolean supportsPartialMessages();
}

