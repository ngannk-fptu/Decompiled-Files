/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.Decoder
 *  javax.websocket.Encoder
 *  javax.websocket.Extension
 *  javax.websocket.server.ServerEndpointConfig
 *  javax.websocket.server.ServerEndpointConfig$Configurator
 */
package org.apache.tomcat.websocket.server;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.Extension;
import javax.websocket.server.ServerEndpointConfig;

class WsPerSessionServerEndpointConfig
implements ServerEndpointConfig {
    private final ServerEndpointConfig perEndpointConfig;
    private final Map<String, Object> perSessionUserProperties = new ConcurrentHashMap<String, Object>();

    WsPerSessionServerEndpointConfig(ServerEndpointConfig perEndpointConfig) {
        this.perEndpointConfig = perEndpointConfig;
        this.perSessionUserProperties.putAll(perEndpointConfig.getUserProperties());
    }

    public List<Class<? extends Encoder>> getEncoders() {
        return this.perEndpointConfig.getEncoders();
    }

    public List<Class<? extends Decoder>> getDecoders() {
        return this.perEndpointConfig.getDecoders();
    }

    public Map<String, Object> getUserProperties() {
        return this.perSessionUserProperties;
    }

    public Class<?> getEndpointClass() {
        return this.perEndpointConfig.getEndpointClass();
    }

    public String getPath() {
        return this.perEndpointConfig.getPath();
    }

    public List<String> getSubprotocols() {
        return this.perEndpointConfig.getSubprotocols();
    }

    public List<Extension> getExtensions() {
        return this.perEndpointConfig.getExtensions();
    }

    public ServerEndpointConfig.Configurator getConfigurator() {
        return this.perEndpointConfig.getConfigurator();
    }
}

