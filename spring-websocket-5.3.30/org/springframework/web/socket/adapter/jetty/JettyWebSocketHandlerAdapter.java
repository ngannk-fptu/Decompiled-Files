/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.eclipse.jetty.websocket.api.Session
 *  org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
 *  org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
 *  org.eclipse.jetty.websocket.api.annotations.OnWebSocketError
 *  org.eclipse.jetty.websocket.api.annotations.OnWebSocketFrame
 *  org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
 *  org.eclipse.jetty.websocket.api.annotations.WebSocket
 *  org.eclipse.jetty.websocket.api.extensions.Frame
 *  org.springframework.util.Assert
 */
package org.springframework.web.socket.adapter.jetty;

import java.nio.ByteBuffer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketFrame;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.springframework.util.Assert;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.adapter.jetty.JettyWebSocketSession;
import org.springframework.web.socket.handler.ExceptionWebSocketHandlerDecorator;

@WebSocket
public class JettyWebSocketHandlerAdapter {
    private static final ByteBuffer EMPTY_PAYLOAD = ByteBuffer.wrap(new byte[0]);
    private static final Log logger = LogFactory.getLog(JettyWebSocketHandlerAdapter.class);
    private final WebSocketHandler webSocketHandler;
    private final JettyWebSocketSession wsSession;

    public JettyWebSocketHandlerAdapter(WebSocketHandler webSocketHandler, JettyWebSocketSession wsSession) {
        Assert.notNull((Object)webSocketHandler, (String)"WebSocketHandler must not be null");
        Assert.notNull((Object)wsSession, (String)"WebSocketSession must not be null");
        this.webSocketHandler = webSocketHandler;
        this.wsSession = wsSession;
    }

    @OnWebSocketConnect
    public void onWebSocketConnect(Session session) {
        try {
            this.wsSession.initializeNativeSession(session);
            this.webSocketHandler.afterConnectionEstablished(this.wsSession);
        }
        catch (Exception ex) {
            ExceptionWebSocketHandlerDecorator.tryCloseWithError(this.wsSession, ex, logger);
        }
    }

    @OnWebSocketMessage
    public void onWebSocketText(String payload) {
        TextMessage message = new TextMessage(payload);
        try {
            this.webSocketHandler.handleMessage(this.wsSession, message);
        }
        catch (Exception ex) {
            ExceptionWebSocketHandlerDecorator.tryCloseWithError(this.wsSession, ex, logger);
        }
    }

    @OnWebSocketMessage
    public void onWebSocketBinary(byte[] payload, int offset, int length) {
        BinaryMessage message = new BinaryMessage(payload, offset, length, true);
        try {
            this.webSocketHandler.handleMessage(this.wsSession, message);
        }
        catch (Exception ex) {
            ExceptionWebSocketHandlerDecorator.tryCloseWithError(this.wsSession, ex, logger);
        }
    }

    @OnWebSocketFrame
    public void onWebSocketFrame(Frame frame) {
        if (10 == frame.getOpCode()) {
            ByteBuffer payload = frame.getPayload() != null ? frame.getPayload() : EMPTY_PAYLOAD;
            PongMessage message = new PongMessage(payload);
            try {
                this.webSocketHandler.handleMessage(this.wsSession, message);
            }
            catch (Exception ex) {
                ExceptionWebSocketHandlerDecorator.tryCloseWithError(this.wsSession, ex, logger);
            }
        }
    }

    @OnWebSocketClose
    public void onWebSocketClose(int statusCode, String reason) {
        block2: {
            CloseStatus closeStatus = new CloseStatus(statusCode, reason);
            try {
                this.webSocketHandler.afterConnectionClosed(this.wsSession, closeStatus);
            }
            catch (Exception ex) {
                if (!logger.isWarnEnabled()) break block2;
                logger.warn((Object)("Unhandled exception after connection closed for " + this), (Throwable)ex);
            }
        }
    }

    @OnWebSocketError
    public void onWebSocketError(Throwable cause) {
        try {
            this.webSocketHandler.handleTransportError(this.wsSession, cause);
        }
        catch (Exception ex) {
            ExceptionWebSocketHandlerDecorator.tryCloseWithError(this.wsSession, ex, logger);
        }
    }
}

