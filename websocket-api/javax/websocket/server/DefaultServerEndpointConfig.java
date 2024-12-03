/*
 * Decompiled with CFR 0.152.
 */
package javax.websocket.server;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.Extension;
import javax.websocket.server.ServerEndpointConfig;

final class DefaultServerEndpointConfig
implements ServerEndpointConfig {
    private final Class<?> endpointClass;
    private final String path;
    private final List<String> subprotocols;
    private final List<Extension> extensions;
    private final List<Class<? extends Encoder>> encoders;
    private final List<Class<? extends Decoder>> decoders;
    private final ServerEndpointConfig.Configurator serverEndpointConfigurator;
    private final Map<String, Object> userProperties = new ConcurrentHashMap<String, Object>();

    DefaultServerEndpointConfig(Class<?> endpointClass, String path, List<String> subprotocols, List<Extension> extensions, List<Class<? extends Encoder>> encoders, List<Class<? extends Decoder>> decoders, ServerEndpointConfig.Configurator serverEndpointConfigurator) {
        this.endpointClass = endpointClass;
        this.path = path;
        this.subprotocols = subprotocols;
        this.extensions = extensions;
        this.encoders = encoders;
        this.decoders = decoders;
        this.serverEndpointConfigurator = serverEndpointConfigurator;
    }

    @Override
    public Class<?> getEndpointClass() {
        return this.endpointClass;
    }

    @Override
    public List<Class<? extends Encoder>> getEncoders() {
        return this.encoders;
    }

    @Override
    public List<Class<? extends Decoder>> getDecoders() {
        return this.decoders;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public ServerEndpointConfig.Configurator getConfigurator() {
        return this.serverEndpointConfigurator;
    }

    @Override
    public Map<String, Object> getUserProperties() {
        return this.userProperties;
    }

    @Override
    public List<String> getSubprotocols() {
        return this.subprotocols;
    }

    @Override
    public List<Extension> getExtensions() {
        return this.extensions;
    }
}

