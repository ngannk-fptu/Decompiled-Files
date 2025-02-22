/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.socket.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

public class LoggingWebSocketHandlerDecorator
extends WebSocketHandlerDecorator {
    private static final Log logger = LogFactory.getLog(LoggingWebSocketHandlerDecorator.class);

    public LoggingWebSocketHandlerDecorator(WebSocketHandler delegate) {
        super(delegate);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("New " + session));
        }
        super.afterConnectionEstablished(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (logger.isTraceEnabled()) {
            logger.trace((Object)("Handling " + message + " in " + session));
        }
        super.handleMessage(session, message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("Transport error in " + session), exception);
        }
        super.handleTransportError(session, exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug((Object)(session + " closed with " + closeStatus));
        }
        super.afterConnectionClosed(session, closeStatus);
    }
}

