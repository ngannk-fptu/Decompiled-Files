/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.concurrent.ListenableFuture
 */
package org.springframework.web.socket.client;

import java.net.URI;
import org.springframework.lang.Nullable;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;

public interface WebSocketClient {
    public ListenableFuture<WebSocketSession> doHandshake(WebSocketHandler var1, String var2, Object ... var3);

    public ListenableFuture<WebSocketSession> doHandshake(WebSocketHandler var1, @Nullable WebSocketHttpHeaders var2, URI var3);
}

