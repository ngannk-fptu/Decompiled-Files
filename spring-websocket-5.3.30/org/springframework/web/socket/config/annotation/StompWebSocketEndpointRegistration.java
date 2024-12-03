/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.socket.config.annotation;

import org.springframework.web.socket.config.annotation.SockJsServiceRegistration;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

public interface StompWebSocketEndpointRegistration {
    public SockJsServiceRegistration withSockJS();

    public StompWebSocketEndpointRegistration setHandshakeHandler(HandshakeHandler var1);

    public StompWebSocketEndpointRegistration addInterceptors(HandshakeInterceptor ... var1);

    public StompWebSocketEndpointRegistration setAllowedOrigins(String ... var1);

    public StompWebSocketEndpointRegistration setAllowedOriginPatterns(String ... var1);
}

