/*
 * Decompiled with CFR 0.152.
 */
package javax.websocket;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.Extension;

final class DefaultClientEndpointConfig
implements ClientEndpointConfig {
    private final List<String> preferredSubprotocols;
    private final List<Extension> extensions;
    private final List<Class<? extends Encoder>> encoders;
    private final List<Class<? extends Decoder>> decoders;
    private final Map<String, Object> userProperties = new ConcurrentHashMap<String, Object>();
    private final ClientEndpointConfig.Configurator configurator;

    DefaultClientEndpointConfig(List<String> preferredSubprotocols, List<Extension> extensions, List<Class<? extends Encoder>> encoders, List<Class<? extends Decoder>> decoders, ClientEndpointConfig.Configurator configurator) {
        this.preferredSubprotocols = preferredSubprotocols;
        this.extensions = extensions;
        this.decoders = decoders;
        this.encoders = encoders;
        this.configurator = configurator;
    }

    @Override
    public List<String> getPreferredSubprotocols() {
        return this.preferredSubprotocols;
    }

    @Override
    public List<Extension> getExtensions() {
        return this.extensions;
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
    public Map<String, Object> getUserProperties() {
        return this.userProperties;
    }

    @Override
    public ClientEndpointConfig.Configurator getConfigurator() {
        return this.configurator;
    }
}

