/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.socket.sockjs.transport;

import org.springframework.web.socket.WebSocketSession;

public interface SockJsSession
extends WebSocketSession {
    public long getTimeSinceLastActive();

    public void disableHeartbeat();
}

