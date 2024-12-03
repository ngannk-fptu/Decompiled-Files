/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.NonNull
 *  okhttp3.ConnectionPool
 */
package io.micrometer.core.instrument.binder.okhttp3;

import io.micrometer.common.lang.NonNull;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.MeterBinder;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import okhttp3.ConnectionPool;

public class OkHttpConnectionPoolMetrics
implements MeterBinder {
    private static final String DEFAULT_NAME_PREFIX = "okhttp.pool";
    private static final String TAG_STATE = "state";
    private final ConnectionPool connectionPool;
    private final String namePrefix;
    private final Iterable<Tag> tags;
    private final Double maxIdleConnectionCount;
    private final ThreadLocal<ConnectionPoolConnectionStats> connectionStats = new ThreadLocal();

    public OkHttpConnectionPoolMetrics(ConnectionPool connectionPool) {
        this(connectionPool, DEFAULT_NAME_PREFIX, Collections.emptyList(), null);
    }

    public OkHttpConnectionPoolMetrics(ConnectionPool connectionPool, Iterable<Tag> tags) {
        this(connectionPool, DEFAULT_NAME_PREFIX, tags, null);
    }

    public OkHttpConnectionPoolMetrics(ConnectionPool connectionPool, String namePrefix, Iterable<Tag> tags) {
        this(connectionPool, namePrefix, tags, null);
    }

    public OkHttpConnectionPoolMetrics(ConnectionPool connectionPool, String namePrefix, Iterable<Tag> tags, Integer maxIdleConnections) {
        if (connectionPool == null) {
            throw new IllegalArgumentException("Given ConnectionPool must not be null.");
        }
        if (namePrefix == null) {
            throw new IllegalArgumentException("Given name prefix must not be null.");
        }
        if (tags == null) {
            throw new IllegalArgumentException("Given list of tags must not be null.");
        }
        this.connectionPool = connectionPool;
        this.namePrefix = namePrefix;
        this.tags = tags;
        this.maxIdleConnectionCount = Optional.ofNullable(maxIdleConnections).map(Integer::doubleValue).orElse(null);
    }

    @Override
    public void bindTo(@NonNull MeterRegistry registry) {
        String connectionCountName = this.namePrefix + ".connection.count";
        Gauge.builder(connectionCountName, this.connectionStats, cs -> {
            if (cs.get() == null) {
                cs.set(new ConnectionPoolConnectionStats());
            }
            return ((ConnectionPoolConnectionStats)cs.get()).getActiveCount();
        }).baseUnit("connections").description("The state of connections in the OkHttp connection pool").tags(Tags.of(this.tags).and(TAG_STATE, "active")).register(registry);
        Gauge.builder(connectionCountName, this.connectionStats, cs -> {
            if (cs.get() == null) {
                cs.set(new ConnectionPoolConnectionStats());
            }
            return ((ConnectionPoolConnectionStats)cs.get()).getIdleConnectionCount();
        }).baseUnit("connections").description("The state of connections in the OkHttp connection pool").tags(Tags.of(this.tags).and(TAG_STATE, "idle")).register(registry);
        if (this.maxIdleConnectionCount != null) {
            Gauge.builder(this.namePrefix + ".connection.limit", () -> this.maxIdleConnectionCount).baseUnit("connections").description("The maximum idle connection count in an OkHttp connection pool.").tags(Tags.concat(this.tags, new String[0])).register(registry);
        }
    }

    private final class ConnectionPoolConnectionStats {
        private CountDownLatch uses = new CountDownLatch(0);
        private int idle;
        private int total;

        private ConnectionPoolConnectionStats() {
        }

        public int getActiveCount() {
            this.snapshotStatsIfNecessary();
            this.uses.countDown();
            return this.total - this.idle;
        }

        public int getIdleConnectionCount() {
            this.snapshotStatsIfNecessary();
            this.uses.countDown();
            return this.idle;
        }

        private void snapshotStatsIfNecessary() {
            if (this.uses.getCount() == 0L) {
                this.idle = OkHttpConnectionPoolMetrics.this.connectionPool.idleConnectionCount();
                this.total = OkHttpConnectionPoolMetrics.this.connectionPool.connectionCount();
                this.uses = new CountDownLatch(2);
            }
        }
    }
}

