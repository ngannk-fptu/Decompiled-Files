/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.MediaType
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.web.socket.sockjs.transport.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.sockjs.SockJsException;
import org.springframework.web.socket.sockjs.transport.SockJsSession;
import org.springframework.web.socket.sockjs.transport.handler.AbstractTransportHandler;
import org.springframework.web.socket.sockjs.transport.session.AbstractHttpSockJsSession;

public abstract class AbstractHttpReceivingTransportHandler
extends AbstractTransportHandler {
    @Override
    public boolean checkSessionType(SockJsSession session) {
        return session instanceof AbstractHttpSockJsSession;
    }

    @Override
    public final void handleRequest(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, SockJsSession wsSession) throws SockJsException {
        Assert.notNull((Object)wsSession, (String)"No session");
        AbstractHttpSockJsSession sockJsSession = (AbstractHttpSockJsSession)wsSession;
        this.handleRequestInternal(request, response, wsHandler, sockJsSession);
    }

    protected void handleRequestInternal(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, AbstractHttpSockJsSession sockJsSession) throws SockJsException {
        Object[] messages;
        try {
            messages = this.readMessages(request);
        }
        catch (IOException ex) {
            this.logger.error((Object)"Failed to read message", (Throwable)ex);
            if (ex.getClass().getName().contains("Mapping")) {
                this.handleReadError(response, "Payload expected.", sockJsSession.getId());
            } else {
                this.handleReadError(response, "Broken JSON encoding.", sockJsSession.getId());
            }
            return;
        }
        catch (Exception ex) {
            this.logger.error((Object)"Failed to read message", (Throwable)ex);
            this.handleReadError(response, "Failed to read message(s)", sockJsSession.getId());
            return;
        }
        if (messages == null) {
            this.handleReadError(response, "Payload expected.", sockJsSession.getId());
            return;
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Received message(s): " + Arrays.toString(messages)));
        }
        response.setStatusCode(this.getResponseStatus());
        response.getHeaders().setContentType(new MediaType("text", "plain", StandardCharsets.UTF_8));
        sockJsSession.delegateMessages((String[])messages);
    }

    private void handleReadError(ServerHttpResponse response, String error, String sessionId) {
        try {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            response.getBody().write(error.getBytes(StandardCharsets.UTF_8));
        }
        catch (IOException ex) {
            throw new SockJsException("Failed to send error: " + error, sessionId, ex);
        }
    }

    @Nullable
    protected abstract String[] readMessages(ServerHttpRequest var1) throws IOException;

    protected abstract HttpStatus getResponseStatus();
}

