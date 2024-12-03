/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.ObjectUtils
 *  org.springframework.web.HttpRequestHandler
 */
package org.springframework.web.socket.config.annotation;

import java.util.Arrays;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.AbstractWebSocketHandlerRegistration;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.WebSocketHttpRequestHandler;
import org.springframework.web.socket.sockjs.SockJsService;
import org.springframework.web.socket.sockjs.support.SockJsHttpRequestHandler;

public class ServletWebSocketHandlerRegistration
extends AbstractWebSocketHandlerRegistration<MultiValueMap<HttpRequestHandler, String>> {
    @Override
    protected MultiValueMap<HttpRequestHandler, String> createMappings() {
        return new LinkedMultiValueMap();
    }

    @Override
    protected void addSockJsServiceMapping(MultiValueMap<HttpRequestHandler, String> mappings, SockJsService sockJsService, WebSocketHandler handler, String pathPattern) {
        SockJsHttpRequestHandler httpHandler = new SockJsHttpRequestHandler(sockJsService, handler);
        mappings.add((Object)httpHandler, (Object)pathPattern);
    }

    @Override
    protected void addWebSocketHandlerMapping(MultiValueMap<HttpRequestHandler, String> mappings, WebSocketHandler webSocketHandler, HandshakeHandler handshakeHandler, HandshakeInterceptor[] interceptors, String path) {
        WebSocketHttpRequestHandler httpHandler = new WebSocketHttpRequestHandler(webSocketHandler, handshakeHandler);
        if (!ObjectUtils.isEmpty((Object[])interceptors)) {
            httpHandler.setHandshakeInterceptors(Arrays.asList(interceptors));
        }
        mappings.add((Object)httpHandler, (Object)path);
    }
}

