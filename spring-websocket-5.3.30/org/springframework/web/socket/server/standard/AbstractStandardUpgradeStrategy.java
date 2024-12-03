/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.websocket.Endpoint
 *  javax.websocket.Extension
 *  javax.websocket.WebSocketContainer
 *  javax.websocket.server.ServerContainer
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.http.server.ServletServerHttpRequest
 *  org.springframework.http.server.ServletServerHttpResponse
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.web.socket.server.standard;

import java.net.InetSocketAddress;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.Endpoint;
import javax.websocket.Extension;
import javax.websocket.WebSocketContainer;
import javax.websocket.server.ServerContainer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.adapter.standard.StandardToWebSocketExtensionAdapter;
import org.springframework.web.socket.adapter.standard.StandardWebSocketHandlerAdapter;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;
import org.springframework.web.socket.adapter.standard.WebSocketToStandardExtensionAdapter;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.RequestUpgradeStrategy;

public abstract class AbstractStandardUpgradeStrategy
implements RequestUpgradeStrategy {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private volatile List<WebSocketExtension> extensions;

    protected ServerContainer getContainer(HttpServletRequest request) {
        ServletContext servletContext = request.getServletContext();
        String attrName = "javax.websocket.server.ServerContainer";
        ServerContainer container = (ServerContainer)servletContext.getAttribute(attrName);
        Assert.notNull((Object)container, (String)"No 'javax.websocket.server.ServerContainer' ServletContext attribute. Are you running in a Servlet container that supports JSR-356?");
        return container;
    }

    protected final HttpServletRequest getHttpServletRequest(ServerHttpRequest request) {
        Assert.isInstanceOf(ServletServerHttpRequest.class, (Object)request, (String)"ServletServerHttpRequest required");
        return ((ServletServerHttpRequest)request).getServletRequest();
    }

    protected final HttpServletResponse getHttpServletResponse(ServerHttpResponse response) {
        Assert.isInstanceOf(ServletServerHttpResponse.class, (Object)response, (String)"ServletServerHttpResponse required");
        return ((ServletServerHttpResponse)response).getServletResponse();
    }

    @Override
    public List<WebSocketExtension> getSupportedExtensions(ServerHttpRequest request) {
        List<WebSocketExtension> extensions = this.extensions;
        if (extensions == null) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest)request).getServletRequest();
            this.extensions = extensions = this.getInstalledExtensions((WebSocketContainer)this.getContainer(servletRequest));
        }
        return extensions;
    }

    protected List<WebSocketExtension> getInstalledExtensions(WebSocketContainer container) {
        ArrayList<WebSocketExtension> result = new ArrayList<WebSocketExtension>();
        for (Extension extension : container.getInstalledExtensions()) {
            result.add(new StandardToWebSocketExtensionAdapter(extension));
        }
        return result;
    }

    @Override
    public void upgrade(ServerHttpRequest request, ServerHttpResponse response, @Nullable String selectedProtocol, List<WebSocketExtension> selectedExtensions, @Nullable Principal user, WebSocketHandler wsHandler, Map<String, Object> attrs) throws HandshakeFailureException {
        HttpHeaders headers = request.getHeaders();
        InetSocketAddress localAddr = null;
        try {
            localAddr = request.getLocalAddress();
        }
        catch (Exception exception) {
            // empty catch block
        }
        InetSocketAddress remoteAddr = null;
        try {
            remoteAddr = request.getRemoteAddress();
        }
        catch (Exception exception) {
            // empty catch block
        }
        StandardWebSocketSession session = new StandardWebSocketSession(headers, attrs, localAddr, remoteAddr, user);
        StandardWebSocketHandlerAdapter endpoint = new StandardWebSocketHandlerAdapter(wsHandler, session);
        ArrayList<Extension> extensions = new ArrayList<Extension>();
        for (WebSocketExtension extension : selectedExtensions) {
            extensions.add(new WebSocketToStandardExtensionAdapter(extension));
        }
        this.upgradeInternal(request, response, selectedProtocol, extensions, endpoint);
    }

    protected abstract void upgradeInternal(ServerHttpRequest var1, ServerHttpResponse var2, @Nullable String var3, List<Extension> var4, Endpoint var5) throws HandshakeFailureException;
}

