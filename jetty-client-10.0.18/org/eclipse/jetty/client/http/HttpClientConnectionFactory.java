/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.io.ClientConnectionFactory
 *  org.eclipse.jetty.io.ClientConnectionFactory$Info
 *  org.eclipse.jetty.io.Connection
 *  org.eclipse.jetty.io.EndPoint
 */
package org.eclipse.jetty.client.http;

import java.util.List;
import java.util.Map;
import org.eclipse.jetty.client.http.HttpConnectionOverHTTP;
import org.eclipse.jetty.io.ClientConnectionFactory;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;

public class HttpClientConnectionFactory
implements ClientConnectionFactory {
    public static final ClientConnectionFactory.Info HTTP11 = new HTTP11(new HttpClientConnectionFactory());

    public Connection newConnection(EndPoint endPoint, Map<String, Object> context) {
        HttpConnectionOverHTTP connection = new HttpConnectionOverHTTP(endPoint, context);
        return this.customize((Connection)connection, context);
    }

    private static class HTTP11
    extends ClientConnectionFactory.Info {
        private static final List<String> protocols = List.of("http/1.1");

        private HTTP11(ClientConnectionFactory factory) {
            super(factory);
        }

        public List<String> getProtocols(boolean secure) {
            return protocols;
        }

        public String toString() {
            return String.format("%s@%x%s", ((Object)((Object)this)).getClass().getSimpleName(), ((Object)((Object)this)).hashCode(), protocols);
        }
    }
}

