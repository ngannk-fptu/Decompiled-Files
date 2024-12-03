/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.eclipse.jetty.websocket.api.WebSocketPolicy
 *  org.eclipse.jetty.websocket.api.extensions.ExtensionConfig
 *  org.eclipse.jetty.websocket.api.extensions.ExtensionFactory
 *  org.eclipse.jetty.websocket.server.WebSocketServerFactory
 *  org.springframework.context.Lifecycle
 *  org.springframework.core.NamedThreadLocal
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.http.server.ServletServerHttpRequest
 *  org.springframework.http.server.ServletServerHttpResponse
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.web.context.ServletContextAware
 */
package org.springframework.web.socket.server.jetty;

import java.io.IOException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.websocket.api.WebSocketPolicy;
import org.eclipse.jetty.websocket.api.extensions.ExtensionConfig;
import org.eclipse.jetty.websocket.api.extensions.ExtensionFactory;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.springframework.context.Lifecycle;
import org.springframework.core.NamedThreadLocal;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.adapter.jetty.JettyWebSocketHandlerAdapter;
import org.springframework.web.socket.adapter.jetty.JettyWebSocketSession;
import org.springframework.web.socket.adapter.jetty.WebSocketToJettyExtensionConfigAdapter;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.RequestUpgradeStrategy;

