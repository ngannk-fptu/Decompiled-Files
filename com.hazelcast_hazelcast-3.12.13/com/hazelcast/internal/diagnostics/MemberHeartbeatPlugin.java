/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.diagnostics;

import com.hazelcast.instance.MemberImpl;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.cluster.impl.ClusterHeartbeatManager;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.diagnostics.DiagnosticsLogWriter;
import com.hazelcast.internal.diagnostics.DiagnosticsPlugin;
import com.hazelcast.internal.diagnostics.MemberHazelcastInstanceInfoPlugin;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.spi.properties.HazelcastProperty;
import java.util.concurrent.TimeUnit;

public class MemberHeartbeatPlugin
extends DiagnosticsPlugin {
    public static final HazelcastProperty PERIOD_SECONDS = new HazelcastProperty("hazelcast.diagnostics.member-heartbeat.period.seconds", 10, TimeUnit.SECONDS);
    public static final HazelcastProperty MAX_DEVIATION_PERCENTAGE = new HazelcastProperty("hazelcast.diagnostics.member-heartbeat.max-deviation-percentage", 100);
    private static final float HUNDRED = 100.0f;
    private final long periodMillis;
    private final NodeEngineImpl nodeEngine;
    private final int maxDeviationPercentage;
    private boolean mainSectionStarted;

    public MemberHeartbeatPlugin(NodeEngineImpl nodeEngine) {
        super(nodeEngine.getLogger(MemberHazelcastInstanceInfoPlugin.class));
        this.nodeEngine = nodeEngine;
        HazelcastProperties properties = nodeEngine.getProperties();
        this.periodMillis = properties.getMillis(PERIOD_SECONDS);
        this.maxDeviationPercentage = properties.getInteger(MAX_DEVIATION_PERCENTAGE);
    }

    @Override
    public long getPeriodMillis() {
        return this.periodMillis;
    }

    @Override
    public void onStart() {
        this.logger.info("Plugin:active, period-millis:" + this.periodMillis);
    }

    @Override
    public void run(DiagnosticsLogWriter writer) {
        ClusterService cs = this.nodeEngine.getClusterService();
        if (!(cs instanceof ClusterServiceImpl)) {
            return;
        }
        this.render(writer, (ClusterServiceImpl)cs);
    }

    private void render(DiagnosticsLogWriter writer, ClusterServiceImpl clusterService) {
        ClusterHeartbeatManager clusterHeartbeatManager = clusterService.getClusterHeartbeatManager();
        long expectedIntervalMillis = clusterHeartbeatManager.getHeartbeatIntervalMillis();
        long nowMillis = System.currentTimeMillis();
        for (MemberImpl member : clusterService.getMemberImpls()) {
            long noHeartbeatMillis;
            float deviation;
            long lastHeartbeatMillis = clusterHeartbeatManager.getLastHeartbeatTime(member);
            if (lastHeartbeatMillis == 0L || !((deviation = 100.0f * (float)((noHeartbeatMillis = nowMillis - lastHeartbeatMillis) - expectedIntervalMillis) / (float)expectedIntervalMillis) >= (float)this.maxDeviationPercentage)) continue;
            this.startLazyMainSection(writer);
            writer.startSection("member" + member.getAddress());
            writer.writeKeyValueEntry("deviation(%)", deviation);
            writer.writeKeyValueEntry("noHeartbeat(ms)", noHeartbeatMillis);
            writer.writeKeyValueEntry("lastHeartbeat(ms)", lastHeartbeatMillis);
            writer.writeKeyValueEntryAsDateTime("lastHeartbeat(date-time)", lastHeartbeatMillis);
            writer.writeKeyValueEntry("now(ms)", nowMillis);
            writer.writeKeyValueEntryAsDateTime("now(date-time)", nowMillis);
            writer.endSection();
        }
        this.endLazyMainSection(writer);
    }

    private void startLazyMainSection(DiagnosticsLogWriter writer) {
        if (!this.mainSectionStarted) {
            this.mainSectionStarted = true;
            writer.startSection("MemberHeartbeats");
        }
    }

    private void endLazyMainSection(DiagnosticsLogWriter writer) {
        if (this.mainSectionStarted) {
            this.mainSectionStarted = false;
            writer.endSection();
        }
    }
}

