/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.HttpHeaders
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.web.socket.handler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

public class WebSocketSessionDecorator
implements WebSocketSession {
    private final WebSocketSession delegate;

    public WebSocketSessionDecorator(WebSocketSession session) {
        Assert.notNull((Object)session, (String)"Delegate WebSocketSessionSession is required");
        this.delegate = session;
    }

    public WebSocketSession getDelegate() {
        return this.delegate;
    }

    public WebSocketSession getLastSession() {
        WebSocketSession result = this.delegate;
        while (result instanceof WebSocketSessionDecorator) {
            result = ((WebSocketSessionDecorator)result).getDelegate();
        }
        return result;
    }

    public static WebSocketSession unwrap(WebSocketSession session) {
        if (session instanceof WebSocketSessionDecorator) {
            return ((WebSocketSessionDecorator)session).getLastSession();
        }
        return session;
    }

    @Override
    public String getId() {
        return this.delegate.getId();
    }

    @Override
    @Nullable
    public URI getUri() {
        return this.delegate.getUri();
    }

    @Override
    public HttpHeaders getHandshakeHeaders() {
        return this.delegate.getHandshakeHeaders();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.delegate.getAttributes();
    }

    @Override
    public Principal getPrincipal() {
        return this.delegate.getPrincipal();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return this.delegate.getLocalAddress();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return this.delegate.getRemoteAddress();
    }

    @Override
    public String getAcceptedProtocol() {
        return this.delegate.getAcceptedProtocol();
    }

    @Override
    public List<WebSocketExtension> getExtensions() {
        return this.delegate.getExtensions();
    }

    @Override
    public void setTextMessageSizeLimit(int messageSizeLimit) {
        this.delegate.setTextMessageSizeLimit(messageSizeLimit);
    }

    @Override
    public int getTextMessageSizeLimit() {
        return this.delegate.getTextMessageSizeLimit();
    }

    @Override
    public void setBinaryMessageSizeLimit(int messageSizeLimit) {
        this.delegate.setBinaryMessageSizeLimit(messageSizeLimit);
    }

    @Override
    public int getBinaryMessageSizeLimit() {
        return this.delegate.getBinaryMessageSizeLimit();
    }

    @Override
    public boolean isOpen() {
        return this.delegate.isOpen();
    }

    @Override
    public void sendMessage(WebSocketMessage<?> message) throws IOException {
        this.delegate.sendMessage(message);
    }

    @Override
    public void close() throws IOException {
        this.delegate.close();
    }

    @Override
    public void close(CloseStatus status) throws IOException {
        this.delegate.close(status);
    }

    public String toString() {
        return this.getClass().getSimpleName() + " [delegate=" + this.delegate + "]";
    }
}

