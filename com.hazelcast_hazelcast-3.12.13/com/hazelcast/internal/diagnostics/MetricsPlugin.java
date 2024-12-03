/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.diagnostics;

import com.hazelcast.internal.diagnostics.DiagnosticsLogWriter;
import com.hazelcast.internal.diagnostics.DiagnosticsPlugin;
import com.hazelcast.internal.metrics.MetricsRegistry;
import com.hazelcast.internal.metrics.renderers.ProbeRenderer;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.spi.properties.HazelcastProperty;
import java.util.concurrent.TimeUnit;

public class MetricsPlugin
extends DiagnosticsPlugin {
    public static final HazelcastProperty PERIOD_SECONDS = new HazelcastProperty("hazelcast.diagnostics.metrics.period.seconds", 60, TimeUnit.SECONDS);
    private final MetricsRegistry metricsRegistry;
    private final long periodMillis;
    private final ProbeRendererImpl probeRenderer = new ProbeRendererImpl();

    public MetricsPlugin(NodeEngineImpl nodeEngine) {
        this(nodeEngine.getLogger(MetricsPlugin.class), nodeEngine.getMetricsRegistry(), nodeEngine.getProperties());
    }

    public MetricsPlugin(ILogger logger, MetricsRegistry metricsRegistry, HazelcastProperties properties) {
        super(logger);
        this.metricsRegistry = metricsRegistry;
        this.periodMillis = properties.getMillis(PERIOD_SECONDS);
    }

    @Override
    public void onStart() {
        this.logger.info("Plugin:active, period-millis:" + this.periodMillis);
    }

    @Override
    public long getPeriodMillis() {
        return this.periodMillis;
    }

    @Override
    public void run(DiagnosticsLogWriter writer) {
        this.probeRenderer.writer = writer;
        this.probeRenderer.timeMillis = System.currentTimeMillis();
        this.metricsRegistry.render(this.probeRenderer);
        this.probeRenderer.writer = null;
    }

    private static class ProbeRendererImpl
    implements ProbeRenderer {
        private static final String SECTION_NAME = "Metric";
        private DiagnosticsLogWriter writer;
        private long timeMillis;

        private ProbeRendererImpl() {
        }

        @Override
        public void renderLong(String name, long value) {
            this.writer.writeSectionKeyValue(SECTION_NAME, this.timeMillis, name, value);
        }

        @Override
        public void renderDouble(String name, double value) {
            this.writer.writeSectionKeyValue(SECTION_NAME, this.timeMillis, name, value);
        }

        @Override
        public void renderException(String name, Exception e) {
            this.writer.writeSectionKeyValue(SECTION_NAME, this.timeMillis, name, e.getClass().getName() + ':' + e.getMessage());
        }

        @Override
        public void renderNoValue(String name) {
            this.writer.writeSectionKeyValue(SECTION_NAME, this.timeMillis, name, "NA");
        }
    }
}

