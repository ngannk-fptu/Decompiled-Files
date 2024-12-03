/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.conn.ConnectionRequest
 *  org.apache.http.conn.HttpClientConnectionManager
 *  org.apache.http.conn.routing.HttpRoute
 */
package software.amazon.awssdk.http.apache.internal.conn;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpClientConnection;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.protocol.HttpContext;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.apache.internal.conn.ClientConnectionRequestFactory;

@SdkInternalApi
public final class ClientConnectionManagerFactory {
    private ClientConnectionManagerFactory() {
    }

    public static HttpClientConnectionManager wrap(HttpClientConnectionManager orig) {
        if (orig instanceof DelegatingHttpClientConnectionManager) {
            throw new IllegalArgumentException();
        }
        return new InstrumentedHttpClientConnectionManager(orig);
    }

    private static class DelegatingHttpClientConnectionManager
    implements HttpClientConnectionManager {
        private final HttpClientConnectionManager delegate;

        protected DelegatingHttpClientConnectionManager(HttpClientConnectionManager delegate) {
            this.delegate = delegate;
        }

        public ConnectionRequest requestConnection(HttpRoute route, Object state) {
            return this.delegate.requestConnection(route, state);
        }

        public void releaseConnection(HttpClientConnection conn, Object newState, long validDuration, TimeUnit timeUnit) {
            this.delegate.releaseConnection(conn, newState, validDuration, timeUnit);
        }

        public void connect(HttpClientConnection conn, HttpRoute route, int connectTimeout, HttpContext context) throws IOException {
            this.delegate.connect(conn, route, connectTimeout, context);
        }

        public void upgrade(HttpClientConnection conn, HttpRoute route, HttpContext context) throws IOException {
            this.delegate.upgrade(conn, route, context);
        }

        public void routeComplete(HttpClientConnection conn, HttpRoute route, HttpContext context) throws IOException {
            this.delegate.routeComplete(conn, route, context);
        }

        public void closeIdleConnections(long idletime, TimeUnit timeUnit) {
            this.delegate.closeIdleConnections(idletime, timeUnit);
        }

        public void closeExpiredConnections() {
            this.delegate.closeExpiredConnections();
        }

        public void shutdown() {
            this.delegate.shutdown();
        }
    }

    private static class InstrumentedHttpClientConnectionManager
    extends DelegatingHttpClientConnectionManager {
        private InstrumentedHttpClientConnectionManager(HttpClientConnectionManager delegate) {
            super(delegate);
        }

        @Override
        public ConnectionRequest requestConnection(HttpRoute route, Object state) {
            ConnectionRequest connectionRequest = super.requestConnection(route, state);
            return ClientConnectionRequestFactory.wrap(connectionRequest);
        }
    }
}

