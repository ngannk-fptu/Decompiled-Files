/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.ResponseEntity
 *  org.springframework.lang.Nullable
 *  org.springframework.util.concurrent.ListenableFuture
 *  org.springframework.util.concurrent.SettableListenableFuture
 *  org.springframework.web.client.HttpServerErrorException
 */
package org.springframework.web.socket.sockjs.client;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.sockjs.client.TransportRequest;
import org.springframework.web.socket.sockjs.client.XhrClientSockJsSession;
import org.springframework.web.socket.sockjs.client.XhrTransport;
import org.springframework.web.socket.sockjs.frame.SockJsFrame;
import org.springframework.web.socket.sockjs.transport.TransportType;

public abstract class AbstractXhrTransport
implements XhrTransport {
    protected static final String PRELUDE;
    protected final Log logger = LogFactory.getLog(this.getClass());
    private boolean xhrStreamingDisabled;

    @Override
    public List<TransportType> getTransportTypes() {
        return this.isXhrStreamingDisabled() ? Collections.singletonList(TransportType.XHR) : Arrays.asList(TransportType.XHR_STREAMING, TransportType.XHR);
    }

    public void setXhrStreamingDisabled(boolean disabled) {
        this.xhrStreamingDisabled = disabled;
    }

    @Override
    public boolean isXhrStreamingDisabled() {
        return this.xhrStreamingDisabled;
    }

    @Override
    public ListenableFuture<WebSocketSession> connect(TransportRequest request, WebSocketHandler handler) {
        SettableListenableFuture connectFuture = new SettableListenableFuture();
        XhrClientSockJsSession session = new XhrClientSockJsSession(request, handler, this, (SettableListenableFuture<WebSocketSession>)connectFuture);
        request.addTimeoutTask(session.getTimeoutTask());
        URI receiveUrl = request.getTransportUrl();
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Starting XHR " + (this.isXhrStreamingDisabled() ? "Polling" : "Streaming") + "session url=" + receiveUrl));
        }
        HttpHeaders handshakeHeaders = new HttpHeaders();
        handshakeHeaders.putAll((Map)request.getHandshakeHeaders());
        this.connectInternal(request, handler, receiveUrl, handshakeHeaders, session, (SettableListenableFuture<WebSocketSession>)connectFuture);
        return connectFuture;
    }

    protected abstract void connectInternal(TransportRequest var1, WebSocketHandler var2, URI var3, HttpHeaders var4, XhrClientSockJsSession var5, SettableListenableFuture<WebSocketSession> var6);

    @Override
    public String executeInfoRequest(URI infoUrl, @Nullable HttpHeaders headers) {
        String result;
        ResponseEntity<String> response;
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Executing SockJS Info request, url=" + infoUrl));
        }
        HttpHeaders infoRequestHeaders = new HttpHeaders();
        if (headers != null) {
            infoRequestHeaders.putAll((Map)headers);
        }
        if ((response = this.executeInfoRequestInternal(infoUrl, infoRequestHeaders)).getStatusCode() != HttpStatus.OK) {
            if (this.logger.isErrorEnabled()) {
                this.logger.error((Object)("SockJS Info request (url=" + infoUrl + ") failed: " + response));
            }
            throw new HttpServerErrorException(response.getStatusCode());
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("SockJS Info request (url=" + infoUrl + ") response: " + response));
        }
        return (result = (String)response.getBody()) != null ? result : "";
    }

    protected abstract ResponseEntity<String> executeInfoRequestInternal(URI var1, HttpHeaders var2);

    @Override
    public void executeSendRequest(URI url, HttpHeaders headers, TextMessage message) {
        ResponseEntity<String> response;
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Starting XHR send, url=" + url));
        }
        if ((response = this.executeSendRequestInternal(url, headers, message)).getStatusCode() != HttpStatus.NO_CONTENT) {
            if (this.logger.isErrorEnabled()) {
                this.logger.error((Object)("XHR send request (url=" + url + ") failed: " + response));
            }
            throw new HttpServerErrorException(response.getStatusCode());
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("XHR send request (url=" + url + ") response: " + response));
        }
    }

    protected abstract ResponseEntity<String> executeSendRequestInternal(URI var1, HttpHeaders var2, TextMessage var3);

    static {
        byte[] bytes = new byte[2048];
        Arrays.fill(bytes, (byte)104);
        PRELUDE = new String(bytes, SockJsFrame.CHARSET);
    }
}

