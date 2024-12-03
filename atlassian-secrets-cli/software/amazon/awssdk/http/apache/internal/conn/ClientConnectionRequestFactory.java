/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.conn.ConnectionPoolTimeoutException
 *  org.apache.http.conn.ConnectionRequest
 */
package software.amazon.awssdk.http.apache.internal.conn;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpClientConnection;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.conn.ConnectionRequest;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.HttpMetric;
import software.amazon.awssdk.metrics.MetricCollector;

@SdkInternalApi
public final class ClientConnectionRequestFactory {
    public static final ThreadLocal<MetricCollector> THREAD_LOCAL_REQUEST_METRIC_COLLECTOR = new ThreadLocal();

    private ClientConnectionRequestFactory() {
    }

    static ConnectionRequest wrap(ConnectionRequest orig) {
        if (orig instanceof DelegatingConnectionRequest) {
            throw new IllegalArgumentException();
        }
        return new InstrumentedConnectionRequest(orig);
    }

    private static class DelegatingConnectionRequest
    implements ConnectionRequest {
        private final ConnectionRequest delegate;

        private DelegatingConnectionRequest(ConnectionRequest delegate) {
            this.delegate = delegate;
        }

        public HttpClientConnection get(long timeout, TimeUnit timeUnit) throws InterruptedException, ExecutionException, ConnectionPoolTimeoutException {
            return this.delegate.get(timeout, timeUnit);
        }

        public boolean cancel() {
            return this.delegate.cancel();
        }
    }

    private static class InstrumentedConnectionRequest
    extends DelegatingConnectionRequest {
        private InstrumentedConnectionRequest(ConnectionRequest delegate) {
            super(delegate);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public HttpClientConnection get(long timeout, TimeUnit timeUnit) throws InterruptedException, ExecutionException, ConnectionPoolTimeoutException {
            Instant startTime = Instant.now();
            try {
                HttpClientConnection httpClientConnection = super.get(timeout, timeUnit);
                return httpClientConnection;
            }
            finally {
                Duration elapsed = Duration.between(startTime, Instant.now());
                MetricCollector metricCollector = THREAD_LOCAL_REQUEST_METRIC_COLLECTOR.get();
                metricCollector.reportMetric(HttpMetric.CONCURRENCY_ACQUIRE_DURATION, elapsed);
            }
        }
    }
}

