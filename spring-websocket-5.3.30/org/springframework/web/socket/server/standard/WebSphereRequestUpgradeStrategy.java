/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.websocket.Endpoint
 *  javax.websocket.Extension
 *  javax.websocket.server.ServerContainer
 *  javax.websocket.server.ServerEndpointConfig
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.socket.server.standard;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Endpoint;
import javax.websocket.Extension;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.standard.AbstractStandardUpgradeStrategy;
import org.springframework.web.socket.server.standard.ServerEndpointRegistration;

public class WebSphereRequestUpgradeStrategy
extends AbstractStandardUpgradeStrategy {
    private static final Method upgradeMethod;

    @Override
    public String[] getSupportedVersions() {
        return new String[]{"13"};
    }

    @Override
    public void upgradeInternal(ServerHttpRequest httpRequest, ServerHttpResponse httpResponse, @Nullable String selectedProtocol, List<Extension> selectedExtensions, Endpoint endpoint) throws HandshakeFailureException {
        HttpServletRequest request = this.getHttpServletRequest(httpRequest);
        HttpServletResponse response = this.getHttpServletResponse(httpResponse);
        StringBuffer requestUrl = request.getRequestURL();
        String path = request.getRequestURI();
        Map pathParams = Collections.emptyMap();
        ServerEndpointRegistration endpointConfig = new ServerEndpointRegistration(path, endpoint);
        endpointConfig.setSubprotocols(Collections.singletonList(selectedProtocol));
        endpointConfig.setExtensions(selectedExtensions);
        try {
            ServerContainer container = this.getContainer(request);
            upgradeMethod.invoke((Object)container, new Object[]{request, response, endpointConfig, pathParams});
        }
        catch (Exception ex) {
            throw new HandshakeFailureException("Servlet request failed to upgrade to WebSocket for " + requestUrl, ex);
        }
    }

    static {
        ClassLoader loader = WebSphereRequestUpgradeStrategy.class.getClassLoader();
        try {
            Class<?> type = loader.loadClass("com.ibm.websphere.wsoc.WsWsocServerContainer");
            upgradeMethod = type.getMethod("doUpgrade", HttpServletRequest.class, HttpServletResponse.class, ServerEndpointConfig.class, Map.class);
        }
        catch (Exception ex) {
            throw new IllegalStateException("No compatible WebSphere version found", ex);
        }
    }
}

