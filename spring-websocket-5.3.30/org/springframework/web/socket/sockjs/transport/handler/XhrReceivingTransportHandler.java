/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.socket.sockjs.transport.handler;

import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.sockjs.transport.TransportType;
import org.springframework.web.socket.sockjs.transport.handler.AbstractHttpReceivingTransportHandler;

public class XhrReceivingTransportHandler
extends AbstractHttpReceivingTransportHandler {
    @Override
    public TransportType getTransportType() {
        return TransportType.XHR_SEND;
    }

    @Override
    @Nullable
    protected String[] readMessages(ServerHttpRequest request) throws IOException {
        return this.getServiceConfig().getMessageCodec().decodeInputStream(request.getBody());
    }

    @Override
    protected HttpStatus getResponseStatus() {
        return HttpStatus.NO_CONTENT;
    }
}

