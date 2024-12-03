/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.socket.config.annotation;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.SockJsServiceRegistration;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

public interface WebSocketHandlerRegistration {
    public WebSocketHandlerRegistration addHandler(WebSocketHandler var1, String ... var2);

    public WebSocketHandlerRegistration setHandshakeHandler(HandshakeHandler var1);

    public WebSocketHandlerRegistration addInterceptors(HandshakeInterceptor ... var1);

    public WebSocketHandlerRegistration setAllowedOrigins(String ... var1);

    public WebSocketHandlerRegistration setAllowedOriginPatterns(String ... var1);

    public SockJsServiceRegistration withSockJS();
}

