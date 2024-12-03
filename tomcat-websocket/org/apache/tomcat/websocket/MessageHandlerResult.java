/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.MessageHandler
 */
package org.apache.tomcat.websocket;

import javax.websocket.MessageHandler;
import org.apache.tomcat.websocket.MessageHandlerResultType;

public class MessageHandlerResult {
    private final MessageHandler handler;
    private final MessageHandlerResultType type;

    public MessageHandlerResult(MessageHandler handler, MessageHandlerResultType type) {
        this.handler = handler;
        this.type = type;
    }

    public MessageHandler getHandler() {
        return this.handler;
    }

    public MessageHandlerResultType getType() {
        return this.type;
    }
}

