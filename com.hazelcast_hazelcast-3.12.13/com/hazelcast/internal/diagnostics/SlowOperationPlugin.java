/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.diagnostics;

import com.hazelcast.internal.diagnostics.DiagnosticsLogWriter;
import com.hazelcast.internal.diagnostics.DiagnosticsPlugin;
import com.hazelcast.internal.management.dto.SlowOperationDTO;
import com.hazelcast.internal.management.dto.SlowOperationInvocationDTO;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.spi.properties.HazelcastProperty;
import com.hazelcast.util.StringUtil;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SlowOperationPlugin
extends DiagnosticsPlugin {
    public static final HazelcastProperty PERIOD_SECONDS = new HazelcastProperty("hazelcast.diagnostics.slowoperations.period.seconds", 60, TimeUnit.SECONDS);
    private final InternalOperationService operationService;
    private final long periodMillis;

    public SlowOperationPlugin(NodeEngineImpl nodeEngine) {
        super(nodeEngine.getLogger(SlowOperationPlugin.class));
        this.operationService = nodeEngine.getOperationService();
        this.periodMillis = this.getPeriodMillis(nodeEngine);
    }

    private long getPeriodMillis(NodeEngineImpl nodeEngine) {
        HazelcastProperties props = nodeEngine.getProperties();
        if (!props.getBoolean(GroupProperty.SLOW_OPERATION_DETECTOR_ENABLED)) {
            return 0L;
        }
        return props.getMillis(PERIOD_SECONDS);
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
        List<SlowOperationDTO> slowOperations = this.operationService.getSlowOperationDTOs();
        writer.startSection("SlowOperations");
        if (slowOperations.size() > 0) {
            for (SlowOperationDTO slowOperation : slowOperations) {
                this.render(writer, slowOperation);
            }
        }
        writer.endSection();
    }

    private void render(DiagnosticsLogWriter writer, SlowOperationDTO slowOperation) {
        writer.startSection(slowOperation.operation);
        writer.writeKeyValueEntry("invocations", slowOperation.totalInvocations);
        this.renderStackTrace(writer, slowOperation);
        this.renderInvocations(writer, slowOperation);
        writer.endSection();
    }

    private void renderInvocations(DiagnosticsLogWriter writer, SlowOperationDTO slowOperation) {
        writer.startSection("slowInvocations");
        for (SlowOperationInvocationDTO invocation : slowOperation.invocations) {
            writer.writeKeyValueEntry("startedAt", invocation.startedAt);
            writer.writeKeyValueEntryAsDateTime("started(date-time)", invocation.startedAt);
            writer.writeKeyValueEntry("duration(ms)", invocation.durationMs);
            writer.writeKeyValueEntry("operationDetails", invocation.operationDetails);
        }
        writer.endSection();
    }

    private void renderStackTrace(DiagnosticsLogWriter writer, SlowOperationDTO slowOperation) {
        String[] stackTraceLines;
        writer.startSection("stackTrace");
        for (String stackTraceLine : stackTraceLines = slowOperation.stackTrace.split(StringUtil.LINE_SEPARATOR)) {
            writer.writeEntry(stackTraceLine);
        }
        writer.endSection();
    }
}

