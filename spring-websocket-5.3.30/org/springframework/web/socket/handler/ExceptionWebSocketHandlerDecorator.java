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

public class ExceptionWebSocketHandlerDecorator
extends WebSocketHandlerDecorator {
    private static final Log logger = LogFactory.getLog(ExceptionWebSocketHandlerDecorator.class);

    public ExceptionWebSocketHandlerDecorator(WebSocketHandler delegate) {
        super(delegate);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            this.getDelegate().afterConnectionEstablished(session);
        }
        catch (Exception ex) {
            ExceptionWebSocketHandlerDecorator.tryCloseWithError(session, ex, logger);
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        try {
            this.getDelegate().handleMessage(session, message);
        }
        catch (Exception ex) {
            ExceptionWebSocketHandlerDecorator.tryCloseWithError(session, ex, logger);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        try {
            this.getDelegate().handleTransportError(session, exception);
        }
        catch (Exception ex) {
            ExceptionWebSocketHandlerDecorator.tryCloseWithError(session, ex, logger);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        block2: {
            try {
                this.getDelegate().afterConnectionClosed(session, closeStatus);
            }
            catch (Exception ex) {
                if (!logger.isWarnEnabled()) break block2;
                logger.warn((Object)("Unhandled exception after connection closed for " + this), (Throwable)ex);
            }
        }
    }

    public static void tryCloseWithError(WebSocketSession session, Throwable exception, Log logger) {
        if (logger.isErrorEnabled()) {
            logger.error((Object)("Closing session due to exception for " + session), exception);
        }
        if (session.isOpen()) {
            try {
                session.close(CloseStatus.SERVER_ERROR);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }
}

