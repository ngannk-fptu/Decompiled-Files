/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.socket.server;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeFailureException;

public interface RequestUpgradeStrategy {
    public String[] getSupportedVersions();

    public List<WebSocketExtension> getSupportedExtensions(ServerHttpRequest var1);

    public void upgrade(ServerHttpRequest var1, ServerHttpResponse var2, @Nullable String var3, List<WebSocketExtension> var4, @Nullable Principal var5, WebSocketHandler var6, Map<String, Object> var7) throws HandshakeFailureException;
}

