/*
 * Decompiled with CFR 0.152.
 */
package javax.websocket.server;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import javax.websocket.Decoder;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;
import javax.websocket.Extension;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.DefaultServerEndpointConfig;
import javax.websocket.server.HandshakeRequest;

public interface ServerEndpointConfig
extends EndpointConfig {
    public Class<?> getEndpointClass();

    public String getPath();

    public List<String> getSubprotocols();

    public List<Extension> getExtensions();

    public Configurator getConfigurator();

    public static class Configurator {
        private static volatile Configurator defaultImpl = null;
        private static final Object defaultImplLock = new Object();
        private static final String DEFAULT_IMPL_CLASSNAME = "org.apache.tomcat.websocket.server.DefaultServerEndpointConfigurator";

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        static Configurator fetchContainerDefaultConfigurator() {
            if (defaultImpl == null) {
                Object object = defaultImplLock;
                synchronized (object) {
                    if (defaultImpl == null) {
                        defaultImpl = System.getSecurityManager() == null ? Configurator.loadDefault() : AccessController.doPrivileged(new PrivilegedLoadDefault());
                    }
                }
            }
            return defaultImpl;
        }

        private static Configurator loadDefault() {
            Configurator result = null;
            ServiceLoader<Configurator> serviceLoader = ServiceLoader.load(Configurator.class);
            Iterator<Configurator> iter = serviceLoader.iterator();
            while (result == null && iter.hasNext()) {
                result = iter.next();
            }
            if (result == null) {
                try {
                    Class<?> clazz = Class.forName(DEFAULT_IMPL_CLASSNAME);
                    result = (Configurator)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                }
                catch (IllegalArgumentException | ReflectiveOperationException | SecurityException exception) {
                    // empty catch block
                }
            }
            return result;
        }

        public String getNegotiatedSubprotocol(List<String> supported, List<String> requested) {
            return Configurator.fetchContainerDefaultConfigurator().getNegotiatedSubprotocol(supported, requested);
        }

        public List<Extension> getNegotiatedExtensions(List<Extension> installed, List<Extension> requested) {
            return Configurator.fetchContainerDefaultConfigurator().getNegotiatedExtensions(installed, requested);
        }

        public boolean checkOrigin(String originHeaderValue) {
            return Configurator.fetchContainerDefaultConfigurator().checkOrigin(originHeaderValue);
        }

        public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
            Configurator.fetchContainerDefaultConfigurator().modifyHandshake(sec, request, response);
        }

        public <T> T getEndpointInstance(Class<T> clazz) throws InstantiationException {
            return Configurator.fetchContainerDefaultConfigurator().getEndpointInstance(clazz);
        }

        private static class PrivilegedLoadDefault
        implements PrivilegedAction<Configurator> {
            private PrivilegedLoadDefault() {
            }

            @Override
            public Configurator run() {
                return Configurator.loadDefault();
            }
        }
    }

    public static final class Builder {
        private final Class<?> endpointClass;
        private final String path;
        private List<Class<? extends Encoder>> encoders = Collections.emptyList();
        private List<Class<? extends Decoder>> decoders = Collections.emptyList();
        private List<String> subprotocols = Collections.emptyList();
        private List<Extension> extensions = Collections.emptyList();
        private Configurator configurator = Configurator.fetchContainerDefaultConfigurator();

        public static Builder create(Class<?> endpointClass, String path) {
            return new Builder(endpointClass, path);
        }

        private Builder(Class<?> endpointClass, String path) {
            if (endpointClass == null) {
                throw new IllegalArgumentException("Endpoint class may not be null");
            }
            if (path == null) {
                throw new IllegalArgumentException("Path may not be null");
            }
            if (path.isEmpty()) {
                throw new IllegalArgumentException("Path may not be empty");
            }
            if (path.charAt(0) != '/') {
                throw new IllegalArgumentException("Path must start with '/'");
            }
            this.endpointClass = endpointClass;
            this.path = path;
        }

        public ServerEndpointConfig build() {
            return new DefaultServerEndpointConfig(this.endpointClass, this.path, this.subprotocols, this.extensions, this.encoders, this.decoders, this.configurator);
        }

        public Builder encoders(List<Class<? extends Encoder>> encoders) {
            this.encoders = encoders == null || encoders.size() == 0 ? Collections.emptyList() : Collections.unmodifiableList(encoders);
            return this;
        }

        public Builder decoders(List<Class<? extends Decoder>> decoders) {
            this.decoders = decoders == null || decoders.size() == 0 ? Collections.emptyList() : Collections.unmodifiableList(decoders);
            return this;
        }

        public Builder subprotocols(List<String> subprotocols) {
            this.subprotocols = subprotocols == null || subprotocols.size() == 0 ? Collections.emptyList() : Collections.unmodifiableList(subprotocols);
            return this;
        }

        public Builder extensions(List<Extension> extensions) {
            this.extensions = extensions == null || extensions.size() == 0 ? Collections.emptyList() : Collections.unmodifiableList(extensions);
            return this;
        }

        public Builder configurator(Configurator serverEndpointConfigurator) {
            this.configurator = serverEndpointConfigurator == null ? Configurator.fetchContainerDefaultConfigurator() : serverEndpointConfigurator;
            return this;
        }
    }
}

