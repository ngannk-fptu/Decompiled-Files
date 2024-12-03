/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.annotation.ManagedAttribute
 */
package org.eclipse.jetty.client;

import org.eclipse.jetty.client.AbstractConnectionPool;
import org.eclipse.jetty.client.ConnectionPool;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpDestination;
import org.eclipse.jetty.client.Origin;
import org.eclipse.jetty.util.annotation.ManagedAttribute;

public class MultiplexHttpDestination
extends HttpDestination
implements HttpDestination.Multiplexed {
    public MultiplexHttpDestination(HttpClient client, Origin origin) {
        this(client, origin, false);
    }

    public MultiplexHttpDestination(HttpClient client, Origin origin, boolean intrinsicallySecure) {
        super(client, origin, intrinsicallySecure);
    }

    @ManagedAttribute(value="The maximum number of concurrent requests per connection")
    public int getMaxRequestsPerConnection() {
        ConnectionPool connectionPool = this.getConnectionPool();
        if (connectionPool instanceof AbstractConnectionPool) {
            return ((AbstractConnectionPool)connectionPool).getMaxMultiplex();
        }
        return 1;
    }

    @Override
    public void setMaxRequestsPerConnection(int maxRequestsPerConnection) {
        ConnectionPool connectionPool = this.getConnectionPool();
        if (connectionPool instanceof AbstractConnectionPool) {
            ((AbstractConnectionPool)connectionPool).setMaxMultiplex(maxRequestsPerConnection);
        }
    }
}

