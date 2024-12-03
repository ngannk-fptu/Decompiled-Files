/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.diagnostics;

import com.hazelcast.internal.diagnostics.DiagnosticsLogWriter;
import com.hazelcast.internal.diagnostics.DiagnosticsPlugin;
import com.hazelcast.internal.diagnostics.OperationDescriptors;
import com.hazelcast.internal.diagnostics.PendingInvocationsPlugin;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.impl.operationservice.impl.Invocation;
import com.hazelcast.spi.impl.operationservice.impl.InvocationRegistry;
import com.hazelcast.spi.impl.operationservice.impl.OperationServiceImpl;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.spi.properties.HazelcastProperty;
import com.hazelcast.util.Clock;
import com.hazelcast.util.ItemCounter;
import java.util.concurrent.TimeUnit;

public class InvocationPlugin
extends DiagnosticsPlugin {
    public static final HazelcastProperty SAMPLE_PERIOD_SECONDS = new HazelcastProperty("hazelcast.diagnostics.invocation.sample.period.seconds", 0, TimeUnit.SECONDS);
    public static final HazelcastProperty SLOW_THRESHOLD_SECONDS = new HazelcastProperty("hazelcast.diagnostics.invocation.slow.threshold.seconds", 5, TimeUnit.SECONDS);
    public static final HazelcastProperty SLOW_MAX_COUNT = new HazelcastProperty("hazelcast.diagnostics.invocation.slow.max.count", 100);
    private final InvocationRegistry invocationRegistry;
    private final long samplePeriodMillis;
    private final long thresholdMillis;
    private final int maxCount;
    private final ItemCounter<String> slowOccurrences = new ItemCounter();
    private final ItemCounter<String> occurrences = new ItemCounter();

    public InvocationPlugin(NodeEngineImpl nodeEngine) {
        super(nodeEngine.getLogger(PendingInvocationsPlugin.class));
        InternalOperationService operationService = nodeEngine.getOperationService();
        this.invocationRegistry = ((OperationServiceImpl)operationService).getInvocationRegistry();
        HazelcastProperties props = nodeEngine.getProperties();
        this.samplePeriodMillis = props.getMillis(SAMPLE_PERIOD_SECONDS);
        this.thresholdMillis = props.getMillis(SLOW_THRESHOLD_SECONDS);
        this.maxCount = props.getInteger(SLOW_MAX_COUNT);
    }

    @Override
    public long getPeriodMillis() {
        return this.samplePeriodMillis;
    }

    @Override
    public void onStart() {
        this.logger.info("Plugin:active: period-millis:" + this.samplePeriodMillis + " threshold-millis:" + this.thresholdMillis);
    }

    @Override
    public void run(DiagnosticsLogWriter writer) {
        long now = Clock.currentTimeMillis();
        writer.startSection("Invocations");
        this.runCurrent(writer, now);
        this.renderHistory(writer);
        this.renderSlowHistory(writer);
        writer.endSection();
    }

    private void runCurrent(DiagnosticsLogWriter writer, long now) {
        writer.startSection("Pending");
        int count = 0;
        boolean maxPrinted = false;
        for (Invocation invocation : this.invocationRegistry) {
            long durationMs = now - invocation.firstInvocationTimeMillis;
            String operationDesc = OperationDescriptors.toOperationDesc(invocation.op);
            this.occurrences.add(operationDesc, 1L);
            if (durationMs < this.thresholdMillis) continue;
            if (++count < this.maxCount) {
                writer.writeEntry(invocation.toString() + " duration=" + durationMs + " ms");
            } else if (!maxPrinted) {
                maxPrinted = true;
                writer.writeEntry("max number of invocations to print reached.");
            }
            this.slowOccurrences.add(operationDesc, 1L);
        }
        writer.endSection();
    }

    private void renderHistory(DiagnosticsLogWriter writer) {
        writer.startSection("History");
        for (String item : this.occurrences.descendingKeys()) {
            writer.writeEntry(item + " samples=" + this.occurrences.get(item));
        }
        writer.endSection();
    }

    private void renderSlowHistory(DiagnosticsLogWriter writer) {
        writer.startSection("SlowHistory");
        for (String item : this.slowOccurrences.descendingKeys()) {
            writer.writeEntry(item + " samples=" + this.slowOccurrences.get(item));
        }
        writer.endSection();
    }
}

