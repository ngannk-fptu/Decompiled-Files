/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.undertow.websockets.core.WebSocketVersion
 *  io.undertow.websockets.jsr.ServerWebSocketContainer
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.websocket.Endpoint
 *  javax.websocket.Extension
 *  javax.websocket.server.ServerEndpointConfig
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.socket.server.standard;

import io.undertow.websockets.core.WebSocketVersion;
import io.undertow.websockets.jsr.ServerWebSocketContainer;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Endpoint;
import javax.websocket.Extension;
import javax.websocket.server.ServerEndpointConfig;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.standard.AbstractStandardUpgradeStrategy;
import org.springframework.web.socket.server.standard.ServerEndpointRegistration;

public class UndertowRequestUpgradeStrategy
extends AbstractStandardUpgradeStrategy {
    private static final String[] VERSIONS = new String[]{WebSocketVersion.V13.toHttpHeaderValue(), WebSocketVersion.V08.toHttpHeaderValue(), WebSocketVersion.V07.toHttpHeaderValue()};

    @Override
    public String[] getSupportedVersions() {
        return VERSIONS;
    }

    @Override
    protected void upgradeInternal(ServerHttpRequest request, ServerHttpResponse response, @Nullable String selectedProtocol, List<Extension> selectedExtensions, Endpoint endpoint) throws HandshakeFailureException {
        HttpServletRequest servletRequest = this.getHttpServletRequest(request);
        HttpServletResponse servletResponse = this.getHttpServletResponse(response);
        StringBuffer requestUrl = servletRequest.getRequestURL();
        String path = servletRequest.getRequestURI();
        Map pathParams = Collections.emptyMap();
        ServerEndpointRegistration endpointConfig = new ServerEndpointRegistration(path, endpoint);
        endpointConfig.setSubprotocols(Collections.singletonList(selectedProtocol));
        endpointConfig.setExtensions(selectedExtensions);
        try {
            this.getContainer(servletRequest).doUpgrade(servletRequest, servletResponse, (ServerEndpointConfig)endpointConfig, pathParams);
        }
        catch (ServletException ex) {
            throw new HandshakeFailureException("Servlet request failed to upgrade to WebSocket: " + requestUrl, ex);
        }
        catch (IOException ex) {
            throw new HandshakeFailureException("Response update failed during upgrade to WebSocket: " + requestUrl, ex);
        }
    }

    public ServerWebSocketContainer getContainer(HttpServletRequest request) {
        return (ServerWebSocketContainer)super.getContainer(request);
    }
}

