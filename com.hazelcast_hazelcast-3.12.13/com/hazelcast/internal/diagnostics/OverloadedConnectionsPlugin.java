/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.diagnostics;

import com.hazelcast.internal.diagnostics.DiagnosticsLogWriter;
import com.hazelcast.internal.diagnostics.DiagnosticsPlugin;
import com.hazelcast.internal.diagnostics.OperationDescriptors;
import com.hazelcast.internal.networking.OutboundFrame;
import com.hazelcast.internal.networking.nio.NioChannel;
import com.hazelcast.internal.networking.nio.NioOutboundPipeline;
import com.hazelcast.nio.AggregateEndpointManager;
import com.hazelcast.nio.NetworkingService;
import com.hazelcast.nio.Packet;
import com.hazelcast.nio.tcp.TcpIpConnection;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.spi.properties.HazelcastProperty;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.ItemCounter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class OverloadedConnectionsPlugin
extends DiagnosticsPlugin {
    public static final HazelcastProperty PERIOD_SECONDS = new HazelcastProperty("hazelcast.diagnostics.overloaded.connections.period.seconds", 0, TimeUnit.SECONDS);
    public static final HazelcastProperty THRESHOLD = new HazelcastProperty("hazelcast.diagnostics.overloaded.connections.threshold", 10000);
    public static final HazelcastProperty SAMPLES = new HazelcastProperty("hazelcast.diagnostics.overloaded.connections.samples", 1000);
    private static final Queue<OutboundFrame> EMPTY_QUEUE = new LinkedList<OutboundFrame>();
    private final SerializationService serializationService;
    private final ItemCounter<String> occurrenceMap = new ItemCounter();
    private final ArrayList<OutboundFrame> packets = new ArrayList();
    private final Random random = new Random();
    private final NumberFormat defaultFormat = NumberFormat.getPercentInstance();
    private final NodeEngineImpl nodeEngine;
    private final long periodMillis;
    private final int threshold;
    private final int samples;

    public OverloadedConnectionsPlugin(NodeEngineImpl nodeEngine) {
        super(nodeEngine.getLogger(OverloadedConnectionsPlugin.class));
        this.nodeEngine = nodeEngine;
        this.serializationService = nodeEngine.getSerializationService();
        this.defaultFormat.setMinimumFractionDigits(3);
        HazelcastProperties props = nodeEngine.getProperties();
        this.periodMillis = props.getMillis(PERIOD_SECONDS);
        this.threshold = props.getInteger(THRESHOLD);
        this.samples = props.getInteger(SAMPLES);
    }

    @Override
    public long getPeriodMillis() {
        return this.periodMillis;
    }

    @Override
    public void onStart() {
        this.logger.info("Plugin:active, period-millis:" + this.periodMillis + " threshold:" + this.threshold + " samples:" + this.samples);
    }

    @Override
    public void run(DiagnosticsLogWriter writer) {
        writer.startSection("OverloadedConnections");
        Collection<TcpIpConnection> connections = this.getTcpIpConnections();
        for (TcpIpConnection connection : connections) {
            this.clear();
            this.scan(writer, connection, false);
            this.clear();
            this.scan(writer, connection, true);
        }
        writer.endSection();
    }

    private Collection<TcpIpConnection> getTcpIpConnections() {
        NetworkingService networkingService = this.nodeEngine.getNode().getNetworkingService();
        AggregateEndpointManager endpointManager = networkingService.getAggregateEndpointManager();
        return endpointManager.getActiveConnections();
    }

    private void scan(DiagnosticsLogWriter writer, TcpIpConnection connection, boolean priority) {
        Queue<OutboundFrame> q = this.getOutboundQueue(connection, priority);
        int sampleCount = this.sample(q);
        if (sampleCount < 0) {
            return;
        }
        this.render(writer, connection, priority, sampleCount);
    }

    private Queue<OutboundFrame> getOutboundQueue(TcpIpConnection connection, boolean priority) {
        if (connection.getChannel() instanceof NioChannel) {
            NioChannel nioChannel = (NioChannel)connection.getChannel();
            NioOutboundPipeline outboundPipeline = nioChannel.outboundPipeline();
            return priority ? outboundPipeline.priorityWriteQueue : outboundPipeline.writeQueue;
        }
        return EMPTY_QUEUE;
    }

    private void render(DiagnosticsLogWriter writer, TcpIpConnection connection, boolean priority, int sampleCount) {
        writer.startSection(connection.toString());
        writer.writeKeyValueEntry(priority ? "urgentPacketCount" : "packetCount", this.packets.size());
        writer.writeKeyValueEntry("sampleCount", sampleCount);
        this.renderSamples(writer, sampleCount);
        writer.endSection();
    }

    private void renderSamples(DiagnosticsLogWriter writer, int sampleCount) {
        writer.startSection("samples");
        for (String key : this.occurrenceMap.keySet()) {
            long value = this.occurrenceMap.get(key);
            if (value == 0L) continue;
            double percentage = 1.0 * (double)value / (double)sampleCount;
            writer.writeEntry(key + " sampleCount=" + value + " " + this.defaultFormat.format(percentage));
        }
        writer.endSection();
    }

    private void clear() {
        this.occurrenceMap.reset();
        this.packets.clear();
    }

    private int sample(Queue<OutboundFrame> q) {
        this.packets.addAll(q);
        if (this.packets.size() < this.threshold) {
            return -1;
        }
        int sampleCount = Math.min(this.samples, this.packets.size());
        int actualSampleCount = 0;
        for (int k = 0; k < sampleCount; ++k) {
            OutboundFrame packet = this.packets.get(this.random.nextInt(this.packets.size()));
            String key = this.toKey(packet);
            if (key == null) continue;
            ++actualSampleCount;
            this.occurrenceMap.add(key, 1L);
        }
        return actualSampleCount;
    }

    String toKey(OutboundFrame packet) {
        if (packet instanceof Packet) {
            try {
                Object result = this.serializationService.toObject(packet);
                if (result == null) {
                    return "null";
                }
                if (result instanceof Operation) {
                    return OperationDescriptors.toOperationDesc((Operation)result);
                }
                return result.getClass().getName();
            }
            catch (Exception ignore) {
                this.logger.severe(ignore);
                return null;
            }
        }
        return packet.getClass().getName();
    }
}

