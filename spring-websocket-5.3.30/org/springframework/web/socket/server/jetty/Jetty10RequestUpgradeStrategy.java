/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.http.server.ServletServerHttpRequest
 *  org.springframework.http.server.ServletServerHttpResponse
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.web.socket.server.jetty;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.adapter.jetty.Jetty10WebSocketHandlerAdapter;
import org.springframework.web.socket.adapter.jetty.JettyWebSocketSession;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.RequestUpgradeStrategy;

public class Jetty10RequestUpgradeStrategy
implements RequestUpgradeStrategy {
    private static final String[] SUPPORTED_VERSIONS = new String[]{String.valueOf(13)};
    private static final Class<?> webSocketCreatorClass;
    private static final Method getContainerMethod;
    private static final Method upgradeMethod;
    private static final Method setAcceptedSubProtocol;

    @Override
    public String[] getSupportedVersions() {
        return SUPPORTED_VERSIONS;
    }

    @Override
    public List<WebSocketExtension> getSupportedExtensions(ServerHttpRequest request) {
        return Collections.emptyList();
    }

    @Override
    public void upgrade(ServerHttpRequest request, ServerHttpResponse response, @Nullable String selectedProtocol, List<WebSocketExtension> selectedExtensions, @Nullable Principal user, WebSocketHandler handler, Map<String, Object> attributes) throws HandshakeFailureException {
        Assert.isInstanceOf(ServletServerHttpRequest.class, (Object)request, (String)"ServletServerHttpRequest required");
        HttpServletRequest servletRequest = ((ServletServerHttpRequest)request).getServletRequest();
        ServletContext servletContext = servletRequest.getServletContext();
        Assert.isInstanceOf(ServletServerHttpResponse.class, (Object)response, (String)"ServletServerHttpResponse required");
        HttpServletResponse servletResponse = ((ServletServerHttpResponse)response).getServletResponse();
        JettyWebSocketSession session = new JettyWebSocketSession(attributes, user);
        Jetty10WebSocketHandlerAdapter handlerAdapter = new Jetty10WebSocketHandlerAdapter(handler, session);
        try {
            Object creator = Jetty10RequestUpgradeStrategy.createJettyWebSocketCreator(handlerAdapter, selectedProtocol);
            Object container = ReflectionUtils.invokeMethod((Method)getContainerMethod, null, (Object[])new Object[]{servletContext});
            ReflectionUtils.invokeMethod((Method)upgradeMethod, (Object)container, (Object[])new Object[]{creator, servletRequest, servletResponse});
        }
        catch (UndeclaredThrowableException ex) {
            throw new HandshakeFailureException("Failed to upgrade", ex.getUndeclaredThrowable());
        }
        catch (Exception ex) {
            throw new HandshakeFailureException("Failed to upgrade", ex);
        }
    }

    private static Object createJettyWebSocketCreator(Jetty10WebSocketHandlerAdapter adapter, @Nullable String protocol) {
        return Proxy.newProxyInstance(webSocketCreatorClass.getClassLoader(), new Class[]{webSocketCreatorClass}, (proxy, method, args) -> {
            if (protocol != null) {
                ReflectionUtils.invokeMethod((Method)setAcceptedSubProtocol, (Object)args[1], (Object[])new Object[]{protocol});
            }
            return adapter;
        });
    }

    static {
        ClassLoader loader = Jetty10RequestUpgradeStrategy.class.getClassLoader();
        try {
            webSocketCreatorClass = loader.loadClass("org.eclipse.jetty.websocket.server.JettyWebSocketCreator");
            Class<?> type = loader.loadClass("org.eclipse.jetty.websocket.server.JettyWebSocketServerContainer");
            getContainerMethod = type.getMethod("getContainer", ServletContext.class);
            Method upgrade = ReflectionUtils.findMethod(type, (String)"upgrade", (Class[])null);
            Assert.state((upgrade != null ? 1 : 0) != 0, (String)"Upgrade method not found");
            upgradeMethod = upgrade;
            type = loader.loadClass("org.eclipse.jetty.websocket.server.JettyServerUpgradeResponse");
            setAcceptedSubProtocol = type.getMethod("setAcceptedSubProtocol", String.class);
        }
        catch (Exception ex) {
            throw new IllegalStateException("No compatible Jetty version found", ex);
        }
    }
}

