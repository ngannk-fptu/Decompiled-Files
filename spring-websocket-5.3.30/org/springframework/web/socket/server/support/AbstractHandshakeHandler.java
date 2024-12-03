/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.context.Lifecycle
 *  org.springframework.core.log.LogFormatUtils
 *  org.springframework.http.HttpMethod
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.socket.server.support;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.Lifecycle;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.RequestUpgradeStrategy;

public abstract class AbstractHandshakeHandler
implements HandshakeHandler,
Lifecycle {
    private static final boolean tomcatWsPresent;
    private static final boolean jettyWsPresent;
    private static final boolean jetty10WsPresent;
    private static final boolean undertowWsPresent;
    private static final boolean glassfishWsPresent;
    private static final boolean weblogicWsPresent;
    private static final boolean websphereWsPresent;
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final RequestUpgradeStrategy requestUpgradeStrategy;
    private final List<String> supportedProtocols = new ArrayList<String>();
    private volatile boolean running;

    protected AbstractHandshakeHandler() {
        this(AbstractHandshakeHandler.initRequestUpgradeStrategy());
    }

    protected AbstractHandshakeHandler(RequestUpgradeStrategy requestUpgradeStrategy) {
        Assert.notNull((Object)requestUpgradeStrategy, (String)"RequestUpgradeStrategy must not be null");
        this.requestUpgradeStrategy = requestUpgradeStrategy;
    }

    private static RequestUpgradeStrategy initRequestUpgradeStrategy() {
        String className;
        if (tomcatWsPresent) {
            className = "org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy";
        } else if (jettyWsPresent) {
            className = "org.springframework.web.socket.server.jetty.JettyRequestUpgradeStrategy";
        } else if (jetty10WsPresent) {
            className = "org.springframework.web.socket.server.jetty.Jetty10RequestUpgradeStrategy";
        } else if (undertowWsPresent) {
            className = "org.springframework.web.socket.server.standard.UndertowRequestUpgradeStrategy";
        } else if (glassfishWsPresent) {
            className = "org.springframework.web.socket.server.standard.GlassFishRequestUpgradeStrategy";
        } else if (weblogicWsPresent) {
            className = "org.springframework.web.socket.server.standard.WebLogicRequestUpgradeStrategy";
        } else if (websphereWsPresent) {
            className = "org.springframework.web.socket.server.standard.WebSphereRequestUpgradeStrategy";
        } else {
            throw new IllegalStateException("No suitable default RequestUpgradeStrategy found");
        }
        try {
            Class clazz = ClassUtils.forName((String)className, (ClassLoader)AbstractHandshakeHandler.class.getClassLoader());
            return (RequestUpgradeStrategy)ReflectionUtils.accessibleConstructor((Class)clazz, (Class[])new Class[0]).newInstance(new Object[0]);
        }
        catch (Exception ex) {
            throw new IllegalStateException("Failed to instantiate RequestUpgradeStrategy: " + className, ex);
        }
    }

    public RequestUpgradeStrategy getRequestUpgradeStrategy() {
        return this.requestUpgradeStrategy;
    }

    public void setSupportedProtocols(String ... protocols) {
        this.supportedProtocols.clear();
        for (String protocol : protocols) {
            this.supportedProtocols.add(protocol.toLowerCase());
        }
    }

    public String[] getSupportedProtocols() {
        return StringUtils.toStringArray(this.supportedProtocols);
    }

    public void start() {
        if (!this.isRunning()) {
            this.running = true;
            this.doStart();
        }
    }

    protected void doStart() {
        if (this.requestUpgradeStrategy instanceof Lifecycle) {
            ((Lifecycle)this.requestUpgradeStrategy).start();
        }
    }

    public void stop() {
        if (this.isRunning()) {
            this.running = false;
            this.doStop();
        }
    }

    protected void doStop() {
        if (this.requestUpgradeStrategy instanceof Lifecycle) {
            ((Lifecycle)this.requestUpgradeStrategy).stop();
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    @Override
    public final boolean doHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws HandshakeFailureException {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders(request.getHeaders());
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Processing request " + request.getURI() + " with headers=" + (Object)((Object)headers)));
        }
        try {
            if (HttpMethod.GET != request.getMethod()) {
                response.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
                response.getHeaders().setAllow(Collections.singleton(HttpMethod.GET));
                if (this.logger.isErrorEnabled()) {
                    this.logger.error((Object)("Handshake failed due to unexpected HTTP method: " + request.getMethod()));
                }
                return false;
            }
            if (!"WebSocket".equalsIgnoreCase(headers.getUpgrade())) {
                this.handleInvalidUpgradeHeader(request, response);
                return false;
            }
            if (!headers.getConnection().contains("Upgrade") && !headers.getConnection().contains("upgrade")) {
                this.handleInvalidConnectHeader(request, response);
                return false;
            }
            if (!this.isWebSocketVersionSupported(headers)) {
                this.handleWebSocketVersionNotSupported(request, response);
                return false;
            }
            if (!this.isValidOrigin(request)) {
                response.setStatusCode(HttpStatus.FORBIDDEN);
                return false;
            }
            String wsKey = headers.getSecWebSocketKey();
            if (wsKey == null) {
                if (this.logger.isErrorEnabled()) {
                    this.logger.error((Object)"Missing \"Sec-WebSocket-Key\" header");
                }
                response.setStatusCode(HttpStatus.BAD_REQUEST);
                return false;
            }
        }
        catch (IOException ex) {
            throw new HandshakeFailureException("Response update failed during upgrade to WebSocket: " + request.getURI(), ex);
        }
        String subProtocol = this.selectProtocol(headers.getSecWebSocketProtocol(), wsHandler);
        List<WebSocketExtension> requested = headers.getSecWebSocketExtensions();
        List<WebSocketExtension> supported = this.requestUpgradeStrategy.getSupportedExtensions(request);
        List<WebSocketExtension> extensions = this.filterRequestedExtensions(request, requested, supported);
        Principal user = this.determineUser(request, wsHandler, attributes);
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Upgrading to WebSocket, subProtocol=" + subProtocol + ", extensions=" + extensions));
        }
        this.requestUpgradeStrategy.upgrade(request, response, subProtocol, extensions, user, wsHandler, attributes);
        return true;
    }

    protected void handleInvalidUpgradeHeader(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
        if (this.logger.isErrorEnabled()) {
            this.logger.error((Object)LogFormatUtils.formatValue((Object)("Handshake failed due to invalid Upgrade header: " + request.getHeaders().getUpgrade()), (int)-1, (boolean)true));
        }
        response.setStatusCode(HttpStatus.BAD_REQUEST);
        response.getBody().write("Can \"Upgrade\" only to \"WebSocket\".".getBytes(StandardCharsets.UTF_8));
    }

    protected void handleInvalidConnectHeader(ServerHttpRequest request, ServerHttpResponse response) throws IOException {
        if (this.logger.isErrorEnabled()) {
            this.logger.error((Object)LogFormatUtils.formatValue((Object)("Handshake failed due to invalid Connection header" + request.getHeaders().getConnection()), (int)-1, (boolean)true));
        }
        response.setStatusCode(HttpStatus.BAD_REQUEST);
        response.getBody().write("\"Connection\" must be \"upgrade\".".getBytes(StandardCharsets.UTF_8));
    }

    protected boolean isWebSocketVersionSupported(WebSocketHttpHeaders httpHeaders) {
        String[] supportedVersions;
        String version = httpHeaders.getSecWebSocketVersion();
        for (String supportedVersion : supportedVersions = this.getSupportedVersions()) {
            if (!supportedVersion.trim().equals(version)) continue;
            return true;
        }
        return false;
    }

    protected String[] getSupportedVersions() {
        return this.requestUpgradeStrategy.getSupportedVersions();
    }

    protected void handleWebSocketVersionNotSupported(ServerHttpRequest request, ServerHttpResponse response) {
        if (this.logger.isErrorEnabled()) {
            String version = request.getHeaders().getFirst("Sec-WebSocket-Version");
            this.logger.error((Object)LogFormatUtils.formatValue((Object)("Handshake failed due to unsupported WebSocket version: " + version + ". Supported versions: " + Arrays.toString(this.getSupportedVersions())), (int)-1, (boolean)true));
        }
        response.setStatusCode(HttpStatus.UPGRADE_REQUIRED);
        response.getHeaders().set("Sec-WebSocket-Version", StringUtils.arrayToCommaDelimitedString((Object[])this.getSupportedVersions()));
    }

    protected boolean isValidOrigin(ServerHttpRequest request) {
        return true;
    }

    @Nullable
    protected String selectProtocol(List<String> requestedProtocols, WebSocketHandler webSocketHandler) {
        List<String> handlerProtocols = this.determineHandlerSupportedProtocols(webSocketHandler);
        for (String protocol : requestedProtocols) {
            if (handlerProtocols.contains(protocol.toLowerCase())) {
                return protocol;
            }
            if (!this.supportedProtocols.contains(protocol.toLowerCase())) continue;
            return protocol;
        }
        return null;
    }

    protected final List<String> determineHandlerSupportedProtocols(WebSocketHandler handler) {
        WebSocketHandler handlerToCheck = WebSocketHandlerDecorator.unwrap(handler);
        List<String> subProtocols = null;
        if (handlerToCheck instanceof SubProtocolCapable) {
            subProtocols = ((SubProtocolCapable)((Object)handlerToCheck)).getSubProtocols();
        }
        return subProtocols != null ? subProtocols : Collections.emptyList();
    }

    protected List<WebSocketExtension> filterRequestedExtensions(ServerHttpRequest request, List<WebSocketExtension> requestedExtensions, List<WebSocketExtension> supportedExtensions) {
        ArrayList<WebSocketExtension> result = new ArrayList<WebSocketExtension>(requestedExtensions.size());
        for (WebSocketExtension extension : requestedExtensions) {
            if (!supportedExtensions.contains(extension)) continue;
            result.add(extension);
        }
        return result;
    }

    @Nullable
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        return request.getPrincipal();
    }

    static {
        ClassLoader classLoader = AbstractHandshakeHandler.class.getClassLoader();
        tomcatWsPresent = ClassUtils.isPresent((String)"org.apache.tomcat.websocket.server.WsHttpUpgradeHandler", (ClassLoader)classLoader);
        jetty10WsPresent = ClassUtils.isPresent((String)"org.eclipse.jetty.websocket.server.JettyWebSocketServerContainer", (ClassLoader)classLoader);
        jettyWsPresent = ClassUtils.isPresent((String)"org.eclipse.jetty.websocket.server.WebSocketServerFactory", (ClassLoader)classLoader);
        undertowWsPresent = ClassUtils.isPresent((String)"io.undertow.websockets.jsr.ServerWebSocketContainer", (ClassLoader)classLoader);
        glassfishWsPresent = ClassUtils.isPresent((String)"org.glassfish.tyrus.servlet.TyrusHttpUpgradeHandler", (ClassLoader)classLoader);
        weblogicWsPresent = ClassUtils.isPresent((String)"weblogic.websocket.tyrus.TyrusServletWriter", (ClassLoader)classLoader);
        websphereWsPresent = ClassUtils.isPresent((String)"com.ibm.websphere.wsoc.WsWsocServerContainer", (ClassLoader)classLoader);
    }
}

