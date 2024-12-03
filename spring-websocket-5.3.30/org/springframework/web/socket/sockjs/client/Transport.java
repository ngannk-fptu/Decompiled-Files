/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.concurrent.ListenableFuture
 */
package org.springframework.web.socket.sockjs.client;

import java.util.List;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.sockjs.client.TransportRequest;
import org.springframework.web.socket.sockjs.transport.TransportType;

public interface Transport {
    public List<TransportType> getTransportTypes();

    public ListenableFuture<WebSocketSession> connect(TransportRequest var1, WebSocketHandler var2);
}

