/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.websocket.Endpoint
 *  javax.websocket.Extension
 *  javax.websocket.server.ServerEndpointConfig
 *  org.apache.tomcat.websocket.server.WsServerContainer
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.socket.server.standard;

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
import org.apache.tomcat.websocket.server.WsServerContainer;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.standard.AbstractStandardUpgradeStrategy;
import org.springframework.web.socket.server.standard.ServerEndpointRegistration;

public class TomcatRequestUpgradeStrategy
extends AbstractStandardUpgradeStrategy {
    @Override
    public String[] getSupportedVersions() {
        return new String[]{"13"};
    }

    @Override
    public void upgradeInternal(ServerHttpRequest request, ServerHttpResponse response, @Nullable String selectedProtocol, List<Extension> selectedExtensions, Endpoint endpoint) throws HandshakeFailureException {
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

    public WsServerContainer getContainer(HttpServletRequest request) {
        return (WsServerContainer)super.getContainer(request);
    }
}

