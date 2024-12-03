/*
 * Decompiled with CFR 0.152.
 */
package javax.websocket;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.websocket.Decoder;
import javax.websocket.DefaultClientEndpointConfig;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import javax.websocket.Extension;
import javax.websocket.HandshakeResponse;

public interface ClientEndpointConfig
extends EndpointConfig {
    public List<String> getPreferredSubprotocols();

    public List<Extension> getExtensions();

    public Configurator getConfigurator();

    public static class Configurator {
        public void beforeRequest(Map<String, List<String>> headers) {
        }

        public void afterResponse(HandshakeResponse handshakeResponse) {
        }
    }

    public static final class Builder {
        private static final Configurator DEFAULT_CONFIGURATOR = new Configurator(){};
        private Configurator configurator = DEFAULT_CONFIGURATOR;
        private List<String> preferredSubprotocols = Collections.emptyList();
        private List<Extension> extensions = Collections.emptyList();
        private List<Class<? extends Encoder>> encoders = Collections.emptyList();
        private List<Class<? extends Decoder>> decoders = Collections.emptyList();

        public static Builder create() {
            return new Builder();
        }

        private Builder() {
        }

        public ClientEndpointConfig build() {
            return new DefaultClientEndpointConfig(this.preferredSubprotocols, this.extensions, this.encoders, this.decoders, this.configurator);
        }

        public Builder configurator(Configurator configurator) {
            this.configurator = configurator == null ? DEFAULT_CONFIGURATOR : configurator;
            return this;
        }

        public Builder preferredSubprotocols(List<String> preferredSubprotocols) {
            this.preferredSubprotocols = preferredSubprotocols == null || preferredSubprotocols.size() == 0 ? Collections.emptyList() : Collections.unmodifiableList(preferredSubprotocols);
            return this;
        }

        public Builder extensions(List<Extension> extensions) {
            this.extensions = extensions == null || extensions.size() == 0 ? Collections.emptyList() : Collections.unmodifiableList(extensions);
            return this;
        }

        public Builder encoders(List<Class<? extends Encoder>> encoders) {
            this.encoders = encoders == null || encoders.size() == 0 ? Collections.emptyList() : Collections.unmodifiableList(encoders);
            return this;
        }

        public Builder decoders(List<Class<? extends Decoder>> decoders) {
            this.decoders = decoders == null || decoders.size() == 0 ? Collections.emptyList() : Collections.unmodifiableList(decoders);
            return this;
        }
    }
}

