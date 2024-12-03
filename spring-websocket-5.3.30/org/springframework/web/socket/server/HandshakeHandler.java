/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 */
package org.springframework.web.socket.server;

import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeFailureException;

public interface HandshakeHandler {
    public boolean doHandshake(ServerHttpRequest var1, ServerHttpResponse var2, WebSocketHandler var3, Map<String, Object> var4) throws HandshakeFailureException;
}

