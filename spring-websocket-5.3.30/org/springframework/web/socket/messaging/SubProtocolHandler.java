/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.messaging.Message
 *  org.springframework.messaging.MessageChannel
 */
package org.springframework.web.socket.messaging;

import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

public interface SubProtocolHandler {
    public List<String> getSupportedProtocols();

    public void handleMessageFromClient(WebSocketSession var1, WebSocketMessage<?> var2, MessageChannel var3) throws Exception;

    public void handleMessageToClient(WebSocketSession var1, Message<?> var2) throws Exception;

    @Nullable
    public String resolveSessionId(Message<?> var1);

    public void afterSessionStarted(WebSocketSession var1, MessageChannel var2) throws Exception;

    public void afterSessionEnded(WebSocketSession var1, CloseStatus var2, MessageChannel var3) throws Exception;
}

