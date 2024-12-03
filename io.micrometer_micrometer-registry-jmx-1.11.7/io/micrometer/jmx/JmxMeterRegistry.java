/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codahale.metrics.MetricRegistry
 *  com.codahale.metrics.jmx.JmxReporter
 *  io.micrometer.core.instrument.Clock
 *  io.micrometer.core.instrument.dropwizard.DropwizardConfig
 *  io.micrometer.core.instrument.dropwizard.DropwizardMeterRegistry
 *  io.micrometer.core.instrument.util.HierarchicalNameMapper
 */
package io.micrometer.jmx;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jmx.JmxReporter;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.dropwizard.DropwizardConfig;
import io.micrometer.core.instrument.dropwizard.DropwizardMeterRegistry;
import io.micrometer.core.instrument.util.HierarchicalNameMapper;
import io.micrometer.jmx.JmxConfig;

public class JmxMeterRegistry
extends DropwizardMeterRegistry {
    private final JmxReporter reporter;

    public JmxMeterRegistry(JmxConfig config, Clock clock) {
        this(config, clock, HierarchicalNameMapper.DEFAULT);
    }

    public JmxMeterRegistry(JmxConfig config, Clock clock, HierarchicalNameMapper nameMapper) {
        this(config, clock, nameMapper, new MetricRegistry());
    }

    public JmxMeterRegistry(JmxConfig config, Clock clock, HierarchicalNameMapper nameMapper, MetricRegistry metricRegistry) {
        this(config, clock, nameMapper, metricRegistry, JmxMeterRegistry.defaultJmxReporter(config, metricRegistry));
    }

    public JmxMeterRegistry(JmxConfig config, Clock clock, HierarchicalNameMapper nameMapper, MetricRegistry metricRegistry, JmxReporter jmxReporter) {
        super((DropwizardConfig)config, metricRegistry, nameMapper, clock);
        this.reporter = jmxReporter;
        this.reporter.start();
    }

    private static JmxReporter defaultJmxReporter(JmxConfig config, MetricRegistry metricRegistry) {
        return JmxReporter.forRegistry((MetricRegistry)metricRegistry).inDomain(config.domain()).build();
    }

    public void stop() {
        this.reporter.stop();
    }

    public void start() {
        this.reporter.start();
    }

    public void close() {
        this.stop();
        super.close();
    }

    protected Double nullGaugeValue() {
        return Double.NaN;
    }
}

