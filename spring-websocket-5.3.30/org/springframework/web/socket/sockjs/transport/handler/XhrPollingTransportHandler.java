/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.MediaType
 *  org.springframework.http.server.ServerHttpRequest
 */
package org.springframework.web.socket.sockjs.transport.handler;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.sockjs.frame.DefaultSockJsFrameFormat;
import org.springframework.web.socket.sockjs.frame.SockJsFrameFormat;
import org.springframework.web.socket.sockjs.transport.SockJsSession;
import org.springframework.web.socket.sockjs.transport.TransportType;
import org.springframework.web.socket.sockjs.transport.handler.AbstractHttpSendingTransportHandler;
import org.springframework.web.socket.sockjs.transport.session.PollingSockJsSession;

public class XhrPollingTransportHandler
extends AbstractHttpSendingTransportHandler {
    @Override
    public TransportType getTransportType() {
        return TransportType.XHR;
    }

    @Override
    protected MediaType getContentType() {
        return new MediaType("application", "javascript", StandardCharsets.UTF_8);
    }

    @Override
    protected SockJsFrameFormat getFrameFormat(ServerHttpRequest request) {
        return new DefaultSockJsFrameFormat("%s\n");
    }

    @Override
    public boolean checkSessionType(SockJsSession session) {
        return session instanceof PollingSockJsSession;
    }

    @Override
    public PollingSockJsSession createSession(String sessionId, WebSocketHandler handler, Map<String, Object> attributes) {
        return new PollingSockJsSession(sessionId, this.getServiceConfig(), handler, attributes);
    }
}

