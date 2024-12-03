/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.socket.adapter;

import org.springframework.lang.Nullable;
import org.springframework.web.socket.WebSocketSession;

public interface NativeWebSocketSession
extends WebSocketSession {
    public Object getNativeSession();

    @Nullable
    public <T> T getNativeSession(@Nullable Class<T> var1);
}

