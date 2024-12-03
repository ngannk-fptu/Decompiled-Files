/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.concurrent.SettableListenableFuture
 */
package org.springframework.web.socket.sockjs.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.SettableListenableFuture;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.NativeWebSocketSession;
import org.springframework.web.socket.sockjs.client.AbstractClientSockJsSession;
import org.springframework.web.socket.sockjs.client.TransportRequest;

public class WebSocketClientSockJsSession
extends AbstractClientSockJsSession
implements NativeWebSocketSession {
    @Nullable
    private WebSocketSession webSocketSession;

    public WebSocketClientSockJsSession(TransportRequest request, WebSocketHandler handler, SettableListenableFuture<WebSocketSession> connectFuture) {
        super(request, handler, connectFuture);
    }

    @Override
    public Object getNativeSession() {
        Assert.state((this.webSocketSession != null ? 1 : 0) != 0, (String)"WebSocketSession not yet initialized");
        return this.webSocketSession;
    }

    @Override
    @Nullable
    public <T> T getNativeSession(@Nullable Class<T> requiredType) {
        return (T)(requiredType == null || requiredType.isInstance(this.webSocketSession) ? this.webSocketSession : null);
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

    public void initializeDelegateSession(WebSocketSession session) {
        this.webSocketSession = session;
    }

    @Override
    protected void sendInternal(TextMessage textMessage) throws IOException {
        Assert.state((this.webSocketSession != null ? 1 : 0) != 0, (String)"WebSocketSession not yet initialized");
        this.webSocketSession.sendMessage(textMessage);
    }

    @Override
    protected void disconnect(CloseStatus status) throws IOException {
        if (this.webSocketSession != null) {
            this.webSocketSession.close(status);
        }
    }
}

