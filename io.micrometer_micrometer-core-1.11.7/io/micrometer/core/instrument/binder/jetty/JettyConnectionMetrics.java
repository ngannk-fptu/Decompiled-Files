/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.io.Connection
 *  org.eclipse.jetty.io.Connection$Listener
 *  org.eclipse.jetty.server.Connector
 *  org.eclipse.jetty.server.HttpConnection
 *  org.eclipse.jetty.server.Server
 *  org.eclipse.jetty.util.component.AbstractLifeCycle
 */
package io.micrometer.core.instrument.binder.jetty;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.TimeWindowMax;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConnection;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public class JettyConnectionMetrics
extends AbstractLifeCycle
implements Connection.Listener {
    private final MeterRegistry registry;
    private final Iterable<Tag> tags;
    private final Object connectionSamplesLock = new Object();
    private final Map<Connection, Timer.Sample> connectionSamples = new HashMap<Connection, Timer.Sample>();
    private final Counter messagesIn;
    private final Counter messagesOut;
    private final DistributionSummary bytesIn;
    private final DistributionSummary bytesOut;
    private final TimeWindowMax maxConnections;

    public JettyConnectionMetrics(MeterRegistry registry) {
        this(registry, Tags.empty());
    }

    public JettyConnectionMetrics(MeterRegistry registry, Iterable<Tag> tags) {
        this.registry = registry;
        this.tags = tags;
        this.messagesIn = Counter.builder("jetty.connections.messages.in").baseUnit("messages").description("Messages received by tracked connections").tags(tags).register(registry);
        this.messagesOut = Counter.builder("jetty.connections.messages.out").baseUnit("messages").description("Messages sent by tracked connections").tags(tags).register(registry);
        this.bytesIn = DistributionSummary.builder("jetty.connections.bytes.in").baseUnit("bytes").description("Bytes received by tracked connections").tags(tags).register(registry);
        this.bytesOut = DistributionSummary.builder("jetty.connections.bytes.out").baseUnit("bytes").description("Bytes sent by tracked connections").tags(tags).register(registry);
        this.maxConnections = new TimeWindowMax(registry.config().clock(), DistributionStatisticConfig.DEFAULT);
        Gauge.builder("jetty.connections.max", this, jcm -> jcm.maxConnections.poll()).strongReference(true).baseUnit("connections").description("The maximum number of observed connections over a rolling 2-minute interval").tags(tags).register(registry);
        Gauge.builder("jetty.connections.current", this, jcm -> jcm.connectionSamples.size()).strongReference(true).baseUnit("connections").description("The current number of open Jetty connections").tags(tags).register(registry);
    }

    public JettyConnectionMetrics(MeterRegistry registry, Connector connector) {
        this(registry, connector, Tags.empty());
    }

    public JettyConnectionMetrics(MeterRegistry registry, Connector connector, Iterable<Tag> tags) {
        this(registry, JettyConnectionMetrics.getConnectorNameTag(connector).and(tags));
    }

    private static Tags getConnectorNameTag(Connector connector) {
        String name = connector.getName();
        return Tags.of("connector.name", name != null ? name : "unnamed");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onOpened(Connection connection) {
        Timer.Sample started = Timer.start(this.registry);
        Object object = this.connectionSamplesLock;
        synchronized (object) {
            this.connectionSamples.put(connection, started);
            this.maxConnections.record((double)this.connectionSamples.size());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onClosed(Connection connection) {
        Timer.Sample sample;
        Object object = this.connectionSamplesLock;
        synchronized (object) {
            sample = this.connectionSamples.remove(connection);
        }
        if (sample != null) {
            String serverOrClient = connection instanceof HttpConnection ? "server" : "client";
            sample.stop(((Timer.Builder)Timer.builder("jetty.connections.request").description("Jetty client or server requests").tag("type", serverOrClient).tags((Iterable)this.tags)).register(this.registry));
        }
        this.messagesIn.increment(connection.getMessagesIn());
        this.messagesOut.increment(connection.getMessagesOut());
        this.bytesIn.record(connection.getBytesIn());
        this.bytesOut.record(connection.getBytesOut());
    }

    public static void addToAllConnectors(Server server, MeterRegistry registry, Iterable<Tag> tags) {
        for (Connector connector : server.getConnectors()) {
            if (connector == null) continue;
            connector.addBean((Object)new JettyConnectionMetrics(registry, connector, tags));
        }
    }

    public static void addToAllConnectors(Server server, MeterRegistry registry) {
        JettyConnectionMetrics.addToAllConnectors(server, registry, Tags.empty());
    }
}

