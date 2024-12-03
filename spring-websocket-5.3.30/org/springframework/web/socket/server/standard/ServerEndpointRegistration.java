/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.Decoder
 *  javax.websocket.Encoder
 *  javax.websocket.Endpoint
 *  javax.websocket.Extension
 *  javax.websocket.HandshakeResponse
 *  javax.websocket.server.HandshakeRequest
 *  javax.websocket.server.ServerEndpointConfig
 *  javax.websocket.server.ServerEndpointConfig$Configurator
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.web.socket.server.standard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.Endpoint;
import javax.websocket.Extension;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.socket.handler.BeanCreatingHandlerProvider;

public class ServerEndpointRegistration
extends ServerEndpointConfig.Configurator
implements ServerEndpointConfig,
BeanFactoryAware {
    private final String path;
    @Nullable
    private final Endpoint endpoint;
    @Nullable
    private final BeanCreatingHandlerProvider<Endpoint> endpointProvider;
    private List<String> subprotocols = new ArrayList<String>(0);
    private List<Extension> extensions = new ArrayList<Extension>(0);
    private List<Class<? extends Encoder>> encoders = new ArrayList<Class<? extends Encoder>>(0);
    private List<Class<? extends Decoder>> decoders = new ArrayList<Class<? extends Decoder>>(0);
    private final Map<String, Object> userProperties = new HashMap<String, Object>(4);

    public ServerEndpointRegistration(String path, Endpoint endpoint) {
        Assert.hasText((String)path, (String)"Path must not be empty");
        Assert.notNull((Object)endpoint, (String)"Endpoint must not be null");
        this.path = path;
        this.endpoint = endpoint;
        this.endpointProvider = null;
    }

    public ServerEndpointRegistration(String path, Class<? extends Endpoint> endpointClass) {
        Assert.hasText((String)path, (String)"Path must not be empty");
        Assert.notNull(endpointClass, (String)"Endpoint Class must not be null");
        this.path = path;
        this.endpoint = null;
        this.endpointProvider = new BeanCreatingHandlerProvider<Endpoint>(endpointClass);
    }

    public String getPath() {
        return this.path;
    }

    public Class<? extends Endpoint> getEndpointClass() {
        if (this.endpoint != null) {
            return this.endpoint.getClass();
        }
        Assert.state((this.endpointProvider != null ? 1 : 0) != 0, (String)"No endpoint set");
        return this.endpointProvider.getHandlerType();
    }

    public Endpoint getEndpoint() {
        if (this.endpoint != null) {
            return this.endpoint;
        }
        Assert.state((this.endpointProvider != null ? 1 : 0) != 0, (String)"No endpoint set");
        return this.endpointProvider.getHandler();
    }

    public void setSubprotocols(List<String> subprotocols) {
        this.subprotocols = subprotocols;
    }

    public List<String> getSubprotocols() {
        return this.subprotocols;
    }

    public void setExtensions(List<Extension> extensions) {
        this.extensions = extensions;
    }

    public List<Extension> getExtensions() {
        return this.extensions;
    }

    public void setEncoders(List<Class<? extends Encoder>> encoders) {
        this.encoders = encoders;
    }

    public List<Class<? extends Encoder>> getEncoders() {
        return this.encoders;
    }

    public void setDecoders(List<Class<? extends Decoder>> decoders) {
        this.decoders = decoders;
    }

    public List<Class<? extends Decoder>> getDecoders() {
        return this.decoders;
    }

    public void setUserProperties(Map<String, Object> userProperties) {
        this.userProperties.clear();
        this.userProperties.putAll(userProperties);
    }

    public Map<String, Object> getUserProperties() {
        return this.userProperties;
    }

    public ServerEndpointConfig.Configurator getConfigurator() {
        return this;
    }

    public final <T> T getEndpointInstance(Class<T> clazz) throws InstantiationException {
        return (T)this.getEndpoint();
    }

    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        super.modifyHandshake((ServerEndpointConfig)this, request, response);
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        if (this.endpointProvider != null) {
            this.endpointProvider.setBeanFactory(beanFactory);
        }
    }

    public String toString() {
        return "ServerEndpointRegistration for path '" + this.getPath() + "': " + this.getEndpointClass();
    }
}

