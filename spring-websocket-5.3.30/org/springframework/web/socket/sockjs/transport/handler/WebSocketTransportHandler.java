/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.springframework.context.Lifecycle
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.util.Assert
 *  org.springframework.web.context.ServletContextAware
 */
package org.springframework.web.socket.sockjs.transport.handler;

import java.util.Map;
import javax.servlet.ServletContext;
import org.springframework.context.Lifecycle;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.sockjs.SockJsException;
import org.springframework.web.socket.sockjs.SockJsTransportFailureException;
import org.springframework.web.socket.sockjs.transport.SockJsSession;
import org.springframework.web.socket.sockjs.transport.SockJsSessionFactory;
import org.springframework.web.socket.sockjs.transport.TransportType;
import org.springframework.web.socket.sockjs.transport.handler.AbstractTransportHandler;
import org.springframework.web.socket.sockjs.transport.handler.SockJsWebSocketHandler;
import org.springframework.web.socket.sockjs.transport.session.AbstractSockJsSession;
import org.springframework.web.socket.sockjs.transport.session.WebSocketServerSockJsSession;

public class WebSocketTransportHandler
extends AbstractTransportHandler
implements SockJsSessionFactory,
HandshakeHandler,
Lifecycle,
ServletContextAware {
    private final HandshakeHandler handshakeHandler;
    private volatile boolean running;

    public WebSocketTransportHandler(HandshakeHandler handshakeHandler) {
        Assert.notNull((Object)handshakeHandler, (String)"HandshakeHandler must not be null");
        this.handshakeHandler = handshakeHandler;
    }

    @Override
    public TransportType getTransportType() {
        return TransportType.WEBSOCKET;
    }

    public HandshakeHandler getHandshakeHandler() {
        return this.handshakeHandler;
    }

    public void setServletContext(ServletContext servletContext) {
        if (this.handshakeHandler instanceof ServletContextAware) {
            ((ServletContextAware)this.handshakeHandler).setServletContext(servletContext);
        }
    }

    public void start() {
        if (!this.isRunning()) {
            this.running = true;
            if (this.handshakeHandler instanceof Lifecycle) {
                ((Lifecycle)this.handshakeHandler).start();
            }
        }
    }

    public void stop() {
        if (this.isRunning()) {
            this.running = false;
            if (this.handshakeHandler instanceof Lifecycle) {
                ((Lifecycle)this.handshakeHandler).stop();
            }
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    @Override
    public boolean checkSessionType(SockJsSession session) {
        return session instanceof WebSocketServerSockJsSession;
    }

    @Override
    public AbstractSockJsSession createSession(String id, WebSocketHandler handler, Map<String, Object> attrs) {
        return new WebSocketServerSockJsSession(id, this.getServiceConfig(), handler, attrs);
    }

    @Override
    public void handleRequest(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, SockJsSession wsSession) throws SockJsException {
        WebSocketServerSockJsSession sockJsSession = (WebSocketServerSockJsSession)wsSession;
        try {
            wsHandler = new SockJsWebSocketHandler(this.getServiceConfig(), wsHandler, sockJsSession);
            this.handshakeHandler.doHandshake(request, response, wsHandler, sockJsSession.getAttributes());
        }
        catch (Exception ex) {
            sockJsSession.tryCloseWithSockJsTransportError(ex, CloseStatus.SERVER_ERROR);
            throw new SockJsTransportFailureException("WebSocket handshake failure", wsSession.getId(), ex);
        }
    }

    @Override
    public boolean doHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler, Map<String, Object> attributes) throws HandshakeFailureException {
        return this.handshakeHandler.doHandshake(request, response, handler, attributes);
    }
}

