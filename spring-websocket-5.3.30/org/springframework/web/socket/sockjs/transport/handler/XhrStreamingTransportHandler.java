/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.MediaType
 *  org.springframework.http.server.ServerHttpRequest
 */
package org.springframework.web.socket.sockjs.transport.handler;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.sockjs.frame.DefaultSockJsFrameFormat;
import org.springframework.web.socket.sockjs.frame.SockJsFrameFormat;
import org.springframework.web.socket.sockjs.transport.SockJsServiceConfig;
import org.springframework.web.socket.sockjs.transport.SockJsSession;
import org.springframework.web.socket.sockjs.transport.TransportType;
import org.springframework.web.socket.sockjs.transport.handler.AbstractHttpSendingTransportHandler;
import org.springframework.web.socket.sockjs.transport.session.StreamingSockJsSession;

public class XhrStreamingTransportHandler
extends AbstractHttpSendingTransportHandler {
    private static final byte[] PRELUDE = new byte[2049];

    @Override
    public TransportType getTransportType() {
        return TransportType.XHR_STREAMING;
    }

    @Override
    protected MediaType getContentType() {
        return new MediaType("application", "javascript", StandardCharsets.UTF_8);
    }

    @Override
    public boolean checkSessionType(SockJsSession session) {
        return session instanceof XhrStreamingSockJsSession;
    }

    @Override
    public StreamingSockJsSession createSession(String sessionId, WebSocketHandler handler, Map<String, Object> attributes) {
        return new XhrStreamingSockJsSession(sessionId, this.getServiceConfig(), handler, attributes);
    }

    @Override
    protected SockJsFrameFormat getFrameFormat(ServerHttpRequest request) {
        return new DefaultSockJsFrameFormat("%s\n");
    }

    static {
        Arrays.fill(PRELUDE, (byte)104);
        XhrStreamingTransportHandler.PRELUDE[2048] = 10;
    }

    private static class XhrStreamingSockJsSession
    extends StreamingSockJsSession {
        public XhrStreamingSockJsSession(String sessionId, SockJsServiceConfig config, WebSocketHandler wsHandler, Map<String, Object> attributes) {
            super(sessionId, config, wsHandler, attributes);
        }

        @Override
        protected byte[] getPrelude(ServerHttpRequest request) {
            return PRELUDE;
        }
    }
}

