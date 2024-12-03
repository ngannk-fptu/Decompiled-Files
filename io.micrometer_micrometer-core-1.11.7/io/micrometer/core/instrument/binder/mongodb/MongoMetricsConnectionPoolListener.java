/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mongodb.connection.ServerId
 *  com.mongodb.event.ConnectionCheckOutFailedEvent
 *  com.mongodb.event.ConnectionCheckOutStartedEvent
 *  com.mongodb.event.ConnectionCheckedInEvent
 *  com.mongodb.event.ConnectionCheckedOutEvent
 *  com.mongodb.event.ConnectionClosedEvent
 *  com.mongodb.event.ConnectionCreatedEvent
 *  com.mongodb.event.ConnectionPoolClosedEvent
 *  com.mongodb.event.ConnectionPoolCreatedEvent
 *  com.mongodb.event.ConnectionPoolListener
 *  io.micrometer.common.lang.NonNullApi
 *  io.micrometer.common.lang.NonNullFields
 */
package io.micrometer.core.instrument.binder.mongodb;

import com.mongodb.connection.ServerId;
import com.mongodb.event.ConnectionCheckOutFailedEvent;
import com.mongodb.event.ConnectionCheckOutStartedEvent;
import com.mongodb.event.ConnectionCheckedInEvent;
import com.mongodb.event.ConnectionCheckedOutEvent;
import com.mongodb.event.ConnectionClosedEvent;
import com.mongodb.event.ConnectionCreatedEvent;
import com.mongodb.event.ConnectionPoolClosedEvent;
import com.mongodb.event.ConnectionPoolCreatedEvent;
import com.mongodb.event.ConnectionPoolListener;
import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.NonNullFields;
import io.micrometer.core.annotation.Incubating;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.mongodb.DefaultMongoConnectionPoolTagsProvider;
import io.micrometer.core.instrument.binder.mongodb.MongoConnectionPoolTagsProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@NonNullApi
@NonNullFields
@Incubating(since="1.2.0")
public class MongoMetricsConnectionPoolListener
implements ConnectionPoolListener {
    private static final String METRIC_PREFIX = "mongodb.driver.pool.";
    private final Map<ServerId, AtomicInteger> poolSizes = new ConcurrentHashMap<ServerId, AtomicInteger>();
    private final Map<ServerId, AtomicInteger> checkedOutCounts = new ConcurrentHashMap<ServerId, AtomicInteger>();
    private final Map<ServerId, AtomicInteger> waitQueueSizes = new ConcurrentHashMap<ServerId, AtomicInteger>();
    private final Map<ServerId, List<Meter>> meters = new ConcurrentHashMap<ServerId, List<Meter>>();
    private final MeterRegistry registry;
    private final MongoConnectionPoolTagsProvider tagsProvider;

    public MongoMetricsConnectionPoolListener(MeterRegistry registry) {
        this(registry, new DefaultMongoConnectionPoolTagsProvider());
    }

    public MongoMetricsConnectionPoolListener(MeterRegistry registry, MongoConnectionPoolTagsProvider tagsProvider) {
        this.registry = registry;
        this.tagsProvider = tagsProvider;
    }

    public void connectionPoolCreated(ConnectionPoolCreatedEvent event) {
        ArrayList<Gauge> connectionMeters = new ArrayList<Gauge>();
        connectionMeters.add(this.registerGauge(event, "mongodb.driver.pool.size", "the current size of the connection pool, including idle and and in-use members", this.poolSizes));
        connectionMeters.add(this.registerGauge(event, "mongodb.driver.pool.checkedout", "the count of connections that are currently in use", this.checkedOutCounts));
        connectionMeters.add(this.registerGauge(event, "mongodb.driver.pool.waitqueuesize", "the current size of the wait queue for a connection from the pool", this.waitQueueSizes));
        this.meters.put(event.getServerId(), connectionMeters);
    }

    public void connectionPoolClosed(ConnectionPoolClosedEvent event) {
        ServerId serverId = event.getServerId();
        for (Meter meter : this.meters.get(serverId)) {
            this.registry.remove(meter);
        }
        this.meters.remove(serverId);
        this.poolSizes.remove(serverId);
        this.checkedOutCounts.remove(serverId);
        this.waitQueueSizes.remove(serverId);
    }

    public void connectionCheckOutStarted(ConnectionCheckOutStartedEvent event) {
        AtomicInteger waitQueueSize = this.waitQueueSizes.get(event.getServerId());
        if (waitQueueSize != null) {
            waitQueueSize.incrementAndGet();
        }
    }

    public void connectionCheckedOut(ConnectionCheckedOutEvent event) {
        AtomicInteger waitQueueSize;
        AtomicInteger checkedOutCount = this.checkedOutCounts.get(event.getConnectionId().getServerId());
        if (checkedOutCount != null) {
            checkedOutCount.incrementAndGet();
        }
        if ((waitQueueSize = this.waitQueueSizes.get(event.getConnectionId().getServerId())) != null) {
            waitQueueSize.decrementAndGet();
        }
    }

    public void connectionCheckOutFailed(ConnectionCheckOutFailedEvent event) {
        AtomicInteger waitQueueSize = this.waitQueueSizes.get(event.getServerId());
        if (waitQueueSize != null) {
            waitQueueSize.decrementAndGet();
        }
    }

    public void connectionCheckedIn(ConnectionCheckedInEvent event) {
        AtomicInteger checkedOutCount = this.checkedOutCounts.get(event.getConnectionId().getServerId());
        if (checkedOutCount != null) {
            checkedOutCount.decrementAndGet();
        }
    }

    public void connectionCreated(ConnectionCreatedEvent event) {
        AtomicInteger poolSize = this.poolSizes.get(event.getConnectionId().getServerId());
        if (poolSize != null) {
            poolSize.incrementAndGet();
        }
    }

    public void connectionClosed(ConnectionClosedEvent event) {
        AtomicInteger poolSize = this.poolSizes.get(event.getConnectionId().getServerId());
        if (poolSize != null) {
            poolSize.decrementAndGet();
        }
    }

    private Gauge registerGauge(ConnectionPoolCreatedEvent event, String metricName, String description, Map<ServerId, AtomicInteger> metrics) {
        AtomicInteger value = new AtomicInteger();
        metrics.put(event.getServerId(), value);
        return Gauge.builder(metricName, value, AtomicInteger::doubleValue).description(description).tags(this.tagsProvider.connectionPoolTags(event)).register(this.registry);
    }
}

