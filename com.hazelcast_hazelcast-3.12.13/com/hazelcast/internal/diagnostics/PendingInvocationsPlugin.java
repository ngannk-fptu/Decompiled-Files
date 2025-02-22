/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.diagnostics;

import com.hazelcast.internal.diagnostics.DiagnosticsLogWriter;
import com.hazelcast.internal.diagnostics.DiagnosticsPlugin;
import com.hazelcast.internal.diagnostics.OperationDescriptors;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.impl.operationservice.impl.Invocation;
import com.hazelcast.spi.impl.operationservice.impl.InvocationRegistry;
import com.hazelcast.spi.impl.operationservice.impl.OperationServiceImpl;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.spi.properties.HazelcastProperty;
import com.hazelcast.util.ItemCounter;
import java.util.concurrent.TimeUnit;

public final class PendingInvocationsPlugin
extends DiagnosticsPlugin {
    public static final HazelcastProperty PERIOD_SECONDS = new HazelcastProperty("hazelcast.diagnostics.pending.invocations.period.seconds", 0, TimeUnit.SECONDS);
    public static final HazelcastProperty THRESHOLD = new HazelcastProperty("hazelcast.diagnostics.pending.invocations.threshold", 1);
    private final InvocationRegistry invocationRegistry;
    private final ItemCounter<String> occurrenceMap = new ItemCounter();
    private final long periodMillis;
    private final int threshold;

    public PendingInvocationsPlugin(NodeEngineImpl nodeEngine) {
        super(nodeEngine.getLogger(PendingInvocationsPlugin.class));
        InternalOperationService operationService = nodeEngine.getOperationService();
        this.invocationRegistry = ((OperationServiceImpl)operationService).getInvocationRegistry();
        HazelcastProperties props = nodeEngine.getProperties();
        this.periodMillis = props.getMillis(PERIOD_SECONDS);
        this.threshold = props.getInteger(THRESHOLD);
    }

    @Override
    public long getPeriodMillis() {
        return this.periodMillis;
    }

    @Override
    public void onStart() {
        this.logger.info("Plugin:active: period-millis:" + this.periodMillis + " threshold:" + this.threshold);
    }

    @Override
    public void run(DiagnosticsLogWriter writer) {
        this.clean();
        this.scan();
        this.render(writer);
    }

    private void clean() {
        this.occurrenceMap.reset();
    }

    private void scan() {
        for (Invocation invocation : this.invocationRegistry) {
            this.occurrenceMap.add(OperationDescriptors.toOperationDesc(invocation.op), 1L);
        }
    }

    private void render(DiagnosticsLogWriter writer) {
        writer.startSection("PendingInvocations");
        writer.writeKeyValueEntry("count", this.invocationRegistry.size());
        this.renderInvocations(writer);
        writer.endSection();
    }

    private void renderInvocations(DiagnosticsLogWriter writer) {
        writer.startSection("invocations");
        for (String op : this.occurrenceMap.keySet()) {
            long count = this.occurrenceMap.get(op);
            if (count < (long)this.threshold) continue;
            writer.writeKeyValueEntry(op, count);
        }
        writer.endSection();
    }
}

