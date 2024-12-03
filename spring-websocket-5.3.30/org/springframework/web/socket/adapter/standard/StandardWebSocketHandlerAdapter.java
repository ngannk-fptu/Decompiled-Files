/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.CloseReason
 *  javax.websocket.Endpoint
 *  javax.websocket.EndpointConfig
 *  javax.websocket.MessageHandler
 *  javax.websocket.MessageHandler$Partial
 *  javax.websocket.MessageHandler$Whole
 *  javax.websocket.PongMessage
 *  javax.websocket.Session
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.util.Assert
 */
package org.springframework.web.socket.adapter.standard;

import java.nio.ByteBuffer;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;
import org.springframework.web.socket.handler.ExceptionWebSocketHandlerDecorator;

public class StandardWebSocketHandlerAdapter
extends Endpoint {
    private final Log logger = LogFactory.getLog(StandardWebSocketHandlerAdapter.class);
    private final WebSocketHandler handler;
    private final StandardWebSocketSession wsSession;

    public StandardWebSocketHandlerAdapter(WebSocketHandler handler, StandardWebSocketSession wsSession) {
        Assert.notNull((Object)handler, (String)"WebSocketHandler must not be null");
        Assert.notNull((Object)wsSession, (String)"WebSocketSession must not be null");
        this.handler = handler;
        this.wsSession = wsSession;
    }

    public void onOpen(final Session session, EndpointConfig config) {
        this.wsSession.initializeNativeSession(session);
        if (this.handler.supportsPartialMessages()) {
            session.addMessageHandler((MessageHandler)new MessageHandler.Partial<String>(){

                public void onMessage(String message, boolean isLast) {
                    StandardWebSocketHandlerAdapter.this.handleTextMessage(session, message, isLast);
                }
            });
            session.addMessageHandler((MessageHandler)new MessageHandler.Partial<ByteBuffer>(){

                public void onMessage(ByteBuffer message, boolean isLast) {
                    StandardWebSocketHandlerAdapter.this.handleBinaryMessage(session, message, isLast);
                }
            });
        } else {
            session.addMessageHandler((MessageHandler)new MessageHandler.Whole<String>(){

                public void onMessage(String message) {
                    StandardWebSocketHandlerAdapter.this.handleTextMessage(session, message, true);
                }
            });
            session.addMessageHandler((MessageHandler)new MessageHandler.Whole<ByteBuffer>(){

                public void onMessage(ByteBuffer message) {
                    StandardWebSocketHandlerAdapter.this.handleBinaryMessage(session, message, true);
                }
            });
        }
        session.addMessageHandler((MessageHandler)new MessageHandler.Whole<javax.websocket.PongMessage>(){

            public void onMessage(javax.websocket.PongMessage message) {
                StandardWebSocketHandlerAdapter.this.handlePongMessage(session, message.getApplicationData());
            }
        });
        try {
            this.handler.afterConnectionEstablished(this.wsSession);
        }
        catch (Exception ex) {
            ExceptionWebSocketHandlerDecorator.tryCloseWithError(this.wsSession, ex, this.logger);
        }
    }

    private void handleTextMessage(Session session, String payload, boolean isLast) {
        TextMessage textMessage = new TextMessage(payload, isLast);
        try {
            this.handler.handleMessage(this.wsSession, textMessage);
        }
        catch (Exception ex) {
            ExceptionWebSocketHandlerDecorator.tryCloseWithError(this.wsSession, ex, this.logger);
        }
    }

    private void handleBinaryMessage(Session session, ByteBuffer payload, boolean isLast) {
        BinaryMessage binaryMessage = new BinaryMessage(payload, isLast);
        try {
            this.handler.handleMessage(this.wsSession, binaryMessage);
        }
        catch (Exception ex) {
            ExceptionWebSocketHandlerDecorator.tryCloseWithError(this.wsSession, ex, this.logger);
        }
    }

    private void handlePongMessage(Session session, ByteBuffer payload) {
        PongMessage pongMessage = new PongMessage(payload);
        try {
            this.handler.handleMessage(this.wsSession, pongMessage);
        }
        catch (Exception ex) {
            ExceptionWebSocketHandlerDecorator.tryCloseWithError(this.wsSession, ex, this.logger);
        }
    }

    public void onClose(Session session, CloseReason reason) {
        block2: {
            CloseStatus closeStatus = new CloseStatus(reason.getCloseCode().getCode(), reason.getReasonPhrase());
            try {
                this.handler.afterConnectionClosed(this.wsSession, closeStatus);
            }
            catch (Exception ex) {
                if (!this.logger.isWarnEnabled()) break block2;
                this.logger.warn((Object)("Unhandled on-close exception for " + this.wsSession), (Throwable)ex);
            }
        }
    }

    public void onError(Session session, Throwable exception) {
        try {
            this.handler.handleTransportError(this.wsSession, exception);
        }
        catch (Exception ex) {
            ExceptionWebSocketHandlerDecorator.tryCloseWithError(this.wsSession, ex, this.logger);
        }
    }
}

