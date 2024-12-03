/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.socket.handler;

import java.io.IOException;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

public class TextWebSocketHandler
extends AbstractWebSocketHandler {
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        try {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Binary messages not supported"));
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }
}

