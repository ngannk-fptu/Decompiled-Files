/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.socket.sockjs.transport;

import java.util.Map;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.sockjs.transport.SockJsSession;

public interface SockJsSessionFactory {
    public SockJsSession createSession(String var1, WebSocketHandler var2, Map<String, Object> var3);
}

