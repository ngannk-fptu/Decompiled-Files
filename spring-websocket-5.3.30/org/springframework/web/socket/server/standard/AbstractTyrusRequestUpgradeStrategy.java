/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.websocket.DeploymentException
 *  javax.websocket.Endpoint
 *  javax.websocket.EndpointConfig
 *  javax.websocket.Extension
 *  javax.websocket.WebSocketContainer
 *  org.glassfish.tyrus.core.ComponentProviderService
 *  org.glassfish.tyrus.core.RequestContext
 *  org.glassfish.tyrus.core.RequestContext$Builder
 *  org.glassfish.tyrus.core.TyrusEndpointWrapper
 *  org.glassfish.tyrus.core.TyrusUpgradeResponse
 *  org.glassfish.tyrus.core.TyrusWebSocketEngine
 *  org.glassfish.tyrus.core.Version
 *  org.glassfish.tyrus.server.TyrusServerContainer
 *  org.glassfish.tyrus.spi.UpgradeRequest
 *  org.glassfish.tyrus.spi.UpgradeResponse
 *  org.glassfish.tyrus.spi.WebSocketEngine$UpgradeInfo
 *  org.glassfish.tyrus.spi.WebSocketEngine$UpgradeStatus
 *  org.springframework.beans.DirectFieldAccessor
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.socket.server.standard;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Extension;
import javax.websocket.WebSocketContainer;
import org.glassfish.tyrus.core.ComponentProviderService;
import org.glassfish.tyrus.core.RequestContext;
import org.glassfish.tyrus.core.TyrusEndpointWrapper;
import org.glassfish.tyrus.core.TyrusUpgradeResponse;
import org.glassfish.tyrus.core.TyrusWebSocketEngine;
import org.glassfish.tyrus.core.Version;
import org.glassfish.tyrus.server.TyrusServerContainer;
import org.glassfish.tyrus.spi.UpgradeRequest;
import org.glassfish.tyrus.spi.UpgradeResponse;
import org.glassfish.tyrus.spi.WebSocketEngine;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.standard.AbstractStandardUpgradeStrategy;
import org.springframework.web.socket.server.standard.ServerEndpointRegistration;

