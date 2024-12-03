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

public abstract class StreamingSockJsSession
extends AbstractHttpSockJsSession {
    private int byteCount;

    public StreamingSockJsSession(String sessionId, SockJsServiceConfig config, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        super(sessionId, config, wsHandler, attributes);
    }

    protected abstract byte[] getPrelude(ServerHttpRequest var1);

    @Override
    protected void handleRequestInternal(ServerHttpRequest request, ServerHttpResponse response, boolean initialRequest) throws IOException {
        byte[] prelude = this.getPrelude(request);
        response.getBody().write(prelude);
        response.flush();
        if (initialRequest) {
            this.writeFrame(SockJsFrame.openFrame());
        }
        this.flushCache();
    }

    @Override
    protected void flushCache() throws SockJsTransportFailureException {
        while (!this.getMessageCache().isEmpty()) {
            String message = this.getMessageCache().poll();
            SockJsMessageCodec messageCodec = this.getSockJsServiceConfig().getMessageCodec();
            SockJsFrame frame = SockJsFrame.messageFrame(messageCodec, message);
            this.writeFrame(frame);
            this.byteCount += frame.getContentBytes().length + 1;
            if (this.logger.isTraceEnabled()) {
                this.logger.trace((Object)(this.byteCount + " bytes written so far, " + this.getMessageCache().size() + " more messages not flushed"));
            }
            if (this.byteCount < this.getSockJsServiceConfig().getStreamBytesLimit()) continue;
            this.logger.trace((Object)"Streamed bytes limit reached, recycling current request");
            this.resetRequest();
            this.byteCount = 0;
            break;
        }
        this.scheduleHeartbeat();
    }
}

