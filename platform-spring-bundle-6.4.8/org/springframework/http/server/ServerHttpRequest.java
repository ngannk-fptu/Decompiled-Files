/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.server;

import java.net.InetSocketAddress;
import java.security.Principal;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpRequest;
import org.springframework.http.server.ServerHttpAsyncRequestControl;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;

public interface ServerHttpRequest
extends HttpRequest,
HttpInputMessage {
    @Nullable
    public Principal getPrincipal();

    public InetSocketAddress getLocalAddress();

    public InetSocketAddress getRemoteAddress();

    public ServerHttpAsyncRequestControl getAsyncRequestControl(ServerHttpResponse var1);
}