public abstract class AbstractTyrusRequestUpgradeStrategy
extends AbstractStandardUpgradeStrategy {
    private static final Random random = new Random();
    private static final Constructor<?> constructor;
    private static final boolean constructorWithBooleanArgument;
    private static final Method registerMethod;
    private static final Method unRegisterMethod;
    private final ComponentProviderService componentProvider = ComponentProviderService.create();

    private static Constructor<?> getEndpointConstructor() {
        for (Constructor<?> current : TyrusEndpointWrapper.class.getConstructors()) {
            Class<?>[] types = current.getParameterTypes();
            if (Endpoint.class != types[0] || EndpointConfig.class != types[1]) continue;
            return current;
        }
        throw new IllegalStateException("No compatible Tyrus version found");
    }

    @Override
    public String[] getSupportedVersions() {
        return StringUtils.tokenizeToStringArray((String)Version.getSupportedWireProtocolVersions(), (String)",");
    }

    @Override
    protected List<WebSocketExtension> getInstalledExtensions(WebSocketContainer container) {
        try {
            return super.getInstalledExtensions(container);
        }
        catch (UnsupportedOperationException ex) {
            return new ArrayList<WebSocketExtension>(0);
        }
    }

    @Override
    public void upgradeInternal(ServerHttpRequest request, ServerHttpResponse response, @Nullable String selectedProtocol, List<Extension> extensions, Endpoint endpoint) throws HandshakeFailureException {
        boolean success;
        HttpServletRequest servletRequest = this.getHttpServletRequest(request);
        HttpServletResponse servletResponse = this.getHttpServletResponse(response);
        TyrusServerContainer serverContainer = (TyrusServerContainer)this.getContainer(servletRequest);
        TyrusWebSocketEngine engine = (TyrusWebSocketEngine)serverContainer.getWebSocketEngine();
        Object tyrusEndpoint = null;
        try {
            String path = "/" + random.nextLong();
            tyrusEndpoint = this.createTyrusEndpoint(endpoint, path, selectedProtocol, extensions, (WebSocketContainer)serverContainer, engine);
            this.register(engine, tyrusEndpoint);
            HttpHeaders headers = request.getHeaders();
            RequestContext requestContext = this.createRequestContext(servletRequest, path, headers);
            TyrusUpgradeResponse upgradeResponse = new TyrusUpgradeResponse();
            WebSocketEngine.UpgradeInfo upgradeInfo = engine.upgrade((UpgradeRequest)requestContext, (UpgradeResponse)upgradeResponse);
            success = WebSocketEngine.UpgradeStatus.SUCCESS.equals((Object)upgradeInfo.getStatus());
            if (success) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace((Object)("Successful request upgrade: " + upgradeResponse.getHeaders()));
                }
                this.handleSuccess(servletRequest, servletResponse, upgradeInfo, upgradeResponse);
            }
        }
        catch (Exception ex) {
            this.unregisterTyrusEndpoint(engine, tyrusEndpoint);
            throw new HandshakeFailureException("Error during handshake: " + request.getURI(), ex);
        }
        this.unregisterTyrusEndpoint(engine, tyrusEndpoint);
        if (!success) {
            throw new HandshakeFailureException("Unexpected handshake failure: " + request.getURI());
        }
    }

    private Object createTyrusEndpoint(Endpoint endpoint, String endpointPath, @Nullable String protocol, List<Extension> extensions, WebSocketContainer container, TyrusWebSocketEngine engine) throws DeploymentException {
        ServerEndpointRegistration endpointConfig = new ServerEndpointRegistration(endpointPath, endpoint);
        endpointConfig.setSubprotocols(Collections.singletonList(protocol));
        endpointConfig.setExtensions(extensions);
        return this.createEndpoint(endpointConfig, this.componentProvider, container, engine);
    }

    private RequestContext createRequestContext(HttpServletRequest request, String endpointPath, HttpHeaders headers) {
        RequestContext context = RequestContext.Builder.create().requestURI(URI.create(endpointPath)).userPrincipal(request.getUserPrincipal()).secure(request.isSecure()).remoteAddr(request.getRemoteAddr()).build();
        headers.forEach((header, value) -> context.getHeaders().put(header, value));
        return context;
    }

    private void unregisterTyrusEndpoint(TyrusWebSocketEngine engine, @Nullable Object tyrusEndpoint) {
        if (tyrusEndpoint != null) {
            try {
                this.unregister(engine, tyrusEndpoint);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    private Object createEndpoint(ServerEndpointRegistration registration, ComponentProviderService provider, WebSocketContainer container, TyrusWebSocketEngine engine) throws DeploymentException {
        DirectFieldAccessor accessor = new DirectFieldAccessor((Object)engine);
        Object sessionListener = accessor.getPropertyValue("sessionListener");
        Object clusterContext = accessor.getPropertyValue("clusterContext");
        try {
            if (constructorWithBooleanArgument) {
                return constructor.newInstance(new Object[]{registration.getEndpoint(), registration, provider, container, "/", registration.getConfigurator(), sessionListener, clusterContext, null, Boolean.TRUE});
            }
            return constructor.newInstance(new Object[]{registration.getEndpoint(), registration, provider, container, "/", registration.getConfigurator(), sessionListener, clusterContext, null});
        }
        catch (Exception ex) {
            throw new HandshakeFailureException("Failed to register " + (Object)((Object)registration), ex);
        }
    }

    private void register(TyrusWebSocketEngine engine, Object endpoint) {
        try {
            registerMethod.invoke((Object)engine, endpoint);
        }
        catch (Exception ex) {
            throw new HandshakeFailureException("Failed to register " + endpoint, ex);
        }
    }

    private void unregister(TyrusWebSocketEngine engine, Object endpoint) {
        try {
            unRegisterMethod.invoke((Object)engine, endpoint);
        }
        catch (Exception ex) {
            throw new HandshakeFailureException("Failed to unregister " + endpoint, ex);
        }
    }

    protected abstract void handleSuccess(HttpServletRequest var1, HttpServletResponse var2, WebSocketEngine.UpgradeInfo var3, TyrusUpgradeResponse var4) throws IOException, ServletException;

    static {
        try {
            constructor = AbstractTyrusRequestUpgradeStrategy.getEndpointConstructor();
            int parameterCount = constructor.getParameterCount();
            boolean bl = constructorWithBooleanArgument = parameterCount == 10;
            if (!constructorWithBooleanArgument && parameterCount != 9) {
                throw new IllegalStateException("Expected TyrusEndpointWrapper constructor with 9 or 10 arguments");
            }
            registerMethod = TyrusWebSocketEngine.class.getDeclaredMethod("register", TyrusEndpointWrapper.class);
            unRegisterMethod = TyrusWebSocketEngine.class.getDeclaredMethod("unregister", TyrusEndpointWrapper.class);
            ReflectionUtils.makeAccessible((Method)registerMethod);
        }
        catch (Exception ex) {
            throw new IllegalStateException("No compatible Tyrus version found", ex);
        }
    }
}

