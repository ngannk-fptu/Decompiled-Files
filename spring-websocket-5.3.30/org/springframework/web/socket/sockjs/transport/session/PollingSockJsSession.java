/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 */
package org.springframework.web.socket.sockjs.transport.session;

import java.io.IOException;
import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.sockjs.SockJsTransportFailureException;
import org.springframework.web.socket.sockjs.frame.SockJsFrame;
import org.springframework.web.socket.sockjs.frame.SockJsMessageCodec;
import org.springframework.web.socket.sockjs.transport.SockJsServiceConfig;
import org.springframework.web.socket.sockjs.transport.session.AbstractHttpSockJsSession;

public class PollingSockJsSession
extends AbstractHttpSockJsSession {
    public PollingSockJsSession(String sessionId, SockJsServiceConfig config, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        super(sessionId, config, wsHandler, attributes);
    }

    @Override
    protected void handleRequestInternal(ServerHttpRequest request, ServerHttpResponse response, boolean initialRequest) throws IOException {
        if (initialRequest) {
            this.writeFrame(SockJsFrame.openFrame());
        } else if (!this.getMessageCache().isEmpty()) {
            this.flushCache();
        } else {
            this.scheduleHeartbeat();
        }
    }

    @Override
    protected void flushCache() throws SockJsTransportFailureException {
        String[] messages = new String[this.getMessageCache().size()];
        for (int i = 0; i < messages.length; ++i) {
            messages[i] = this.getMessageCache().poll();
        }
        SockJsMessageCodec messageCodec = this.getSockJsServiceConfig().getMessageCodec();
        SockJsFrame frame = SockJsFrame.messageFrame(messageCodec, messages);
        this.writeFrame(frame);
    }

    @Override
    protected void writeFrame(SockJsFrame frame) throws SockJsTransportFailureException {
        super.writeFrame(frame);
        this.resetRequest();
    }
}

