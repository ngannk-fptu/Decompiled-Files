/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.io.ClientConnectionFactory
 */
package org.eclipse.jetty.client;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import org.eclipse.jetty.client.ConnectionPool;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpDestination;
import org.eclipse.jetty.client.HttpRequest;
import org.eclipse.jetty.client.Origin;
import org.eclipse.jetty.io.ClientConnectionFactory;

public interface HttpClientTransport
extends ClientConnectionFactory {
    public static final String HTTP_DESTINATION_CONTEXT_KEY = "org.eclipse.jetty.client.destination";
    public static final String HTTP_CONNECTION_PROMISE_CONTEXT_KEY = "org.eclipse.jetty.client.connection.promise";

    public void setHttpClient(HttpClient var1);

    public Origin newOrigin(HttpRequest var1);

    public HttpDestination newHttpDestination(Origin var1);

    @Deprecated
    public void connect(InetSocketAddress var1, Map<String, Object> var2);

    default public void connect(SocketAddress address, Map<String, Object> context) {
        if (!(address instanceof InetSocketAddress)) {
            throw new UnsupportedOperationException("Unsupported SocketAddress " + address);
        }
        this.connect((InetSocketAddress)address, context);
    }

    public ConnectionPool.Factory getConnectionPoolFactory();

    public void setConnectionPoolFactory(ConnectionPool.Factory var1);
}

