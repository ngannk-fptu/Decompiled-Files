/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.component.ContainerLifeCycle
 */
package org.eclipse.jetty.io;

import java.io.IOException;
import java.util.EventListener;
import java.util.List;
import java.util.Map;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.component.ContainerLifeCycle;

public interface ClientConnectionFactory {
    public static final String CLIENT_CONTEXT_KEY = "org.eclipse.jetty.client";

    public Connection newConnection(EndPoint var1, Map<String, Object> var2) throws IOException;

    default public Connection customize(Connection connection, Map<String, Object> context) {
        ContainerLifeCycle client = (ContainerLifeCycle)context.get(CLIENT_CONTEXT_KEY);
        if (client != null) {
            client.getBeans(EventListener.class).forEach(connection::addEventListener);
        }
        return connection;
    }

    public static abstract class Info
    extends ContainerLifeCycle {
        private final ClientConnectionFactory factory;

        public Info(ClientConnectionFactory factory) {
            this.factory = factory;
            this.addBean(factory);
        }

        public abstract List<String> getProtocols(boolean var1);

        public ClientConnectionFactory getClientConnectionFactory() {
            return this.factory;
        }

        public boolean matches(List<String> candidates, boolean secure) {
            return this.getProtocols(secure).stream().anyMatch(p -> candidates.stream().anyMatch(c -> c.equalsIgnoreCase((String)p)));
        }

        public void upgrade(EndPoint endPoint, Map<String, Object> context) {
            throw new UnsupportedOperationException(this + " does not support upgrade to another protocol");
        }
    }

    public static interface Decorator {
        public ClientConnectionFactory apply(ClientConnectionFactory var1);
    }
}

