/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.diagnostics;

import com.hazelcast.internal.diagnostics.DiagnosticsLogWriter;
import com.hazelcast.internal.diagnostics.DiagnosticsPlugin;
import com.hazelcast.internal.networking.Networking;
import com.hazelcast.internal.networking.nio.NioNetworking;
import com.hazelcast.internal.networking.nio.NioThread;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.NetworkingService;
import com.hazelcast.nio.tcp.TcpIpNetworkingService;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.spi.properties.HazelcastProperty;
import java.util.concurrent.TimeUnit;

public class NetworkingImbalancePlugin
extends DiagnosticsPlugin {
    public static final HazelcastProperty PERIOD_SECONDS = new HazelcastProperty("hazelcast.diagnostics.networking-imbalance.seconds", 0, TimeUnit.SECONDS);
    private static final double HUNDRED = 100.0;
    private final NioNetworking networking;
    private final long periodMillis;

    public NetworkingImbalancePlugin(NodeEngineImpl nodeEngine) {
        this(nodeEngine.getProperties(), NetworkingImbalancePlugin.getThreadingModel(nodeEngine), nodeEngine.getLogger(NetworkingImbalancePlugin.class));
    }

    public NetworkingImbalancePlugin(HazelcastProperties properties, Networking networking, ILogger logger) {
        super(logger);
        this.networking = networking instanceof NioNetworking ? (NioNetworking)networking : null;
        this.periodMillis = this.networking == null ? 0L : properties.getMillis(PERIOD_SECONDS);
    }

    private static Networking getThreadingModel(NodeEngineImpl nodeEngine) {
        NetworkingService networkingService = nodeEngine.getNode().getNetworkingService();
        if (!(networkingService instanceof TcpIpNetworkingService)) {
            return null;
        }
        return ((TcpIpNetworkingService)networkingService).getNetworking();
    }

    @Override
    public long getPeriodMillis() {
        return this.periodMillis;
    }

    @Override
    public void onStart() {
        this.logger.info("Plugin:active: period-millis:" + this.periodMillis);
    }

    @Override
    public void run(DiagnosticsLogWriter writer) {
        writer.startSection("NetworkingImbalance");
        writer.startSection("InputThreads");
        this.render(writer, this.networking.getInputThreads());
        writer.endSection();
        writer.startSection("OutputThreads");
        this.render(writer, this.networking.getOutputThreads());
        writer.endSection();
        writer.endSection();
    }

    private void render(DiagnosticsLogWriter writer, NioThread[] threads) {
        if (threads == null) {
            return;
        }
        long totalPriorityFramesReceived = 0L;
        long totalFramesReceived = 0L;
        long totalBytesReceived = 0L;
        long totalEvents = 0L;
        long totalTaskCount = 0L;
        long totalHandleCount = 0L;
        for (NioThread thread : threads) {
            totalBytesReceived += thread.bytesTransceived();
            totalFramesReceived += thread.framesTransceived();
            totalPriorityFramesReceived += thread.priorityFramesTransceived();
            totalEvents += thread.eventCount();
            totalTaskCount += thread.completedTaskCount();
            totalHandleCount += thread.handleCount();
        }
        for (NioThread thread : threads) {
            writer.startSection(thread.getName());
            writer.writeKeyValueEntry("frames-percentage", this.toPercentage(thread.framesTransceived(), totalFramesReceived));
            writer.writeKeyValueEntry("frames", thread.framesTransceived());
            writer.writeKeyValueEntry("priority-frames-percentage", this.toPercentage(thread.priorityFramesTransceived(), totalPriorityFramesReceived));
            writer.writeKeyValueEntry("priority-frames", thread.priorityFramesTransceived());
            writer.writeKeyValueEntry("bytes-percentage", this.toPercentage(thread.bytesTransceived(), totalBytesReceived));
            writer.writeKeyValueEntry("bytes", thread.bytesTransceived());
            writer.writeKeyValueEntry("events-percentage", this.toPercentage(thread.eventCount(), totalEvents));
            writer.writeKeyValueEntry("events", thread.eventCount());
            writer.writeKeyValueEntry("handle-count-percentage", this.toPercentage(thread.handleCount(), totalHandleCount));
            writer.writeKeyValueEntry("handle-count", thread.handleCount());
            writer.writeKeyValueEntry("tasks-percentage", this.toPercentage(thread.completedTaskCount(), totalTaskCount));
            writer.writeKeyValueEntry("tasks", thread.completedTaskCount());
            writer.endSection();
        }
    }

    private String toPercentage(long amount, long total) {
        double percentage = amount == 0L ? 0.0 : (total == 0L ? Double.NaN : 100.0 * (double)amount / (double)total);
        return String.format("%1$,.2f", percentage) + " %";
    }
}

