/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.HttpHeaders
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.socket;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketMessage;

public interface WebSocketSession
extends Closeable {
    public String getId();

    @Nullable
    public URI getUri();

    public HttpHeaders getHandshakeHeaders();

    public Map<String, Object> getAttributes();

    @Nullable
    public Principal getPrincipal();

    @Nullable
    public InetSocketAddress getLocalAddress();

    @Nullable
    public InetSocketAddress getRemoteAddress();

    @Nullable
    public String getAcceptedProtocol();

    public void setTextMessageSizeLimit(int var1);

    public int getTextMessageSizeLimit();

    public void setBinaryMessageSizeLimit(int var1);

    public int getBinaryMessageSizeLimit();

    public List<WebSocketExtension> getExtensions();

    public void sendMessage(WebSocketMessage<?> var1) throws IOException;

    public boolean isOpen();

    @Override
    public void close() throws IOException;

    public void close(CloseStatus var1) throws IOException;
}

