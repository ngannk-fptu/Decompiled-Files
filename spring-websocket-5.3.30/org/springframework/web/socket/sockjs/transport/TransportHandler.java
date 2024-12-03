/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 */
package org.springframework.web.socket.sockjs.transport;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.sockjs.SockJsException;
import org.springframework.web.socket.sockjs.transport.SockJsServiceConfig;
import org.springframework.web.socket.sockjs.transport.SockJsSession;
import org.springframework.web.socket.sockjs.transport.TransportType;

public interface TransportHandler {
    public void initialize(SockJsServiceConfig var1);

    public TransportType getTransportType();

    public boolean checkSessionType(SockJsSession var1);

    public void handleRequest(ServerHttpRequest var1, ServerHttpResponse var2, WebSocketHandler var3, SockJsSession var4) throws SockJsException;
}

