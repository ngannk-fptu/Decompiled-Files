/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.HttpHeaders
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.socket.sockjs.client;

import java.net.URI;
import java.security.Principal;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.sockjs.client.SockJsUrlInfo;
import org.springframework.web.socket.sockjs.frame.SockJsMessageCodec;

public interface TransportRequest {
    public SockJsUrlInfo getSockJsUrlInfo();

    public HttpHeaders getHandshakeHeaders();

    public HttpHeaders getHttpRequestHeaders();

    public URI getTransportUrl();

    @Nullable
    public Principal getUser();

    public SockJsMessageCodec getMessageCodec();

    public void addTimeoutTask(Runnable var1);
}

