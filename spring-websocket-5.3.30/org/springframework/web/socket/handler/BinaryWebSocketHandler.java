/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.socket.handler;

import java.io.IOException;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

public class BinaryWebSocketHandler
extends AbstractWebSocketHandler {
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Text messages not supported"));
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }
}

