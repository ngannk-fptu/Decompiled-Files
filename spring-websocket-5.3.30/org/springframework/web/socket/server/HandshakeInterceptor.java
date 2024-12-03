/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.socket.server;

import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.WebSocketHandler;

public interface HandshakeInterceptor {
    public boolean beforeHandshake(ServerHttpRequest var1, ServerHttpResponse var2, WebSocketHandler var3, Map<String, Object> var4) throws Exception;

    public void afterHandshake(ServerHttpRequest var1, ServerHttpResponse var2, WebSocketHandler var3, @Nullable Exception var4);
}

