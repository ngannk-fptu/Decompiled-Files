/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.MediaType
 *  org.springframework.util.Assert
 *  org.springframework.util.concurrent.SettableListenableFuture
 */
package org.springframework.web.socket.sockjs.client;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.SettableListenableFuture;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.sockjs.client.AbstractClientSockJsSession;
import org.springframework.web.socket.sockjs.client.TransportRequest;
import org.springframework.web.socket.sockjs.client.XhrTransport;
import org.springframework.web.socket.sockjs.transport.TransportType;

public class XhrClientSockJsSession
extends AbstractClientSockJsSession {
    private final XhrTransport transport;
    private HttpHeaders headers;
    private HttpHeaders sendHeaders;
    private final URI sendUrl;
    private int textMessageSizeLimit = -1;
    private int binaryMessageSizeLimit = -1;

    public XhrClientSockJsSession(TransportRequest request, WebSocketHandler handler, XhrTransport transport, SettableListenableFuture<WebSocketSession> connectFuture) {
        super(request, handler, connectFuture);
        Assert.notNull((Object)transport, (String)"XhrTransport is required");
        this.transport = transport;
        this.headers = request.getHttpRequestHeaders();
        this.sendHeaders = new HttpHeaders();
        this.sendHeaders.putAll((Map)this.headers);
        this.sendHeaders.setContentType(MediaType.APPLICATION_JSON);
        this.sendUrl = request.getSockJsUrlInfo().getTransportUrl(TransportType.XHR_SEND);
    }

    public HttpHeaders getHeaders() {
        return this.headers;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return null;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        URI uri = this.getUri();
        return uri != null ? new InetSocketAddress(uri.getHost(), uri.getPort()) : null;
    }

    @Override
    public String getAcceptedProtocol() {
        return null;
    }

    @Override
    public void setTextMessageSizeLimit(int messageSizeLimit) {
        this.textMessageSizeLimit = messageSizeLimit;
    }

    @Override
    public int getTextMessageSizeLimit() {
        return this.textMessageSizeLimit;
    }

    @Override
    public void setBinaryMessageSizeLimit(int messageSizeLimit) {
        this.binaryMessageSizeLimit = -1;
    }

    @Override
    public int getBinaryMessageSizeLimit() {
        return this.binaryMessageSizeLimit;
    }

    @Override
    public List<WebSocketExtension> getExtensions() {
        return Collections.emptyList();
    }

    @Override
    protected void sendInternal(TextMessage message) {
        this.transport.executeSendRequest(this.sendUrl, this.sendHeaders, message);
    }

    @Override
    protected void disconnect(CloseStatus status) {
    }
}

