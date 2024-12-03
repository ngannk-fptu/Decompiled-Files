/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.HttpHeaders
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.socket.sockjs.transport.session;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.NativeWebSocketSession;
import org.springframework.web.socket.sockjs.SockJsTransportFailureException;
import org.springframework.web.socket.sockjs.frame.SockJsFrame;
import org.springframework.web.socket.sockjs.transport.SockJsServiceConfig;
import org.springframework.web.socket.sockjs.transport.session.AbstractSockJsSession;

public class WebSocketServerSockJsSession
extends AbstractSockJsSession
implements NativeWebSocketSession {
    @Nullable
    private WebSocketSession webSocketSession;
    private volatile boolean openFrameSent;
    private final Queue<String> initSessionCache = new LinkedBlockingDeque<String>();
    private final Object initSessionLock = new Object();
    private final Object disconnectLock = new Object();
    private volatile boolean disconnected;

    public WebSocketServerSockJsSession(String id, SockJsServiceConfig config, WebSocketHandler handler, @Nullable Map<String, Object> attributes) {
        super(id, config, handler, attributes);
    }

    @Override
    @Nullable
    public URI getUri() {
        Assert.state((this.webSocketSession != null ? 1 : 0) != 0, (String)"WebSocketSession not yet initialized");
        return this.webSocketSession.getUri();
    }

    @Override
    public HttpHeaders getHandshakeHeaders() {
        Assert.state((this.webSocketSession != null ? 1 : 0) != 0, (String)"WebSocketSession not yet initialized");
        return this.webSocketSession.getHandshakeHeaders();
    }

    @Override
    public Principal getPrincipal() {
        Assert.state((this.webSocketSession != null ? 1 : 0) != 0, (String)"WebSocketSession not yet initialized");
        return this.webSocketSession.getPrincipal();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        Assert.state((this.webSocketSession != null ? 1 : 0) != 0, (String)"WebSocketSession not yet initialized");
        return this.webSocketSession.getLocalAddress();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        Assert.state((this.webSocketSession != null ? 1 : 0) != 0, (String)"WebSocketSession not yet initialized");
        return this.webSocketSession.getRemoteAddress();
    }

    @Override
    public String getAcceptedProtocol() {
        Assert.state((this.webSocketSession != null ? 1 : 0) != 0, (String)"WebSocketSession not yet initialized");
        return this.webSocketSession.getAcceptedProtocol();
    }

    @Override
    public void setTextMessageSizeLimit(int messageSizeLimit) {
        Assert.state((this.webSocketSession != null ? 1 : 0) != 0, (String)"WebSocketSession not yet initialized");
        this.webSocketSession.setTextMessageSizeLimit(messageSizeLimit);
    }

    @Override
    public int getTextMessageSizeLimit() {
        Assert.state((this.webSocketSession != null ? 1 : 0) != 0, (String)"WebSocketSession not yet initialized");
        return this.webSocketSession.getTextMessageSizeLimit();
    }

    @Override
    public void setBinaryMessageSizeLimit(int messageSizeLimit) {
        Assert.state((this.webSocketSession != null ? 1 : 0) != 0, (String)"WebSocketSession not yet initialized");
        this.webSocketSession.setBinaryMessageSizeLimit(messageSizeLimit);
    }

    @Override
    public int getBinaryMessageSizeLimit() {
        Assert.state((this.webSocketSession != null ? 1 : 0) != 0, (String)"WebSocketSession not yet initialized");
        return this.webSocketSession.getBinaryMessageSizeLimit();
    }

    @Override
    public List<WebSocketExtension> getExtensions() {
        Assert.state((this.webSocketSession != null ? 1 : 0) != 0, (String)"WebSocketSession not yet initialized");
        return this.webSocketSession.getExtensions();
    }

    @Override
    public Object getNativeSession() {
        Assert.state((this.webSocketSession != null ? 1 : 0) != 0, (String)"WebSocketSession not yet initialized");
        return this.webSocketSession instanceof NativeWebSocketSession ? ((NativeWebSocketSession)this.webSocketSession).getNativeSession() : this.webSocketSession;
    }

    @Override
    @Nullable
    public <T> T getNativeSession(@Nullable Class<T> requiredType) {
        return this.webSocketSession instanceof NativeWebSocketSession ? (T)((NativeWebSocketSession)this.webSocketSession).getNativeSession(requiredType) : null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void initializeDelegateSession(WebSocketSession session) {
        Object object = this.initSessionLock;
        synchronized (object) {
            this.webSocketSession = session;
            try {
                this.delegateConnectionEstablished();
                this.webSocketSession.sendMessage(new TextMessage(SockJsFrame.openFrame().getContent()));
                while (!this.initSessionCache.isEmpty()) {
                    this.writeFrame(SockJsFrame.messageFrame(this.getMessageCodec(), this.initSessionCache.poll()));
                }
                this.scheduleHeartbeat();
                this.openFrameSent = true;
            }
            catch (Exception ex) {
                this.tryCloseWithSockJsTransportError(ex, CloseStatus.SERVER_ERROR);
            }
        }
    }

    @Override
    public boolean isActive() {
        return this.webSocketSession != null && this.webSocketSession.isOpen() && !this.disconnected;
    }

    public void handleMessage(TextMessage message, WebSocketSession wsSession) throws Exception {
        String[] messages;
        String payload = (String)message.getPayload();
        if (!StringUtils.hasLength((String)payload)) {
            return;
        }
        try {
            messages = this.getSockJsServiceConfig().getMessageCodec().decode(payload);
        }
        catch (Exception ex) {
            this.logger.error((Object)"Broken data received. Terminating WebSocket connection abruptly", (Throwable)ex);
            this.tryCloseWithSockJsTransportError(ex, CloseStatus.BAD_DATA);
            return;
        }
        if (messages != null) {
            this.delegateMessages(messages);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void sendMessageInternal(String message) throws SockJsTransportFailureException {
        if (!this.openFrameSent) {
            Object object = this.initSessionLock;
            synchronized (object) {
                if (!this.openFrameSent) {
                    this.initSessionCache.add(message);
                    return;
                }
            }
        }
        this.cancelHeartbeat();
        this.writeFrame(SockJsFrame.messageFrame(this.getMessageCodec(), message));
        this.scheduleHeartbeat();
    }

    @Override
    protected void writeFrameInternal(SockJsFrame frame) throws IOException {
        Assert.state((this.webSocketSession != null ? 1 : 0) != 0, (String)"WebSocketSession not yet initialized");
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Writing " + frame));
        }
        TextMessage message = new TextMessage(frame.getContent());
        this.webSocketSession.sendMessage(message);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void disconnect(CloseStatus status) throws IOException {
        if (this.isActive()) {
            Object object = this.disconnectLock;
            synchronized (object) {
                if (this.isActive()) {
                    this.disconnected = true;
                    if (this.webSocketSession != null) {
                        this.webSocketSession.close(status);
                    }
                }
            }
        }
    }
}