public class JettyRequestUpgradeStrategy
implements RequestUpgradeStrategy,
ServletContextAware,
Lifecycle {
    private static final ThreadLocal<WebSocketHandlerContainer> containerHolder = new NamedThreadLocal("WebSocketHandlerContainer");
    @Nullable
    private WebSocketPolicy policy;
    @Nullable
    private volatile WebSocketServerFactory factory;
    @Nullable
    private ServletContext servletContext;
    private volatile boolean running;
    @Nullable
    private volatile List<WebSocketExtension> supportedExtensions;

    public JettyRequestUpgradeStrategy() {
        this.policy = WebSocketPolicy.newServerPolicy();
    }

    public JettyRequestUpgradeStrategy(WebSocketPolicy policy) {
        Assert.notNull((Object)policy, (String)"WebSocketPolicy must not be null");
        this.policy = policy;
    }

    public JettyRequestUpgradeStrategy(WebSocketServerFactory factory) {
        Assert.notNull((Object)factory, (String)"WebSocketServerFactory must not be null");
        this.factory = factory;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void start() {
        if (!this.isRunning()) {
            this.running = true;
            try {
                WebSocketServerFactory factory = this.factory;
                if (factory == null) {
                    Assert.state((this.servletContext != null ? 1 : 0) != 0, (String)"No ServletContext set");
                    this.factory = factory = new WebSocketServerFactory(this.servletContext, this.policy);
                }
                factory.setCreator((request, response) -> {
                    WebSocketHandlerContainer container = containerHolder.get();
                    Assert.state((container != null ? 1 : 0) != 0, (String)"Expected WebSocketHandlerContainer");
                    response.setAcceptedSubProtocol(container.getSelectedProtocol());
                    response.setExtensions(container.getExtensionConfigs());
                    return container.getHandler();
                });
                factory.start();
            }
            catch (Throwable ex) {
                throw new IllegalStateException("Unable to start Jetty WebSocketServerFactory", ex);
            }
        }
    }

    public void stop() {
        if (this.isRunning()) {
            this.running = false;
            WebSocketServerFactory factory = this.factory;
            if (factory != null) {
                try {
                    factory.stop();
                }
                catch (Throwable ex) {
                    throw new IllegalStateException("Unable to stop Jetty WebSocketServerFactory", ex);
                }
            }
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    @Override
    public String[] getSupportedVersions() {
        return new String[]{String.valueOf(13)};
    }

    @Override
    public List<WebSocketExtension> getSupportedExtensions(ServerHttpRequest request) {
        List<WebSocketExtension> extensions = this.supportedExtensions;
        if (extensions == null) {
            this.supportedExtensions = extensions = this.buildWebSocketExtensions();
        }
        return extensions;
    }

    private List<WebSocketExtension> buildWebSocketExtensions() {
        Set<String> names = this.getExtensionNames();
        ArrayList<WebSocketExtension> result = new ArrayList<WebSocketExtension>(names.size());
        for (String name : names) {
            result.add(new WebSocketExtension(name));
        }
        return result;
    }

    private Set<String> getExtensionNames() {
        WebSocketServerFactory factory = this.factory;
        Assert.state((factory != null ? 1 : 0) != 0, (String)"No WebSocketServerFactory available");
        try {
            return factory.getAvailableExtensionNames();
        }
        catch (IncompatibleClassChangeError ex) {
            Class<ExtensionFactory> clazz = ExtensionFactory.class;
            Method method = ClassUtils.getMethod(clazz, (String)"getExtensionNames", (Class[])new Class[0]);
            Set<String> result = (Set<String>)ReflectionUtils.invokeMethod((Method)method, (Object)factory.getExtensionFactory());
            return result != null ? result : Collections.emptySet();
        }
    }

    @Override
    public void upgrade(ServerHttpRequest request, ServerHttpResponse response, @Nullable String selectedProtocol, List<WebSocketExtension> selectedExtensions, @Nullable Principal user, WebSocketHandler wsHandler, Map<String, Object> attributes) throws HandshakeFailureException {
        Assert.isInstanceOf(ServletServerHttpRequest.class, (Object)request, (String)"ServletServerHttpRequest required");
        HttpServletRequest servletRequest = ((ServletServerHttpRequest)request).getServletRequest();
        Assert.isInstanceOf(ServletServerHttpResponse.class, (Object)response, (String)"ServletServerHttpResponse required");
        HttpServletResponse servletResponse = ((ServletServerHttpResponse)response).getServletResponse();
        WebSocketServerFactory factory = this.factory;
        Assert.state((factory != null ? 1 : 0) != 0, (String)"No WebSocketServerFactory available");
        Assert.isTrue((boolean)factory.isUpgradeRequest(servletRequest, servletResponse), (String)"Not a WebSocket handshake");
        JettyWebSocketSession session = new JettyWebSocketSession(attributes, user);
        JettyWebSocketHandlerAdapter handlerAdapter = new JettyWebSocketHandlerAdapter(wsHandler, session);
        WebSocketHandlerContainer container = new WebSocketHandlerContainer(handlerAdapter, selectedProtocol, selectedExtensions);
        try {
            containerHolder.set(container);
            factory.acceptWebSocket(servletRequest, servletResponse);
        }
        catch (IOException ex) {
            throw new HandshakeFailureException("Response update failed during upgrade to WebSocket: " + request.getURI(), ex);
        }
        finally {
            containerHolder.remove();
        }
    }

    private static class WebSocketHandlerContainer {
        private final JettyWebSocketHandlerAdapter handler;
        @Nullable
        private final String selectedProtocol;
        private final List<ExtensionConfig> extensionConfigs;

        public WebSocketHandlerContainer(JettyWebSocketHandlerAdapter handler, @Nullable String protocol, List<WebSocketExtension> extensions) {
            this.handler = handler;
            this.selectedProtocol = protocol;
            if (CollectionUtils.isEmpty(extensions)) {
                this.extensionConfigs = new ArrayList<ExtensionConfig>(0);
            } else {
                this.extensionConfigs = new ArrayList<ExtensionConfig>(extensions.size());
                for (WebSocketExtension extension : extensions) {
                    this.extensionConfigs.add(new WebSocketToJettyExtensionConfigAdapter(extension));
                }
            }
        }

        public JettyWebSocketHandlerAdapter getHandler() {
            return this.handler;
        }

        @Nullable
        public String getSelectedProtocol() {
            return this.selectedProtocol;
        }

        public List<ExtensionConfig> getExtensionConfigs() {
            return this.extensionConfigs;
        }
    }
}

