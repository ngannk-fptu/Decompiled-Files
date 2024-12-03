/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.diagnostics;

import com.hazelcast.internal.diagnostics.DiagnosticsLogWriter;
import com.hazelcast.internal.diagnostics.DiagnosticsPlugin;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationservice.impl.InvocationMonitor;
import com.hazelcast.spi.impl.operationservice.impl.OperationServiceImpl;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.spi.properties.HazelcastProperty;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class OperationHeartbeatPlugin
extends DiagnosticsPlugin {
    public static final HazelcastProperty PERIOD_SECONDS = new HazelcastProperty("hazelcast.diagnostics.operation-heartbeat.seconds", 10, TimeUnit.SECONDS);
    public static final HazelcastProperty MAX_DEVIATION_PERCENTAGE = new HazelcastProperty("hazelcast.diagnostics.operation-heartbeat.max-deviation-percentage", 33);
    private static final float HUNDRED = 100.0f;
    private final long periodMillis;
    private final long expectedIntervalMillis;
    private final int maxDeviationPercentage;
    private final ConcurrentMap<Address, AtomicLong> heartbeatPerMember;
    private boolean mainSectionStarted;

    public OperationHeartbeatPlugin(NodeEngineImpl nodeEngine) {
        super(nodeEngine.getLogger(OperationHeartbeatPlugin.class));
        InvocationMonitor invocationMonitor = ((OperationServiceImpl)nodeEngine.getOperationService()).getInvocationMonitor();
        HazelcastProperties properties = nodeEngine.getProperties();
        this.periodMillis = properties.getMillis(PERIOD_SECONDS);
        this.maxDeviationPercentage = properties.getInteger(MAX_DEVIATION_PERCENTAGE);
        this.expectedIntervalMillis = invocationMonitor.getHeartbeatBroadcastPeriodMillis();
        this.heartbeatPerMember = invocationMonitor.getHeartbeatPerMember();
    }

    @Override
    public long getPeriodMillis() {
        return this.periodMillis;
    }

    @Override
    public void onStart() {
        this.logger.info("Plugin:active: period-millis:" + this.periodMillis + " max-deviation:" + this.maxDeviationPercentage + "%");
    }

    @Override
    public void run(DiagnosticsLogWriter writer) {
        long nowMillis = System.currentTimeMillis();
        for (Map.Entry entry : this.heartbeatPerMember.entrySet()) {
            Address member = (Address)entry.getKey();
            long lastHeartbeatMillis = ((AtomicLong)entry.getValue()).longValue();
            long noHeartbeatMillis = nowMillis - lastHeartbeatMillis;
            float deviation = 100.0f * (float)(noHeartbeatMillis - this.expectedIntervalMillis) / (float)this.expectedIntervalMillis;
            if (!(deviation >= (float)this.maxDeviationPercentage)) continue;
            this.startLazyMainSection(writer);
            writer.startSection("member" + member);
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
            writer.startSection("OperationHeartbeat");
        }
    }

    private void endLazyMainSection(DiagnosticsLogWriter writer) {
        if (this.mainSectionStarted) {
            this.mainSectionStarted = false;
            writer.endSection();
        }
    }
}

